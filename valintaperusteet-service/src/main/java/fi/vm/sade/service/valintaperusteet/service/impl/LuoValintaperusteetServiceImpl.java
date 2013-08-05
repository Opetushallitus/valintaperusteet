package fi.vm.sade.service.valintaperusteet.service.impl;

import com.mysema.query.NonUniqueResultException;
import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 25.2.2013
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Transactional
public class LuoValintaperusteetServiceImpl implements LuoValintaperusteetService, ResourceLoaderAware {

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private OpetuskielikoodiDAO opetuskielikoodiDAO;

    @Autowired
    private OpetuskielikoodiService opetuskielikoodiService;

    @Autowired
    private ValintakoekoodiService valintakoekoodiService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private ValintakoeService valintakoeService;

    private ResourceLoader resourceLoader;

    private static final String HAKU_OID = "toisenAsteenSyksynYhteishaku";

    private static final String CSV_DELIMITER = ";";

    public static final String KIELI_FI_URI = "kieli_fi";
    public static final String KIELI_SV_URI = "kieli_sv";

    public static final String OLETUS_VALINTAKOEURI = "valintakokeentyyppi_1";

    public enum Kielikoodi {
        SUOMI("Suomi", KIELI_FI_URI, "FI"), RUOTSI("Ruotsi", KIELI_SV_URI, "SV");

        Kielikoodi(String nimi, String kieliUri, String kieliarvo) {
            this.nimi = nimi;
            this.kieliUri = kieliUri;
            this.kieliarvo = kieliarvo;
        }

        private String nimi;
        private String kieliUri;
        private String kieliarvo;

        public String getNimi() {
            return nimi;
        }

        public String getKieliUri() {
            return kieliUri;
        }

        public String getKieliarvo() {
            return kieliarvo;
        }
    }

    @Override
    public void luo() throws IOException {

        for (Kielikoodi k : Kielikoodi.values()) {
            Opetuskielikoodi opetuskieli = new Opetuskielikoodi();
            opetuskieli.setUri(k.kieliUri);
            opetuskieli.setNimiFi(k.nimi);
            opetuskieli.setNimiSv(k.nimi);
            opetuskieli.setNimiEn(k.nimi);

            try {
                opetuskielikoodiDAO.readByUri(k.kieliUri);
            } catch (NonUniqueResultException e) {
                opetuskielikoodiDAO.insert(opetuskieli);
            }
        }


        PkAineet pkAineet = new PkAineet();
        YoAineet yoAineet = new YoAineet();

        Valintaryhma ammatillinenKoulutusVr = new Valintaryhma();
        ammatillinenKoulutusVr.setNimi("Ammatillinen koulutus");
        ammatillinenKoulutusVr.setHakuOid(HAKU_OID);
        ammatillinenKoulutusVr = valintaryhmaService.insert(ammatillinenKoulutusVr);

        ValinnanVaihe kielikoevalinnanVaihe = new ValinnanVaihe();
        kielikoevalinnanVaihe.setAktiivinen(true);
        kielikoevalinnanVaihe.setNimi("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setKuvaus("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
        kielikoevalinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(ammatillinenKoulutusVr.getOid(), kielikoevalinnanVaihe, null);

        Map<Kielikoodi, Laskentakaava> kielikokeidenLaskentakaavat = new HashMap<Kielikoodi, Laskentakaava>();
        for (Kielikoodi k : Kielikoodi.values()) {
            kielikokeidenLaskentakaavat.put(k,
                    asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(k),
                            ammatillinenKoulutusVr));
        }

        for (Kielikoodi k : Kielikoodi.values()) {
            final String kielikoeNimi = k.nimi + " - Kielikoe";
            ValintakoeDTO kielikoe = new ValintakoeDTO();
            kielikoe.setAktiivinen(false);
            kielikoe.setKuvaus(kielikoeNimi);
            kielikoe.setNimi(kielikoeNimi);
            kielikoe.setTunniste(k.kieliUri);
            kielikoe.setLaskentakaavaId(kielikokeidenLaskentakaavat.get(k).getId());

            valintakoeService.lisaaValintakoeValinnanVaiheelle(kielikoevalinnanVaihe.getOid(), kielikoe);

        }

        Valintaryhma peruskouluVr = new Valintaryhma();
        peruskouluVr.setNimi("PK");
        peruskouluVr.setHakuOid(HAKU_OID);
        peruskouluVr = valintaryhmaService.insert(peruskouluVr, ammatillinenKoulutusVr.getOid());

        Valintaryhma lukioVr = new Valintaryhma();
        lukioVr.setNimi("LK");
        lukioVr.setHakuOid(HAKU_OID);
        lukioVr = valintaryhmaService.insert(lukioVr, ammatillinenKoulutusVr.getOid());

        for (Laskentakaava kaava : pkAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, peruskouluVr);
        }

