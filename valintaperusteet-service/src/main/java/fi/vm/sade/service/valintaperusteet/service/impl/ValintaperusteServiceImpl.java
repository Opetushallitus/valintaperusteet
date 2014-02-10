package fi.vm.sade.service.valintaperusteet.service.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.service.valintaperusteet.GenericFault;
import fi.vm.sade.service.valintaperusteet.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.messages.HakuparametritTyyppi;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.schema.FunktiokutsuTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohteenValintaperusteTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.JarjestyskriteeriTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.TasasijasaantoTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.TavallinenValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintakoeTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintakoeValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintaperusteetTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintatapajonoJarjestyskriteereillaTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintatapajonoTyyppi;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeImportService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakuparametritOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEpaaktiivinenException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheJarjestyslukuOutOfBoundsException;
import fi.vm.sade.service.valintaperusteet.service.impl.util.ValintaperusteServiceUtil;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.00
 */
@Service
// @PreAuthorize("isAuthenticated()")
public class ValintaperusteServiceImpl implements ValintaperusteService {

    private static final Logger LOG = LoggerFactory.getLogger(ValintaperusteServiceImpl.class);

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private SadeConversionService conversionService;

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

    @Override
    @Secured({ READ, UPDATE, CRUD })
    public List<ValintatapajonoTyyppi> haeValintatapajonotSijoittelulle(
            @WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid) throws GenericFault {
        List<Valintatapajono> jonot = valintatapajonoDAO.haeValintatapajonotSijoittelulle(hakukohdeOid);

        return conversionService.convertAll(jonot, ValintatapajonoTyyppi.class);
    }

    @Override
    @Secured({ READ, UPDATE, CRUD })
    public List<ValintaperusteetTyyppi> haeValintaperusteet(
            @WebParam(name = "hakuparametrit", targetNamespace = "") List<HakuparametritTyyppi> hakuparametrit)
            throws GenericFault {
        List<ValintaperusteetTyyppi> list = new ArrayList<ValintaperusteetTyyppi>();

        if (hakuparametrit == null) {
            throw new HakuparametritOnTyhjaException("Hakuparametrit oli tyhjä.");
        }
        Long start = System.currentTimeMillis();
        LOG.info("Hakuparametrien lkm {}", hakuparametrit.size());

        for (HakuparametritTyyppi param : hakuparametrit) {
            LOG.info("Haetaan hakukohteen {}, valinnanvaihe {} valintaperusteet",
                    new Object[] { param.getHakukohdeOid(), param.getValinnanVaiheJarjestysluku() });

            HakukohdeViite hakukohde = null;
            try {
                hakukohde = hakukohdeService.readByOid(param.getHakukohdeOid());
            } catch (HakukohdeViiteEiOleOlemassaException e) {
                LOG.warn("Hakukohdetta {} ei ole olemassa. Jätetään hakukohde huomioimatta.", param.getHakukohdeOid());
                continue;
            }

            Integer jarjestysluku = param.getValinnanVaiheJarjestysluku();

            Long startFind = System.currentTimeMillis();
            List<ValinnanVaihe> valinnanVaiheList = valinnanVaiheService.findByHakukohde(param.getHakukohdeOid());
            if (LOG.isInfoEnabled()) {
                LOG.info("findByHakukohde: " + (System.currentTimeMillis() - startFind));
            }
            Long startConvert = System.currentTimeMillis();
            if (jarjestysluku != null) {
                if (jarjestysluku < 0 || jarjestysluku >= valinnanVaiheList.size()) {
                    throw new ValinnanVaiheJarjestyslukuOutOfBoundsException("Hakukohteen " + param.getHakukohdeOid()
                            + " valinnan vaiheen jarjestysluku " + jarjestysluku + " on epäkelpo.");
                } else if (!valinnanVaiheList.get(jarjestysluku).getAktiivinen()) {
                    throw new ValinnanVaiheEpaaktiivinenException("Valinnan vaihe (oid "
                            + valinnanVaiheList.get(jarjestysluku).getOid() + ", järjestysluku " + jarjestysluku
                            + ") ei ole aktiivinen");
                }

                ValinnanVaihe kasiteltava = valinnanVaiheList.get(jarjestysluku);
                if(!kasiteltava.getAktiivinen()) {
                    LOG.info("Yritetään laskea valinnanvaihetta, joka ei ole aktiivinen");
                    continue;
                }

                for (ValinnanVaihe vaihe : valinnanVaiheList) {
                    if(!vaihe.getAktiivinen()) {
                        LOG.info("Jätetään käsittelemättä ei-aktiivinen valinnanvaihe");
                        valinnanVaiheList.remove(vaihe);
                    }
                }

                int todellinenJarjestysluku = valinnanVaiheList.indexOf(kasiteltava);

                ValintaperusteetTyyppi valinnanVaihe = convertValintaperusteet(kasiteltava,
                        hakukohde, todellinenJarjestysluku);
                if (valinnanVaihe != null) {
                    list.add(valinnanVaihe);
                }

            } else {
                for (ValinnanVaihe vaihe : valinnanVaiheList) {
                    if(!vaihe.getAktiivinen()) {
                        valinnanVaiheList.remove(vaihe);
                    }
                }
                for (int i = 0; i < valinnanVaiheList.size(); i++) {
                    if (valinnanVaiheList.get(i).getAktiivinen()) {
                        ValintaperusteetTyyppi valinnanVaihe = convertValintaperusteet(valinnanVaiheList.get(i),
                                hakukohde, i);
                        if (valinnanVaihe != null) {
                            list.add(valinnanVaihe);
                        }
                    }
                }
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("Convert: " + (System.currentTimeMillis() - startConvert));
            }
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("haeValintaperusteet: " + (System.currentTimeMillis() - start));
        }

        LOG.info("Haettu {} kpl valintaperusteita", list.size());
        return list;
    }

