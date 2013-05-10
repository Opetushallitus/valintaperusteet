package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValinnanVaiheKopioija;
import fi.vm.sade.service.valintaperusteet.util.ValinnanVaiheUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * User: tommiha
 * Date: 1/22/13
 * Time: 2:33 PM
 */
@Transactional
@Service
public class ValinnanVaiheServiceImpl extends AbstractCRUDServiceImpl<ValinnanVaihe, Long, String> implements ValinnanVaiheService {


    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValintakoeService valintakoeService;

    private static ValinnanVaiheKopioija kopioija = new ValinnanVaiheKopioija();


    @Autowired
    public ValinnanVaiheServiceImpl(ValinnanVaiheDAO dao) {
        super(dao);
        this.valinnanVaiheDAO = dao;
    }

    @Override
    public ValinnanVaihe update(String oid, ValinnanVaihe entity) {
        ValinnanVaihe managed = haeVaiheOidilla(oid);
        return LinkitettavaJaKopioitavaUtil.paivita(managed, entity, kopioija);
    }

    @Override
    public ValinnanVaihe insert(ValinnanVaihe entity) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public ValinnanVaihe readByOid(String oid) {
        return haeVaiheOidilla(oid);
    }

    @Override
    public List<ValinnanVaihe> findByHakukohde(String oid) {
        List<ValinnanVaihe> byHakukohde = valinnanVaiheDAO.findByHakukohde(oid);
        return LinkitettavaJaKopioitavaUtil.jarjesta(byHakukohde);
    }