        //pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet), peruskouluVr);
        Laskentakaava pkPohjainenLukuaineidenKeskiarvo = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pkAineet), peruskouluVr);

        //pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenLukuaineidenKeskiarvo, "Yleinen koulumenestys pisteytysmalli, PK"), peruskouluVr);
        Laskentakaava pk_pohjakoulutuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPohjakoulutuspisteytysmalli(), peruskouluVr);
        Laskentakaava pk_ilmanKoulutuspaikkaaPisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(), peruskouluVr);
        Laskentakaava hakutoivejarjestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(), ammatillinenKoulutusVr);
        Laskentakaava tyokokemuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(), ammatillinenKoulutusVr);
        Laskentakaava sukupuolipisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoSukupuolipisteytysmalli(), ammatillinenKoulutusVr);


        // Pk koostava iso kaava
        Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(pk_painotettavatKeskiarvotLaskentakaava,
                pk_yleinenkoulumenestyspisteytysmalli, pk_pohjakoulutuspisteytysmalli, pk_ilmanKoulutuspaikkaaPisteytysmalli, hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli), peruskouluVr);

        for (Laskentakaava kaava : yoAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, lukioVr);
        }

        Laskentakaava lk_paattotodistuksenkeskiarvo = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), lukioVr);

        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"), lukioVr);

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli), lukioVr);

        //        Tasasijakriteerit on
        //
        //        pk-pohjaiseen ammatilliseen:
        //
        //        1.       Hakutoivejärjestys (eli jos on vaikka 1.sijainen ja 2. sijainen hakija samoilla pisteillä, valitaan se 1. sijainen hakija)
        //        2.       Mahd. pääsy- ja soveltuvuuskokeen pistemäärä
        //        3.       Yleinen koulumenestys (eli se sama kaava josta saa pisteet, kaikkien aineiden keskiarvo)
        //        4.       painotettavat arvosanat (tästäkin olemassa kaava)
        //        5.       arvonta

        //yo-pohjaisessa samat kriteerit paitsi kohta 4 jää pois kun yo -pohjaisessa ei ole noita painotettavia
        //arvosanoja.

        Laskentakaava hakutoivejarjestystasapistekaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava(), ammatillinenKoulutusVr);

        Laskentakaava[] pkTasasijakriteerit = new Laskentakaava[]{hakutoivejarjestystasapistekaava, pk_yleinenkoulumenestyspisteytysmalli, pk_painotettavatKeskiarvotLaskentakaava};
        Laskentakaava[] lkTasasijakriteerit = new Laskentakaava[]{hakutoivejarjestystasapistekaava, lk_yleinenkoulumenestyspisteytysmalli};

        lisaaHakukohdekoodit(peruskouluVr, lukioVr, toisenAsteenPeruskoulupohjainenPeruskaava, toisenAsteenYlioppilaspohjainenPeruskaava, pkTasasijakriteerit, lkTasasijakriteerit);
    }


    private void lisaaHakukohdekoodit(Valintaryhma peruskouluVr, Valintaryhma lukioVr,
                                      Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava,
                                      Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava,
                                      Laskentakaava[] pkTasasijakriteerit,
                                      Laskentakaava[] lkTasasijakriteerit) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:hakukohdekoodit/hakukohdekoodit.csv").getInputStream(), Charset.forName("UTF-8")));

            // Luetaan otsikkorivi pois
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(CSV_DELIMITER);
                String arvo = splitted[0];
                String uri = splitted[1];
                String nimi = splitted[2].replace("\"", "");

                Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
                hakukohdekoodi.setArvo(arvo);
                hakukohdekoodi.setUri(uri);
                hakukohdekoodi.setNimiFi(nimi);
                hakukohdekoodi.setNimiSv(nimi);
                hakukohdekoodi.setNimiEn(nimi);

                Valintaryhma valintaryhma = new Valintaryhma();
                valintaryhma.setHakuOid(HAKU_OID);
                valintaryhma.setNimi(nimi);

                if (nimi.contains(", pk")) {
                    valintaryhma = valintaryhmaService.insert(valintaryhma, peruskouluVr.getOid());
                } else {
                    valintaryhma = valintaryhmaService.insert(valintaryhma, lukioVr.getOid());
                }

                ValinnanVaihe valintakoevaihe = valinnanVaiheService.findByValintaryhma(valintaryhma.getOid()).get(0);
                assert (valintakoevaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.VALINTAKOE));
                valintakoevaihe.setNimi("Kielikokeen pakollisuus ja pääsykoe");
                valintakoevaihe.setKuvaus("Kielikokeen pakollisuus ja pääsykoe");
                valintakoevaihe = valinnanVaiheService.update(valintakoevaihe.getOid(), valintakoevaihe);

                String valintakoetunniste = nimi + " - pääsykoe";
                ValintakoeDTO valintakoe = new ValintakoeDTO();
                valintakoe.setAktiivinen(false);
                valintakoe.setKuvaus(valintakoetunniste);
                valintakoe.setTunniste(valintakoetunniste);
                valintakoe.setNimi(valintakoetunniste);

                // Valintakoe on aina pakollinen (eli null)
                valintakoe.setLaskentakaavaId(null);
                valintakoeService.lisaaValintakoeValinnanVaiheelle(valintakoevaihe.getOid(), valintakoe);

                ValinnanVaihe valinnanVaihe = new ValinnanVaihe();
                valinnanVaihe.setAktiivinen(true);
                valinnanVaihe.setKuvaus("generoitu");
                valinnanVaihe.setNimi("Generoitu valinnanvaihe");
                valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

                valinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhma.getOid(), valinnanVaihe,
                        valintakoevaihe.getOid());

                Valintatapajono jono = new Valintatapajono();

                jono.setAktiivinen(true);
                jono.setAloituspaikat(0);
                jono.setKuvaus("generoitu");
                jono.setNimi("Generoitu valintatapajono");
                jono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
                jono.setSiirretaanSijoitteluun(true);

                valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaihe.getOid(), jono, null);

                String valintakoekaavaNimi = nimi + ", pääsykoe";
                Laskentakaava valintakoekaava = asetaValintaryhmaJaTallennaKantaan(
                        PkJaYoPohjaiset.luoValintakoekaava(valintakoekaavaNimi), valintaryhma);


                Laskentakaava peruskaava = null;
                Laskentakaava[] tasasijakriteerit = null;

                if (nimi.contains(", pk")) {
                    peruskaava = toisenAsteenPeruskoulupohjainenPeruskaava;
                    tasasijakriteerit = pkTasasijakriteerit;
                } else {
                    peruskaava = toisenAsteenYlioppilaspohjainenPeruskaava;
                    tasasijakriteerit = lkTasasijakriteerit;
                }

                Funktiokutsu funktiokutsu = GenericHelper.luoSumma(peruskaava, valintakoekaava);
                Laskentakaava peruskaavaJaValintakoekaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(funktiokutsu,
                        peruskaava.getNimi() + " + " + valintakoekaavaNimi);

                peruskaavaJaValintakoekaava = asetaValintaryhmaJaTallennaKantaan(peruskaavaJaValintakoekaava, valintaryhma);

                for (Kielikoodi k : Kielikoodi.values()) {
                    Opetuskielikoodi opetuskieli = new Opetuskielikoodi();
                    opetuskieli.setUri(k.kieliUri);
                    opetuskieli.setNimiFi(k.nimi);
                    opetuskieli.setNimiSv(k.nimi);
                    opetuskieli.setNimiEn(k.nimi);

                    Valintaryhma kielivalintaryhma = new Valintaryhma();
                    kielivalintaryhma.setNimi(k.nimi);
                    kielivalintaryhma.setHakuOid(HAKU_OID);

                    kielivalintaryhma = valintaryhmaService.insert(kielivalintaryhma, valintaryhma.getOid());
                    List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByValintaryhma(kielivalintaryhma.getOid());
                    ValinnanVaihe kielikoeValinnanvaihe = valinnanVaiheet.get(0);
                    assert (kielikoeValinnanvaihe.getNimi().contains("Kielikokeen pakollisuus"));

                    List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(kielikoeValinnanvaihe.getOid());

                    boolean loydetty = false;
                    for (Valintakoe koe : valintakokeet) {
                        if (koe.getTunniste().equals(k.getKieliUri())) {
                            ValintakoeDTO dto = new ValintakoeDTO();
                            dto.setKuvaus(koe.getKuvaus());
                            dto.setLaskentakaavaId(koe.getLaskentakaavaId());
                            dto.setNimi(koe.getNimi());
                            dto.setTunniste(koe.getTunniste());
                            dto.setAktiivinen(true);

                            valintakoeService.update(koe.getOid(), dto);

                            loydetty = true;
                            break;
                        }
                    }

                    assert (loydetty);

                    insertKoe(kielivalintaryhma, valintakoetunniste, peruskaavaJaValintakoekaava, valintakoekaava, tasasijakriteerit,
                            opetuskieli, hakukohdekoodi);
                    insertEiKoetta(kielivalintaryhma, peruskaava, tasasijakriteerit, opetuskieli, hakukohdekoodi);
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }

        }
    }

    private void insertKoe(Valintaryhma kielivalintaryhma, String valintakoetunniste,
                           Laskentakaava peruskaavaJaValintakoekaava, Laskentakaava valintakoekaava,
                           Laskentakaava[] tasasijakriteerit, Opetuskielikoodi opetuskielikoodi,
                           Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma koevalintaryhma = new Valintaryhma();
        koevalintaryhma.setNimi("Pääsykokeelliset");
        koevalintaryhma.setHakuOid(HAKU_OID);
        koevalintaryhma = valintaryhmaService.insert(koevalintaryhma, kielivalintaryhma.getOid());

        // Aktivoidaan pääsykoe
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByValintaryhma(koevalintaryhma.getOid());

        ValinnanVaihe valintakoevaihe = valinnanVaiheet.get(0);
        assert (valintakoevaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.VALINTAKOE));
        assert (valintakoevaihe.getNimi().contains("ja pääsykoe"));

        List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valintakoevaihe.getOid());
        Valintakoe paasykoe = null;
        for (Valintakoe koe : valintakokeet) {
            if (valintakoetunniste.equals(koe.getTunniste())) {
                paasykoe = koe;
                break;
            }
        }

        assert (paasykoe != null);

        paasykoe.setAktiivinen(true);
        ValintakoeDTO dto = new ValintakoeDTO();
        dto.setAktiivinen(true);
        dto.setNimi(paasykoe.getNimi());
        dto.setKuvaus(paasykoe.getKuvaus());
        dto.setTunniste(paasykoe.getTunniste());
        dto.setLaskentakaavaId(paasykoe.getLaskentakaavaId());
        valintakoeService.update(paasykoe.getOid(), dto);

        opetuskielikoodiService.lisaaOpetuskielikoodiValintaryhmalle(koevalintaryhma.getOid(), opetuskielikoodi);
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koevalintaryhma.getOid(), hakukohdekoodi);
        Valintakoekoodi valintakoekoodi = new Valintakoekoodi();
        valintakoekoodi.setUri(OLETUS_VALINTAKOEURI);

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(koevalintaryhma.getOid(), valintakoekoodi);

        ValinnanVaihe tavallinenVaihe = valinnanVaiheet.get(1);
        assert (tavallinenVaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(tavallinenVaihe.getOid()).get(0);

        Jarjestyskriteeri kriteeri = new Jarjestyskriteeri();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaavaJaValintakoekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null,
                peruskaavaJaValintakoekaava.getId());

        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            if (i == 1) {
                Jarjestyskriteeri jk = new Jarjestyskriteeri();
                jk.setAktiivinen(true);
                jk.setMetatiedot(valintakoekaava.getNimi());
                jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, valintakoekaava.getId());
            }

            Laskentakaava kaava = tasasijakriteerit[i];
            Jarjestyskriteeri jk = new Jarjestyskriteeri();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }
    }

    private void insertEiKoetta(Valintaryhma kielivalintaryhma, Laskentakaava peruskaava,
                                Laskentakaava[] tasasijakriteerit, Opetuskielikoodi opetuskielikoodi,
                                Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma koe = new Valintaryhma();
        koe.setNimi("Pääsykokeettomat");
        koe.setHakuOid(HAKU_OID);
        koe = valintaryhmaService.insert(koe, kielivalintaryhma.getOid());

        opetuskielikoodiService.lisaaOpetuskielikoodiValintaryhmalle(koe.getOid(), opetuskielikoodi);
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);

        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(1);
        assert (vaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid()).get(0);

        Jarjestyskriteeri kriteeri = new Jarjestyskriteeri();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, peruskaava.getId());

        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            Laskentakaava kaava = tasasijakriteerit[i];
            Jarjestyskriteeri jk = new Jarjestyskriteeri();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }
    }


    private Laskentakaava asetaValintaryhmaJaTallennaKantaan(Laskentakaava kaava, Valintaryhma valintaryhma) {
        kaava.setValintaryhma(valintaryhma);
        return laskentakaavaService.insert(kaava);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
