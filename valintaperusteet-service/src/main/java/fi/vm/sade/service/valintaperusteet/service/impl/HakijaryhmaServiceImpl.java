package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaKopioija;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 1.10.2013
 * Time: 16.23
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class HakijaryhmaServiceImpl extends AbstractCRUDServiceImpl<Hakijaryhma, Long, String> implements HakijaryhmaService {

    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintapajonoService;

    private static HakijaryhmaKopioija kopioija = new HakijaryhmaKopioija();

    @Autowired
    public HakijaryhmaServiceImpl(HakijaryhmaDAO dao) {
        super(dao);
    }

    private Hakijaryhma haeHakijaryhma(String oid) {
        Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(oid);

        if (hakijaryhma == null) {
            throw new HakijaryhmaEiOleOlemassaException("Hakijaryhma (" + oid + ") ei ole olemassa", oid);
        }

        return hakijaryhma;
    }

    @Override
    public void deleteByOid(String oid) {
        Hakijaryhma hakijaryhma = haeHakijaryhma(oid);

        if (hakijaryhma.getMaster() != null) {
            throw new HakijaryhmaaEiVoiPoistaaException("hakijaryhma on peritty.");
        }

        delete(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findHakijaryhmaByJono(String oid) {
        return hakijaryhmaDAO.findByValintatapajono(oid);
    }

    @Override
    public List<Hakijaryhma> findByHakukohde(String oid) {
        return hakijaryhmaDAO.findByHakukohde(oid);
    }

    @Override
    public List<Hakijaryhma> findByValintaryhma(String oid) {
        return hakijaryhmaDAO.findByValintaryhma(oid);
    }

    @Override
    public Hakijaryhma readByOid(String oid) {
        return haeHakijaryhma(oid);
    }

    @Override
    public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {
        Valintatapajono valintatapajono = valintapajonoService.readByOid(valintatapajonoOid);
        if (valintatapajono == null) {
            throw new ValintatapajonoEiOleOlemassaException("Valintatpajono (" + valintatapajonoOid + ") ei ole olemassa");
        }
        Hakijaryhma hakijaryhma = haeHakijaryhma(hakijaryhmaOid);

        valintatapajono.getHakijaryhmat().add(hakijaryhma);
    }

    @Override
    public Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, Hakijaryhma hakijaryhma) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        if (valintaryhma == null) {
            throw new ValintaryhmaEiOleOlemassaException("Valintaryhmää (" + valintaryhmaOid + ") ei ole olemassa");
        }

        Hakijaryhma edellinenHakijaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(valintaryhmaOid);


        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
        hakijaryhma.setValintaryhma(valintaryhma);

        hakijaryhma.setEdellinen(edellinenHakijaryhma);
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);

        List<Valintaryhma> alaValintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhmaOid);
        for (Valintaryhma alavalintaryhma : alaValintaryhmat) {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(alavalintaryhma, lisatty, edellinenHakijaryhma);
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
        for (HakukohdeViite hakukohde : hakukohteet) {
            lisaaHakukohteelleKopioMasterHakijaryhmasta(hakukohde, lisatty, edellinenHakijaryhma);
        }

        return lisatty;
    }


    private Hakijaryhma lisaaKopio(Hakijaryhma kopio, Hakijaryhma edellinenMasterHakijaryhma,
                                     List<Hakijaryhma> vaiheet) {
        Hakijaryhma edellinenHakijaryhma =
                LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMasterHakijaryhma, vaiheet);
        kopio.setEdellinen(edellinenHakijaryhma);
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);

        return kopio;
    }

    private void lisaaHakukohteelleKopioMasterHakijaryhmasta(HakukohdeViite hakukohde,
                                                                Hakijaryhma masterHakijaryhma,
                                                                Hakijaryhma edellinenMasterHakijaryhma) {
        Hakijaryhma kopio = kopioija.luoKlooni(masterHakijaryhma);
        kopio.setHakukohdeViite(hakukohde);
        kopio.setOid(oidService.haeHakijaryhmaOid());
        List<Hakijaryhma> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(
                hakijaryhmaDAO.findByHakukohde(hakukohde.getOid()));

        lisaaKopio(kopio, edellinenMasterHakijaryhma, vaiheet);
    }

    private void lisaaValintaryhmalleKopioMasterHakijaryhmasta(Valintaryhma valintaryhma,
                                                                  Hakijaryhma masterHakijaryhma,
                                                                  Hakijaryhma edellinenMasterHakijaryhma) {

        Hakijaryhma kopio = kopioija.luoKlooni(masterHakijaryhma);
        kopio.setValintaryhma(valintaryhma);
        kopio.setOid(oidService.haeHakijaryhmaOid());
        List<Hakijaryhma> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(
                hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid()));

        Hakijaryhma lisatty = lisaaKopio(kopio, edellinenMasterHakijaryhma, vaiheet);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(alavalintaryhma, lisatty,
                    lisatty.getEdellinen());
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            lisaaHakukohteelleKopioMasterHakijaryhmasta(hakukohde, lisatty, lisatty.getEdellinen());
        }

    }

    @Override
    public Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, Hakijaryhma hakijaryhma) {
        HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
        Hakijaryhma edellinenHakijaryhma = hakijaryhmaDAO.haeHakukohteenViimeinenHakijaryhma(hakukohdeOid);

        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
        hakijaryhma.setHakukohdeViite(hakukohde);

        hakijaryhma.setEdellinen(edellinenHakijaryhma);
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);

        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);

        return lisatty;
    }

    @Override
    public List<Hakijaryhma> jarjestaHakijaryhmat(List<String> oids) {
        if (oids.isEmpty()) {
            throw new HakijaryhmaOidListaOnTyhjaException("Hakijaryhma OID-lista on tyhjä");
        }

        String ensimmainen = oids.get(0);
        Hakijaryhma hakijaryhma = haeHakijaryhma(ensimmainen);
        if (hakijaryhma.getValintaryhma() != null) {
            return jarjestaHakijaryhmat(hakijaryhma.getValintaryhma(), oids);
        } else {
            return jarjestaHakijaryhmat(hakijaryhma.getHakukohdeViite(), oids);
        }
    }

    private List<Hakijaryhma> jarjestaHakijaryhmat(HakukohdeViite hakukohde, List<String> oids) {
        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByHakukohde(hakukohde.getOid())));

        LinkedHashMap<String, Hakijaryhma> jarjestetty =
                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, oids);

        return new ArrayList<Hakijaryhma>(jarjestetty.values());
    }

    private void jarjestaAlavalintaryhmanHakijaryhmat(Valintaryhma valintaryhma,
                                                      LinkedHashMap<String, Hakijaryhma> uusiMasterJarjestys) {

        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys =
                LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                        LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid())));

        LinkedHashMap<String, Hakijaryhma> jarjestetty = LinkitettavaJaKopioitavaUtil.
                jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            jarjestaAlavalintaryhmanHakijaryhmat(alavalintaryhma, jarjestetty);
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            jarjestaHakukohteenHakijaryhmat(hakukohde, jarjestetty);
        }
    }

    private void jarjestaHakukohteenHakijaryhmat(HakukohdeViite hakukohde,
                                                 LinkedHashMap<String, Hakijaryhma> uusiMasterJarjestys) {
        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys =
                LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                        LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByHakukohde(hakukohde.getOid())));
        LinkitettavaJaKopioitavaUtil.jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);
    }

    private List<Hakijaryhma> jarjestaHakijaryhmat(Valintaryhma valintaryhma, List<String> hakijaryhmaOidit) {
        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid())));

        LinkedHashMap<String, Hakijaryhma> jarjestetty =
                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, hakijaryhmaOidit);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            jarjestaAlavalintaryhmanHakijaryhmat(alavalintaryhma, jarjestetty);
        }

        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
        for (HakukohdeViite hakukohde : hakukohteet) {
            jarjestaHakukohteenHakijaryhmat(hakukohde, jarjestetty);
        }

        return new ArrayList<Hakijaryhma>(jarjestetty.values());
    }


    // CRUD
    @Override
    public Hakijaryhma read(Long key) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Hakijaryhma update(String oid, Hakijaryhma entity) {
        Hakijaryhma managedObject = haeHakijaryhma(oid);
        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, entity, kopioija);
    }

    @Override
    public Hakijaryhma insert(Hakijaryhma entity) {
        entity.setOid(oidService.haeValintaryhmaOid());
        return hakijaryhmaDAO.insert(entity);
    }

    @Override
    public void delete(Hakijaryhma entity) {
        for (Hakijaryhma hakijaryhma : entity.getKopiot()) {
            delete(hakijaryhma);
        }

        if (entity.getSeuraava() != null) {
            Hakijaryhma seuraava = entity.getSeuraava();
            seuraava.setEdellinen(entity.getEdellinen());
        }

        hakijaryhmaDAO.remove(entity);
    }

    @Override
    public void deleteById(Long aLong) {
        throw new RuntimeException("not implemented");
    }
}