    private ValintaperusteetTyyppi convertValintaperusteet(ValinnanVaihe valinnanVaihe, HakukohdeViite hakukohde,
            int valinnanvaiheJarjestysluku) {

        ValintaperusteetTyyppi valintaperusteetTyyppi = new ValintaperusteetTyyppi();
        valintaperusteetTyyppi.setHakukohdeOid(hakukohde.getOid());
        valintaperusteetTyyppi.setHakuOid(hakukohde.getHakuoid());
        valintaperusteetTyyppi.setTarjoajaOid(hakukohde.getTarjoajaOid());

        ValinnanVaiheTyyppi vv = null;
        switch (valinnanVaihe.getValinnanVaiheTyyppi()) {

        case TAVALLINEN:
            TavallinenValinnanVaiheTyyppi tavallinen = new TavallinenValinnanVaiheTyyppi();
            tavallinen.getValintatapajono().addAll(convertJonot(valinnanVaihe));

            vv = tavallinen;
            break;
        case VALINTAKOE:
            ValintakoeValinnanVaiheTyyppi valintakoe = new ValintakoeValinnanVaiheTyyppi();
            valintakoe.getValintakoe().addAll(convertValintakokeet(valinnanVaihe));
            vv = valintakoe;
            break;

        default:
            throw new UnsupportedOperationException("Virheellinen valinnan vaiheen tyyppi. Ei pystytä käsittelemään");
        }

        vv.setValinnanVaiheJarjestysluku(valinnanvaiheJarjestysluku);
        vv.setValinnanVaiheOid(valinnanVaihe.getOid());
        vv.setNimi(valinnanVaihe.getNimi());
        valintaperusteetTyyppi.setValinnanVaihe(vv);

        for (HakukohteenValintaperuste vp : hakukohde.getHakukohteenValintaperusteet().values()) {
            HakukohteenValintaperusteTyyppi vpt = new HakukohteenValintaperusteTyyppi();
            vpt.setTunniste(vp.getTunniste());
            vpt.setArvo(vp.getArvo());
            valintaperusteetTyyppi.getHakukohteenValintaperuste().add(vpt);
        }

        return valintaperusteetTyyppi;
    }

    private List<ValintakoeTyyppi> convertValintakokeet(ValinnanVaihe valinnanVaihe) {
        List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe.getOid());
        List<ValintakoeTyyppi> valintakoetyypit = new ArrayList<ValintakoeTyyppi>();

