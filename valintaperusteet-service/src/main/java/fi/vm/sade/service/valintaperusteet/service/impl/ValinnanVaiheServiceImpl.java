package fi.vm.sade.service.valintaperusteet.service.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValinnanVaiheKopioija;
import fi.vm.sade.service.valintaperusteet.util.ValinnanVaiheUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class ValinnanVaiheServiceImpl implements ValinnanVaiheService {

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
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private ValintakoeDAO valintakoeDAO;


    @Override
    public ValinnanVaihe update(String oid, ValinnanVaiheCreateDTO dto) {
        ValinnanVaihe entity = modelMapper.map(dto, ValinnanVaihe.class);
        ValinnanVaihe managed = haeVaiheOidilla(oid);

        return LinkitettavaJaKopioitavaUtil.paivita(managed, entity, kopioija);
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

    private ValinnanVaihe lisaaKopio(ValinnanVaihe kopio, ValinnanVaihe edellinenMasterValinnanVaihe, List<ValinnanVaihe> vaiheet) {
        ValinnanVaihe edellinenValinnanVaihe = LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMasterValinnanVaihe, vaiheet);
        kopio.setEdellinenValinnanVaihe(edellinenValinnanVaihe);
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValinnanVaihe, lisatty);
        return kopio;
    }

    private void lisaaHakukohteelleKopioMasterValinnanVaiheesta(HakukohdeViite hakukohde,
                                                                ValinnanVaihe masterValinnanVaihe,
                                                                ValinnanVaihe edellinenMasterValinnanVaihe) {
        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(masterValinnanVaihe, null);
        kopio.setHakukohdeViite(hakukohde);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohde.getOid()));
        lisaaKopio(kopio, edellinenMasterValinnanVaihe, vaiheet);
    }

    private void lisaaValintaryhmalleKopioMasterValinnanVaiheesta(Valintaryhma valintaryhma,
                                                                  ValinnanVaihe masterValinnanVaihe,
                                                                  ValinnanVaihe edellinenMasterValinnanVaihe) {

        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(masterValinnanVaihe, null);
        kopio.setValintaryhma(valintaryhma);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(valintaryhma.getOid()));
        ValinnanVaihe lisatty = lisaaKopio(kopio, edellinenMasterValinnanVaihe, vaiheet);
        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            lisaaValintaryhmalleKopioMasterValinnanVaiheesta(alavalintaryhma, lisatty, lisatty.getEdellinenValinnanVaihe());
        }
        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            lisaaHakukohteelleKopioMasterValinnanVaiheesta(hakukohde, lisatty, lisatty.getEdellinenValinnanVaihe());
        }
    }

    @Override
    public ValinnanVaihe lisaaValinnanVaiheValintaryhmalle(String valintaryhmaOid, ValinnanVaiheCreateDTO dto,
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
        ValinnanVaihe valinnanVaihe = modelMapper.map(dto, ValinnanVaihe.class);
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
    public ValinnanVaihe lisaaValinnanVaiheHakukohteelle(String hakukohdeOid, ValinnanVaiheCreateDTO dto,
                                                         String edellinenValinnanVaiheOid) {
        HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
        ValinnanVaihe edellinenValinnanVaihe = null;
        if (StringUtils.isNotBlank(edellinenValinnanVaiheOid)) {
            edellinenValinnanVaihe = haeVaiheOidilla(edellinenValinnanVaiheOid);
            tarkistaValinnanVaiheKuuluuHakukohteeseen(edellinenValinnanVaihe, hakukohde);
        } else {
            edellinenValinnanVaihe = valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(hakukohdeOid);
        }
        ValinnanVaihe valinnanVaihe = modelMapper.map(dto, ValinnanVaihe.class);
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
        deleteByOid(oid, false);
    }

    @Override
    public void deleteByOid(String oid, boolean skipInheritedCheck) {
        ValinnanVaihe valinnanVaihe = haeVaiheOidilla(oid);
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
        valinnanVaihe.setKopioValinnanvaiheet(new HashSet<ValinnanVaihe>());

        ValinnanVaihe seuraava = valinnanVaihe.getSeuraava();
        ValinnanVaihe edellinen = valinnanVaihe.getEdellinen();

        if(edellinen != null) {
            edellinen.setSeuraava(null);
            valinnanVaiheDAO.update(edellinen);
        }

        valinnanVaihe.setEdellinen(null);
        valinnanVaihe.setSeuraava(null);
        valinnanVaiheDAO.update(valinnanVaihe);

        if (seuraava != null) {
            seuraava.setEdellinen(edellinen);
            valinnanVaiheDAO.update(seuraava);
            if(edellinen!=null) {
                edellinen.setSeuraava(seuraava);
                valinnanVaiheDAO.update(edellinen);
            }
        }
        if(valinnanVaihe.getJonot() != null) {
            for (Valintatapajono valintatapajono : valinnanVaihe.getJonot()) {
                valintatapajonoService.delete(valintatapajono);
            }
        }
        if(valinnanVaihe.getValintakokeet() != null) {
            for (Valintakoe valintakoe : valinnanVaihe.getValintakokeet()) {
                valintakoeDAO.remove(valintakoe);
            }
        }
        valinnanVaihe = valinnanVaiheDAO.readByOid(valinnanVaihe.getOid());
        if(valinnanVaihe != null) {
            valinnanVaihe.setJonot(null);
            valinnanVaihe.setValintakokeet(null);
            valinnanVaiheDAO.remove(valinnanVaihe);
        }
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
        return new ArrayList<>(jarjestetty.values());
    }

    private void jarjestaAlavalintaryhmanValnnanVaiheet(Valintaryhma valintaryhma,
                                                        LinkedHashMap<String, ValinnanVaihe> uusiMasterJarjestys) {
        LinkedHashMap<String, ValinnanVaihe> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
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
        LinkedHashMap<String, ValinnanVaihe> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
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
        return new ArrayList<>(jarjestetty.values());
    }

    public List<ValinnanVaihe> findByValintaryhma(String oid) {
        return LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(oid));
    }

    private ValinnanVaihe kopioiValinnanVaiheetRekursiivisesti(Valintaryhma valintaryhma, ValinnanVaihe master, JuureenKopiointiCache kopiointiCache) {
        if (master == null) {
            return null;
        }
        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(master, kopiointiCache);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        kopio.setValintaryhma(valintaryhma);
        valintaryhma.addValinnanVaihe(kopio);
        ValinnanVaihe edellinen = kopioiValinnanVaiheetRekursiivisesti(valintaryhma,
                master.getEdellinenValinnanVaihe(), kopiointiCache);
        if (edellinen != null) {
            kopio.setEdellinenValinnanVaihe(edellinen);
            edellinen.setSeuraavaValinnanVaihe(kopio);
        }
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        if(kopiointiCache != null) {
            kopiointiCache.kopioidutValinnanVaiheet.put(master.getId(), lisatty);
        }
        valintatapajonoService.kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(lisatty, master, kopiointiCache);
        valintakoeService.kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(lisatty, master);
        return lisatty;
    }

    private ValinnanVaihe kopioiValinnanVaiheetRekursiivisesti(HakukohdeViite hakukohde, ValinnanVaihe master, JuureenKopiointiCache kopiointiCache) {
        if (master == null) {
            return null;
        }
        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(master, kopiointiCache);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        hakukohde.addValinnanVaihe(kopio);
        ValinnanVaihe edellinen = kopioiValinnanVaiheetRekursiivisesti(hakukohde, master.getEdellinenValinnanVaihe(), kopiointiCache);
        if (edellinen != null) {
            kopio.setEdellinenValinnanVaihe(edellinen);
            edellinen.setSeuraavaValinnanVaihe(kopio);
        }
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        if(kopiointiCache != null) {
            kopiointiCache.kopioidutValinnanVaiheet.put(master.getId(), lisatty);
        }
        valintatapajonoService.kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(lisatty, master, kopiointiCache);
        valintakoeService.kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(lisatty, master);
        return lisatty;
    }

    @Override
    public void kopioiValinnanVaiheetParentilta(Valintaryhma valintaryhma, Valintaryhma parentValintaryhma, JuureenKopiointiCache kopiointiCache) {
        if (parentValintaryhma != null) {
            ValinnanVaihe vv = valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(parentValintaryhma.getOid());
            kopioiValinnanVaiheetRekursiivisesti(valintaryhma, vv, kopiointiCache);
        }
    }

    @Override
    public void kopioiValinnanVaiheetParentilta(HakukohdeViite hakukohde, Valintaryhma parentValintaryhma, JuureenKopiointiCache kopiointiCache) {
        if (parentValintaryhma != null) {
            ValinnanVaihe vv = valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(parentValintaryhma.getOid());
            kopioiValinnanVaiheetRekursiivisesti(hakukohde, vv, kopiointiCache);
        }
    }

    @Override
    public boolean kuuluuSijoitteluun(String oid) {
        return valinnanVaiheDAO.kuuluuSijoitteluun(oid);
    }

    @Override
    public Set<String> getValintaryhmaOids(String oid) {
        Set<String> res = Sets.newHashSet();
        getValintaryhmaOids(readByOid(oid), res);
        return res;
    }

    // XXX: This recursive call is super slow, rather than making single queries for each ValinnanVaihe object
    // XXX: implement interface that takes batch of these objects and processes these in a single run
    private void getValintaryhmaOids(ValinnanVaihe valinnanvaihe, Set<String> res) {
        if(valinnanvaihe.getAktiivinen() && valinnanvaihe.getValintaryhma() != null)
            res.add(valinnanvaihe.getValintaryhma().getOid());
        for (ValinnanVaihe child: valinnanvaihe.getKopioValinnanVaiheet()) {
            getValintaryhmaOids(child, res);
        }
    }


}