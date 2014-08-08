package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaValintatapajonoKopioija;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: jukais
 * Date: 1.10.2013
 * Time: 16.23
 */
@Service
@Transactional
public class HakijaryhmaServiceImpl implements HakijaryhmaService {

    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintapajonoService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private OidService oidService;

    private static HakijaryhmaValintatapajonoKopioija kopioija = new HakijaryhmaValintatapajonoKopioija();

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

        if (!skipInheritedCheck && hakijaryhma.getJonot() != null && !hakijaryhma.getJonot().isEmpty()) {
            throw new HakijaryhmaaEiVoiPoistaaException("hakijaryhma on peritty.");
        }

        delete(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findByHakukohde(String oid) {
        List<HakijaryhmaValintatapajono> byHakukohde = hakijaryhmaValintatapajonoDAO.findByHakukohde(oid);
        List<HakijaryhmaValintatapajono> jarjestetty = LinkitettavaJaKopioitavaUtil.jarjesta(byHakukohde);
        return jarjestetty.stream().map(HakijaryhmaValintatapajono::getHakijaryhma).collect(Collectors.toList());
    }

    @Override
    public List<Hakijaryhma> findByValintaryhma(String oid) {
        List<Hakijaryhma> byValintaryhma = hakijaryhmaDAO.findByValintaryhma(oid);
        return byValintaryhma;
    }

    @Override
    public Hakijaryhma readByOid(String oid) {
        return haeHakijaryhma(oid);
    }

    @Override
    public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {

        hakijaryhmaValintatapajonoService.liitaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhmaOid);


    }

    private void kopioHakijaryhmat(Valintatapajono valintatapajono, Hakijaryhma hakijaryhma, HakijaryhmaValintatapajono master) {
        for (Valintatapajono kopio : valintatapajono.getKopiot()) {

            HakukohdeViite kopioHakukohdeViite = kopio.getValinnanVaihe().getHakukohdeViite();
            Valintaryhma kopioValintaryhma = kopio.getValinnanVaihe().getValintaryhma();

            if (kopioHakukohdeViite == null && kopioValintaryhma == null) {
                throw new ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException("");
            }

            HakijaryhmaValintatapajono kopioLink = new HakijaryhmaValintatapajono();

            for (HakijaryhmaValintatapajono hrKopio : master.getKopiot()) {

                if (kopioValintaryhma != null && hrKopio.getHakijaryhma().getValintaryhma() != null
                        && hrKopio.getHakijaryhma().getValintaryhma().getOid().equals(kopioValintaryhma.getOid())) {
                    kopioLink.setHakijaryhma(hrKopio.getHakijaryhma());
                } else if (kopioHakukohdeViite != null && hrKopio.getHakukohdeViite() != null
                        && hrKopio.getHakukohdeViite().getOid().equals(kopioHakukohdeViite.getOid())) {
                    kopioLink.setHakijaryhma(hrKopio.getHakijaryhma());
                }

            }


            if (hakijaryhmaValintatapajonoDAO.readByOid(kopioLink.getHakijaryhma().getOid() + "_" + kopio.getOid()) != null) {
                // Hakijaryhma on jo liitetty aikaisemmin lapselle..
                continue;
            }

            if (kopioLink.getHakijaryhma() == null) {
                throw new HakijaryhmanKopiotaEiLoytynytException("");
            }

            kopioLink.setValintatapajono(kopio);
            kopioLink.setOid(kopioLink.getHakijaryhma().getOid() + "_" + kopio.getOid());
            kopioLink.setAktiivinen(true);
            kopioLink.setMaster(master);
            kopioLink.setKiintio(master.getKiintio());
            kopioLink = hakijaryhmaValintatapajonoDAO.insert(kopioLink);

            kopioHakijaryhmat(kopio, kopioLink.getHakijaryhma(), kopioLink);
        }
    }

    @Override
    public Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, HakijaryhmaCreateDTO dto) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        if (valintaryhma == null) {
            throw new ValintaryhmaEiOleOlemassaException("Valintaryhmää (" + valintaryhmaOid + ") ei ole olemassa");
        }

        Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
        hakijaryhma.setValintaryhma(valintaryhma);
        hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
        hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(hakijaryhma.getLaskentakaavaId()));

        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);

        List<Valintaryhma> alaValintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhmaOid);
        for (Valintaryhma alavalintaryhma : alaValintaryhmat) {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(alavalintaryhma, lisatty);
        }

