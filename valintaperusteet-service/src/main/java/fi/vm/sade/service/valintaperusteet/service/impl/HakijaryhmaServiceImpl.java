package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaKopioija;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaUtil;
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
    private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintapajonoService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

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
    public void deleteByOid(String oid, boolean skipInheritedCheck) {
        Hakijaryhma hakijaryhma = haeHakijaryhma(oid);

        if (!skipInheritedCheck && hakijaryhma.getMaster() != null) {
            throw new HakijaryhmaaEiVoiPoistaaException("hakijaryhma on peritty.");
        }

        delete(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findByHakukohde(String oid) {
        List<Hakijaryhma> byHakukohde = hakijaryhmaDAO.findByHakukohde(oid);
        return LinkitettavaJaKopioitavaUtil.jarjesta(byHakukohde);
    }

    @Override
    public List<Hakijaryhma> findByValintaryhma(String oid) {
        List<Hakijaryhma> byValintaryhma = hakijaryhmaDAO.findByValintaryhma(oid);
        return LinkitettavaJaKopioitavaUtil.jarjesta(byValintaryhma);
    }

    @Override
    public Hakijaryhma readByOid(String oid) {
        return haeHakijaryhma(oid);
    }

    @Override
    public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {
        if(hakijaryhmaValintatapajonoDAO.readByOid(hakijaryhmaOid + "_" + valintatapajonoOid) != null) {
            throw new HakijaryhmaValintatapajonoOnJoOlemassaException("HakijaryhmaValintatapajono (" + hakijaryhmaOid + "_" + valintatapajonoOid + ") on jo olemassa");
        }
        Valintatapajono valintatapajono = valintapajonoService.readByOid(valintatapajonoOid);
        if (valintatapajono == null) {
            throw new ValintatapajonoEiOleOlemassaException("Valintatpajono (" + valintatapajonoOid + ") ei ole olemassa");
        }
        Hakijaryhma hakijaryhma = haeHakijaryhma(hakijaryhmaOid);


        // Tarkistetaan kuuluuko hakijaryhma valintatapajonon hakukohteelle tai valintaryhmaan.
        HakukohdeViite hakukohdeViite = valintatapajono.getValinnanVaihe().getHakukohdeViite();
        Valintaryhma valintaryhma = valintatapajono.getValinnanVaihe().getValintaryhma();

        boolean hakijaryhmaFound = false;
        if(hakukohdeViite != null) {
            for (Hakijaryhma hr : hakukohdeViite.getHakijaryhmat()) {
                if(hakijaryhma.getOid().equals(hr.getOid())) {
                    hakijaryhmaFound = true;
                    break;
                }
            }
        } else if(valintaryhma != null) {
            for (Hakijaryhma hr : valintaryhma.getHakijaryhmat()) {
                if(hakijaryhma.getOid().equals(hr.getOid())) {
                    hakijaryhmaFound = true;
                    break;
                }
            }
        }

        if(!hakijaryhmaFound) {
            throw new HakijaryhmaEiKuuluValintatapajonolleException("");
        }

        HakijaryhmaValintatapajono master = new HakijaryhmaValintatapajono();

        master.setHakijaryhma(hakijaryhma);
        master.setValintatapajono(valintatapajono);
        master.setOid(hakijaryhma.getOid() + "_" + valintatapajono.getOid());
        master.setAktiivinen(true);
        master = hakijaryhmaValintatapajonoDAO.insert(master);

        kopioHakijaryhmat(valintatapajono, hakijaryhma, master);


    }

    private void kopioHakijaryhmat(Valintatapajono valintatapajono, Hakijaryhma hakijaryhma, HakijaryhmaValintatapajono master) {
        for (Valintatapajono kopio : valintatapajono.getKopiot()) {

            HakukohdeViite kopioHakukohdeViite = kopio.getValinnanVaihe().getHakukohdeViite();
            Valintaryhma kopioValintaryhma = kopio.getValinnanVaihe().getValintaryhma();

            if(kopioHakukohdeViite == null && kopioValintaryhma == null) {
                throw new ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException("");
            }

            HakijaryhmaValintatapajono kopioLink = new HakijaryhmaValintatapajono();

            for (Hakijaryhma hrKopio : hakijaryhma.getKopiot()) {

                if(kopioValintaryhma != null && hrKopio.getValintaryhma() != null
                        && hrKopio.getValintaryhma().getOid().equals(kopioValintaryhma.getOid())) {
                    kopioLink.setHakijaryhma(hrKopio);
                } else if(kopioHakukohdeViite != null && hrKopio.getHakukohdeViite() != null
                        && hrKopio.getHakukohdeViite().getOid().equals(kopioHakukohdeViite.getOid())) {
                    kopioLink.setHakijaryhma(hrKopio);
                }

            }


            if(hakijaryhmaValintatapajonoDAO.readByOid(kopioLink.getHakijaryhma().getOid() + "_" + kopio.getOid()) != null) {
                // Hakijaryhma on jo liitetty aikaisemmin lapselle..
                continue;
            }

            if(kopioLink.getHakijaryhma() == null) {
                throw new HakijaryhmanKopiotaEiLoytynytException("");
            }

            kopioLink.setValintatapajono(kopio);
            kopioLink.setOid(kopioLink.getHakijaryhma().getOid() + "_" + kopio.getOid());
            kopioLink.setAktiivinen(true);
            kopioLink.setMaster(master);
            kopioLink = hakijaryhmaValintatapajonoDAO.insert(kopioLink);

            kopioHakijaryhmat(kopio, kopioLink.getHakijaryhma(), kopioLink);
        }
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

        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(hakijaryhma.getLaskentakaavaId()));

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
        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(masterHakijaryhma);
        kopio.setHakukohdeViite(hakukohde);
        kopio.setOid(oidService.haeHakijaryhmaOid());
        List<Hakijaryhma> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(
                hakijaryhmaDAO.findByHakukohde(hakukohde.getOid()));

        lisaaKopio(kopio, edellinenMasterHakijaryhma, vaiheet);
    }

    private void lisaaValintaryhmalleKopioMasterHakijaryhmasta(Valintaryhma valintaryhma,
                                                                  Hakijaryhma masterHakijaryhma,
                                                                  Hakijaryhma edellinenMasterHakijaryhma) {

        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(masterHakijaryhma);
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

        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(hakijaryhma.getLaskentakaavaId()));

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

    @Override
    public void kopioiHakijaryhmatParentilta(Valintaryhma inserted, Valintaryhma parent) {
        if(parent == null) {
            return;
        }
        Hakijaryhma hakijaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(parent.getOid());
        kopioiHakijaryhmatRekursiivisesti(inserted, hakijaryhma);
    }

    @Override
    public void kopioiHakijaryhmatParentilta(HakukohdeViite inserted, Valintaryhma parent) {
        if(parent == null) {
            return;
        }
        Hakijaryhma hakijaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(parent.getOid());
        kopioiHakijaryhmatRekursiivisesti(inserted, hakijaryhma);
    }

    private Hakijaryhma kopioiHakijaryhmatRekursiivisesti(Valintaryhma valintaryhma, Hakijaryhma master) {
        if (master == null) {
            return null;
        }

        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(master);
        kopio.setOid(oidService.haeHakijaryhmaOid());
        valintaryhma.addHakijaryhma(kopio);
        Hakijaryhma edellinen = kopioiHakijaryhmatRekursiivisesti(valintaryhma,
                master.getEdellinen());
        if (edellinen != null) {
            kopio.setEdellinen(edellinen);
            edellinen.setSeuraava(kopio);
        }
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);

        return lisatty;
    }

    private Hakijaryhma kopioiHakijaryhmatRekursiivisesti(HakukohdeViite hakukohde, Hakijaryhma master) {
        if (master == null) {
            return null;
        }

        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(master);
        kopio.setOid(oidService.haeHakijaryhmaOid());
        hakukohde.addHakijaryhma(kopio);
        Hakijaryhma edellinen = kopioiHakijaryhmatRekursiivisesti(hakukohde,
                master.getEdellinen());
        if (edellinen != null) {
            kopio.setEdellinen(edellinen);
            edellinen.setSeuraava(kopio);
        }
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);

        return lisatty;
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
        entity.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(entity.getLaskentakaavaId()));
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

    public void deleteHakijaryhmaValintatajono(HakijaryhmaValintatapajono entity) {
        for (HakijaryhmaValintatapajono hakijaryhma : entity.getKopiot()) {
            deleteHakijaryhmaValintatajono(hakijaryhma);
        }

        if (entity.getSeuraava() != null) {
            HakijaryhmaValintatapajono seuraava = entity.getSeuraava();
            seuraava.setEdellinen(entity.getEdellinen());
        }

        hakijaryhmaValintatapajonoDAO.remove(entity);
    }

    @Override
    public void deleteById(Long aLong) {
        throw new RuntimeException("not implemented");
    }
}