        for (Valintakoe koe : valintakokeet) {
            if (koe.getAktiivinen()) {
                ValintakoeTyyppi tyyppi = new ValintakoeTyyppi();
                tyyppi.setKuvaus(koe.getKuvaus());
                tyyppi.setNimi(koe.getNimi());
                tyyppi.setOid(koe.getOid());
                tyyppi.setTunniste(koe.getTunniste());

                FunktiokutsuTyyppi converted = null;
                if (koe.ainaPakollinen()) {
                    converted = conversionService.convert(ValintaperusteServiceUtil.getAinaPakollinenFunktiokutsu(),
                            FunktiokutsuTyyppi.class);
                } else {
                    Laskentakaava laskentakaava = laskentakaavaService.haeLaskettavaKaava(koe.getLaskentakaava()
                            .getId(), Laskentamoodi.VALINTAKOELASKENTA);
                    converted = conversionService.convert(laskentakaava.getFunktiokutsu(), FunktiokutsuTyyppi.class);
                }
                tyyppi.setFunktiokutsu(converted);

                valintakoetyypit.add(tyyppi);
            }
        }

        return valintakoetyypit;
    }

    private List<ValintatapajonoJarjestyskriteereillaTyyppi> convertJonot(ValinnanVaihe valinnanVaihe) {
        List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe.getOid());

        List<ValintatapajonoJarjestyskriteereillaTyyppi> valintatapajonot = new ArrayList<ValintatapajonoJarjestyskriteereillaTyyppi>();

        for (int prioriteetti = 0; prioriteetti < jonot.size(); prioriteetti++) {
            Valintatapajono valintatapajono = jonot.get(prioriteetti);
            if (!valintatapajono.getAktiivinen())
                continue;
            ValintatapajonoJarjestyskriteereillaTyyppi tyyppi = new ValintatapajonoJarjestyskriteereillaTyyppi();

            tyyppi.setAloituspaikat(valintatapajono.getAloituspaikat());
            tyyppi.setKuvaus(valintatapajono.getKuvaus());
            tyyppi.setNimi(valintatapajono.getNimi());
            tyyppi.setOid(valintatapajono.getOid());

            tyyppi.setPrioriteetti(prioriteetti);

            tyyppi.setSiirretaanSijoitteluun(valintatapajono.getSiirretaanSijoitteluun());
            tyyppi.setTasasijasaanto(TasasijasaantoTyyppi.fromValue(valintatapajono.getTasapistesaanto().name()));

            tyyppi.getJarjestyskriteerit().addAll(convertJarjestyskriteerit(valintatapajono));

            valintatapajonot.add(tyyppi);
        }

        return valintatapajonot;
    }

    private List<JarjestyskriteeriTyyppi> convertJarjestyskriteerit(Valintatapajono valintatapajono) {
        List<Jarjestyskriteeri> jarjestyskriteeris = jarjestyskriteeriService
                .findJarjestyskriteeriByJono(valintatapajono.getOid());

        List<JarjestyskriteeriTyyppi> jarjestyskriteerit = new ArrayList<JarjestyskriteeriTyyppi>();

        for (int prioriteetti = 0; prioriteetti < jarjestyskriteeris.size(); prioriteetti++) {
            Jarjestyskriteeri jarjestyskriteeri = jarjestyskriteeris.get(prioriteetti);
            if (!jarjestyskriteeri.getAktiivinen())
                continue;

            JarjestyskriteeriTyyppi jarjestyskriteeriTyyppi = new JarjestyskriteeriTyyppi();
            jarjestyskriteeriTyyppi.setPrioriteetti(prioriteetti);

            Long start = System.currentTimeMillis();
            Laskentakaava laskentakaava = laskentakaavaService.haeLaskettavaKaava(jarjestyskriteeri.getLaskentakaava()
                    .getId(), Laskentamoodi.VALINTALASKENTA);
            if (LOG.isInfoEnabled()) {
                LOG.info("haeLaskettavaKaava: " + jarjestyskriteeri.getLaskentakaava().getId() + ":"
                        + (System.currentTimeMillis() - start));
            }
            FunktiokutsuTyyppi convert = conversionService.convert(laskentakaava.getFunktiokutsu(),
                    FunktiokutsuTyyppi.class);

            jarjestyskriteeriTyyppi.setFunktiokutsu(convert);

            jarjestyskriteerit.add(jarjestyskriteeriTyyppi);
        }

        return jarjestyskriteerit;
    }

    @Override
    @Secured({ CRUD })
    public void tuoHakukohde(@WebParam(name = "hakukohde", targetNamespace = "") HakukohdeImportTyyppi hakukohde)
            throws GenericFault {
//        try {
//            hakukohdeImportService.tuoHakukohde(hakukohde);
//        } catch (Exception e) {
//            LOG.error("Hakukohteen tuominen epäonnistui.", e);
//        }
        hakukohdeImportService.tuoHakukohde(hakukohde);
    }
}