    private ValinnanVaihe lisaaKopio(ValinnanVaihe kopio, ValinnanVaihe edellinenMasterValinnanVaihe,
                                     List<ValinnanVaihe> vaiheet) {
        ValinnanVaihe edellinenValinnanVaihe =
                LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMasterValinnanVaihe, vaiheet);
        kopio.setEdellinenValinnanVaihe(edellinenValinnanVaihe);
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValinnanVaihe, lisatty);

        return kopio;
    }

    private void lisaaHakukohteelleKopioMasterValinnanVaiheesta(HakukohdeViite hakukohde,
                                                                ValinnanVaihe masterValinnanVaihe,
                                                                ValinnanVaihe edellinenMasterValinnanVaihe) {
        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(masterValinnanVaihe);
        kopio.setHakukohdeViite(hakukohde);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(
                valinnanVaiheDAO.findByHakukohde(hakukohde.getOid()));

        lisaaKopio(kopio, edellinenMasterValinnanVaihe, vaiheet);
    }

    private void lisaaValintaryhmalleKopioMasterValinnanVaiheesta(Valintaryhma valintaryhma,
                                                                  ValinnanVaihe masterValinnanVaihe,
                                                                  ValinnanVaihe edellinenMasterValinnanVaihe) {

        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(masterValinnanVaihe);
        kopio.setValintaryhma(valintaryhma);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(
                valinnanVaiheDAO.findByValintaryhma(valintaryhma.getOid()));

        ValinnanVaihe lisatty = lisaaKopio(kopio, edellinenMasterValinnanVaihe, vaiheet);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            lisaaValintaryhmalleKopioMasterValinnanVaiheesta(alavalintaryhma, lisatty,
                    lisatty.getEdellinenValinnanVaihe());
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            lisaaHakukohteelleKopioMasterValinnanVaiheesta(hakukohde, lisatty, lisatty.getEdellinenValinnanVaihe());
        }

    }

    @Override
    public ValinnanVaihe lisaaValinnanVaiheValintaryhmalle(String valintaryhmaOid, ValinnanVaihe valinnanVaihe,
                                                           String edellinenValinnanVaiheOid) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        if (valintaryhma == null) {
            throw new ValintaryhmaEiOleOlemassaException("Valintaryhmää (" + valintaryhmaOid + ") ei ole olemassa");
        }

        ValinnanVaihe edellinenValinnanVaihe = null;
        if (StringUtils.isNotBlank(edellinenValinnanVaiheOid)) {
            edellinenValinnanVaihe = haeVaiheOidilla(edellinenValinnanVaiheOid);
            tarkistaValinnanVaiheKuuluuValintaryhmaan(edellinenValinnanVaihe, valintaryhma);
        } else {
            edellinenValinnanVaihe = valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(valintaryhmaOid);
        }

        valinnanVaihe.setOid(oidService.haeValinnanVaiheOid());
        valinnanVaihe.setValintaryhma(valintaryhma);

        valinnanVaihe.setEdellinenValinnanVaihe(edellinenValinnanVaihe);
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(valinnanVaihe);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValinnanVaihe, lisatty);

        List<Valintaryhma> alaValintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhmaOid);
        for (Valintaryhma alavalintaryhma : alaValintaryhmat) {
            lisaaValintaryhmalleKopioMasterValinnanVaiheesta(alavalintaryhma, lisatty, edellinenValinnanVaihe);
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
        for (HakukohdeViite hakukohde : hakukohteet) {
            lisaaHakukohteelleKopioMasterValinnanVaiheesta(hakukohde, lisatty, edellinenValinnanVaihe);
        }

        return lisatty;
    }

    @Override
    public ValinnanVaihe lisaaValinnanVaiheHakukohteelle(String hakukohdeOid, ValinnanVaihe valinnanVaihe,
                                                         String edellinenValinnanVaiheOid) {
        HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
        ValinnanVaihe edellinenValinnanVaihe = null;
        if (StringUtils.isNotBlank(edellinenValinnanVaiheOid)) {
            edellinenValinnanVaihe = haeVaiheOidilla(edellinenValinnanVaiheOid);
            tarkistaValinnanVaiheKuuluuHakukohteeseen(edellinenValinnanVaihe, hakukohde);
        } else {
            edellinenValinnanVaihe = valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(hakukohdeOid);
        }
        valinnanVaihe.setOid(oidService.haeValinnanVaiheOid());
        valinnanVaihe.setHakukohdeViite(hakukohde);

        valinnanVaihe.setEdellinenValinnanVaihe(edellinenValinnanVaihe);
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(valinnanVaihe);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValinnanVaihe, lisatty);

        return lisatty;
    }

    private ValinnanVaihe haeVaiheOidilla(String oid) {
        ValinnanVaihe valinnanVaihe = valinnanVaiheDAO.readByOid(oid);
        if (valinnanVaihe == null) {
            throw new ValinnanVaiheEiOleOlemassaException("Valinnan vaihetta (" + oid + ") ei ole " +
                    "olemassa", oid);
        }
        return valinnanVaihe;
    }

    private static void tarkistaValinnanVaiheKuuluuValintaryhmaan(ValinnanVaihe valinnanVaihe,
                                                                  Valintaryhma valintaryhma) {
        if (!valintaryhma.equals(valinnanVaihe.getValintaryhma())) {
            throw new ValinnanVaiheEiKuuluValintaryhmaanException("Valinnan vaihe ("
                    + valinnanVaihe.getOid() + ") ei " + "kuulu valintaryhmään (" + valintaryhma.getOid() + ")",
                    valinnanVaihe.getOid(), valintaryhma.getOid());
        }
    }

    private static void tarkistaValinnanVaiheKuuluuHakukohteeseen(ValinnanVaihe valinnanVaihe,
                                                                  HakukohdeViite hakukohdeViite) {
        if (!hakukohdeViite.equals(valinnanVaihe.getHakukohdeViite())) {
            throw new ValinnanVaiheEiKuuluHakukohteeseenException("Valinnan vaihe ("
                    + valinnanVaihe.getOid() + ") ei " + "kuulu hakukohteeseen (" + hakukohdeViite.getOid() + ")"
                    , valinnanVaihe.getOid(), hakukohdeViite.getOid());
        }
    }

    @Override
    public void deleteByOid(String oid) {
        ValinnanVaihe valinnanVaihe = haeVaiheOidilla(oid);
        if (valinnanVaihe.getMasterValinnanVaihe() != null) {
            throw new ValinnanVaihettaEiVoiPoistaaException("Valinnan vaihe on peritty.");
        }

        removeValinnanvaihe(valinnanVaihe);
    }

    @Override
    public void delete(ValinnanVaihe valinnanVaihe) {
        removeValinnanvaihe(valinnanVaihe);
    }

    private void removeValinnanvaihe(ValinnanVaihe valinnanVaihe) {
        for (ValinnanVaihe vaihe : valinnanVaihe.getKopioValinnanVaiheet()) {
            removeValinnanvaihe(vaihe);
        }

        if (valinnanVaihe.getSeuraava() != null) {
            ValinnanVaihe seuraava = valinnanVaihe.getSeuraava();
            seuraava.setEdellinen(valinnanVaihe.getEdellinen());
        }

        for (Valintatapajono valintatapajono : valinnanVaihe.getJonot()) {
            valintatapajonoService.delete(valintatapajono);
        }

        for (Valintakoe valintakoe : valinnanVaihe.getValintakokeet()) {
            valintakoeService.delete(valintakoe);
        }

        valinnanVaiheDAO.remove(valinnanVaihe);
    }

    @Override
    public List<ValinnanVaihe> jarjestaValinnanVaiheet(List<String> valinnanVaiheOidit) {
        if (valinnanVaiheOidit.isEmpty()) {
            throw new ValinnanVaiheOidListaOnTyhjaException("Valinnan vaiheiden OID-lista on tyhjä");
        }

        String ensimmainen = valinnanVaiheOidit.get(0);
        ValinnanVaihe valinnanVaihe = haeVaiheOidilla(ensimmainen);
        if (valinnanVaihe.getValintaryhma() != null) {
            return jarjestaValinnanVaiheet(valinnanVaihe.getValintaryhma(), valinnanVaiheOidit);
        } else {
            return jarjestaValinnanVaiheet(valinnanVaihe.getHakukohdeViite(), valinnanVaiheOidit);
        }
    }

    private List<ValinnanVaihe> jarjestaValinnanVaiheet(HakukohdeViite hakukohde, List<String> valinnanVaiheOidit) {
        LinkedHashMap<String, ValinnanVaihe> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohde.getOid())));

        LinkedHashMap<String, ValinnanVaihe> jarjestetty =
                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, valinnanVaiheOidit);

        return new ArrayList<ValinnanVaihe>(jarjestetty.values());
    }

    private void jarjestaAlavalintaryhmanValnnanVaiheet(Valintaryhma valintaryhma,
                                                        LinkedHashMap<String, ValinnanVaihe> uusiMasterJarjestys) {

        LinkedHashMap<String, ValinnanVaihe> alkuperainenJarjestys =
                LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                        LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(valintaryhma.getOid())));

        LinkedHashMap<String, ValinnanVaihe> jarjestetty = LinkitettavaJaKopioitavaUtil.
                jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            jarjestaAlavalintaryhmanValnnanVaiheet(alavalintaryhma, jarjestetty);
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            jarjestaHakukohteenValinnanVaiheet(hakukohde, jarjestetty);
        }
    }

    private void jarjestaHakukohteenValinnanVaiheet(HakukohdeViite hakukohde,
                                                    LinkedHashMap<String, ValinnanVaihe> uusiMasterJarjestys) {
        LinkedHashMap<String, ValinnanVaihe> alkuperainenJarjestys =
                LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                        LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohde.getOid())));
        LinkitettavaJaKopioitavaUtil.jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);
    }

    private List<ValinnanVaihe> jarjestaValinnanVaiheet(Valintaryhma valintaryhma, List<String> valinnanVaiheOidit) {
        LinkedHashMap<String, ValinnanVaihe> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(valintaryhma.getOid())));

        LinkedHashMap<String, ValinnanVaihe> jarjestetty =
                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, valinnanVaiheOidit);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            jarjestaAlavalintaryhmanValnnanVaiheet(alavalintaryhma, jarjestetty);
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            jarjestaHakukohteenValinnanVaiheet(hakukohde, jarjestetty);
        }

        return new ArrayList<ValinnanVaihe>(jarjestetty.values());
    }

    public List<ValinnanVaihe> findByValintaryhma(String oid) {
        return LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(oid));
    }

    private ValinnanVaihe kopioiValinnanVaiheetRekursiivisesti(Valintaryhma valintaryhma, ValinnanVaihe master) {
        if (master == null) {
            return null;
        }

        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(master);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        valintaryhma.addValinnanVaihe(kopio);
        ValinnanVaihe edellinen = kopioiValinnanVaiheetRekursiivisesti(valintaryhma,
                master.getEdellinenValinnanVaihe());
        if (edellinen != null) {
            kopio.setEdellinenValinnanVaihe(edellinen);
        }
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        valintatapajonoService.kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(lisatty, master);

        return lisatty;
    }

    private ValinnanVaihe kopioiValinnanVaiheetRekursiivisesti(HakukohdeViite hakukohde, ValinnanVaihe master) {
        if (master == null) {
            return null;
        }

        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(master);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        hakukohde.addValinnanVaihe(kopio);
        ValinnanVaihe edellinen = kopioiValinnanVaiheetRekursiivisesti(hakukohde,
                master.getEdellinenValinnanVaihe());
        if (edellinen != null) {
            kopio.setEdellinenValinnanVaihe(edellinen);
        }
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        valintatapajonoService.kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(lisatty, master);
        valintakoeService.kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(lisatty, master);

        return lisatty;
    }

    @Override
    public void kopioiValinnanVaiheetParentilta(Valintaryhma valintaryhma, Valintaryhma parentValintaryhma) {
        ValinnanVaihe vv = valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(parentValintaryhma.getOid());
        kopioiValinnanVaiheetRekursiivisesti(valintaryhma, vv);
    }

    @Override
    public void kopioiValinnanVaiheetParentilta(HakukohdeViite hakukohde, Valintaryhma parentValintaryhma) {
        if (parentValintaryhma != null) {
            ValinnanVaihe vv = valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(parentValintaryhma.getOid());
            kopioiValinnanVaiheetRekursiivisesti(hakukohde, vv);
        }
    }

    @Override
    public boolean kuuluuSijoitteluun(String oid) {
        return valinnanVaiheDAO.kuuluuSijoitteluun(oid);
    }
}