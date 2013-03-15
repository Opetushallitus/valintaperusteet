package fi.vm.sade.service.valintaperusteet.service.impl;

import com.mysema.query.Tuple;
import fi.vm.sade.generic.dao.GenericDAO;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.Option;
import scala.Tuple2;

import java.util.List;
import java.util.logging.Level;
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

    @Transactional(readOnly = true)
    private Funktiokutsu haeFunktiokutsuRekursiivisesti(Long id, boolean laajennaAlakaavat) {

        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        if (funktiokutsu == null) {
            throw new FunktiokutsuEiOleOlemassaException("Funktiokutsu (" + id + ") ei ole olemassa", id);
        }

        for (Funktioargumentti fa : funktiokutsu.getFunktioargumentit()) {
            if (fa.getFunktiokutsuChild() != null) {
                haeFunktiokutsuRekursiivisesti(fa.getFunktiokutsuChild().getId(), laajennaAlakaavat);
            } else if (laajennaAlakaavat && fa.getLaskentakaavaChild() != null) {
                Funktiokutsu fk = haeFunktiokutsuRekursiivisesti(fa.getLaskentakaavaChild().getFunktiokutsu().getId(),
                        laajennaAlakaavat);
                fa.setLaajennettuKaava(fk);
            }
        }

        return funktiokutsu;
    }

    @Override
    @Transactional(readOnly = true)
    public Funktiokutsu haeMallinnettuFunktiokutsu(Long id) {
        return haeFunktiokutsuRekursiivisesti(id, false);
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
    public Laskentakaava update(String oid, Laskentakaava incoming) {
        asetaNullitOletusarvoiksi(incoming.getFunktiokutsu());


        if (incoming.getId() == null) {
            return insert(incoming);
        } else if (!incoming.getOnLuonnos() && !Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(incoming)) {
            throw new LaskentakaavaEiValidiException("Laskentakaava ei ole validi",
                    Laskentakaavavalidaattori.validoiMallinnettuKaava(incoming));
        }

        Laskentakaava managed = haeLaskentakaava(incoming.getId());
        managed.setOnLuonnos(incoming.getOnLuonnos());
        managed.setFunktiokutsu(updateFunktiokutsu(incoming.getFunktiokutsu()));
        funktiokutsuDAO.deleteOrphans();

        return managed;
    }

    private void asetaNullitOletusarvoiksi(Funktiokutsu fk) {
        for(Arvokonvertteriparametri p : fk.getArvokonvertteriparametrit()) {
            if(p.getHylkaysperuste() == null) {
                p.setHylkaysperuste(false);
            }
        }

        for(Arvovalikonvertteriparametri p: fk.getArvovalikonvertteriparametrit()) {
            if(p.getPalautaHaettuArvo() == null) {
                p.setPalautaHaettuArvo(false);
            }

            if(p.getHylkaysperuste() == null) {
                p.setHylkaysperuste(false);
            }
        }

        if(fk.getValintaperuste() != null) {
            ValintaperusteViite vp = fk.getValintaperuste();

            if(vp.getOnPaasykoe() == null) {
                vp.setOnPaasykoe(false);
            }

            if(vp.getOnPakollinen() == null) {
                vp.setOnPakollinen(false);
            }
        }

        for(Funktioargumentti arg :  fk.getFunktioargumentit()) {
            if(arg.getFunktiokutsuChild() != null) {
                asetaNullitOletusarvoiksi(arg.getFunktiokutsuChild());
            }
        }
    }

    private Funktiokutsu updateFunktiokutsu(Funktiokutsu incoming) {
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

            managed.getFunktioargumentit().clear();
            managed.getArvokonvertteriparametrit().clear();
            managed.getArvovalikonvertteriparametrit().clear();
            managed.getSyoteparametrit().clear();
        } else {
            managed = new Funktiokutsu();
        }

        managed.setFunktionimi(incoming.getFunktionimi());

        for (Funktioargumentti arg : incoming.getFunktioargumentit()) {
            Funktioargumentti newArg = new Funktioargumentti();
            newArg.setParent(managed);

            if (arg.getFunktiokutsuChild() != null) {
                newArg.setFunktiokutsuChild(updateFunktiokutsu(arg.getFunktiokutsuChild()));
            } else {
                newArg.setLaskentakaavaChild(laskentakaavaDAO.read(arg.getLaskentakaavaChild().getId()));
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
            newParam.setHylkaysperuste(k.getHylkaysperuste());
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

        if(incoming.getValintaperuste() != null) {
            ValintaperusteViite newVp = incoming.getValintaperuste();

            ValintaperusteViite vp = null;
            if(managed.getValintaperuste() != null) {
                vp = managed.getValintaperuste();
            } else {
                vp = new ValintaperusteViite();
            }

            vp.setKuvaus(newVp.getKuvaus());
            vp.setLahde(newVp.getLahde());
            vp.setOnPaasykoe(newVp.getOnPaasykoe());
            vp.setOnPakollinen(newVp.getOnPakollinen());
            vp.setTunniste(newVp.getTunniste());

            if(managed.getValintaperuste() == null) {
                vp.setFunktiokutsu(managed);
                managed.setValintaperuste(vp);
            }
        } else if(managed.getValintaperuste() != null) {
            genericDAO.remove(managed.getValintaperuste());
        }


        if (managed.getId() == null) {
            funktiokutsuDAO.insert(managed);
        }

        return managed;
    }

    @Override
    public Laskentakaava insert(Laskentakaava laskentakaava) {
        asetaNullitOletusarvoiksi(laskentakaava.getFunktiokutsu());

        if (laskentakaava.getId() != null) {
            return update(laskentakaava.getId().toString(), laskentakaava);
        } else if (!laskentakaava.getOnLuonnos() && !Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(laskentakaava)) {
            throw new LaskentakaavaEiValidiException("Laskentakaava ei ole validi",
                    Laskentakaavavalidaattori.validoiMallinnettuKaava(laskentakaava));
        }
        laskentakaava.setFunktiokutsu(updateFunktiokutsu(laskentakaava.getFunktiokutsu()));

        if (laskentakaava.getValintaryhma() != null && laskentakaava.getValintaryhma().getOid() != null) {
            Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(laskentakaava.getValintaryhma().getOid());
            laskentakaava.setValintaryhma(valintaryhma);
        }

        return laskentakaavaDAO.insert(laskentakaava);
    }

    @Override
    public void delete(Laskentakaava entity) {
        // TODO:
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteById(Long id) {
        // TODO:
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public JSONObject findAvaimetForHakukohdes(List<String> oids) {
        if (oids == null || (oids.size() == 0)) {
            List<HakukohdeViite> all = hakukohdeViiteDAO.findAll();
            for (HakukohdeViite hakukohdeViite : all) {
                oids.add(hakukohdeViite.getOid());
            }
        }

        List<Tuple> tuples = laskentakaavaDAO.findLaskentakaavatByHakukohde(oids);
        if (tuples == null) {
            return null;
        }
        JSONObject hakukohteet = new JSONObject();

        for (Tuple tuple : tuples) {
            try {
                findValintaperustetunnisteet(hakukohteet, tuple.get(QHakukohdeViite.hakukohdeViite.oid),
                        tuple.get(QValinnanVaihe.valinnanVaihe.oid), tuple.get(QLaskentakaava.laskentakaava)
                        .getFunktiokutsu());
            } catch (JSONException e) {
                LOGGER.log(Level.WARNING, "Error in constructing JSONObject", e);
            }
        }

        return hakukohteet;
    }

    @Override
    @Transactional(readOnly = true)
    public Laskentakaava validoi(Laskentakaava laskentakaava) {
        return Laskentakaavavalidaattori.validoiMallinnettuKaava(laskentakaava);
    }

    private void findValintaperustetunnisteet(JSONObject hakukohteet, final String hakukohdeOid,
                                              final String valinnanVaiheOid, final Funktiokutsu funktiokutsu) throws JSONException {

        ValintaperusteViite vp = funktiokutsu.getValintaperuste();
        JSONObject valinnanvaiheet = hakukohteet.optJSONObject(hakukohdeOid);
        if (valinnanvaiheet == null) {
            valinnanvaiheet = new JSONObject();
        }

        JSONObject avaimet = valinnanvaiheet.optJSONObject(valinnanVaiheOid);
        if (avaimet == null) {
            avaimet = new JSONObject();
        }

        Tuple2<Funktionimi, Funktiokuvaaja.Funktiokuvaus> funktiokuvaus;
        Option<Funktiokuvaaja.Valintaperusteparametrikuvaus> valintaperusteparametri;

        funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi());
        valintaperusteparametri = funktiokuvaus._2().valintaperusteparametri();

        if (!valintaperusteparametri.isEmpty()) {
            Funktiokuvaaja.Valintaperusteparametrikuvaus kuvaus = valintaperusteparametri.get();
            String tyyppi = kuvaus.tyyppi().toString();

            hakukohteet.put(hakukohdeOid, valinnanvaiheet);
            valinnanvaiheet.put(valinnanVaiheOid, avaimet);
            avaimet.put(vp.getTunniste(), tyyppi);
        }


        for (Funktioargumentti funktioargumentti : funktiokutsu.getFunktioargumentit()) {
            findValintaperustetunnisteet(hakukohteet, hakukohdeOid, valinnanVaiheOid, funktioargumentti.getFunktiokutsuChild());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean onkoKaavaValidi(Laskentakaava laskentakaava) {
        return Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(laskentakaava);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, Funktiotyyppi tyyppi) {
        return laskentakaavaDAO.findKaavas(all, valintaryhmaOid, tyyppi);
    }

    @Override
    public Laskentakaava updateMetadata(Laskentakaava laskentakaava) {
        Laskentakaava managed = laskentakaavaDAO.read(laskentakaava.getId());
        managed.setKuvaus(laskentakaava.getKuvaus());
        managed.setNimi(laskentakaava.getNimi());
        return managed;
    }

    private Laskentakaava haeKokoLaskentakaava(Long id, boolean laajennaAlakaavat) {
        Laskentakaava laskentakaava = haeLaskentakaava(id);
        if (laskentakaava != null) {
            for (Funktioargumentti fa : laskentakaava.getFunktiokutsu().getFunktioargumentit()) {
                if (fa.getFunktiokutsuChild() != null) {
                    haeFunktiokutsuRekursiivisesti(fa.getFunktiokutsuChild().getId(), laajennaAlakaavat);
                } else {
                    laskentakaavaDAO.read(fa.getLaskentakaavaChild().getId());
                }
            }
        }

        return laskentakaava;
    }

    @Override
    @Transactional(readOnly = true)
    public Laskentakaava haeLaskettavaKaava(Long id) {

        return haeKokoLaskentakaava(id, true);
    }
}