//        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
//        for (HakukohdeViite hakukohde : hakukohteet) {
//            lisaaHakukohteelleKopioMasterHakijaryhmasta(hakukohde, lisatty, edellinenHakijaryhma);
//        }

        return lisatty;
    }


//    private Hakijaryhma lisaaKopio(Hakijaryhma kopio, Hakijaryhma edellinenMasterHakijaryhma,
//                                   List<Hakijaryhma> vaiheet) {
//        Hakijaryhma edellinenHakijaryhma =
//                LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMasterHakijaryhma, vaiheet);
//        kopio.setEdellinen(edellinenHakijaryhma);
//        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);
//        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);
//
//        return kopio;
//    }
//
//    private void lisaaHakukohteelleKopioMasterHakijaryhmasta(HakukohdeViite hakukohde,
//                                                             Hakijaryhma masterHakijaryhma,
//                                                             Hakijaryhma edellinenMasterHakijaryhma) {
//        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(masterHakijaryhma);
//        kopio.setHakukohdeViite(hakukohde);
//        kopio.setOid(oidService.haeHakijaryhmaOid());
//        List<Hakijaryhma> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(
//                hakijaryhmaDAO.findByHakukohde(hakukohde.getOid()));
//
//        lisaaKopio(kopio, edellinenMasterHakijaryhma, vaiheet);
//    }

    private void lisaaValintaryhmalleKopioMasterHakijaryhmasta(Valintaryhma valintaryhma,
                                                               Hakijaryhma masterHakijaryhma) {

        Hakijaryhma kopio = new Hakijaryhma();
        kopio.setValintaryhma(valintaryhma);
        kopio.setOid(oidService.haeHakijaryhmaOid());
        kopio.setKiintio(masterHakijaryhma.getKiintio());
        kopio.setKuvaus(masterHakijaryhma.getKuvaus());
        kopio.setKaytaKaikki(masterHakijaryhma.isKaytaKaikki());
        kopio.setLaskentakaava(masterHakijaryhma.getLaskentakaava());
        kopio.setNimi(masterHakijaryhma.getNimi());
        kopio.setTarkkaKiintio(masterHakijaryhma.isTarkkaKiintio());

        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);

        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(alavalintaryhma, lisatty);
        }

    }

//    @Override
//    public Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, HakijaryhmaCreateDTO dto) {
//        Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
//        HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
//        Hakijaryhma edellinenHakijaryhma = hakijaryhmaDAO.haeHakukohteenViimeinenHakijaryhma(hakukohdeOid);
//        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
//        hakijaryhma.setHakukohdeViite(hakukohde);
//        hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
//        hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
//        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
//
//        hakijaryhma.setEdellinen(edellinenHakijaryhma);
//        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
//
//        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);
//
//        return lisatty;
//    }

