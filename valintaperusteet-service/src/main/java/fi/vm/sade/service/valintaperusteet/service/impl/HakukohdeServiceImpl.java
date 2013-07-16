package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 21.1.2013
 * Time: 15.42
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class HakukohdeServiceImpl extends AbstractCRUDServiceImpl<HakukohdeViite, Long, String> implements HakukohdeService {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    public HakukohdeServiceImpl(HakukohdeViiteDAO dao) {
        super(dao);
    }

    @Override
    public List<HakukohdeViite> findAll() {
        return hakukohdeViiteDAO.findAll();
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

    @Override
    public HakukohdeViite insert(HakukohdeViiteDTO hakukohdeViiteDTO) {
        HakukohdeViite hkv = convertDTO(hakukohdeViiteDTO);
        HakukohdeViite insert = hakukohdeViiteDAO.insert(hkv);
        valinnanVaiheService.kopioiValinnanVaiheetParentilta(insert, hkv.getValintaryhma());
        return insert;
    }

    private HakukohdeViite haeHakukohdeViite(String oid) {
        HakukohdeViite hakukohdeViite = hakukohdeViiteDAO.readByOid(oid);
        if (hakukohdeViite == null) {
            throw new HakukohdeViiteEiOleOlemassaException("Hakukohde (" + oid + ") ei ole olemassa.", oid);
        }

        return hakukohdeViite;
    }

    private HakukohdeViite convertDTO(HakukohdeViiteDTO hakukohdeViiteDTO) {
        HakukohdeViite hkv = new HakukohdeViite();
        hkv.setNimi(hakukohdeViiteDTO.getNimi());
        hkv.setOid(hakukohdeViiteDTO.getOid());
        hkv.setHakuoid(hakukohdeViiteDTO.getHakuoid());

        String valintaryhmaOid = hakukohdeViiteDTO.getValintaryhmaOid();

        Set<String> valinnanvaiheetOids = hakukohdeViiteDTO.getValinnanvaiheetOids();

        if (StringUtils.isNotBlank(valintaryhmaOid)) {
            Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
            hkv.setValintaryhma(valintaryhma);
        }
        if (valinnanvaiheetOids != null && valinnanvaiheetOids.size() > 0) {
            List<ValinnanVaihe> valinnanVaihes = valinnanVaiheDAO.readByOids(valinnanvaiheetOids);
            hkv.setValinnanvaiheet(new HashSet<ValinnanVaihe>(valinnanVaihes));
        }
        return hkv;
    }

    @Override
    public HakukohdeViite update(String oid, HakukohdeViiteDTO incoming) throws Exception {
        HakukohdeViite managedObject = haeHakukohdeViite(oid);
        HakukohdeViite hkv = convertDTO(incoming);

        managedObject.setNimi(hkv.getNimi());
        managedObject.setValinnanvaiheet(hkv.getValinnanvaiheet());
        managedObject.setValintaryhma(hkv.getValintaryhma());

        return managedObject;
    }

    @Override
    public HakukohdeViite update(String oid, HakukohdeViite incoming) {
        HakukohdeViite managedObject = haeHakukohdeViite(oid);
        managedObject.setNimi(incoming.getNimi());
        if (managedObject.getValintaryhma() == null && incoming.getValintaryhma() != null) {
            Valintaryhma valintaryhma = valintaryhmaService.readByOid(incoming.getValintaryhma().getOid());
            managedObject.setValintaryhma(valintaryhma);
        }
        return managedObject;
    }

    @Override
    public HakukohdeViite insert(HakukohdeViite hakukohde, String valintaryhmaOid) {
        HakukohdeViite lisatty = null;

        if (StringUtils.isNotBlank(valintaryhmaOid)) {
            Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
            hakukohde.setValintaryhma(valintaryhma);
            lisatty = hakukohdeViiteDAO.insert(hakukohde);
            valinnanVaiheService.kopioiValinnanVaiheetParentilta(lisatty, valintaryhma);
        } else {
            lisatty = hakukohdeViiteDAO.insert(hakukohde);
        }

        return lisatty;
    }

    @Override
    public boolean kuuluuSijoitteluun(String oid) {
        return hakukohdeViiteDAO.kuuluuSijoitteluun(oid);
    }

    @Override
    public HakukohdeViite insert(HakukohdeViite entity) {
        return hakukohdeViiteDAO.insert(entity);
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

    private HakukohdeViite luoKopio(HakukohdeViite hakukohdeViite) {
        HakukohdeViite kopio = new HakukohdeViite();
        kopio.setHakuoid(hakukohdeViite.getHakuoid());
        kopio.setOid(hakukohdeViite.getOid());
        kopio.setNimi(hakukohdeViite.getNimi());
        return kopio;
    }

    @Override
    public HakukohdeViite siirraHakukohdeValintaryhmaan(String hakukohdeOid, String valintaryhmaOid) {
        HakukohdeViite hakukohdeViite = haeHakukohdeViite(hakukohdeOid);

        Valintaryhma valintaryhma = null;
        if (StringUtils.isNotBlank(valintaryhmaOid)) {
            valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        }

        if ((valintaryhma != null ^ hakukohdeViite.getValintaryhma() != null) ||
                (valintaryhma != null && hakukohdeViite.getValintaryhma() != null
                        && !valintaryhma.getOid().equals(hakukohdeViite.getValintaryhma().getOid()))) {

            poistaHakukohteenPeriytyvatValinnanVaiheet(hakukohdeOid);
            List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);

            // Käydään läpi kaikki ei-periytyvät valinnan vaiheet ja asetetaan hakukohdeviittaus tilapäisesti
            // nulliksi
            for (ValinnanVaihe vv : valinnanVaiheet) {
                vv.setHakukohdeViite(null);
            }

            // Poistetaan vanha hakukohde
            deleteByOid(hakukohdeOid);

            // Luodaan uusi hakukohde
            HakukohdeViite uusiHakukohde = luoKopio(hakukohdeViite);
            HakukohdeViite lisatty = insert(uusiHakukohde,
                    valintaryhma != null ? valintaryhma.getOid() : null);

            if (hakukohdeViite.getHakukohdekoodi() != null) {
                Hakukohdekoodi koodi = hakukohdeViite.getHakukohdekoodi();
                lisatty.setHakukohdekoodi(koodi);
                koodi.addHakukohde(lisatty);
            }

            lisatty.getOpetuskielet().addAll(hakukohdeViite.getOpetuskielet());
            lisatty.getValintakokeet().addAll(hakukohdeViite.getValintakokeet());

            ValinnanVaihe viimeinenValinnanVaihe =
                    valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(hakukohdeOid);
            if (!valinnanVaiheet.isEmpty()) {
                valinnanVaiheet.get(0).setEdellinen(viimeinenValinnanVaihe);
                if (viimeinenValinnanVaihe != null) {
                    viimeinenValinnanVaihe.setSeuraava(valinnanVaiheet.get(0));
                }

                // Asetetaan hakukohteen omat valinnan vaiheet viittaamaan taas uuteen hakukohteeseen
                for (ValinnanVaihe vv : valinnanVaiheet) {
                    vv.setHakukohdeViite(uusiHakukohde);
                }
            }

            return lisatty;
        } else {
            return hakukohdeViite;
        }
    }

    private void poistaHakukohteenPeriytyvatValinnanVaiheet(String hakukohdeOid) {
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        // Poistetaan kaikki periytyvät valinnan vaiheet
        for (ValinnanVaihe vv : valinnanVaiheet) {
            if (vv.getMasterValinnanVaihe() != null) {
                valinnanVaiheService.deleteByOid(vv.getOid(), true);
            }
        }
    }
}
