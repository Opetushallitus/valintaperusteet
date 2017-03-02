package fi.vm.sade.service.valintaperusteet.service.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.pattern.Patterns;
import akka.util.Timeout;
import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.ActorService;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiHakukohteenValintaperusteRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiValintaperusteRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.util.LaskentakaavaCache;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

@Service
@Transactional
public class LaskentakaavaServiceImpl implements LaskentakaavaService {
    final static private Logger LOGGER = LoggerFactory.getLogger(LaskentakaavaService.class.getName());
    private static final String r = "\\{\\{([A-Za-z0–9\\-_]+)\\.([A-Za-z0–9\\-_]+)\\}\\}";
    public static final Pattern pattern = Pattern.compile(r);

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private LaskentakaavaCache laskentakaavaCache;

    @Autowired
    private JarjestyskriteeriDAO jarjestyskriteeriDAO;

    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private ValintakoeDAO valintakoeDAO;

    @Autowired
    private SyotettavanarvontyyppiDAO syotettavanarvontyyppiDAO;


    @Autowired
    private ActorService actorService;

    @Transactional(readOnly = true)
    public Funktiokutsu haeFunktiokutsuRekursiivisesti(final Long id, final boolean laajennaAlakaavat,
                                                       final Set<Long> laskentakaavaIds) throws FunktiokutsuMuodostaaSilmukanException {
        Timeout timeout = new Timeout(Duration.create(30, "seconds"));
        ActorSystem actorSystem = actorService.getActorSystem();
        ActorRef master = actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("HaeFunktiokutsuRekursiivisestiActorBean"), UUID.randomUUID().toString());
        Future<Object> future = Patterns.ask(master, new UusiRekursio(id, laajennaAlakaavat, laskentakaavaIds), timeout);
        try {
            Funktiokutsu funktiokutsu = (Funktiokutsu) Await.result(future, timeout.duration());
            master.tell(PoisonPill.getInstance(), ActorRef.noSender());
            return funktiokutsu;
        } catch (Exception e) {
            if (e instanceof FunktiokutsuMuodostaaSilmukanException) {
                FunktiokutsuMuodostaaSilmukanException exp = (FunktiokutsuMuodostaaSilmukanException) e;
                throw new FunktiokutsuMuodostaaSilmukanException(exp.getMessage(), exp.getFunktiokutsuId(), exp.getFunktionimi(), exp.getLaskentakaavaId());
            } else if (e instanceof FunktiokutsuEiOleOlemassaException) {
                throw new FunktiokutsuEiOleOlemassaException("Funktiokutsu (" + id + ") ei ole olemassa", id);
            } else {
                LOGGER.error("Virhe laskentakaavan haussa!", e);
                throw new FunktiokutsuEiOleOlemassaException("Odottomaton virhe haettaessa funktiokutsua " + id + ": " + e.getCause(), id);
            }
        }
    }

    @Override
    @Transactional
    public Funktiokutsu haeMallinnettuFunktiokutsu(Long id) throws FunktiokutsuMuodostaaSilmukanException {
        return haeFunktiokutsuRekursiivisesti(id, false, new HashSet<Long>());
    }

    private Laskentakaava haeLaskentakaava(Long id) {
        Laskentakaava laskentakaava = laskentakaavaDAO.getLaskentakaava(id);
        if (laskentakaava == null) {
            throw new LaskentakaavaEiOleOlemassaException("Laskentakaava (" + id + ") ei ole olemassa.", id);
        }
        return laskentakaava;
    }

    @Override
    @Transactional
    public Laskentakaava haeMallinnettuKaava(Long id) {
        return haeKokoLaskentakaava(id, false);
    }

    @Override
    public Laskentakaava update(Long id, LaskentakaavaCreateDTO incoming) {
        laskentakaavaCache.clear();
        try {
            Laskentakaava entity = modelMapper.map(incoming, Laskentakaava.class);
            asetaNullitOletusarvoiksi(entity.getFunktiokutsu());
            Laskentakaava managed = haeLaskentakaava(id);
            Set<Long> laskentakaavaIds = new HashSet<Long>();
            laskentakaavaIds.add(managed.getId());
            managed.setNimi(entity.getNimi());
            managed.setKuvaus(entity.getKuvaus());
            managed.setFunktiokutsu(updateFunktiokutsu(entity.getFunktiokutsu(), false, entity.getHakukohde(), entity.getValintaryhma(), laskentakaavaIds));
            if (!Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(managed)) {
                throw new LaskentakaavaEiValidiException("Laskentakaava ei ole validi", Laskentakaavavalidaattori.validoiMallinnettuKaava(entity));
            }
            // Ajastetaan orpojen poisto jos ajastin jostain syystä kaatunut
            actorService.runSchedulerIfNotRunning();
            return managed;
        } catch (FunktiokutsuMuodostaaSilmukanException e) {
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava " + id + " muodostaa silmukan "
                    + "laskentakaavaan " + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta", e, id,
                    e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
    }

    private void asetaNullitOletusarvoiksi(Funktiokutsu fk) {
        for (ValintaperusteViite vp : fk.getValintaperusteviitteet()) {
            if (vp.getOnPakollinen() == null) {
                vp.setOnPakollinen(false);
            }
            if (vp.getEpasuoraViittaus() == null) {
                vp.setEpasuoraViittaus(false);
            }
        }
        for (Arvokonvertteriparametri a : fk.getArvokonvertteriparametrit()) {
            if (a.getHylkaysperuste() == null) {
                a.setHylkaysperuste("false");
            }
        }
        for (Arvovalikonvertteriparametri a : fk.getArvovalikonvertteriparametrit()) {
            if (a.getPalautaHaettuArvo() == null) {
                a.setPalautaHaettuArvo("false");
            }
        }
        for (Syoteparametri s : fk.getSyoteparametrit()) {
            if (s.getArvo() == null) {
                s.setArvo("");
            }
        }
        if (fk.getValintaperusteviitteet().size() == 1) {
            fk.getValintaperusteviitteet().iterator().next().setIndeksi(1);
        }
        for (Funktioargumentti arg : fk.getFunktioargumentit()) {
            if (arg.getFunktiokutsuChild() != null) {
                asetaNullitOletusarvoiksi(arg.getFunktiokutsuChild());
            }
        }
    }

    private Funktiokutsu updateFunktiokutsu(Funktiokutsu incoming, boolean copy, HakukohdeViite hakukohde, Valintaryhma valintaryhma) throws FunktiokutsuMuodostaaSilmukanException {
        return updateFunktiokutsu(incoming, copy, hakukohde, valintaryhma, new HashSet<Long>());
    }

    private Funktiokutsu updateFunktiokutsu(Funktiokutsu incoming, boolean copy, HakukohdeViite hakukohde, Valintaryhma valintaryhma, Set<Long> laskentakaavaIds) throws FunktiokutsuMuodostaaSilmukanException {
        Funktiokutsu managed = null;
        if (!copy && incoming.getId() != null) {
            managed = funktiokutsuDAO.read(incoming.getId());
            for (Funktioargumentti arg : managed.getFunktioargumentit()) {
                genericDAO.remove(arg);
            }
            for (Arvokonvertteriparametri p : managed.getArvokonvertteriparametrit()) {
                genericDAO.remove(p);
            }
            for (Arvovalikonvertteriparametri p : managed.getArvovalikonvertteriparametrit()) {
                genericDAO.remove(p);
            }
            for (Syoteparametri p : managed.getSyoteparametrit()) {
                genericDAO.remove(p);
            }
            for (ValintaperusteViite vp : managed.getValintaperusteviitteet()) {
                genericDAO.remove(vp);
            }
            managed.getFunktioargumentit().clear();
            managed.getArvokonvertteriparametrit().clear();
            managed.getArvovalikonvertteriparametrit().clear();
            managed.getSyoteparametrit().clear();
            managed.getValintaperusteviitteet().clear();
            funktiokutsuDAO.flush();
        } else {
            managed = new Funktiokutsu();
        }
        managed.setFunktionimi(incoming.getFunktionimi());
        managed.setTallennaTulos(incoming.getTallennaTulos());
        managed.setTulosTunniste(incoming.getTulosTunniste());
        managed.setTulosTekstiEn(incoming.getTulosTekstiEn());
        managed.setTulosTekstiFi(incoming.getTulosTekstiFi());
        managed.setTulosTekstiSv(incoming.getTulosTekstiSv());
        for (Funktioargumentti arg : incoming.getFunktioargumentit()) {
            Funktioargumentti newArg = new Funktioargumentti();
            newArg.setParent(managed);
            if (arg.getFunktiokutsuChild() != null) {
                newArg.setFunktiokutsuChild(updateFunktiokutsu(arg.getFunktiokutsuChild(), copy, hakukohde, valintaryhma, laskentakaavaIds));
            } else {
                Long laskentakaavaId = arg.getLaskentakaavaChild().getId();
                if (laskentakaavaIds.contains(laskentakaavaId)) {
                    throw new FunktiokutsuMuodostaaSilmukanException("Funktiokutsu "
                            + (managed.getId() != null ? managed.getId() : managed.getFunktionimi().name())
                            + " muodostaa silmukan " + "laskentakaavaan " + laskentakaavaId, managed.getId(),
                            managed.getFunktionimi(), laskentakaavaId);
                }
                Set<Long> newLaskentakaavaIds = new HashSet<Long>(laskentakaavaIds);
                newLaskentakaavaIds.add(laskentakaavaId);
                final Laskentakaava oldLaskentakaava = haeKokoLaskentakaavaJaTarkistaSilmukat(laskentakaavaId, newLaskentakaavaIds);
                if (hakukohde == null && valintaryhma == null) {
                    newArg.setLaskentakaavaChild(oldLaskentakaava);
                } else {
                    newArg.setLaskentakaavaChild(kopioiJosEiJoKopioitu(oldLaskentakaava, hakukohde, valintaryhma));
                }
            }
            newArg.setIndeksi(arg.getIndeksi());
            managed.getFunktioargumentit().add(newArg);
        }
        for (Arvokonvertteriparametri k : incoming.getArvokonvertteriparametrit()) {
            Arvokonvertteriparametri newParam = new Arvokonvertteriparametri();
            newParam.setArvo(k.getArvo());
            newParam.setHylkaysperuste(k.getHylkaysperuste());
            newParam.setPaluuarvo(k.getPaluuarvo());
            newParam.setFunktiokutsu(managed);
            if (k.getKuvaukset() != null) {
                TekstiRyhma ryhma = new TekstiRyhma();
                genericDAO.insert(ryhma, false);
                for (LokalisoituTeksti teksti : k.getKuvaukset().getTekstit()) {
                    LokalisoituTeksti newTeksti = new LokalisoituTeksti();
                    newTeksti.setKieli(teksti.getKieli());
                    newTeksti.setTeksti(teksti.getTeksti());
                    newTeksti.setRyhma(ryhma);
                    genericDAO.insert(newTeksti, false);
                    ryhma.getTekstit().add(newTeksti);
                }
                newParam.setKuvaukset(ryhma);
            }
            managed.getArvokonvertteriparametrit().add(newParam);
        }
        for (Arvovalikonvertteriparametri k : incoming.getArvovalikonvertteriparametrit()) {
            Arvovalikonvertteriparametri newParam = new Arvovalikonvertteriparametri();
            newParam.setMaxValue(k.getMaxValue());
            newParam.setMinValue(k.getMinValue());
            newParam.setPalautaHaettuArvo(k.getPalautaHaettuArvo());
            newParam.setPaluuarvo(k.getPaluuarvo());
            newParam.setFunktiokutsu(managed);
            newParam.setHylkaysperuste(k.getHylkaysperuste());
            if (k.getKuvaukset() != null) {
                TekstiRyhma ryhma = new TekstiRyhma();
                genericDAO.insert(ryhma, false);
                for (LokalisoituTeksti teksti : k.getKuvaukset().getTekstit()) {
                    LokalisoituTeksti newTeksti = new LokalisoituTeksti();
                    newTeksti.setKieli(teksti.getKieli());
                    newTeksti.setTeksti(teksti.getTeksti());
                    newTeksti.setRyhma(ryhma);
                    genericDAO.insert(newTeksti, false);
                    ryhma.getTekstit().add(newTeksti);
                }
                newParam.setKuvaukset(ryhma);
            }
            managed.getArvovalikonvertteriparametrit().add(newParam);
        }
        for (Syoteparametri s : incoming.getSyoteparametrit()) {
            Syoteparametri newParam = new Syoteparametri();
            newParam.setArvo(s.getArvo());
            newParam.setAvain(s.getAvain());
            newParam.setFunktiokutsu(managed);
            managed.getSyoteparametrit().add(newParam);
        }
        for (ValintaperusteViite vp : incoming.getValintaperusteviitteet()) {
            ValintaperusteViite newVp = new ValintaperusteViite();
            newVp.setEpasuoraViittaus(vp.getEpasuoraViittaus());
            newVp.setIndeksi(vp.getIndeksi() + 1);
            newVp.setKuvaus(vp.getKuvaus());
            newVp.setLahde(vp.getLahde());
            newVp.setOnPakollinen(vp.getOnPakollinen());
            newVp.setTunniste(vp.getTunniste());

            if (vp.getSyotettavanarvontyyppi() != null) {
                Syotettavanarvontyyppi syokoodi = vp.getSyotettavanarvontyyppi();
                Syotettavanarvontyyppi found = syotettavanarvontyyppiDAO.readByUri(syokoodi.getUri());

                if (found != null) {
                    newVp.setSyotettavanarvontyyppi(found);
                } else {
                    Syotettavanarvontyyppi uusikoodi = new Syotettavanarvontyyppi();
                    uusikoodi.setUri(syokoodi.getUri());
                    uusikoodi.setArvo(syokoodi.getArvo());
                    uusikoodi.setNimiFi(syokoodi.getNimiFi());
                    uusikoodi.setNimiEn(syokoodi.getNimiEn());
                    uusikoodi.setNimiSv(syokoodi.getNimiSv());
                    Syotettavanarvontyyppi inserted = syotettavanarvontyyppiDAO.insertOrUpdate(uusikoodi);
                    newVp.setSyotettavanarvontyyppi(inserted);
                }
            }
            newVp.setTilastoidaan(vp.getTilastoidaan());

            newVp.setVaatiiOsallistumisen(vp.getVaatiiOsallistumisen());
            newVp.setSyotettavissaKaikille(vp.getSyotettavissaKaikille());
            newVp.setFunktiokutsu(managed);
            if (vp.getKuvaukset() != null) {
                TekstiRyhma ryhma = new TekstiRyhma();
                genericDAO.insert(ryhma, false);
                for (LokalisoituTeksti teksti : vp.getKuvaukset().getTekstit()) {
                    LokalisoituTeksti newTeksti = new LokalisoituTeksti();
                    newTeksti.setKieli(teksti.getKieli());
                    newTeksti.setTeksti(teksti.getTeksti());
                    newTeksti.setRyhma(ryhma);
                    genericDAO.insert(newTeksti, false);
                    ryhma.getTekstit().add(newTeksti);
                }
                newVp.setKuvaukset(ryhma);
            }
            managed.getValintaperusteviitteet().add(newVp);
        }
        if (managed.getId() == null) {
            funktiokutsuDAO.insert(managed, false);
        }
        return managed;
    }

    @Override
    public Laskentakaava insert(Laskentakaava laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        laskentakaavaCache.clear();
        try {
            asetaNullitOletusarvoiksi(laskentakaava.getFunktiokutsu());
            Laskentakaava entity = modelMapper.map(laskentakaava, Laskentakaava.class);
            if (!Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(entity)) {
                throw new LaskentakaavaEiValidiException("Laskentakaava ei ole validi", Laskentakaavavalidaattori.validoiMallinnettuKaava(entity));
            }
            HakukohdeViite hakukohde = null;
            Valintaryhma valintaryhma = null;
            if (StringUtils.isNotBlank(hakukohdeOid)) {
                hakukohde = hakukohdeViiteDAO.readForImport(hakukohdeOid);
                entity.setHakukohde(hakukohde);
            } else if (StringUtils.isNotBlank(valintaryhmaOid)) {
                valintaryhma = valintaryhmaDAO.readByOid(valintaryhmaOid);
                entity.setValintaryhma(valintaryhma);
            }
            entity.setFunktiokutsu(updateFunktiokutsu(entity.getFunktiokutsu(), false, hakukohde, valintaryhma));
            return laskentakaavaDAO.insert(entity);
        } catch (FunktiokutsuMuodostaaSilmukanException e) {
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava  muodostaa silmukan " + "laskentakaavaan "
                    + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta", e,
                    null, e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
    }

    @Override
    public void tyhjennaCache() {
        laskentakaavaCache.clear();
    }


    @Override
    public Optional<Laskentakaava> siirra(LaskentakaavaSiirraDTO dto) {
        if (dto.getUusinimi() != null) {
            dto.setNimi(dto.getUusinimi());
        }
        String valintaryhmaOid = null;
        String hakukohdeOid = null;
        if (dto.getValintaryhmaOid() != null && !dto.getValintaryhmaOid().isEmpty()) {
            Optional<Valintaryhma> ryhma = Optional.ofNullable(valintaryhmaDAO.readByOid(dto.getValintaryhmaOid()));
            if (ryhma.isPresent()) {
                valintaryhmaOid = ryhma.get().getOid();
            }
        }
        if (dto.getHakukohdeOid() != null && !dto.getHakukohdeOid().isEmpty()) {
            Optional<HakukohdeViite> hakukohde = Optional.ofNullable(hakukohdeViiteDAO.readForImport(dto.getHakukohdeOid()));
            if (hakukohde.isPresent()) {
                hakukohdeOid = hakukohde.get().getOid();
            }
        }
        if (valintaryhmaOid == null && hakukohdeOid == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(insert(modelMapper.map(dto, Laskentakaava.class), hakukohdeOid, valintaryhmaOid));
    }

    @Override
    public Optional<Laskentakaava> haeLaskentakaavaTaiSenKopioVanhemmilta(Long laskentakaavaId, HakukohdeViite hakukohde) {
        if (laskentakaavaId == null || hakukohde == null) {
            return Optional.empty();
        }
        Optional<Laskentakaava> kaava = (hakukohde.getLaskentakaava().stream()
                .filter(k -> onSamaTaiKopioSamastaKaavasta(laskentakaavaId, k)))
                .findFirst();

        if (kaava.isPresent()){
            return kaava;
        } else {
            Set<Long> tarkistetutLaskentaKaavaIdt = getLaskentakaavaIds(hakukohde.getLaskentakaava());
            return haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(laskentakaavaId, hakukohde.getValintaryhma(), tarkistetutLaskentaKaavaIdt);
        }
    }

    @Override
    public Optional<Laskentakaava> haeLaskentakaavaTaiSenKopioVanhemmilta(Long laskentakaavaId, Valintaryhma valintaryhma) {
        if (laskentakaavaId == null || valintaryhma == null) {
            return Optional.empty();
        }
        return haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(laskentakaavaId, valintaryhma, new HashSet<>());
    }

    private boolean onSamaTaiKopioSamastaKaavasta(Long laskentakaavaId, Laskentakaava kaava) {
        return laskentakaavaId.equals(kaava.getId()) ||
                (kaava.getKopioLaskentakaavasta() != null && laskentakaavaId.equals(kaava.getKopioLaskentakaavasta().getId()));
    }

    private Set<Long> getLaskentakaavaIds(Set<Laskentakaava> set) {
        return set.stream()
                .map(Laskentakaava::getId)
                .collect(Collectors.toSet());
    }

    private Optional<Laskentakaava> haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(Long laskentakaavaId, Valintaryhma valintaryhma, final Set<Long> tarkistetutLaskentaKaavaIdt) {
        Optional<Laskentakaava> kaava = (valintaryhma.getLaskentakaava().stream()
                .filter(k -> !tarkistetutLaskentaKaavaIdt.contains(k.getId()))
                .filter(k -> onSamaTaiKopioSamastaKaavasta(laskentakaavaId, k)))
                .findFirst();

        if (kaava.isPresent()){
            return kaava;
        } else if (valintaryhma.getYlavalintaryhma() != null) {
            tarkistetutLaskentaKaavaIdt.addAll(getLaskentakaavaIds(valintaryhma.getLaskentakaava()));
            return haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(laskentakaavaId, valintaryhma.getYlavalintaryhma(), tarkistetutLaskentaKaavaIdt);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Laskentakaava kopioiJosEiJoKopioitu(Laskentakaava lahdeLaskentakaava, HakukohdeViite kohdeHakukohde, Valintaryhma kohdeValintaryhma) {
        Optional<Laskentakaava> aikaisemminKopioituLaskentakaava;
        if (kohdeHakukohde != null) {
            aikaisemminKopioituLaskentakaava = haeLaskentakaavaTaiSenKopioVanhemmilta(lahdeLaskentakaava.getId(), kohdeHakukohde);
        } else {
            aikaisemminKopioituLaskentakaava = haeLaskentakaavaTaiSenKopioVanhemmilta(lahdeLaskentakaava.getId(), kohdeValintaryhma);
        }
        if (aikaisemminKopioituLaskentakaava.isPresent()) {
            LOGGER.info("Käytetään laskentakaavan {} olemassaolevaa versiota {}: kohde hakukohde={}, kohde valintaryhma={}", lahdeLaskentakaava, aikaisemminKopioituLaskentakaava.get(), kohdeHakukohde, kohdeValintaryhma);
            return aikaisemminKopioituLaskentakaava.get();
        }
        LOGGER.info("Kopioidaan laskentakaava {}: kohde hakukohde={}, kohde valintaryhma={}", lahdeLaskentakaava, kohdeHakukohde, kohdeValintaryhma);
        Laskentakaava copy = new Laskentakaava();
        copy.setKopioLaskentakaavasta(lahdeLaskentakaava);
        copy.setHakukohde(kohdeHakukohde);
        copy.setValintaryhma(kohdeValintaryhma);
        copy.setKuvaus(lahdeLaskentakaava.getKuvaus());
        copy.setTyyppi(lahdeLaskentakaava.getTyyppi());
        copy.setNimi(lahdeLaskentakaava.getNimi());
        copy.setOnLuonnos(lahdeLaskentakaava.getOnLuonnos());
        if (kohdeValintaryhma != null) {
            kohdeValintaryhma.getLaskentakaava().add(copy);
        }
        if (kohdeHakukohde != null) {
            kohdeHakukohde.getLaskentakaava().add(copy);
        }
        try {
            copy.setFunktiokutsu(updateFunktiokutsu(lahdeLaskentakaava.getFunktiokutsu(), true, kohdeHakukohde, kohdeValintaryhma));
        } catch (FunktiokutsuMuodostaaSilmukanException e) {
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava  muodostaa silmukan laskentakaavaan "
                    + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta", e,
                    null, e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
        return laskentakaavaDAO.insert(copy);
    }

    @Override
    public Optional<Valintaryhma> valintaryhma(long id) {
        Optional<Laskentakaava> kaava = Optional.ofNullable(laskentakaavaDAO.getLaskentakaavaValintaryhma(id));
        return kaava.map(k -> Optional.ofNullable(k.getValintaryhma())).orElse(Optional.empty());
    }

    @Override
    public Optional<Laskentakaava> pelkkaKaava(Long key) {
        return Optional.ofNullable(laskentakaavaDAO.getLaskentakaava(key));
    }

    private void poistaFunktiokutsu(Funktiokutsu kutsu) {
        for (Funktioargumentti arg : kutsu.getFunktioargumentit()) {
            genericDAO.remove(arg);
        }
        for (Arvokonvertteriparametri p : kutsu.getArvokonvertteriparametrit()) {
            genericDAO.remove(p);
        }
        for (Arvovalikonvertteriparametri p : kutsu.getArvovalikonvertteriparametrit()) {
            genericDAO.remove(p);
        }
        for (Syoteparametri p : kutsu.getSyoteparametrit()) {
            genericDAO.remove(p);
        }
        for (ValintaperusteViite vp : kutsu.getValintaperusteviitteet()) {
            genericDAO.remove(vp);
        }
        kutsu.getFunktioargumentit().clear();
        kutsu.getArvokonvertteriparametrit().clear();
        kutsu.getArvovalikonvertteriparametrit().clear();
        kutsu.getSyoteparametrit().clear();
        kutsu.getValintaperusteviitteet().clear();
        funktiokutsuDAO.flush();
    }

    @Override
    public boolean poista(long id) {
        Optional<Laskentakaava> kaava = Optional.ofNullable(laskentakaavaDAO.getLaskentakaava(id));
        if (!kaava.isPresent()) {
            return false;
        }
        List<Jarjestyskriteeri> j = jarjestyskriteeriDAO.findByLaskentakaava(id);
        List<Hakijaryhma> h = hakijaryhmaDAO.findByLaskentakaava(id);
        List<Valintakoe> v = valintakoeDAO.findByLaskentakaava(id);
        List<Funktioargumentti> f = funktiokutsuDAO.findByLaskentakaavaChild(id);
        if (j.isEmpty() && h.isEmpty() && v.isEmpty() && f.isEmpty()) {
            Laskentakaava l = kaava.get();
            poistaFunktiokutsu(l.getFunktiokutsu());
            laskentakaavaDAO.remove(kaava.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void poistaOrpoFunktiokutsu(Long id) {
        funktiokutsuDAO.deleteOrphan(id);
    }

    @Override
    public Laskentakaava insert(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        return insert(modelMapper.map(laskentakaava, Laskentakaava.class), hakukohdeOid, valintaryhmaOid);
    }

    @Override
    public String haeHakuoid(String hakukohdeOid, String valintaryhmaOid) {
        final Optional<String> hakuoid;
        if (StringUtils.isNotBlank(hakukohdeOid)) {
            hakuoid = getHakuOid(hakukohdeViiteDAO.readForImport(hakukohdeOid));
        } else if (StringUtils.isNotBlank(valintaryhmaOid)) {
            hakuoid = getHakuOid(valintaryhmaDAO.readByOid(valintaryhmaOid));
        } else {
            hakuoid = Optional.empty();
        }
        return hakuoid.orElse("");
    }

    private Optional<String> getHakuOid(Valintaryhma valintaryhma) {
        if (valintaryhma.getHakuoid() != null) {
            return Optional.of(valintaryhma.getHakuoid());
        }
        if (valintaryhma.getYlavalintaryhma() != null) {
            return getHakuOid(valintaryhma.getYlavalintaryhma());
        }
        return Optional.empty();
    }

    private Optional<String> getHakuOid(HakukohdeViite hakukohde) {
        if (hakukohde.getHakuoid() != null) {
            return Optional.of(hakukohde.getHakuoid());
        }
        if (hakukohde.getValintaryhma() != null) {
            return getHakuOid(hakukohde.getValintaryhma());
        }
        return Optional.empty();
    }

    private Map<String, String> hakukohteenValintaperusteetMap(List<HakukohteenValintaperuste> vps) {
        Map<String, String> map = new HashMap<String, String>();
        for (HakukohteenValintaperuste vp : vps) {
            map.put(vp.getTunniste(), vp.getArvo());
        }
        return map;
    }

    @Override
    public List<ValintaperusteDTO> findAvaimetForHakukohde(String hakukohdeOid) {
        List<Funktiokutsu> funktiokutsut = funktiokutsuDAO.findFunktiokutsuByHakukohdeOids(hakukohdeOid);
        Map<String, String> hakukohteenValintaperusteet = hakukohteenValintaperusteetMap(hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohdeOid));
        Map<String, ValintaperusteDTO> valintaperusteet = new HashMap<String, ValintaperusteDTO>();
        for (Funktiokutsu kutsu : funktiokutsut) {
            haeValintaperusteetRekursiivisesti(kutsu, valintaperusteet, hakukohteenValintaperusteet);
        }
        return new ArrayList<>(valintaperusteet.values());
    }

    private void haeValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu, Map<String, ValintaperusteDTO> valintaperusteet,
                                                    Map<String, String> hakukohteenValintaperusteet) {
        Timeout timeout = new Timeout(Duration.create(30, "seconds"));
        ActorSystem actorSystem = actorService.getActorSystem();
        ActorRef master = actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("HaeValintaperusteetRekursiivisestiActorBean"), UUID.randomUUID().toString());
        Future<Object> future = Patterns.ask(master, new UusiValintaperusteRekursio(funktiokutsu.getId(), valintaperusteet, hakukohteenValintaperusteet), timeout);
        try {
            funktiokutsu = (Funktiokutsu) Await.result(future, timeout.duration());
            master.tell(PoisonPill.getInstance(), ActorRef.noSender());
        } catch (Exception e) {
            LOGGER.error("Valintaperusteiden rekursiivisen haun virhetilanne.", e);
        }
    }

    @Override
    public HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(String oid) {
        List<Funktiokutsu> funktiokutsut = funktiokutsuDAO.findFunktiokutsuByHakukohdeOids(oid);
        HakukohteenValintaperusteAvaimetDTO valintaperusteet = new HakukohteenValintaperusteAvaimetDTO();
        for (Funktiokutsu kutsu : funktiokutsut) {
            haeHakukohteenValintaperusteetRekursiivisesti(kutsu, valintaperusteet);
        }
        return valintaperusteet;
    }

    private void haeHakukohteenValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu, HakukohteenValintaperusteAvaimetDTO valintaperusteet) {
        Timeout timeout = new Timeout(Duration.create(30, "seconds"));
        ActorSystem actorSystem = actorService.getActorSystem();
        ActorRef master = actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("HaeHakukohteenValintaperusteetRekursiivisestiActorBean"), UUID.randomUUID().toString());
        Future<Object> future = Patterns.ask(master, new UusiHakukohteenValintaperusteRekursio(funktiokutsu.getId(), valintaperusteet), timeout);
        try {
            funktiokutsu = (Funktiokutsu) Await.result(future, timeout.duration());
            master.tell(PoisonPill.getInstance(), ActorRef.noSender());
        } catch (Exception e) {
            LOGGER.error("Valintaperusteiden rekursiivisen haun virhetilanne.", e);
        }
    }

    @Override
    @Transactional
    public Laskentakaava validoi(LaskentakaavaDTO dto) {
        Laskentakaava kaava = modelMapper.map(dto, Laskentakaava.class);
        asetaNullitOletusarvoiksi(kaava.getFunktiokutsu());
        return Laskentakaavavalidaattori.validoiMallinnettuKaava(laajennaAlakaavat(kaava));
    }

    @Override
    @Transactional
    public boolean onkoKaavaValidi(Laskentakaava laskentakaava) {
        return Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(laskentakaava);
    }

    @Override
    @Transactional
    public List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi) {
        return laskentakaavaDAO.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi);
    }

    private Laskentakaava laajennaAlakaavat(Laskentakaava laskentakaava) {
        laajennaAlakaavat(laskentakaava.getFunktiokutsu());
        return laskentakaava;
    }

    private void laajennaAlakaavat(Funktiokutsu funktiokutsu) {
        if (funktiokutsu != null && funktiokutsu.getFunktioargumentit() != null) {
            for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
                if (arg.getFunktiokutsuChild() != null) {
                    laajennaAlakaavat(arg.getFunktiokutsuChild());
                } else if (arg.getLaskentakaavaChild() != null && arg.getLaskentakaavaChild().getId() != null) {
                    arg.setLaskentakaavaChild(haeLaskentakaava(arg.getLaskentakaavaChild().getId()));
                }
            }
        }
    }

    private Laskentakaava haeKokoLaskentakaava(Long id, boolean laajennaAlakaavat, Set<Long> laskentakaavaIds)
            throws FunktiokutsuMuodostaaSilmukanException {
        Laskentakaava laskentakaava = haeLaskentakaava(id);
        if (laskentakaava != null) {
            laskentakaavaIds.add(laskentakaava.getId());
            Funktiokutsu funktiokutsu = haeFunktiokutsuRekursiivisesti(laskentakaava.getFunktiokutsu().getId(),
                    laajennaAlakaavat, laskentakaavaIds);
            laskentakaava.setFunktiokutsu(funktiokutsu);
            genericDAO.detach(laskentakaava);

        }
        return laskentakaava;
    }

    private Laskentakaava haeKokoLaskentakaavaJaTarkistaSilmukat(Long id, Set<Long> laskentakaavaIds) throws FunktiokutsuMuodostaaSilmukanException {
        Laskentakaava laskentakaava = haeLaskentakaava(id);
        if (laskentakaava != null) {
            laskentakaavaIds.add(laskentakaava.getId());
            Funktiokutsu funktiokutsu = haeFunktiokutsuRekursiivisesti(laskentakaava.getFunktiokutsu().getId(), true, laskentakaavaIds);
        }
        return laskentakaava;
    }

    private Laskentakaava haeKokoLaskentakaava(Long id, boolean laajennaAlakaavat) {
        try {
            return haeKokoLaskentakaava(id, laajennaAlakaavat, new HashSet<Long>());
        } catch (FunktiokutsuMuodostaaSilmukanException e) {
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava " + id + " muodostaa silmukan "
                    + "laskentakaavaan " + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta", e, id,
                    e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
    }

    private void validoiFunktiokutsuMoodiaVasten(final Funktiokutsu funktiokutsu, final Laskentamoodi laskentamoodi) {
        if (funktiokutsu != null) {
            if (!funktiokutsu.getFunktionimi().getLaskentamoodit().contains(laskentamoodi)) {
                switch (laskentamoodi) {
                    case VALINTALASKENTA:
                        throw new FunktiokutsuaEiVoidaKayttaaValintalaskennassaException("Funktiokutsua "
                                + funktiokutsu.getFunktionimi().name() + ", id " + funktiokutsu.getId()
                                + " ei voida käyttää valintalaskennassa.", funktiokutsu.getId(),
                                funktiokutsu.getFunktionimi());
                    case VALINTAKOELASKENTA:
                        throw new FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException("Funktiokutsua "
                                + funktiokutsu.getFunktionimi().name() + ", id " + funktiokutsu.getId()
                                + " ei voida käyttää valintakoelaskennassa.", funktiokutsu.getId(),
                                funktiokutsu.getFunktionimi());
                }
            }
            for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
                if (arg.getFunktiokutsuChild() != null) {
                    validoiFunktiokutsuMoodiaVasten(arg.getFunktiokutsuChild(), laskentamoodi);
                } else if (arg.getLaskentakaavaChild() != null) {
                    validoiFunktiokutsuMoodiaVasten(arg.getLaskentakaavaChild().getFunktiokutsu(), laskentamoodi);
                }
            }
        }
    }

    @Override
    @Transactional
    public Laskentakaava haeLaskettavaKaava(final Long id, final Laskentamoodi laskentamoodi) {
        Laskentakaava laskentakaava = laskentakaavaCache.get(id);
        if (laskentakaava == null) {
            laskentakaava = haeKokoLaskentakaava(id, true);
            laskentakaavaCache.addLaskentakaava(laskentakaava, id);
        }
        validoiFunktiokutsuMoodiaVasten(laskentakaava.getFunktiokutsu(), laskentamoodi);
        return laskentakaava;
    }
}
