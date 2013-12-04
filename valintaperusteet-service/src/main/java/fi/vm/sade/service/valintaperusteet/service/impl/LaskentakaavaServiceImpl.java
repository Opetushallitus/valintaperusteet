package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.generic.dao.GenericDAO;
import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.service.impl.util.LaskentakaavaCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;

/**
 * User: kwuoti Date: 21.1.2013 Time: 9.34
 */
@Service
@Transactional
public class LaskentakaavaServiceImpl implements LaskentakaavaService {

    final static private Logger LOGGER = Logger.getLogger(LaskentakaavaService.class.getName());

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
    private ValintaperusteetModelMapper modelMapper;

    @Transactional(readOnly = true)
    private Funktiokutsu haeFunktiokutsuRekursiivisesti(Long id, boolean laajennaAlakaavat, Set<Long> laskentakaavaIds)
            throws FunktiokutsuMuodostaaSilmukanException {

        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        if (funktiokutsu == null) {
            throw new FunktiokutsuEiOleOlemassaException("Funktiokutsu (" + id + ") ei ole olemassa", id);
        }

        for (Funktioargumentti fa : funktiokutsu.getFunktioargumentit()) {
            if (fa.getFunktiokutsuChild() != null) {
                haeFunktiokutsuRekursiivisesti(fa.getFunktiokutsuChild().getId(), laajennaAlakaavat, laskentakaavaIds);
            } else if (laajennaAlakaavat && fa.getLaskentakaavaChild() != null) {
                if (laskentakaavaIds.contains(fa.getLaskentakaavaChild().getId())) {
                    throw new FunktiokutsuMuodostaaSilmukanException("Funktiokutsu " + id + " muodostaa silmukan " +
                            "laskentakaavaan " + fa.getLaskentakaavaChild().getId(), id,
                            funktiokutsu.getFunktionimi(), fa.getLaskentakaavaChild().getId());
                }
                Set<Long> newLaskentakaavaIds = new HashSet<Long>(laskentakaavaIds);
                newLaskentakaavaIds.add(fa.getLaskentakaavaChild().getId());

                Funktiokutsu fk = haeFunktiokutsuRekursiivisesti(fa.getLaskentakaavaChild().getFunktiokutsu().getId(),
                        laajennaAlakaavat, newLaskentakaavaIds);
                fa.setLaajennettuKaava(fk);
            }
        }

        return funktiokutsu;
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Laskentakaava haeMallinnettuKaava(Long id) {
        return haeKokoLaskentakaava(id, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Laskentakaava read(Long key) {
        return haeMallinnettuKaava(key);
    }

    @Override
    public Laskentakaava update(Long id, LaskentakaavaCreateDTO incoming) {
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
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava " + id + " muodostaa silmukan " +
                    "laskentakaavaan " + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta",
                    e, id, e.getFunktiokutsuId(), e.getLaskentakaavaId());
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

        for (Funktioargumentti arg : incoming.getFunktioargumentit()) {
            Funktioargumentti newArg = new Funktioargumentti();
            newArg.setParent(managed);

            if (arg.getFunktiokutsuChild() != null) {
                newArg.setFunktiokutsuChild(updateFunktiokutsu(arg.getFunktiokutsuChild(), laskentakaavaIds));
            } else {
                Long laskentakaavaId = arg.getLaskentakaavaChild().getId();
                if (laskentakaavaIds.contains(laskentakaavaId)) {
                    throw new FunktiokutsuMuodostaaSilmukanException("Funktiokutsu " +
                            (managed.getId() != null ? managed.getId() : managed.getFunktionimi().name()) +
                            " muodostaa silmukan " + "laskentakaavaan " + laskentakaavaId, managed.getId(),
                            managed.getFunktionimi(), laskentakaavaId);
                }

                Set<Long> newLaskentakaavaIds = new HashSet<Long>(laskentakaavaIds);
                newLaskentakaavaIds.add(laskentakaavaId);

                newArg.setLaskentakaavaChild(haeKokoLaskentakaava(laskentakaavaId, true, newLaskentakaavaIds));
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
            managed.getArvokonvertteriparametrit().add(newParam);
        }

        for (Arvovalikonvertteriparametri k : incoming.getArvovalikonvertteriparametrit()) {
            Arvovalikonvertteriparametri newParam = new Arvovalikonvertteriparametri();
            newParam.setMaxValue(k.getMaxValue());
            newParam.setMinValue(k.getMinValue());
            newParam.setPalautaHaettuArvo(k.getPalautaHaettuArvo());
            newParam.setPaluuarvo(k.getPaluuarvo());
            newParam.setFunktiokutsu(managed);
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
        try {
            asetaNullitOletusarvoiksi(laskentakaava.getFunktiokutsu());

            Laskentakaava entity = modelMapper.map(laskentakaava, Laskentakaava.class);

            if (!laskentakaava.getOnLuonnos()
                    && !Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(entity)) {
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
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava  muodostaa silmukan " +
                    "laskentakaavaan " + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta",
                    e, null, e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
    }

    @Override
    public Laskentakaava insert(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        return insert(modelMapper.map(laskentakaava, Laskentakaava.class), hakukohdeOid, valintaryhmaOid);
    }

    @Override
    public List<ValintaperusteDTO> findAvaimetForHakukohdes(List<String> oids) {
        List<Funktiokutsu> funktiokutsut = funktiokutsuDAO.findFunktiokutsuByHakukohdeOids(oids);

        Map<String, ValintaperusteDTO> valintaperusteet = new HashMap<String, ValintaperusteDTO>();

        for (Funktiokutsu kutsu : funktiokutsut) {
            haeValintaperusteetRekursiivisesti(kutsu, valintaperusteet);
        }

        return new ArrayList<ValintaperusteDTO>(valintaperusteet.values());
    }

    private void haeValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu,
                                                    Map<String, ValintaperusteDTO> valintaperusteet) {
        for (ValintaperusteViite vp : funktiokutsu.getValintaperusteviitteet()) {
            ValintaperusteDTO valintaperuste = new ValintaperusteDTO();
            valintaperuste.setFunktiotyyppi(funktiokutsu.getFunktionimi().getTyyppi());
            valintaperuste.setTunniste(vp.getTunniste());
            valintaperuste.setKuvaus(vp.getKuvaus());
            valintaperuste.setLahde(vp.getLahde());
            valintaperuste.setOnPakollinen(vp.getOnPakollinen());
            valintaperuste.setOsallistuminenTunniste(vp.getOsallistuminenTunniste());

            if (funktiokutsu.getArvokonvertteriparametrit() != null
                    && funktiokutsu.getArvokonvertteriparametrit().size() > 0) {
                List<String> arvot = new ArrayList<String>();

                for (Arvokonvertteriparametri ap : funktiokutsu.getArvokonvertteriparametrit()) {
                    arvot.add(ap.getArvo());
                }

                valintaperuste.setArvot(arvot);
            } else if (funktiokutsu.getArvovalikonvertteriparametrit() != null
                    && funktiokutsu.getArvovalikonvertteriparametrit().size() > 0) {
                List<Arvovalikonvertteriparametri> arvovalikonvertterit = new ArrayList<Arvovalikonvertteriparametri>(
                        funktiokutsu.getArvovalikonvertteriparametrit());

                String min = arvovalikonvertterit.get(0).getMinValue().toString();
                String max = arvovalikonvertterit.get(arvovalikonvertterit.size() - 1).getMaxValue().toString();

                valintaperuste.setMin(min);
                valintaperuste.setMax(max);
            }

            valintaperusteet.put(valintaperuste.getTunniste(), valintaperuste);
        }

        for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
            if (arg.getFunktiokutsuChild() != null) {
                haeValintaperusteetRekursiivisesti(funktiokutsuDAO.getFunktiokutsu(arg.getFunktiokutsuChild().getId()),
                        valintaperusteet);
            } else if (arg.getLaskentakaavaChild() != null) {
                haeValintaperusteetRekursiivisesti(
                        laskentakaavaDAO.getLaskentakaava(arg.getLaskentakaavaChild().getId()).getFunktiokutsu(),
                        valintaperusteet);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Laskentakaava validoi(LaskentakaavaDTO dto) {
        Laskentakaava kaava = modelMapper.map(dto, Laskentakaava.class);
        asetaNullitOletusarvoiksi(kaava.getFunktiokutsu());
        return Laskentakaavavalidaattori.validoiMallinnettuKaava(laajennaAlakaavat(kaava));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean onkoKaavaValidi(Laskentakaava laskentakaava) {
        return Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(laskentakaava);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi) {
        return laskentakaavaDAO.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi);
    }

    @Override
    public Laskentakaava updateMetadata(Long id, LaskentakaavaCreateDTO laskentakaava) {
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
            laskentakaavaIds.add(laskentakaava.getId());
            haeFunktiokutsuRekursiivisesti(laskentakaava.getFunktiokutsu().getId(), laajennaAlakaavat,
                    laskentakaavaIds);
        }

        return laskentakaava;
    }

    private Laskentakaava haeKokoLaskentakaava(Long id, boolean laajennaAlakaavat) {
        try {
            return haeKokoLaskentakaava(id, laajennaAlakaavat, new HashSet<Long>());
        } catch (FunktiokutsuMuodostaaSilmukanException e) {
            throw new LaskentakaavaMuodostaaSilmukanException("Laskentakaava " + id + " muodostaa silmukan " +
                    "laskentakaavaan " + e.getLaskentakaavaId() + " funktiokutsun "
                    + (e.getFunktiokutsuId() != null ? e.getFunktiokutsuId() : e.getFunktionimi()) + " kautta",
                    e, id, e.getFunktiokutsuId(), e.getLaskentakaavaId());
        }
    }

    @Autowired
    private LaskentakaavaCache laskentakaavaCache;

    private void validoiFunktiokutsuMoodiaVasten(final Funktiokutsu funktiokutsu, final Laskentamoodi laskentamoodi) {
        if (funktiokutsu != null) {
            if (!funktiokutsu.getFunktionimi().getLaskentamoodit().contains(laskentamoodi)) {
                switch (laskentamoodi) {
                    case VALINTALASKENTA:
                        throw new FunktiokutsuaEiVoidaKayttaaValintalaskennassaException("Funktiokutsua " +
                                funktiokutsu.getFunktionimi().name() + ", id " + funktiokutsu.getId() +
                                " ei voida käyttää valintalaskennassa.", funktiokutsu.getId(), funktiokutsu.getFunktionimi());
                    case VALINTAKOELASKENTA:
                        throw new FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException("Funktiokutsua " +
                                funktiokutsu.getFunktionimi().name() + ", id " + funktiokutsu.getId() +
                                " ei voida käyttää valintakoelaskennassa.", funktiokutsu.getId(), funktiokutsu.getFunktionimi());
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
    @Transactional(readOnly = true)
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
