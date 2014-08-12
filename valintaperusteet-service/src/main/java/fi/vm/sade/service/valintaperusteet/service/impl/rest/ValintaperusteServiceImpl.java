package fi.vm.sade.service.valintaperusteet.service.impl.rest;

import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakuparametritOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEpaaktiivinenException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheJarjestyslukuOutOfBoundsException;
import fi.vm.sade.service.valintaperusteet.service.impl.util.ValintaperusteServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.00
 */
@Service
// @PreAuthorize("isAuthenticated()")
public class ValintaperusteServiceImpl implements ValintaperusteService {

    private static final Logger LOG = LoggerFactory
            .getLogger(ValintaperusteServiceImpl.class);

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    // @Autowired
    // private PaasykoeTunnisteetService tunnisteService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private HakukohdeImportService hakukohdeImportService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
//    @PreAuthorize(READ_UPDATE_CRUD)
    public List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(String hakukohdeOid) {
        List<Valintatapajono> jonot = valintatapajonoDAO
                .haeValintatapajonotSijoittelulle(hakukohdeOid);

        List<ValintatapajonoDTO> valintatapajonoDTOs = modelMapper.mapList(jonot, ValintatapajonoDTO.class);
        return valintatapajonoDTOs;
    }

    @Override
//    @PreAuthorize(READ_UPDATE_CRUD)
    public List<ValintaperusteetDTO> haeValintaperusteet(List<HakuparametritDTO> hakuparametrit) {
        try {
            List<ValintaperusteetDTO> list = new ArrayList<ValintaperusteetDTO>();

            if (hakuparametrit == null) {
                throw new HakuparametritOnTyhjaException(
                        "Hakuparametrit oli tyhjä.");
            }
            Long start = System.currentTimeMillis();
            LOG.info("Hakuparametrien lkm {}", hakuparametrit.size());

            for (HakuparametritDTO param : hakuparametrit) {
                LOG.info(
                        "Haetaan hakukohteen {}, valinnanvaihe {} valintaperusteet",
                        new Object[]{param.getHakukohdeOid(),
                                param.getValinnanVaiheJarjestysluku()}
                );

                HakukohdeViite hakukohde = null;
                try {
                    hakukohde = hakukohdeService.readByOid(param
                            .getHakukohdeOid());
                } catch (HakukohdeViiteEiOleOlemassaException e) {
                    LOG.warn(
                            "Hakukohdetta {} ei ole olemassa. Jätetään hakukohde huomioimatta.",
                            param.getHakukohdeOid());
                    continue;
                }

                Integer jarjestysluku = param.getValinnanVaiheJarjestysluku();

                Long startFind = System.currentTimeMillis();
                List<ValinnanVaihe> valinnanVaiheList = valinnanVaiheService
                        .findByHakukohde(param.getHakukohdeOid());
                if (LOG.isInfoEnabled()) {
                    LOG.info("findByHakukohde: "
                            + (System.currentTimeMillis() - startFind));
                }
                Long startConvert = System.currentTimeMillis();
                if (jarjestysluku != null) {
                    if (jarjestysluku < 0
                            || jarjestysluku >= valinnanVaiheList.size()) {
                        throw new ValinnanVaiheJarjestyslukuOutOfBoundsException(
                                "Hakukohteen " + param.getHakukohdeOid()
                                        + " valinnan vaiheen jarjestysluku "
                                        + jarjestysluku + " on epäkelpo."
                        );
                    } else if (!valinnanVaiheList.get(jarjestysluku)
                            .getAktiivinen()) {
                        throw new ValinnanVaiheEpaaktiivinenException(
                                "Valinnan vaihe (oid "
                                        + valinnanVaiheList.get(jarjestysluku)
                                        .getOid() + ", järjestysluku "
                                        + jarjestysluku + ") ei ole aktiivinen"
                        );
                    }

                    ValinnanVaihe kasiteltava = valinnanVaiheList
                            .get(jarjestysluku);
                    if (!kasiteltava.getAktiivinen()) {
                        LOG.info("Yritetään laskea valinnanvaihetta, joka ei ole aktiivinen");
                        continue;
                    }

//                    for (ValinnanVaihe vaihe : valinnanVaiheList) {
//                        if (!vaihe.getAktiivinen()) {
//                            LOG.info("Jätetään käsittelemättä ei-aktiivinen valinnanvaihe");
//                            valinnanVaiheList.remove(vaihe);
//                        }
//                    }


                    List<ValinnanVaihe> vaiheet = valinnanVaiheList.stream().filter(ValinnanVaihe::getAktiivinen).collect(Collectors.toList());

                    int todellinenJarjestysluku = vaiheet
                            .indexOf(kasiteltava);

                    ValintaperusteetDTO valinnanVaihe = convertValintaperusteet(
                            kasiteltava, hakukohde, todellinenJarjestysluku);
                    if (valinnanVaihe != null) {
                        list.add(valinnanVaihe);
                    }

                } else {
//                    for (ValinnanVaihe vaihe : valinnanVaiheList) {
//                        if (!vaihe.getAktiivinen()) {
//                            valinnanVaiheList.remove(vaihe);
//                        }
//                    }

                    List<ValinnanVaihe> vaiheet = valinnanVaiheList.stream().filter(ValinnanVaihe::getAktiivinen).collect(Collectors.toList());

                    for (int i = 0; i < vaiheet.size(); i++) {
                        if (vaiheet.get(i).getAktiivinen()) {
                            ValintaperusteetDTO valinnanVaihe = convertValintaperusteet(
                                    vaiheet.get(i), hakukohde, i);
                            if (valinnanVaihe != null) {
                                list.add(valinnanVaihe);
                            }
                        }
                    }
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Convert: "
                            + (System.currentTimeMillis() - startConvert));
                }
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("haeValintaperusteet: "
                        + (System.currentTimeMillis() - start));
            }

            LOG.info("Haettu {} kpl valintaperusteita", list.size());
            return list;
        } catch (Exception e) {
            LOG.error("Valintaperusteiden haussa virhe {} {} {}",
                    e.getMessage(), e.getCause(),
                    Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ValintaperusteetDTO convertValintaperusteet(
            ValinnanVaihe valinnanVaihe, HakukohdeViite hakukohde,
            int valinnanvaiheJarjestysluku) {

        ValintaperusteetDTO valintaperusteetDTO = new ValintaperusteetDTO();
        valintaperusteetDTO.setHakukohdeOid(hakukohde.getOid());
        valintaperusteetDTO.setHakuOid(hakukohde.getHakuoid());
        valintaperusteetDTO.setTarjoajaOid(hakukohde.getTarjoajaOid());

        ValintaperusteetValinnanVaiheDTO vv = new ValintaperusteetValinnanVaiheDTO();
        switch (valinnanVaihe.getValinnanVaiheTyyppi()) {

            case TAVALLINEN:
                vv.getValintatapajono().addAll(convertJonot(valinnanVaihe));
                vv.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);
                break;
            case VALINTAKOE:
                vv.getValintakoe().addAll(
                        convertValintakokeet(valinnanVaihe));
                vv.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
                break;

            default:
                throw new UnsupportedOperationException(
                        "Virheellinen valinnan vaiheen tyyppi. Ei pystytä käsittelemään");
        }

        vv.setValinnanVaiheJarjestysluku(valinnanvaiheJarjestysluku);
        vv.setValinnanVaiheOid(valinnanVaihe.getOid());
        vv.setNimi(valinnanVaihe.getNimi());
        valintaperusteetDTO.setValinnanVaihe(vv);

        valintaperusteetDTO.getHakukohteenValintaperuste().addAll(
            hakukohde.getHakukohteenValintaperusteet().values().parallelStream().map(vp -> {
                HakukohteenValintaperusteDTO vpDTO = new HakukohteenValintaperusteDTO();
                vpDTO.setTunniste(vp.getTunniste());
                vpDTO.setArvo(vp.getArvo());
                return vpDTO;
            }).collect(Collectors.toList())
        );

//        for (HakukohteenValintaperuste vp : hakukohde
//                .getHakukohteenValintaperusteet().values()) {
//            HakukohteenValintaperusteDTO vpDTO = new HakukohteenValintaperusteDTO();
//            vpDTO.setTunniste(vp.getTunniste());
//            vpDTO.setArvo(vp.getArvo());
//            valintaperusteetDTO.getHakukohteenValintaperuste().add(vpDTO);
//        }

        return valintaperusteetDTO;
    }

    private List<ValintakoeDTO> convertValintakokeet(
            ValinnanVaihe valinnanVaihe) {
        List<Valintakoe> valintakokeet = valintakoeService
                .findValintakoeByValinnanVaihe(valinnanVaihe.getOid());
        List<ValintakoeDTO> valintakoeDTOs = new ArrayList<ValintakoeDTO>();

        for (Valintakoe koe : valintakokeet) {
            if (koe.getAktiivinen()) {
                ValintakoeDTO dto = modelMapper.map(koe, ValintakoeDTO.class);

                FunktiokutsuDTO converted = null;
                if (koe.ainaPakollinen()) {
                    converted = modelMapper.map(ValintaperusteServiceUtil.getAinaPakollinenFunktiokutsu(), FunktiokutsuDTO.class);
                } else {
                    Laskentakaava laskentakaava = laskentakaavaService
                            .haeLaskettavaKaava(koe.getLaskentakaava().getId(),
                                    Laskentamoodi.VALINTAKOELASKENTA);

                    converted = modelMapper.map(laskentakaava.getFunktiokutsu(), FunktiokutsuDTO.class);
                }
                dto.setFunktiokutsu(converted);

                valintakoeDTOs.add(dto);
            }
        }

        return valintakoeDTOs;
    }

    private List<ValintatapajonoJarjestyskriteereillaDTO> convertJonot(
            ValinnanVaihe valinnanVaihe) {
        List<Valintatapajono> jonot = valintatapajonoService
                .findJonoByValinnanvaihe(valinnanVaihe.getOid());

        List<ValintatapajonoJarjestyskriteereillaDTO> valintatapajonot = new ArrayList<ValintatapajonoJarjestyskriteereillaDTO>();

        for (int prioriteetti = 0; prioriteetti < jonot.size(); prioriteetti++) {
            Valintatapajono valintatapajono = jonot.get(prioriteetti);
            if (!valintatapajono.getAktiivinen())
                continue;
            ValintatapajonoJarjestyskriteereillaDTO dto = new ValintatapajonoJarjestyskriteereillaDTO();

            dto.setAloituspaikat(valintatapajono.getAloituspaikat());
            dto.setKuvaus(valintatapajono.getKuvaus());
            dto.setNimi(valintatapajono.getNimi());
            dto.setOid(valintatapajono.getOid());

            dto.setPrioriteetti(prioriteetti);

            dto.setSiirretaanSijoitteluun(valintatapajono
                    .getSiirretaanSijoitteluun());
            dto.setTasasijasaanto(valintatapajono.getTasapistesaanto().name());

            dto.setPoissaOlevaTaytto(valintatapajono.getPoissaOlevaTaytto());
            dto.setEiVarasijatayttoa(valintatapajono.getEiVarasijatayttoa());
            dto.setKaikkiEhdonTayttavatHyvaksytaan(valintatapajono.getKaikkiEhdonTayttavatHyvaksytaan());
            dto.setKaytetaanValintalaskentaa(valintatapajono.getKaytetaanValintalaskentaa());

            dto.getJarjestyskriteerit().addAll(
                    convertJarjestyskriteerit(valintatapajono));

            valintatapajonot.add(dto);
        }

        return valintatapajonot;
    }

    private List<ValintaperusteetJarjestyskriteeriDTO> convertJarjestyskriteerit(
            Valintatapajono valintatapajono) {
        List<Jarjestyskriteeri> jarjestyskriteeris = jarjestyskriteeriService
                .findJarjestyskriteeriByJono(valintatapajono.getOid());

        List<ValintaperusteetJarjestyskriteeriDTO> jarjestyskriteerit = new ArrayList<ValintaperusteetJarjestyskriteeriDTO>();

        for (int prioriteetti = 0; prioriteetti < jarjestyskriteeris.size(); prioriteetti++) {
            Jarjestyskriteeri jarjestyskriteeri = jarjestyskriteeris
                    .get(prioriteetti);
            if (!jarjestyskriteeri.getAktiivinen())
                continue;

            ValintaperusteetJarjestyskriteeriDTO jarjestyskriteeriDTO = modelMapper.map(jarjestyskriteeri, ValintaperusteetJarjestyskriteeriDTO.class);
            jarjestyskriteeriDTO.setPrioriteetti(prioriteetti);

            Long start = System.currentTimeMillis();
            Laskentakaava laskentakaava = laskentakaavaService
                    .haeLaskettavaKaava(jarjestyskriteeri.getLaskentakaava()
                            .getId(), Laskentamoodi.VALINTALASKENTA);
            if (LOG.isInfoEnabled()) {
                LOG.info("haeLaskettavaKaava: "
                        + jarjestyskriteeri.getLaskentakaava().getId() + ":"
                        + (System.currentTimeMillis() - start));
            }
            ValintaperusteetFunktiokutsuDTO convert = modelMapper.map(laskentakaava.getFunktiokutsu(), ValintaperusteetFunktiokutsuDTO.class);

            jarjestyskriteeriDTO.setFunktiokutsu(convert);

            jarjestyskriteerit.add(jarjestyskriteeriDTO);
        }

        return jarjestyskriteerit;
    }

    @Override
//    @PreAuthorize(CRUD)
    public void tuoHakukohde(HakukohdeImportDTO hakukohde) {
        // try {
        // hakukohdeImportService.tuoHakukohde(hakukohde);
        // } catch (Exception e) {
        // LOG.error("Hakukohteen tuominen epäonnistui.", e);
        // }

        hakukohdeImportService.tuoHakukohde(hakukohde);
    }
}
