package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.service.valintaperusteet.GenericFault;
import fi.vm.sade.service.valintaperusteet.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.messages.HakuparametritTyyppi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.schema.*;
import fi.vm.sade.service.valintaperusteet.schema.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakuparametritOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheJarjestyslukuOutOfBoundsException;
import fi.vm.sade.service.valintaperusteet.service.impl.util.ValintaperusteServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.00
 */
@Service
//@PreAuthorize("isAuthenticated()")
public class ValintaperusteServiceImpl implements ValintaperusteService {

    private static final Logger logger = LoggerFactory.getLogger(ValintaperusteServiceImpl.class);

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private PaasykoeTunnisteetService tunnisteService;

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
//    @Secured({READ, UPDATE, CRUD})
    public List<ValintatapajonoTyyppi> haeValintatapajonotSijoittelulle(
            @WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid) throws GenericFault {
        List<Valintatapajono> jonot = valintatapajonoDAO.haeValintatapajonotSijoittelulle(hakukohdeOid);

        return conversionService.convertAll(jonot, ValintatapajonoTyyppi.class);
    }

    @Override
//    @Secured({READ, UPDATE, CRUD})
    public List<ValintaperusteetTyyppi> haeValintaperusteet(
            @WebParam(name = "hakuparametrit", targetNamespace = "") List<HakuparametritTyyppi> hakuparametrit)
            throws GenericFault {
        List<ValintaperusteetTyyppi> list = new ArrayList<ValintaperusteetTyyppi>();

        if (hakuparametrit == null) {
            throw new HakuparametritOnTyhjaException("Hakuparametrit oli tyhjä.");
        }

        for (HakuparametritTyyppi param : hakuparametrit) {
            List<ValinnanVaihe> valinnanVaiheList = new ArrayList<ValinnanVaihe>();
            HakukohdeViite hakukohde = hakukohdeService.readByOid(param.getHakukohdeOid());

            for (ValinnanVaihe valinnanVaihe : valinnanVaiheService.findByHakukohde(param.getHakukohdeOid())) {
                if (valinnanVaihe.getAktiivinen()) {
                    valinnanVaiheList.add(valinnanVaihe);
                }
            }

            Integer jarjestysluku = param.getValinnanVaiheJarjestysluku();
            if (jarjestysluku != null) {
                if (jarjestysluku < 0 || jarjestysluku >= valinnanVaiheList.size()) {
                    throw new ValinnanVaiheJarjestyslukuOutOfBoundsException("Jarjestysluku on epäkelpo.");
                }

                ValintaperusteetTyyppi valinnanVaihe = convertValintaperusteet(valinnanVaiheList.get(jarjestysluku),
                        param.getHakukohdeOid(), hakukohde.getHakuoid(), jarjestysluku);
                if (valinnanVaihe != null) {
                    list.add(valinnanVaihe);
                }

            } else {
                for (int i = 0; i < valinnanVaiheList.size(); i++) {
                    ValintaperusteetTyyppi valinnanVaihe = convertValintaperusteet(valinnanVaiheList.get(i),
                            param.getHakukohdeOid(), hakukohde.getHakuoid(), i);
                    if (valinnanVaihe != null) {
                        list.add(valinnanVaihe);
                    }
                }
            }
        }

        return list;
    }

    private ValintaperusteetTyyppi convertValintaperusteet(ValinnanVaihe valinnanVaihe, String hakukohdeOid,
                                                           String hakuOid, int valinnanvaiheJarjestysluku) {

        ValintaperusteetTyyppi valintaperusteetTyyppi = new ValintaperusteetTyyppi();
        valintaperusteetTyyppi.setHakukohdeOid(hakukohdeOid);
        valintaperusteetTyyppi.setHakuOid(hakuOid);

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
        valintaperusteetTyyppi.setValinnanVaihe(vv);

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
                            .getId());
                    converted = conversionService.convert(laskentakaava.getFunktiokutsu(),
                            FunktiokutsuTyyppi.class);
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

            Laskentakaava laskentakaava = laskentakaavaService.haeLaskettavaKaava(jarjestyskriteeri.getLaskentakaava()
                    .getId());
            FunktiokutsuTyyppi convert = conversionService.convert(laskentakaava.getFunktiokutsu(),
                    FunktiokutsuTyyppi.class);

            jarjestyskriteeriTyyppi.setFunktiokutsu(convert);

            jarjestyskriteerit.add(jarjestyskriteeriTyyppi);
        }

        return jarjestyskriteerit;
    }

    @Override
//    @Secured({CRUD})
    public void tuoHakukohde(@WebParam(name = "hakukohde", targetNamespace = "") HakukohdeImportTyyppi hakukohde) throws GenericFault {
        try {
            hakukohdeImportService.tuoHakukohde(hakukohde);
        } catch (Exception e) {
            logger.error("Hakukohteen tuominen epäonnistui.", e);
        }
    }
}