//    @Override
//    public List<Hakijaryhma> jarjestaHakijaryhmat(List<String> oids) {
//        if (oids.isEmpty()) {
//            throw new HakijaryhmaOidListaOnTyhjaException("Hakijaryhma OID-lista on tyhjä");
//        }
//
//        String ensimmainen = oids.get(0);
//        Hakijaryhma hakijaryhma = haeHakijaryhma(ensimmainen);
//        if (hakijaryhma.getValintaryhma() != null) {
//            return jarjestaHakijaryhmat(hakijaryhma.getValintaryhma(), oids);
//        } else {
//            return jarjestaHakijaryhmat(hakijaryhma.getHakukohdeViite(), oids);
//        }
//    }
//
//    @Override
//    public void kopioiHakijaryhmatParentilta(Valintaryhma inserted, Valintaryhma parent) {
//        if (parent == null) {
//            return;
//        }
//        Hakijaryhma hakijaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(parent.getOid());
//        kopioiHakijaryhmatRekursiivisesti(inserted, hakijaryhma);
//    }
//
//    @Override
//    public void kopioiHakijaryhmatParentilta(HakukohdeViite inserted, Valintaryhma parent) {
//        if (parent == null) {
//            return;
//        }
//        Hakijaryhma hakijaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(parent.getOid());
//        kopioiHakijaryhmatRekursiivisesti(inserted, hakijaryhma);
//    }
//
//    private Hakijaryhma kopioiHakijaryhmatRekursiivisesti(Valintaryhma valintaryhma, Hakijaryhma master) {
//        if (master == null) {
//            return null;
//        }
//
//        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(master);
//        kopio.setOid(oidService.haeHakijaryhmaOid());
//        valintaryhma.addHakijaryhma(kopio);
//        Hakijaryhma edellinen = kopioiHakijaryhmatRekursiivisesti(valintaryhma,
//                master.getEdellinen());
//        if (edellinen != null) {
//            kopio.setEdellinen(edellinen);
//            edellinen.setSeuraava(kopio);
//        }
//        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);
//
//        return lisatty;
//    }
//
//    private Hakijaryhma kopioiHakijaryhmatRekursiivisesti(HakukohdeViite hakukohde, Hakijaryhma master) {
//        if (master == null) {
//            return null;
//        }
//
//        Hakijaryhma kopio = HakijaryhmaUtil.teeKopioMasterista(master);
//        kopio.setOid(oidService.haeHakijaryhmaOid());
//        hakukohde.addHakijaryhma(kopio);
//        Hakijaryhma edellinen = kopioiHakijaryhmatRekursiivisesti(hakukohde,
//                master.getEdellinen());
//        if (edellinen != null) {
//            kopio.setEdellinen(edellinen);
//            edellinen.setSeuraava(kopio);
//        }
//        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);
//
//        return lisatty;
//    }
//
//    private List<Hakijaryhma> jarjestaHakijaryhmat(HakukohdeViite hakukohde, List<String> oids) {
//        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
//                LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByHakukohde(hakukohde.getOid())));
//
//        LinkedHashMap<String, Hakijaryhma> jarjestetty =
//                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, oids);
//
//        return new ArrayList<Hakijaryhma>(jarjestetty.values());
//    }
//
//    private void jarjestaAlavalintaryhmanHakijaryhmat(Valintaryhma valintaryhma,
//                                                      LinkedHashMap<String, Hakijaryhma> uusiMasterJarjestys) {
//
//        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys =
//                LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
//                        LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid())));
//
//        LinkedHashMap<String, Hakijaryhma> jarjestetty = LinkitettavaJaKopioitavaUtil.
//                jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);
//
//        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
//        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
//            jarjestaAlavalintaryhmanHakijaryhmat(alavalintaryhma, jarjestetty);
//        }
//
//        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
//        for (HakukohdeViite hakukohde : hakukohteet) {
//            jarjestaHakukohteenHakijaryhmat(hakukohde, jarjestetty);
//        }
//    }
//
//    private void jarjestaHakukohteenHakijaryhmat(HakukohdeViite hakukohde,
//                                                 LinkedHashMap<String, Hakijaryhma> uusiMasterJarjestys) {
//        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys =
//                LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
//                        LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByHakukohde(hakukohde.getOid())));
//        LinkitettavaJaKopioitavaUtil.jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);
//    }
//
//    private List<Hakijaryhma> jarjestaHakijaryhmat(Valintaryhma valintaryhma, List<String> hakijaryhmaOidit) {
//        LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
//                LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid())));
//
//        LinkedHashMap<String, Hakijaryhma> jarjestetty =
//                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, hakijaryhmaOidit);
//
//        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
//        for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
//            jarjestaAlavalintaryhmanHakijaryhmat(alavalintaryhma, jarjestetty);
//        }
//
//        List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
//        for (HakukohdeViite hakukohde : hakukohteet) {
//            jarjestaHakukohteenHakijaryhmat(hakukohde, jarjestetty);
//        }
//
//        return new ArrayList<Hakijaryhma>(jarjestetty.values());
//    }

    @Override
    public Hakijaryhma update(String oid, HakijaryhmaCreateDTO dto) {
        Hakijaryhma managedObject = haeHakijaryhma(oid);
        //Hakijaryhma entity = modelMapper.map(dto, Hakijaryhma.class);
        managedObject.setKaytaKaikki(dto.isKaytaKaikki());
        managedObject.setKiintio(dto.getKiintio());
        managedObject.setKuvaus(dto.getKuvaus());
        managedObject.setNimi(dto.getNimi());
        managedObject.setTarkkaKiintio(dto.isTarkkaKiintio());

        managedObject.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));

        hakijaryhmaDAO.update(managedObject);
//        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, entity, kopioija);
        return managedObject;
    }

    @Override
    public Hakijaryhma insert(Hakijaryhma entity) {
        entity.setOid(oidService.haeValintaryhmaOid());
        return hakijaryhmaDAO.insert(entity);
    }

    @Override
    public void delete(Hakijaryhma entity) {

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
}
