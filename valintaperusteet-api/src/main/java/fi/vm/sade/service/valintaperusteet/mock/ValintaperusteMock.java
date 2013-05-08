package fi.vm.sade.service.valintaperusteet.mock;

import fi.vm.sade.service.valintaperusteet.GenericFault;
import fi.vm.sade.service.valintaperusteet.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.messages.HakuparametritTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.*;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eetu Blomqvist
 */
public class ValintaperusteMock implements ValintaperusteService {


    @Override
    public void tuoHakukohde(@WebParam(name = "hakukohde", targetNamespace = "") HakukohdeImportTyyppi hakukohde) throws GenericFault {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ValintatapajonoTyyppi> haeValintatapajonotSijoittelulle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid) throws GenericFault {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ValintaperusteetTyyppi> haeValintaperusteet(
            @WebParam(name = "hakuparametrit", targetNamespace = "") List<HakuparametritTyyppi> hakuparametrit)
            throws GenericFault {
        List<ValintaperusteetTyyppi> list = new ArrayList<ValintaperusteetTyyppi>();

        for (HakuparametritTyyppi param : hakuparametrit) {
            ValintaperusteetTyyppi vp = new ValintaperusteetTyyppi();
            vp.setHakukohdeOid(param.getHakukohdeOid());
            TavallinenValinnanVaiheTyyppi vv = new TavallinenValinnanVaiheTyyppi();
            vv.setValinnanVaiheJarjestysluku(param.getValinnanVaiheJarjestysluku());
            vv.getValintatapajono().addAll(createValintatapajonot(3));
            list.add(vp);
        }
        return list;
    }

    private List<ValintatapajonoJarjestyskriteereillaTyyppi> createValintatapajonot(int count) {
        List<ValintatapajonoJarjestyskriteereillaTyyppi> list = new ArrayList<ValintatapajonoJarjestyskriteereillaTyyppi>();

        for (int i = 0; i < 3; ++i) {
            ValintatapajonoJarjestyskriteereillaTyyppi jono = new ValintatapajonoJarjestyskriteereillaTyyppi();
            jono.setAloituspaikat(10);
            jono.setKuvaus("Kuvaus " + i);
            jono.setNimi("Jono " + i);
            jono.setOid("Oid " + i);
            jono.setPrioriteetti(i);
            jono.setSiirretaanSijoitteluun(true);
            jono.setTasasijasaanto(TasasijasaantoTyyppi.ARVONTA);
            jono.getJarjestyskriteerit().add(createJarjestysKriteeri());
            list.add(jono);
        }
        return list;
    }

    private JarjestyskriteeriTyyppi createJarjestysKriteeri() {
        JarjestyskriteeriTyyppi kriteeri = new JarjestyskriteeriTyyppi();
        kriteeri.setPrioriteetti(1);
        kriteeri.setFunktiokutsu(createFunktiokutsu());
        return kriteeri;
    }

    private FunktiokutsuTyyppi createFunktiokutsu() {
        FunktiokutsuTyyppi kutsu = new FunktiokutsuTyyppi();
        kutsu.setFunktionimi("SUMMA");

        FunktiokutsuTyyppi arg1kutsu = new FunktiokutsuTyyppi();
        arg1kutsu.setFunktionimi("LUKUARVO");

        SyoteparametriTyyppi arg1syote = new SyoteparametriTyyppi();
        arg1syote.setAvain("lukuarvo");
        arg1syote.setArvo("5.0");
        arg1kutsu.getSyoteparametrit().add(arg1syote);

        FunktioargumenttiTyyppi arg1 = new FunktioargumenttiTyyppi();
        arg1.setIndeksi(1);
        arg1.setFunktiokutsu(arg1kutsu);

        FunktiokutsuTyyppi arg2kutsu = new FunktiokutsuTyyppi();
        arg1kutsu.setFunktionimi("LUKUARVO");

        SyoteparametriTyyppi arg2syote = new SyoteparametriTyyppi();
        arg2syote.setAvain("lukuarvo");
        arg2syote.setArvo("8.0");
        arg2kutsu.getSyoteparametrit().add(arg2syote);

        FunktioargumenttiTyyppi arg2 = new FunktioargumenttiTyyppi();
        arg2.setIndeksi(2);
        arg2.setFunktiokutsu(arg2kutsu);

        kutsu.getFunktioargumentit().add(arg1);
        kutsu.getFunktioargumentit().add(arg2);

        return kutsu;
    }
}
