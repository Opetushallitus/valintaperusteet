package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import fi.vm.sade.service.valintaperusteet.service.impl.lukio.LukionValintaperusteet;
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
    public static final String LISANAYTTO = "valintakokeentyyppi_2";
    public static final String LISAPISTE = "valintakokeentyyppi_5";

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

    private final String PAASYKOE_TUNNISTE = "paasykoe_tunniste";
    private final String KIELIKOE_TUNNISTE = "kielikoe_tunniste";
    private final String LISANAYTTO_TUNNISTE = "lisanaytto_tunniste";

    @Override
    public void luo() throws IOException {
        long beginTime = System.currentTimeMillis();

        luoAmmatillinenKoulutus();
        luoLukioKoulutus();

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - beginTime) / 1000L / 60L;

        LOG.info("Valintaperusteet generoitu. Aikaa generointiin kului: {} min", timeTaken);
    }

    public void luoAmmatillinenKoulutus() throws IOException {
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


        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(), ammatillinenKoulutusVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe esivalinnanVaihe = new ValinnanVaihe();
        esivalinnanVaihe.setAktiivinen(true);
        esivalinnanVaihe.setKuvaus("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setNimi("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        esivalinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(ammatillinenKoulutusVr.getOid(), esivalinnanVaihe,
                null);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintatapajono esijono = new Valintatapajono();

        esijono.setAktiivinen(true);
        esijono.setAloituspaikat(0);
        esijono.setKuvaus("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setNimi("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        esijono.setSiirretaanSijoitteluun(false);

        valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(esivalinnanVaihe.getOid(), esijono, null);

        Jarjestyskriteeri esijk = new Jarjestyskriteeri();
        esijk.setAktiivinen(true);
        esijk.setLaskentakaava(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt);
        esijk.setMetatiedot(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(esijono.getOid(), esijk, null, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe kielikoevalinnanVaihe = new ValinnanVaihe();
        kielikoevalinnanVaihe.setAktiivinen(true);
        kielikoevalinnanVaihe.setNimi("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setKuvaus("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
        kielikoevalinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(ammatillinenKoulutusVr.getOid(), kielikoevalinnanVaihe, esivalinnanVaihe.getOid());
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        //Laskentakaava kielikokeenLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt),
        Laskentakaava kielikokeenLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(),
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
        peruskouluVr.setNimi("Peruskoulupohjaiset");
        peruskouluVr.setHakuOid(HAKU_OID);
        peruskouluVr = valintaryhmaService.insert(peruskouluVr, ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma lukioVr = new Valintaryhma();
        lukioVr.setNimi("Lukiopohjaiset");
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
                sukupuolipisteytysmalli), peruskouluVr);
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
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli), lukioVr);
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

        lisaaHakukohdekoodit(peruskouluVr, lukioVr, pkYhdistettyPeruskaavaJaKielikoekaava, lkYhdistettyPeruskaavaJaKielikoekaava, pkTasasijakriteerit, lkTasasijakriteerit, kielikokeenLaskentakaava);

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - beginTime) / 1000L / 60L;

        LOG.info("Valintaperusteet ammatilliseen koulutukseen generoitu. Aikaa generointiin kului: {} min", timeTaken);
    }

    public void luoLukioKoulutus() throws IOException {
        long beginTime = System.currentTimeMillis();

        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        transactionManager.commit(tx);

        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Valintaryhma lukioKoulutusVr = new Valintaryhma();
        lukioKoulutusVr.setNimi("Lukiokoulutus");
        lukioKoulutusVr.setHakuOid(HAKU_OID);
        lukioKoulutusVr = valintaryhmaService.insert(lukioKoulutusVr);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava painotettuKeskiarvo = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo(), lukioKoulutusVr);
        Laskentakaava paasykoe = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.paasykoeLukuarvo(PAASYKOE_TUNNISTE), lukioKoulutusVr);
        Laskentakaava lisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.lisanayttoLukuarvo(LISANAYTTO_TUNNISTE), lukioKoulutusVr);
        Laskentakaava paasykoeJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.paasykoeJaLisanaytto(paasykoe, lisanaytto), lukioKoulutusVr);
        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(), lukioKoulutusVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe esivalinnanVaihe = new ValinnanVaihe();
        esivalinnanVaihe.setAktiivinen(true);
        esivalinnanVaihe.setKuvaus("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setNimi("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        esivalinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(lukioKoulutusVr.getOid(), esivalinnanVaihe,
                null);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintatapajono esijono = new Valintatapajono();

        esijono.setAktiivinen(true);
        esijono.setAloituspaikat(0);
        esijono.setKuvaus("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setNimi("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        esijono.setSiirretaanSijoitteluun(false);

        valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(esivalinnanVaihe.getOid(), esijono, null);

        Jarjestyskriteeri esijk = new Jarjestyskriteeri();
        esijk.setAktiivinen(true);
        esijk.setLaskentakaava(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt);
        esijk.setMetatiedot(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(esijono.getOid(), esijk, null, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe paasykoeValinnanVaihe = new ValinnanVaihe();
        paasykoeValinnanVaihe.setAktiivinen(false);
        paasykoeValinnanVaihe.setNimi("Pääsykokeen ja/tai lisäpisteen pakollisuus");
        paasykoeValinnanVaihe.setKuvaus("Pääsykokeen ja/tai lisäpisteen pakollisuus");
        paasykoeValinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
        paasykoeValinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(lukioKoulutusVr.getOid(), paasykoeValinnanVaihe, esivalinnanVaihe.getOid());

        ValintakoeDTO valintakoePaasykoe = new ValintakoeDTO();
        valintakoePaasykoe.setNimi("Pääsykoe");
        valintakoePaasykoe.setKuvaus("Pääsykoe");
        valintakoePaasykoe.setAktiivinen(false);
        valintakoePaasykoe.setTunniste("{{hakukohde. " + PAASYKOE_TUNNISTE + "}}");

        valintakoeService.lisaaValintakoeValinnanVaiheelle(paasykoeValinnanVaihe.getOid(), valintakoePaasykoe);

        ValintakoeDTO valintakoeLisanaytto = new ValintakoeDTO();
        valintakoeLisanaytto.setNimi("Lisäpiste");
        valintakoeLisanaytto.setKuvaus("Lisäpiste");
        valintakoeLisanaytto.setAktiivinen(false);
        valintakoeLisanaytto.setTunniste("{{hakukohde. " + LISANAYTTO_TUNNISTE + "}}");

        valintakoeService.lisaaValintakoeValinnanVaiheelle(paasykoeValinnanVaihe.getOid(), valintakoeLisanaytto);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe = new ValinnanVaihe();
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setKuvaus("Varsinainen valinnanvaihe");
        valinnanVaihe.setNimi("Varsinainen valinnanvaihe");
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        valinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(lukioKoulutusVr.getOid(), valinnanVaihe,
                paasykoeValinnanVaihe.getOid());

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

        Valintakoekoodi paasykoeKoodi = new Valintakoekoodi();
        paasykoeKoodi.setUri(PAASY_JA_SOVELTUVUUSKOE);

        Valintakoekoodi lisanayttoKoodi = new Valintakoekoodi();
        lisanayttoKoodi.setUri(LISANAYTTO);

        Valintaryhma painotettuKeskiarvoVr = new Valintaryhma();
        painotettuKeskiarvoVr.setNimi("Painotettu keskiarvo");
        painotettuKeskiarvoVr.setHakuOid(HAKU_OID);
        painotettuKeskiarvoVr = valintaryhmaService.insert(painotettuKeskiarvoVr, lukioKoulutusVr.getOid());

        Laskentakaava laskentakaavaPainotettuKeskiarvo = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo(), painotettuKeskiarvoVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoVr.getOid()).get(2);
        Valintatapajono valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        Jarjestyskriteeri jk = new Jarjestyskriteeri();
        jk.setAktiivinen(true);
        jk.setLaskentakaava(laskentakaavaPainotettuKeskiarvo);
        jk.setMetatiedot(laskentakaavaPainotettuKeskiarvo.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavaPainotettuKeskiarvo.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());


        Valintaryhma painotettuKeskiarvoJaPaasykoeVr = new Valintaryhma();
        painotettuKeskiarvoJaPaasykoeVr.setNimi("Painotettu keskiarvo ja pääsykoe");
        painotettuKeskiarvoJaPaasykoeVr.setHakuOid(HAKU_OID);
        painotettuKeskiarvoJaPaasykoeVr = valintaryhmaService.insert(painotettuKeskiarvoJaPaasykoeVr, lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeVr.getOid(), paasykoeKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaPaasykoe = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaPaasykoe(painotettuKeskiarvo, paasykoe), painotettuKeskiarvoJaPaasykoeVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        jk = new Jarjestyskriteeri();
        jk.setAktiivinen(true);
        jk.setLaskentakaava(laskentakaavapainotettuKeskiarvoJaPaasykoe);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaPaasykoe.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavapainotettuKeskiarvoJaPaasykoe.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(), valinnanVaihe0);
        List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());
        Valintakoe koe0 = valintakokeet.get(0);
        koe0.setAktiivinen(true);
        ValintakoeDTO dto = new ValintakoeDTO();
        dto.setAktiivinen(true);
        dto.setNimi(koe0.getNimi());
        dto.setKuvaus(koe0.getKuvaus());
        dto.setTunniste(koe0.getTunniste());
        dto.setLaskentakaavaId(koe0.getLaskentakaavaId());
        valintakoeService.update(koe0.getOid(), dto);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma painotettuKeskiarvoJaLisanayttoVr = new Valintaryhma();
        painotettuKeskiarvoJaLisanayttoVr.setNimi("Painotettu keskiarvo ja lisänäyttö");
        painotettuKeskiarvoJaLisanayttoVr.setHakuOid(HAKU_OID);
        painotettuKeskiarvoJaLisanayttoVr = valintaryhmaService.insert(painotettuKeskiarvoJaLisanayttoVr, lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaLisanayttoVr.getOid(), lisanayttoKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaLisanaytto(painotettuKeskiarvo, lisanaytto), painotettuKeskiarvoJaLisanayttoVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaLisanayttoVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        jk = new Jarjestyskriteeri();
        jk.setAktiivinen(true);
        jk.setLaskentakaava(laskentakaavapainotettuKeskiarvoJaLisanaytto);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaLisanaytto.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavapainotettuKeskiarvoJaLisanaytto.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaLisanayttoVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(), valinnanVaihe0);
        valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());
        Valintakoe koe1 = valintakokeet.get(1);
        koe1.setAktiivinen(true);
        dto = new ValintakoeDTO();
        dto.setAktiivinen(true);
        dto.setNimi(koe1.getNimi());
        dto.setKuvaus(koe1.getKuvaus());
        dto.setTunniste(koe1.getTunniste());
        dto.setLaskentakaavaId(koe1.getLaskentakaavaId());
        valintakoeService.update(koe1.getOid(), dto);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Valintaryhma painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = new Valintaryhma();
        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.setNimi("Painotettu keskiarvo, pääsykoe ja lisänäyttö");
        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.setHakuOid(HAKU_OID);
        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = valintaryhmaService.insert(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr, lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), lisanayttoKoodi);
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), paasykoeKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(laskentakaavapainotettuKeskiarvoJaPaasykoe, painotettuKeskiarvoJaPaasykoeJaLisanayttoVr);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        jk = new Jarjestyskriteeri();
        jk.setAktiivinen(true);
        jk.setLaskentakaava(laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(), valinnanVaihe0);
        valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());

        koe0 = valintakokeet.get(0);
        koe0.setAktiivinen(true);
        dto = new ValintakoeDTO();
        dto.setAktiivinen(true);
        dto.setNimi(koe0.getNimi());
        dto.setKuvaus(koe0.getKuvaus());
        dto.setTunniste(koe0.getTunniste());
        dto.setLaskentakaavaId(koe0.getLaskentakaavaId());
        valintakoeService.update(koe0.getOid(), dto);

        koe1 = valintakokeet.get(1);
        koe1.setAktiivinen(true);
        dto = new ValintakoeDTO();
        dto.setAktiivinen(true);
        dto.setNimi(koe1.getNimi());
        dto.setKuvaus(koe1.getKuvaus());
        dto.setTunniste(koe1.getTunniste());
        dto.setLaskentakaavaId(koe1.getLaskentakaavaId());
        valintakoeService.update(koe1.getOid(), dto);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:hakukohdekoodit/lukiohakukohdekoodit.csv").getInputStream(), Charset.forName("UTF-8")));
            // Luetaan otsikkorivi pois
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(CSV_DELIMITER);
                String arvo = splitted[0];
                String uri = "hakukohteet_" + splitted[0];
                String nimiFi = splitted[1].replace("\"", "");
                String nimiSv = splitted[2].replace("\"", "");

                Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
                hakukohdekoodi.setArvo(arvo);
                hakukohdekoodi.setUri(uri);
                hakukohdekoodi.setNimiFi(nimiFi);
                hakukohdekoodi.setNimiSv(nimiSv);
                hakukohdekoodi.setNimiEn(nimiFi);

                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoVr.getOid(), hakukohdekoodi);
                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoJaLisanayttoVr.getOid(), hakukohdekoodi);
                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeVr.getOid(), hakukohdekoodi);
                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), hakukohdekoodi);

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            }
        } finally {
            if (reader != null) {
                reader.close();
            }

        }

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - beginTime) / 1000L / 60L;

        LOG.info("Valintaperusteet lukiokoulutukseen generoitu. Aikaa generointiin kului: {} min", timeTaken);
    }

    private void lisaaHakukohdekoodit(Valintaryhma peruskouluVr, Valintaryhma lukioVr,
                                      Laskentakaava pkPeruskaava,
                                      Laskentakaava lkPeruskaava,
                                      Laskentakaava[] pkTasasijakriteerit,
                                      Laskentakaava[] lkTasasijakriteerit,
                                      Laskentakaava kielikoeLaskentakaava) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:hakukohdekoodit/ammatillinenkoulutushakukohdekoodit.csv").getInputStream(), Charset.forName("UTF-8")));

            // Luetaan otsikkorivi pois
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(CSV_DELIMITER);
                String arvo = splitted[0];
                String uri = "hakukohteet_" + arvo;
                String nimi = splitted[1].replace("\"", "");
                String nimiSV = splitted[2].replace("\"", "");

                Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
                hakukohdekoodi.setArvo(arvo);
                hakukohdekoodi.setUri(uri);
                hakukohdekoodi.setNimiFi(nimi);
                hakukohdekoodi.setNimiSv(nimiSV);
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

                ValinnanVaihe valintakoevaihe = valinnanVaiheService.findByValintaryhma(valintaryhma.getOid()).get(1);
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
                valintakoe.setLaskentakaavaId(null);
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
                            valintakoekaava, kielikoeLaskentakaava);
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
        koevalintaryhma.setNimi("Peruskaava ja pääsykoe");
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

        ValinnanVaihe valintakoevaihe = valinnanVaiheet.get(1);
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

        ValinnanVaihe tavallinenVaihe = valinnanVaiheet.get(2);
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
        koe.setNimi("Peruskaava");
        koe.setHakuOid(HAKU_OID);
        koe = valintaryhmaService.insert(koe, valintaryhma.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(2);
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
