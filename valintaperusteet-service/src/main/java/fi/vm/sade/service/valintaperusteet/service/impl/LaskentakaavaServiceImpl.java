package fi.vm.sade.service.valintaperusteet.service.impl;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiHakukohteenValintaperusteRekursio;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintalaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiValintaperusteRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.util.LaskentakaavaCache;

/**
 * User: kwuoti Date: 21.1.2013 Time: 9.34
 */
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
    private ApplicationContext applicationContext;

    @Autowired
    private ActorSystem actorSystem;

    @Transactional(readOnly = true)
    public Funktiokutsu haeFunktiokutsuRekursiivisesti(final Long id, final boolean laajennaAlakaavat,
            final Set<Long> laskentakaavaIds) throws FunktiokutsuMuodostaaSilmukanException {

        // Akka toteutus
        ActorSystem system = ActorSystem.create("actorSystem");
        SpringExtProvider.get(system).initialize(applicationContext);
        Timeout timeout = new Timeout(Duration.create(30, "seconds"));

        ActorRef master = system.actorOf(
                SpringExtProvider.get(system).props("HaeFunktiokutsuRekursiivisestiActorBean").withDispatcher("default-dispatcher")
                , UUID.randomUUID()
                        .toString());

        Future<Object> future = Patterns
                .ask(master, new UusiRekursio(id, laajennaAlakaavat, laskentakaavaIds), timeout);

        try {
            Funktiokutsu funktiokutsu = (Funktiokutsu) Await.result(future, timeout.duration());
            system.shutdown();
            return funktiokutsu;
        } catch (Exception e) {
            system.shutdown();
            if (e instanceof FunktiokutsuMuodostaaSilmukanException) {
                FunktiokutsuMuodostaaSilmukanException exp = (FunktiokutsuMuodostaaSilmukanException) e;
                throw new FunktiokutsuMuodostaaSilmukanException(exp.getMessage(), exp.getFunktiokutsuId(),
                        exp.getFunktionimi(), exp.getLaskentakaavaId());
            } else if (e instanceof FunktiokutsuEiOleOlemassaException) {
                throw new FunktiokutsuEiOleOlemassaException("Funktiokutsu (" + id + ") ei ole olemassa", id);
            } else {
                LOGGER.error("Virhe laskentakaavan haussa. Syy: {}, viesti:{}", e.getCause(), e.getMessage());
                throw new FunktiokutsuEiOleOlemassaException("Odottomaton virhe haettaessa funktiokutsua "+id+": "+e.getCause(), id);
            }

        }

    }

    @Override
    @Transactional
    public Funktiokutsu haeMallinnettuFunktiokutsu(Long id) throws FunktiokutsuMuodostaaSilmukanException {
        Funktiokutsu funktiokutsu = haeFunktiokutsuRekursiivisesti(id, false, new HashSet<Long>());

        return funktiokutsu;
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
    @Transactional
    public Laskentakaava read(Long key) {
        long beginTime = System.currentTimeMillis();
        Laskentakaava kaava = haeMallinnettuKaava(key);
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - beginTime);

        LOGGER.info("Kaava haettu. Aikaa hakuun kului: {} millisekuntia", timeTaken);
        return kaava;
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

            managed.setOnLuonnos(incoming.getOnLuonnos());
            managed.setFunktiokutsu(updateFunktiokutsu(entity.getFunktiokutsu(), laskentakaavaIds));

            if (!incoming.getOnLuonnos() && !Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(managed)) {
                throw new LaskentakaavaEiValidiException("Laskentakaava ei ole validi",
                        Laskentakaavavalidaattori.validoiMallinnettuKaava(entity));
            }

            funktiokutsuDAO.deleteOrphans();

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

    private Funktiokutsu updateFunktiokutsu(Funktiokutsu incoming) throws FunktiokutsuMuodostaaSilmukanException {
        return updateFunktiokutsu(incoming, new HashSet<Long>());
    }

    private Funktiokutsu updateFunktiokutsu(Funktiokutsu incoming, Set<Long> laskentakaavaIds)
            throws FunktiokutsuMuodostaaSilmukanException {
        Funktiokutsu managed = null;
        if (incoming.getId() != null) {
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
                newArg.setFunktiokutsuChild(updateFunktiokutsu(arg.getFunktiokutsuChild(), laskentakaavaIds));
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

                newArg.setLaskentakaavaChild(haeKokoLaskentakaavaJaTarkistaSilmukat(laskentakaavaId, true,
                        newLaskentakaavaIds));
                // newArg.setLaskentakaavaChild(haeKokoLaskentakaava(laskentakaavaId,
                // true, newLaskentakaavaIds));
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
            if(k.getKuvaukset() != null) {
                TekstiRyhma ryhma = new TekstiRyhma();
                genericDAO.insert(ryhma);
                for (LokalisoituTeksti teksti : k.getKuvaukset().getTekstit()) {
                    LokalisoituTeksti newTeksti = new LokalisoituTeksti();
                    newTeksti.setKieli(teksti.getKieli());
                    newTeksti.setTeksti(teksti.getTeksti());
                    newTeksti.setRyhma(ryhma);
                    genericDAO.insert(newTeksti);
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
            if(k.getKuvaukset() != null) {
                TekstiRyhma ryhma = new TekstiRyhma();
                genericDAO.insert(ryhma);
                for (LokalisoituTeksti teksti : k.getKuvaukset().getTekstit()) {
                    LokalisoituTeksti newTeksti = new LokalisoituTeksti();
                    newTeksti.setKieli(teksti.getKieli());
                    newTeksti.setTeksti(teksti.getTeksti());
                    newTeksti.setRyhma(ryhma);
                    genericDAO.insert(newTeksti);
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
            newVp.setFunktiokutsu(managed);
            managed.getValintaperusteviitteet().add(newVp);
        }

        if (managed.getId() == null) {
            funktiokutsuDAO.insert(managed);
        }

        return managed;
    }

    @Override
    public Laskentakaava insert(Laskentakaava laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        laskentakaavaCache.clear();
        try {
            asetaNullitOletusarvoiksi(laskentakaava.getFunktiokutsu());

            Laskentakaava entity = modelMapper.map(laskentakaava, Laskentakaava.class);

            if (!laskentakaava.getOnLuonnos() && !Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(entity)) {
                throw new LaskentakaavaEiValidiException("Laskentakaava ei ole validi",
                        Laskentakaavavalidaattori.validoiMallinnettuKaava(entity));
            }
            entity.setFunktiokutsu(updateFunktiokutsu(entity.getFunktiokutsu()));

            if (StringUtils.isNotBlank(hakukohdeOid)) {
                HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
                entity.setHakukohde(hakukohde);
            } else if (StringUtils.isNotBlank(valintaryhmaOid)) {
                Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(valintaryhmaOid);
                entity.setValintaryhma(valintaryhma);
            }
            return laskentakaavaDAO.insert(entity);
        } catch (FunktiokutsuMuodostaaSilmukanException e) {
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava  muodostaa silmukan " + "laskentakaavaan "
                    + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta", e,
                    null, e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
    }

    @Override
    public Laskentakaava insert(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        return insert(modelMapper.map(laskentakaava, Laskentakaava.class), hakukohdeOid, valintaryhmaOid);
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
        Map<String, String> hakukohteenValintaperusteet = hakukohteenValintaperusteetMap(hakukohteenValintaperusteDAO
                .haeHakukohteenValintaperusteet(hakukohdeOid));

        Map<String, ValintaperusteDTO> valintaperusteet = new HashMap<String, ValintaperusteDTO>();

        for (Funktiokutsu kutsu : funktiokutsut) {
            haeValintaperusteetRekursiivisesti(kutsu, valintaperusteet, hakukohteenValintaperusteet);
        }

        return new ArrayList<ValintaperusteDTO>(valintaperusteet.values());
    }

    private void haeValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu,
            Map<String, ValintaperusteDTO> valintaperusteet, Map<String, String> hakukohteenValintaperusteet) {

        // Akka toteutus
        ActorSystem system = ActorSystem.create("ValintaperusteetActorSystem");
        SpringExtProvider.get(system).initialize(applicationContext);
        Timeout timeout = new Timeout(Duration.create(30, "seconds"));

        ActorRef master = system.actorOf(
                SpringExtProvider.get(system).props("HaeValintaperusteetRekursiivisestiActorBean").withDispatcher("default-dispatcher")
                , UUID.randomUUID()
                        .toString());

        Future<Object> future = Patterns.ask(master, new UusiValintaperusteRekursio(funktiokutsu.getId(),
                valintaperusteet, hakukohteenValintaperusteet), timeout);

        try {
            funktiokutsu = (Funktiokutsu) Await.result(future, timeout.duration());
            system.shutdown();
        } catch (Exception e) {
            system.shutdown();
            e.printStackTrace();
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

    private void haeHakukohteenValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu,
            HakukohteenValintaperusteAvaimetDTO valintaperusteet) {

        // Akka toteutus
        ActorSystem system = ActorSystem.create("actorSystem");
        SpringExtProvider.get(system).initialize(applicationContext);
        Timeout timeout = new Timeout(Duration.create(30, "seconds"));

        ActorRef master = system.actorOf(
                SpringExtProvider.get(system).props("HaeHakukohteenValintaperusteetRekursiivisestiActorBean").withDispatcher("default-dispatcher")
                , UUID.randomUUID()
                .toString());

        Future<Object> future = Patterns.ask(master, new UusiHakukohteenValintaperusteRekursio(funktiokutsu.getId(),
                valintaperusteet), timeout);

        try {
            funktiokutsu = (Funktiokutsu) Await.result(future, timeout.duration());
            system.shutdown();
        } catch (Exception e) {
            system.shutdown();
            e.printStackTrace();
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

    @Override
    public Laskentakaava updateMetadata(Long id, LaskentakaavaCreateDTO laskentakaava) {
        laskentakaavaCache.clear();
        Laskentakaava managed = laskentakaavaDAO.read(id);
        managed.setKuvaus(laskentakaava.getKuvaus());
        managed.setNimi(laskentakaava.getNimi());
        return managed;
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
            // System.out.println("############# " + laskentakaava.getNimi() +
            // " #############");
            laskentakaavaIds.add(laskentakaava.getId());
            Funktiokutsu funktiokutsu = haeFunktiokutsuRekursiivisesti(laskentakaava.getFunktiokutsu().getId(),
                    laajennaAlakaavat, laskentakaavaIds);
            laskentakaava.setFunktiokutsu(funktiokutsu);
            genericDAO.detach(laskentakaava);
            // haeFunktiokutsuRekursiivisesti(laskentakaava.getFunktiokutsu().getId(),
            // laajennaAlakaavat,laskentakaavaIds);

        }

        return laskentakaava;
    }

    private Laskentakaava haeKokoLaskentakaavaJaTarkistaSilmukat(Long id, boolean laajennaAlakaavat,
            Set<Long> laskentakaavaIds) throws FunktiokutsuMuodostaaSilmukanException {
        Laskentakaava laskentakaava = haeLaskentakaava(id);
        if (laskentakaava != null) {
            System.out.println("############# " + laskentakaava.getNimi() + " ############# " + laskentakaava.getId());
            laskentakaavaIds.add(laskentakaava.getId());
            Funktiokutsu funktiokutsu = haeFunktiokutsuRekursiivisesti(laskentakaava.getFunktiokutsu().getId(),
                    laajennaAlakaavat, laskentakaavaIds);

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
