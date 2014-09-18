package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 21.1.2013
 * Time: 15.42
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class HakukohdeServiceImpl implements HakukohdeService {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;


    @Override
    public List<HakukohdeViite> findAll() {
        return hakukohdeViiteDAO.findAll();
    }

    @Override
    public List<HakukohdeViite> haunHakukohteet(String hakuOid) {
        return hakukohdeViiteDAO.haunHakukohteet(hakuOid);
    }

    @Override
    public HakukohdeViite readByOid(String oid) {
        return haeHakukohdeViite(oid);
    }

    @Override
    public List<HakukohdeViite> findRoot() {
        return hakukohdeViiteDAO.findRoot();
    }

    @Override
    public List<HakukohdeViite> findByValintaryhmaOid(String oid) {
        return hakukohdeViiteDAO.findByValintaryhmaOid(oid);
    }

    private HakukohdeViite haeHakukohdeViite(String oid) {
        HakukohdeViite hakukohdeViite = hakukohdeViiteDAO.readByOid(oid);
        if (hakukohdeViite == null) {
            throw new HakukohdeViiteEiOleOlemassaException("Hakukohde (" + oid + ") ei ole olemassa.", oid);
        }

        return hakukohdeViite;
    }

    @Override
    public HakukohdeViite update(String oid, HakukohdeViiteCreateDTO incoming) throws Exception {
        HakukohdeViite managedObject = haeHakukohdeViite(oid);

        managedObject.setNimi(incoming.getNimi());
        managedObject.setHakuoid(incoming.getHakuoid());
        managedObject.setTarjoajaOid(incoming.getTarjoajaOid());
        managedObject.setTila(incoming.getTila());

        return managedObject;
    }

    @Override
    public HakukohdeViite insert(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid) {
        HakukohdeViite lisatty = modelMapper.map(hakukohde, HakukohdeViite.class);

        if (StringUtils.isNotBlank(valintaryhmaOid)) {
            Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
            lisatty.setValintaryhma(valintaryhma);
            lisatty = hakukohdeViiteDAO.insert(lisatty);
            valinnanVaiheService.kopioiValinnanVaiheetParentilta(lisatty, valintaryhma);
            //hakijaryhmaService.kopioiHakijaryhmatParentilta(lisatty, valintaryhma);
        } else {
            lisatty = hakukohdeViiteDAO.insert(lisatty);
        }

        return lisatty;
    }

    @Override
    public boolean kuuluuSijoitteluun(String oid) {
        return hakukohdeViiteDAO.kuuluuSijoitteluun(oid);
    }

    @Override
    public List<ValinnanVaihe> ilmanLaskentaa(String oid) {
        return valinnanVaiheDAO.ilmanLaskentaaOlevatHakukohteelle(oid);
    }


    @Override
    public void deleteByOid(String oid) {
        HakukohdeViite hakukohde = haeHakukohdeViite(oid);

        List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohde.getOid());
        for (ValinnanVaihe vv : vaiheet) {
            valinnanVaiheService.delete(vv);
        }

        hakukohdeViiteDAO.remove(hakukohde);

        // Hakukohteiden tuonti saattaa feilata ilman flushausta, jos hakukohde siirretään uuden valintaryhmän alle
        hakukohdeViiteDAO.flush();
    }

    @Override
    public HakukohdeViite siirraHakukohdeValintaryhmaan(String hakukohdeOid, String valintaryhmaOid,
                                                        boolean siirretaanManuaalisesti) {
        HakukohdeViite hakukohdeViite = haeHakukohdeViite(hakukohdeOid);

        Valintaryhma valintaryhma = null;
        if (StringUtils.isNotBlank(valintaryhmaOid)) {
            valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        }

        if ((valintaryhma != null ^ hakukohdeViite.getValintaryhma() != null) ||
                (valintaryhma != null && hakukohdeViite.getValintaryhma() != null
                        && !valintaryhma.getOid().equals(hakukohdeViite.getValintaryhma().getOid()))) {

            poistaHakukohteenPeriytyvatValinnanVaiheetJaHakijaryhmat(hakukohdeOid);
            List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);

            // Käydään läpi kaikki ei-periytyvät valinnan vaiheet ja asetetaan hakukohdeviittaus tilapäisesti
            // nulliksi
            for (ValinnanVaihe vv : valinnanVaiheet) {
                vv.setHakukohdeViite(null);
            }

            List<Laskentakaava> kaavat = laskentakaavaService.findKaavas(true, null, hakukohdeViite.getOid(), null);

            // Poistetaan hakukohteen kaavoilta viittaus vanhaan hakukohteeseen
            kaavat.stream().forEach(kaava -> {
                kaava.setHakukohde(null);
                laskentakaavaDAO.update(kaava);
            });

            // Poistetaan vanha hakukohde
            deleteByOid(hakukohdeOid);

            // Luodaan uusi hakukohde
            HakukohdeViite lisatty = insert(modelMapper.map(hakukohdeViite, HakukohdeViiteCreateDTO.class),
                    valintaryhma != null ? valintaryhma.getOid() : null);

            // Lisätään kaavat takaisin uudelleen luodulle hakukohteelle
            kaavat.stream().forEach(kaava -> {
                kaava.setHakukohde(lisatty);
                laskentakaavaDAO.update(kaava);
            });

            lisatty.setManuaalisestiSiirretty(siirretaanManuaalisesti);

            if (hakukohdeViite.getHakukohdekoodi() != null) {
                Hakukohdekoodi koodi = hakukohdeViite.getHakukohdekoodi();
                lisatty.setHakukohdekoodi(koodi);
            }

            lisatty.getValintakokeet().addAll(hakukohdeViite.getValintakokeet());

            for (String key : hakukohdeViite.getHakukohteenValintaperusteet().keySet()) {
                HakukohteenValintaperuste peruste = hakukohdeViite.getHakukohteenValintaperusteet().get(key);

                HakukohteenValintaperuste lisattava = new HakukohteenValintaperuste();
                lisattava.setArvo(peruste.getArvo());
                lisattava.setKuvaus(peruste.getKuvaus());
                lisattava.setTunniste(peruste.getTunniste());
                lisattava.setHakukohde(lisatty);
                lisatty.getHakukohteenValintaperusteet().put(key, lisattava);
            }

            ValinnanVaihe viimeinenValinnanVaihe =
                    valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(hakukohdeOid);

            if (!valinnanVaiheet.isEmpty()) {
                valinnanVaiheet.get(0).setEdellinen(viimeinenValinnanVaihe);

                if (viimeinenValinnanVaihe != null) {
                    viimeinenValinnanVaihe.setSeuraava(valinnanVaiheet.get(0));
                }

                // Asetetaan hakukohteen omat valinnan vaiheet viittaamaan taas uuteen hakukohteeseen
                for (ValinnanVaihe vv : valinnanVaiheet) {
                    vv.setHakukohdeViite(lisatty);
                }
            }

            return lisatty;
        } else {
            hakukohdeViite.setManuaalisestiSiirretty(siirretaanManuaalisesti);
            return hakukohdeViite;
        }
    }

    private void poistaHakukohteenPeriytyvatValinnanVaiheetJaHakijaryhmat(String hakukohdeOid) {
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        // Poistetaan kaikki periytyvät valinnan vaiheet
        for (ValinnanVaihe vv : valinnanVaiheet) {
            if (vv.getMasterValinnanVaihe() != null) {
                valinnanVaiheService.deleteByOid(vv.getOid(), true);
            }
        }

//        List<Hakijaryhma> byHakukohde = hakijaryhmaService.findByHakukohde(hakukohdeOid);
//        for (Hakijaryhma hakijaryhma : byHakukohde) {
//            if (hakijaryhma.getMaster() != null) {
//                hakijaryhmaService.deleteByOid(hakijaryhma.getOid(), true);
//            }
//        }

    }
}
