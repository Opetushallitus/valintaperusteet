package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: kkammone
 * Date: 25.2.2013
 * Time: 12:57
 */
@Component
public class LuoValintaperusteetServiceImpl implements LuoValintaperusteetService, ResourceLoaderAware {

    private static final Logger LOG = LoggerFactory.getLogger(LuoValintaperusteetServiceImpl.class);

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
    private ValintakoekoodiService valintakoekoodiService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private JpaTransactionManager transactionManager;

    private ResourceLoader resourceLoader;

    private static final String HAKU_OID = "toisenAsteenSyksynYhteishaku";

    private static final String CSV_DELIMITER = ";";

    public static final String KIELI_FI_URI = "kieli_fi";
    public static final String KIELI_SV_URI = "kieli_sv";

    public static final String PAASY_JA_SOVELTUVUUSKOE = "valintakokeentyyppi_1";

    public static final String KIELIKOE_PREFIX = "kielikoe_";

    public static final Set<String> poikkeavatValintaryhmat = new HashSet<String>(Arrays.asList(new String[]{
            "hakukohteet_354", // Lasiala, pk (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_383", // Lasiala, yo (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_483", // Sisustustekstiilit, pk (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_485", // Sisustustekstiilit, yo (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_538", // Tekstiiliala, pk (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_550", // Tekstiiliala, yo (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_551", // Vaatetusala, pk (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_565", // Vaatetusala, yo (Käsi- ja taideteollisuusalan perustutkinto)
            "hakukohteet_452", // Audiovisuaalisen viestinnän perustutkinto, pk
            "hakukohteet_511", // Audiovisuaalisen viestinnän perustutkinto, yo
            "hakukohteet_610", // Äänituotanto, pk (Audiovisuaalisen viestinnän perustutkinto)
            "hakukohteet_611", // Äänituotanto, yo (Audiovisuaalisen viestinnän perustutkinto)
            "hakukohteet_497", // Kuvallisen ilmaisun perustutkinto, pk
            "hakukohteet_523", // Kuvallisen ilmaisun perustutkinto, yo
            "hakukohteet_126", // Liikunnanohjauksen perustutkinto, yo
    }));

    public enum Kielikoodi {
        SUOMI("Suomi", KIELI_FI_URI, "fi"), RUOTSI("Ruotsi", KIELI_SV_URI, "sv");

        Kielikoodi(String nimi, String kieliUri, String kieliarvo) {
            this.nimi = nimi;
            this.kieliUri = kieliUri;
            this.kieliarvo = kieliarvo;
            this.kielikoetunniste = KIELIKOE_PREFIX + kieliarvo;
        }

        private String nimi;
        private String kieliUri;
        private String kieliarvo;
        private String kielikoetunniste;


        public String getNimi() {
            return nimi;
        }

        public String getKieliUri() {
            return kieliUri;
        }

        public String getKieliarvo() {
            return kieliarvo;
        }

        public String getKielikoetunniste() {
            return kielikoetunniste;
        }
    }

