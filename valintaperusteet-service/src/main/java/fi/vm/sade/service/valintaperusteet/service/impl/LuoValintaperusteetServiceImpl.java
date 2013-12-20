package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
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

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

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
        ValintaryhmaDTO ammatillinenKoulutusVr = new ValintaryhmaDTO();
        ammatillinenKoulutusVr.setNimi("Ammatillinen koulutus");

        ammatillinenKoulutusVr = modelMapper.map(valintaryhmaService.insert(ammatillinenKoulutusVr), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());


        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO esivalinnanVaihe = new ValinnanVaiheDTO();
        esivalinnanVaihe.setAktiivinen(true);
        esivalinnanVaihe.setKuvaus("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setNimi("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        esivalinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(ammatillinenKoulutusVr.getOid(), esivalinnanVaihe,
                null), ValinnanVaiheDTO.class);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintatapajonoDTO esijono = new ValintatapajonoDTO();

        esijono.setAktiivinen(true);
        esijono.setAloituspaikat(0);
        esijono.setKuvaus("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setNimi("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        esijono.setSiirretaanSijoitteluun(false);

        esijono = modelMapper.map(valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(esivalinnanVaihe.getOid(), esijono, null), ValintatapajonoDTO.class);

        JarjestyskriteeriDTO esijk = new JarjestyskriteeriDTO();
        esijk.setAktiivinen(true);
        esijk.setLaskentakaavaId(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId());
        esijk.setMetatiedot(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getNimi());
        esijk = modelMapper.map(jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(esijono.getOid(), esijk, null, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId()), JarjestyskriteeriDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO kielikoevalinnanVaihe = new ValinnanVaiheDTO();
        kielikoevalinnanVaihe.setAktiivinen(true);
        kielikoevalinnanVaihe.setNimi("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setKuvaus("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
        kielikoevalinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(ammatillinenKoulutusVr.getOid(), kielikoevalinnanVaihe, esivalinnanVaihe.getOid()), ValinnanVaiheDTO.class);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        //Laskentakaava kielikokeenLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt),
        Laskentakaava kielikokeenLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(),
                ammatillinenKoulutusVr.getOid());

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

        ValintaryhmaDTO peruskouluVr = new ValintaryhmaDTO();
        peruskouluVr.setNimi("Peruskoulupohjaiset");

        peruskouluVr = modelMapper.map(valintaryhmaService.insert(peruskouluVr, ammatillinenKoulutusVr.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO lukioVr = new ValintaryhmaDTO();
        lukioVr.setNimi("Lukiopohjaiset");

        lukioVr = modelMapper.map(valintaryhmaService.insert(lukioVr, ammatillinenKoulutusVr.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : pkAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, peruskouluVr.getOid());
        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        //pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet), peruskouluVr.getOid());
        Laskentakaava pkPohjainenKaikkienAineidenKeskiarvo = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        //pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenKaikkienAineidenKeskiarvo, "Yleinen koulumenestys pisteytysmalli, PK"), peruskouluVr.getOid());
        Laskentakaava pk_pohjakoulutuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPohjakoulutuspisteytysmalli(), peruskouluVr.getOid());
        Laskentakaava pk_ilmanKoulutuspaikkaaPisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(), peruskouluVr.getOid());
        Laskentakaava hakutoivejarjestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(), ammatillinenKoulutusVr.getOid());
        Laskentakaava tyokokemuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(), ammatillinenKoulutusVr.getOid());
        Laskentakaava sukupuolipisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoSukupuolipisteytysmalli(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Pk koostava iso kaava
        Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(pk_painotettavatKeskiarvotLaskentakaava,
                pk_yleinenkoulumenestyspisteytysmalli, pk_pohjakoulutuspisteytysmalli, pk_ilmanKoulutuspaikkaaPisteytysmalli, hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli), peruskouluVr.getOid());
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : yoAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, lukioVr.getOid());
        }
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava lk_paattotodistuksenkeskiarvo = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), lukioVr.getOid());
        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"), lukioVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli), lukioVr.getOid());
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

        Laskentakaava hakutoivejarjestystasapistekaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);

        Laskentakaava[] pkTasasijakriteerit = new Laskentakaava[]{hakutoivejarjestystasapistekaava, pk_yleinenkoulumenestyspisteytysmalli, pk_painotettavatKeskiarvotLaskentakaava};
        Laskentakaava[] lkTasasijakriteerit = new Laskentakaava[]{hakutoivejarjestystasapistekaava, lk_yleinenkoulumenestyspisteytysmalli};

        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Laskentakaava pkYhdistettyPeruskaavaJaKielikoekaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                toisenAsteenPeruskoulupohjainenPeruskaava, kielikokeenLaskentakaava), peruskouluVr.getOid());

        Laskentakaava lkYhdistettyPeruskaavaJaKielikoekaava = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                toisenAsteenYlioppilaspohjainenPeruskaava, kielikokeenLaskentakaava), peruskouluVr.getOid());
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
        ValintaryhmaDTO lukioKoulutusVr = new ValintaryhmaDTO();
        lukioKoulutusVr.setNimi("Lukiokoulutus");

        lukioKoulutusVr = modelMapper.map(valintaryhmaService.insert(lukioKoulutusVr), ValintaryhmaDTO.class);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava painotettuKeskiarvo = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo(), lukioKoulutusVr.getOid());
        Laskentakaava paasykoe = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.paasykoeLukuarvo(PAASYKOE_TUNNISTE), lukioKoulutusVr.getOid());
        Laskentakaava lisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.lisanayttoLukuarvo(LISANAYTTO_TUNNISTE), lukioKoulutusVr.getOid());
        Laskentakaava paasykoeJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.paasykoeJaLisanaytto(paasykoe, lisanaytto), lukioKoulutusVr.getOid());
        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO esivalinnanVaihe = new ValinnanVaiheDTO();
        esivalinnanVaihe.setAktiivinen(true);
        esivalinnanVaihe.setKuvaus("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setNimi("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        esivalinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(lukioKoulutusVr.getOid(), esivalinnanVaihe,
                null), ValinnanVaiheDTO.class);


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintatapajonoDTO esijono = new ValintatapajonoDTO();

        esijono.setAktiivinen(true);
        esijono.setAloituspaikat(0);
        esijono.setKuvaus("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setNimi("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        esijono.setSiirretaanSijoitteluun(false);

        esijono = modelMapper.map(valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(esivalinnanVaihe.getOid(), esijono, null), ValintatapajonoDTO.class);

        JarjestyskriteeriDTO esijk = new JarjestyskriteeriDTO();
        esijk.setAktiivinen(true);
        esijk.setMetatiedot(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(esijono.getOid(), esijk, null, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO paasykoeValinnanVaihe = new ValinnanVaiheDTO();
        paasykoeValinnanVaihe.setAktiivinen(false);
        paasykoeValinnanVaihe.setNimi("Pääsykokeen ja/tai lisäpisteen pakollisuus");
        paasykoeValinnanVaihe.setKuvaus("Pääsykokeen ja/tai lisäpisteen pakollisuus");
        paasykoeValinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.VALINTAKOE);
        paasykoeValinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(lukioKoulutusVr.getOid(), paasykoeValinnanVaihe, esivalinnanVaihe.getOid()), ValinnanVaiheDTO.class);

        ValintakoeDTO valintakoePaasykoe = new ValintakoeDTO();
        valintakoePaasykoe.setNimi("Pääsykoe");
        valintakoePaasykoe.setKuvaus("Pääsykoe");
        valintakoePaasykoe.setAktiivinen(false);
        valintakoePaasykoe.setTunniste("{{hakukohde." + PAASYKOE_TUNNISTE + "}}");

        valintakoeService.lisaaValintakoeValinnanVaiheelle(paasykoeValinnanVaihe.getOid(), valintakoePaasykoe);

        ValintakoeDTO valintakoeLisanaytto = new ValintakoeDTO();
        valintakoeLisanaytto.setNimi("Lisänäyttö");
        valintakoeLisanaytto.setKuvaus("Lisänäyttö");
        valintakoeLisanaytto.setAktiivinen(false);
        valintakoeLisanaytto.setTunniste("{{hakukohde." + LISANAYTTO_TUNNISTE + "}}");

        valintakoeService.lisaaValintakoeValinnanVaiheelle(paasykoeValinnanVaihe.getOid(), valintakoeLisanaytto);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO valinnanVaihe = new ValinnanVaiheDTO();
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setKuvaus("Varsinainen valinnanvaihe");
        valinnanVaihe.setNimi("Varsinainen valinnanvaihe");
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        valinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(lukioKoulutusVr.getOid(), valinnanVaihe,
                paasykoeValinnanVaihe.getOid()), ValinnanVaiheDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintatapajonoDTO jono = new ValintatapajonoDTO();

        jono.setAktiivinen(true);
        jono.setAloituspaikat(0);
        jono.setKuvaus("Varsinaisen valinnanvaiheen valintatapajono");
        jono.setNimi("Varsinaisen valinnanvaiheen valintatapajono");
        jono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        jono.setSiirretaanSijoitteluun(true);

        valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaihe.getOid(), jono, null);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        KoodiDTO paasykoeKoodi = new KoodiDTO();
        paasykoeKoodi.setUri(PAASY_JA_SOVELTUVUUSKOE);

        KoodiDTO lisanayttoKoodi = new KoodiDTO();
        lisanayttoKoodi.setUri(LISANAYTTO);

        ValintaryhmaDTO painotettuKeskiarvoVr = new ValintaryhmaDTO();
        painotettuKeskiarvoVr.setNimi("Painotettu keskiarvo");

        painotettuKeskiarvoVr = modelMapper.map(valintaryhmaService.insert(painotettuKeskiarvoVr, lukioKoulutusVr.getOid()), ValintaryhmaDTO.class);

        Laskentakaava laskentakaavaPainotettuKeskiarvo = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo(), painotettuKeskiarvoVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoVr.getOid()).get(2);
        Valintatapajono valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavaPainotettuKeskiarvo.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavaPainotettuKeskiarvo.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());


        ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeVr = new ValintaryhmaDTO();
        painotettuKeskiarvoJaPaasykoeVr.setNimi("Painotettu keskiarvo ja paasykoe");
        painotettuKeskiarvoJaPaasykoeVr = modelMapper.map(valintaryhmaService.insert(painotettuKeskiarvoJaPaasykoeVr, lukioKoulutusVr.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeVr.getOid(), paasykoeKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaPaasykoe = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaPaasykoe(painotettuKeskiarvo, paasykoe), painotettuKeskiarvoJaPaasykoeVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaPaasykoe.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavapainotettuKeskiarvoJaPaasykoe.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(), modelMapper.map(valinnanVaihe0, ValinnanVaiheCreateDTO.class));
        List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());
        for(Valintakoe k : valintakokeet) {
            if(k.getTunniste().contains(PAASYKOE_TUNNISTE)) {
                k.setAktiivinen(true);
                ValintakoeDTO dto = new ValintakoeDTO();
                dto.setAktiivinen(true);
                dto.setNimi(k.getNimi());
                dto.setKuvaus(k.getKuvaus());
                dto.setTunniste(k.getTunniste());
                dto.setLaskentakaavaId(k.getLaskentakaavaId());
                valintakoeService.update(k.getOid(), dto);
            }
        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO painotettuKeskiarvoJaLisanayttoVr = new ValintaryhmaDTO();
        painotettuKeskiarvoJaLisanayttoVr.setNimi("Painotettu keskiarvo ja lisänäyttö");

        painotettuKeskiarvoJaLisanayttoVr = modelMapper.map(valintaryhmaService.insert(painotettuKeskiarvoJaLisanayttoVr, lukioKoulutusVr.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaLisanayttoVr.getOid(), lisanayttoKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaLisanaytto(painotettuKeskiarvo, lisanaytto), painotettuKeskiarvoJaLisanayttoVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaLisanayttoVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaLisanaytto.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavapainotettuKeskiarvoJaLisanaytto.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaLisanayttoVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(), modelMapper.map(valinnanVaihe0, ValinnanVaiheCreateDTO.class));
        valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());
        for(Valintakoe k : valintakokeet) {
            if(k.getTunniste().contains(LISANAYTTO_TUNNISTE)) {
                k.setAktiivinen(true);
                ValintakoeDTO dto = new ValintakoeDTO();
                dto.setAktiivinen(true);
                dto.setNimi(k.getNimi());
                dto.setKuvaus(k.getKuvaus());
                dto.setTunniste(k.getTunniste());
                dto.setLaskentakaavaId(k.getLaskentakaavaId());
                valintakoeService.update(k.getOid(), dto);
            }
        }


        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = new ValintaryhmaDTO();
        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.setNimi("Painotettu keskiarvo, pääsykoe ja lisänäyttö");

        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = modelMapper.map(valintaryhmaService.insert(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr, lukioKoulutusVr.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), lisanayttoKoodi);
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), paasykoeKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaPaasykoeJaLisanaytto(painotettuKeskiarvo, paasykoeJaLisanaytto), painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);
        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null, laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(), modelMapper.map(valinnanVaihe0, ValinnanVaiheCreateDTO.class));
        valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());

        for(Valintakoe k : valintakokeet) {
            k.setAktiivinen(true);
            ValintakoeDTO dto = new ValintakoeDTO();
            dto.setAktiivinen(true);
            dto.setNimi(k.getNimi());
            dto.setKuvaus(k.getKuvaus());
            dto.setTunniste(k.getTunniste());
            dto.setLaskentakaavaId(k.getLaskentakaavaId());
            valintakoeService.update(k.getOid(), dto);
        }


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

                KoodiDTO hakukohdekoodi = new KoodiDTO();
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

    private void lisaaHakukohdekoodit(ValintaryhmaDTO peruskouluVr, ValintaryhmaDTO lukioVr,
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

                KoodiDTO hakukohdekoodi = new KoodiDTO();
                hakukohdekoodi.setArvo(arvo);
                hakukohdekoodi.setUri(uri);
                hakukohdekoodi.setNimiFi(nimi);
                hakukohdekoodi.setNimiSv(nimiSV);
                hakukohdekoodi.setNimiEn(nimi);


                ValintaryhmaDTO valintaryhma = new ValintaryhmaDTO();

                valintaryhma.setNimi(nimi);

                TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                if (nimi.contains(", pk")) {
                    valintaryhma = modelMapper.map(valintaryhmaService.insert(valintaryhma, peruskouluVr.getOid()), ValintaryhmaDTO.class);
                } else {
                    valintaryhma = modelMapper.map(valintaryhmaService.insert(valintaryhma, lukioVr.getOid()), ValintaryhmaDTO.class);
                }

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                ValinnanVaihe valintakoevaihe = valinnanVaiheService.findByValintaryhma(valintaryhma.getOid()).get(1);
                assert (valintakoevaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.VALINTAKOE));
                valintakoevaihe.setNimi("Kielikokeen pakollisuus ja pääsykoe");
                valintakoevaihe.setKuvaus("Kielikokeen pakollisuus ja pääsykoe");
                valintakoevaihe = valinnanVaiheService.update(valintakoevaihe.getOid(), modelMapper.map(valintakoevaihe, ValinnanVaiheCreateDTO.class));

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

                ValinnanVaiheDTO valinnanVaihe = new ValinnanVaiheDTO();
                valinnanVaihe.setAktiivinen(true);
                valinnanVaihe.setKuvaus("Varsinainen valinnanvaihe");
                valinnanVaihe.setNimi("Varsinainen valinnanvaihe");
                valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

                valinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhma.getOid(), valinnanVaihe,
                        valintakoevaihe.getOid()), ValinnanVaiheDTO.class);

                transactionManager.commit(tx);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

                ValintatapajonoDTO jono = new ValintatapajonoDTO();

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
                        PkJaYoPohjaiset.luoValintakoekaava(valintakoetunniste), valintaryhma.getOid());

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

    private void insertKoe(ValintaryhmaDTO valintaryhma, String valintakoetunniste, Laskentakaava peruskaavaJaValintakoekaava, Laskentakaava valintakoekaava,
                           Laskentakaava[] tasasijakriteerit, KoodiDTO hakukohdekoodi) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO koevalintaryhma = new ValintaryhmaDTO();
        koevalintaryhma.setNimi("Peruskaava ja pääsykoe");

        koevalintaryhma = modelMapper.map(valintaryhmaService.insert(koevalintaryhma, valintaryhma.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koevalintaryhma.getOid(), hakukohdekoodi);
        peruskaavaJaValintakoekaava = asetaValintaryhmaJaTallennaKantaan(peruskaavaJaValintakoekaava, koevalintaryhma.getOid());

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

        KoodiDTO valintakoekoodi = new KoodiDTO();
        valintakoekoodi.setUri(PAASY_JA_SOVELTUVUUSKOE);

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(koevalintaryhma.getOid(), valintakoekoodi);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe tavallinenVaihe = valinnanVaiheet.get(2);
        assert (tavallinenVaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(tavallinenVaihe.getOid()).get(0);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        JarjestyskriteeriDTO kriteeri = new JarjestyskriteeriDTO();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaavaJaValintakoekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null,
                peruskaavaJaValintakoekaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            if (i == 1) {
                JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
                jk.setAktiivinen(true);
                jk.setMetatiedot(valintakoekaava.getNimi());
                jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, valintakoekaava.getId());
            }

            Laskentakaava kaava = tasasijakriteerit[i];
            JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }

        transactionManager.commit(tx);
    }

    private void insertEiKoetta(ValintaryhmaDTO valintaryhma, Laskentakaava peruskaava,
                                Laskentakaava[] tasasijakriteerit, KoodiDTO hakukohdekoodi) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO koe = new ValintaryhmaDTO();
        koe.setNimi("Peruskaava");

        koe = modelMapper.map(valintaryhmaService.insert(koe, valintaryhma.getOid()), ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(2);
        assert (vaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid()).get(0);

        JarjestyskriteeriDTO kriteeri = new JarjestyskriteeriDTO();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, peruskaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            Laskentakaava kaava = tasasijakriteerit[i];
            JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }
        transactionManager.commit(tx);
    }


    private Laskentakaava asetaValintaryhmaJaTallennaKantaan(Laskentakaava kaava, String valintaryhmaOid) {
        return laskentakaavaService.insert(kaava, null, valintaryhmaOid);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