    @Override
    public void luo() throws IOException {
        long beginTime = System.currentTimeMillis();

        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        transactionManager.commit(tx);

        PkAineet pkAineet = new PkAineet();
        YoAineet yoAineet = new YoAineet();

        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Valintaryhma ammatillinenKoulutusVr = new Valintaryhma();
        ammatillinenKoulutusVr.setNimi("Ammatillinen koulutus");
        ammatillinenKoulutusVr.setHakuOid(HAKU_OID);
        ammatillinenKoulutusVr = valintaryhmaService.insert(ammatillinenKoulutusVr);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe kielikoevalinnanVaihe = new ValinnanVaihe();
        kielikoevalinnanVaihe.setAktiivinen(true);
        kielikoevalinnanVaihe.setNimi("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setKuvaus("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
        kielikoevalinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(ammatillinenKoulutusVr.getOid(), kielikoevalinnanVaihe, null);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(), ammatillinenKoulutusVr);
        Laskentakaava eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt), ammatillinenKoulutusVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava kielikokeenLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt),
                ammatillinenKoulutusVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());


        final String kielikoeNimi = "Kielikoe";
        ValintakoeDTO kielikoe = new ValintakoeDTO();
        kielikoe.setAktiivinen(false);
        kielikoe.setKuvaus(kielikoeNimi);
        kielikoe.setNimi(kielikoeNimi);
        kielikoe.setTunniste(PkJaYoPohjaiset.kielikoetunniste);
        kielikoe.setLaskentakaavaId(kielikokeenLaskentakaava.getId());

        valintakoeService.lisaaValintakoeValinnanVaiheelle(kielikoevalinnanVaihe.getOid(), kielikoe);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma peruskouluVr = new Valintaryhma();
        peruskouluVr.setNimi("PK");
        peruskouluVr.setHakuOid(HAKU_OID);
        peruskouluVr = valintaryhmaService.insert(peruskouluVr, ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma lukioVr = new Valintaryhma();
        lukioVr.setNimi("LK");
        lukioVr.setHakuOid(HAKU_OID);
        lukioVr = valintaryhmaService.insert(lukioVr, ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : pkAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, peruskouluVr);
        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        //pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet), peruskouluVr);
        Laskentakaava pkPohjainenKaikkienAineidenKeskiarvo = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet), peruskouluVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        //pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenKaikkienAineidenKeskiarvo, "Yleinen koulumenestys pisteytysmalli, PK"), peruskouluVr);
        Laskentakaava pk_pohjakoulutuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPohjakoulutuspisteytysmalli(), peruskouluVr);
        Laskentakaava pk_ilmanKoulutuspaikkaaPisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(), peruskouluVr);
        Laskentakaava hakutoivejarjestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(), ammatillinenKoulutusVr);
        Laskentakaava tyokokemuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(), ammatillinenKoulutusVr);
        Laskentakaava sukupuolipisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoSukupuolipisteytysmalli(), ammatillinenKoulutusVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Pk koostava iso kaava
        Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(pk_painotettavatKeskiarvotLaskentakaava,
                pk_yleinenkoulumenestyspisteytysmalli, pk_pohjakoulutuspisteytysmalli, pk_ilmanKoulutuspaikkaaPisteytysmalli, hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt), peruskouluVr);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : yoAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, lukioVr);
        }
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava lk_paattotodistuksenkeskiarvo = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), lukioVr);
        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"), lukioVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt), lukioVr);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

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

        transactionManager.commit(tx);

        Laskentakaava[] pkTasasijakriteerit = new Laskentakaava[]{hakutoivejarjestystasapistekaava, pk_yleinenkoulumenestyspisteytysmalli, pk_painotettavatKeskiarvotLaskentakaava};
        Laskentakaava[] lkTasasijakriteerit = new Laskentakaava[]{hakutoivejarjestystasapistekaava, lk_yleinenkoulumenestyspisteytysmalli};

        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Laskentakaava pkYhdistettyPeruskaavaJaKielikoekaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                toisenAsteenPeruskoulupohjainenPeruskaava, kielikokeenLaskentakaava), peruskouluVr);

        Laskentakaava lkYhdistettyPeruskaavaJaKielikoekaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                toisenAsteenYlioppilaspohjainenPeruskaava, kielikokeenLaskentakaava), peruskouluVr);
        transactionManager.commit(tx);

        lisaaHakukohdekoodit(peruskouluVr, lukioVr, pkYhdistettyPeruskaavaJaKielikoekaava, lkYhdistettyPeruskaavaJaKielikoekaava, pkTasasijakriteerit, lkTasasijakriteerit,
                ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt, eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt, kielikokeenLaskentakaava);

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - beginTime) / 1000L / 60L;

        LOG.info("Valintaperusteet generoitu. Aikaa generointiin kului: {} min", timeTaken);
    }

    private void lisaaHakukohdekoodit(Valintaryhma peruskouluVr, Valintaryhma lukioVr,
                                      Laskentakaava pkPeruskaava,
                                      Laskentakaava lkPeruskaava,
                                      Laskentakaava[] pkTasasijakriteerit,
                                      Laskentakaava[] lkTasasijakriteerit,
                                      Laskentakaava ulkomaillaSuoritettuKoulutus,
                                      Laskentakaava eiUlkomaillaSuoritettuKoulutus,
                                      Laskentakaava kielikoeLaskentakaava) throws IOException {
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

                TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                if (nimi.contains(", pk")) {
                    valintaryhma = valintaryhmaService.insert(valintaryhma, peruskouluVr.getOid());
                } else {
                    valintaryhma = valintaryhmaService.insert(valintaryhma, lukioVr.getOid());
                }

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                ValinnanVaihe valintakoevaihe = valinnanVaiheService.findByValintaryhma(valintaryhma.getOid()).get(0);
                assert (valintakoevaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.VALINTAKOE));
                valintakoevaihe.setNimi("Kielikokeen pakollisuus ja pääsykoe");
                valintakoevaihe.setKuvaus("Kielikokeen pakollisuus ja pääsykoe");
                valintakoevaihe = valinnanVaiheService.update(valintakoevaihe.getOid(), valintakoevaihe);

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                String valintakoetunniste = nimi + ", pääsykoe";
                ValintakoeDTO valintakoe = new ValintakoeDTO();
                valintakoe.setAktiivinen(false);
                valintakoe.setKuvaus(valintakoetunniste);
                valintakoe.setTunniste(valintakoetunniste);
                valintakoe.setNimi(valintakoetunniste);

                // Valintakoe on pakollinen niille, joilla ei ole ulkomailla suoritettua koulutusta tai
                // joiden oppivelvollisuuden suorittaminen ei ole keskeytynyt
                valintakoe.setLaskentakaavaId(eiUlkomaillaSuoritettuKoulutus.getId());
                valintakoeService.lisaaValintakoeValinnanVaiheelle(valintakoevaihe.getOid(), valintakoe);

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                ValinnanVaihe valinnanVaihe = new ValinnanVaihe();
                valinnanVaihe.setAktiivinen(true);
                valinnanVaihe.setKuvaus("Varsinainen valinnanvaihe");
                valinnanVaihe.setNimi("Varsinainen valinnanvaihe");
                valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

                valinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhma.getOid(), valinnanVaihe,
                        valintakoevaihe.getOid());

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                Valintatapajono jono = new Valintatapajono();

                jono.setAktiivinen(true);
                jono.setAloituspaikat(0);
                jono.setKuvaus("Varsinaisen valinnanvaiheen valintatapajono");
                jono.setNimi("Varsinaisen valinnanvaiheen valintatapajono");
                jono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
                jono.setSiirretaanSijoitteluun(true);

                valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaihe.getOid(), jono, null);

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                Laskentakaava valintakoekaava = asetaValintaryhmaJaTallennaKantaan(
                        PkJaYoPohjaiset.luoValintakoekaava(valintakoetunniste), valintaryhma);

                transactionManager.commit(tx);

                Laskentakaava peruskaava = null;
                Laskentakaava[] tasasijakriteerit = null;

                if (nimi.contains(", pk")) {
                    peruskaava = pkPeruskaava;
                    tasasijakriteerit = pkTasasijakriteerit;
                } else {
                    peruskaava = lkPeruskaava;
                    tasasijakriteerit = lkTasasijakriteerit;
                }

                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
                Laskentakaava ensisijainenJarjestyskriteeri = null;
                if (poikkeavatValintaryhmat.contains(hakukohdekoodi.getUri())) {
                    ensisijainenJarjestyskriteeri = PkJaYoPohjaiset.luoPoikkeavanValintaryhmanLaskentakaava(
                            valintakoekaava, kielikoeLaskentakaava, ulkomaillaSuoritettuKoulutus);
                } else {
                    ensisijainenJarjestyskriteeri = PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaValintakoekaava(peruskaava, valintakoekaava);
                }

                transactionManager.commit(tx);
                insertKoe(valintaryhma, valintakoetunniste, ensisijainenJarjestyskriteeri, valintakoekaava, tasasijakriteerit, hakukohdekoodi);
                insertEiKoetta(valintaryhma, peruskaava, tasasijakriteerit, hakukohdekoodi);

            }
        } finally {
            if (reader != null) {
                reader.close();
            }

        }
    }

    private void insertKoe(Valintaryhma valintaryhma, String valintakoetunniste, Laskentakaava peruskaavaJaValintakoekaava, Laskentakaava valintakoekaava,
                           Laskentakaava[] tasasijakriteerit, Hakukohdekoodi hakukohdekoodi) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma koevalintaryhma = new Valintaryhma();
        koevalintaryhma.setNimi("Pääsykokeelliset");
        koevalintaryhma.setHakuOid(HAKU_OID);
        koevalintaryhma = valintaryhmaService.insert(koevalintaryhma, valintaryhma.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koevalintaryhma.getOid(), hakukohdekoodi);
        peruskaavaJaValintakoekaava.setValintaryhma(koevalintaryhma);
        peruskaavaJaValintakoekaava = laskentakaavaService.insert(peruskaavaJaValintakoekaava);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

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

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintakoekoodi valintakoekoodi = new Valintakoekoodi();
        valintakoekoodi.setUri(PAASY_JA_SOVELTUVUUSKOE);

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(koevalintaryhma.getOid(), valintakoekoodi);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe tavallinenVaihe = valinnanVaiheet.get(1);
        assert (tavallinenVaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(tavallinenVaihe.getOid()).get(0);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Jarjestyskriteeri kriteeri = new Jarjestyskriteeri();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaavaJaValintakoekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null,
                peruskaavaJaValintakoekaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

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

        transactionManager.commit(tx);
    }

    private void insertEiKoetta(Valintaryhma valintaryhma, Laskentakaava peruskaava,
                                Laskentakaava[] tasasijakriteerit, Hakukohdekoodi hakukohdekoodi) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma koe = new Valintaryhma();
        koe.setNimi("Pääsykokeettomat");
        koe.setHakuOid(HAKU_OID);
        koe = valintaryhmaService.insert(koe, valintaryhma.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(1);
        assert (vaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid()).get(0);

        Jarjestyskriteeri kriteeri = new Jarjestyskriteeri();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, peruskaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            Laskentakaava kaava = tasasijakriteerit[i];
            Jarjestyskriteeri jk = new Jarjestyskriteeri();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }
        transactionManager.commit(tx);
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
