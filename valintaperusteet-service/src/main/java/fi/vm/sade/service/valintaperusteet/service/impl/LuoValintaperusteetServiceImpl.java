package fi.vm.sade.service.valintaperusteet.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LuoValintaperuste;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.LuoValintaperusteetService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

/**
 * User: kkammone Date: 25.2.2013 Time: 12:57
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

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActorSystem actorSystem;

    private ResourceLoader resourceLoader;

    private static final String CSV_DELIMITER = ";";

    public static final String PAASY_JA_SOVELTUVUUSKOE = "valintakokeentyyppi_1";
    public static final String LISANAYTTO = "valintakokeentyyppi_2";

    public static final Set<String> poikkeavatValintaryhmat = new HashSet<String>(Arrays.asList(new String[] {
            "hakukohteet_354", // Lasiala, pk (Käsi- ja taideteollisuusalan
                               // perustutkinto)
            "hakukohteet_383", // Lasiala, yo (Käsi- ja taideteollisuusalan
                               // perustutkinto)
            "hakukohteet_483", // Sisustustekstiilit, pk (Käsi- ja
                               // taideteollisuusalan perustutkinto)
            "hakukohteet_485", // Sisustustekstiilit, yo (Käsi- ja
                               // taideteollisuusalan perustutkinto)
            "hakukohteet_538", // Tekstiiliala, pk (Käsi- ja taideteollisuusalan
                               // perustutkinto)
            "hakukohteet_550", // Tekstiiliala, yo (Käsi- ja taideteollisuusalan
                               // perustutkinto)
            "hakukohteet_551", // Vaatetusala, pk (Käsi- ja taideteollisuusalan
                               // perustutkinto)
            "hakukohteet_565", // Vaatetusala, yo (Käsi- ja taideteollisuusalan
                               // perustutkinto)
            "hakukohteet_452", // Audiovisuaalisen viestinnän perustutkinto, pk
            "hakukohteet_511", // Audiovisuaalisen viestinnän perustutkinto, yo
            "hakukohteet_610", // Äänituotanto, pk (Audiovisuaalisen viestinnän
                               // perustutkinto)
            "hakukohteet_611", // Äänituotanto, yo (Audiovisuaalisen viestinnän
                               // perustutkinto)
            "hakukohteet_497", // Kuvallisen ilmaisun perustutkinto, pk
            "hakukohteet_523", // Kuvallisen ilmaisun perustutkinto, yo
            "hakukohteet_126", // Liikunnanohjauksen perustutkinto, yo
    }));

    private final String PAASYKOE_TUNNISTE = "paasykoe_tunniste";
    private final String KIELIKOE_TUNNISTE = "{{hakukohde." + PkJaYoPohjaiset.kielikoetunniste + "}}";
    private final String LISANAYTTO_TUNNISTE = "lisanaytto_tunniste";
    public static final String URHEILIJA_LISAPISTE_TUNNISTE = "{{hakukohde."
            + PkJaYoPohjaiset.urheilijaLisapisteTunniste + "}}";

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

        ammatillinenKoulutusVr = modelMapper.map(valintaryhmaService.insert(ammatillinenKoulutusVr),
                ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(),
                ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO esivalinnanVaihe = new ValinnanVaiheDTO();
        esivalinnanVaihe.setAktiivinen(true);
        esivalinnanVaihe.setKuvaus("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setNimi("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        esivalinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                ammatillinenKoulutusVr.getOid(), esivalinnanVaihe, null), ValinnanVaiheDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintatapajonoDTO esijono = new ValintatapajonoDTO();

        esijono.setAktiivinen(true);
        esijono.setAloituspaikat(0);
        esijono.setKuvaus("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setNimi("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);
        esijono.setSiirretaanSijoitteluun(false);

        esijono = modelMapper.map(
                valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(esivalinnanVaihe.getOid(), esijono, null),
                ValintatapajonoDTO.class);

        JarjestyskriteeriDTO esijk = new JarjestyskriteeriDTO();
        esijk.setAktiivinen(true);
        esijk.setLaskentakaavaId(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId());
        esijk.setMetatiedot(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getNimi());
        modelMapper.map(jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(esijono.getOid(), esijk,
                null, ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId()),
                JarjestyskriteeriDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO kielikoevalinnanVaihe = new ValinnanVaiheDTO();
        kielikoevalinnanVaihe.setAktiivinen(true);
        kielikoevalinnanVaihe.setNimi("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe.setKuvaus("Kielikokeen pakollisuus");
        kielikoevalinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.VALINTAKOE);
        kielikoevalinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                ammatillinenKoulutusVr.getOid(), kielikoevalinnanVaihe, esivalinnanVaihe.getOid()),
                ValinnanVaiheDTO.class);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Laskentakaava kielikokeenLaskentakaava =
        // asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(eiUlkomaillaSuoritettuaKoulutustaEikaOppivelvollisuusKeskeytynyt),
        Laskentakaava kielikokeenLaskentakaava = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava urheilijaLisapisteenMahdollisuusLaskentakaava = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoUrheilijaLisapisteenMahdollisuus(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        final String kielikoeNimi = "Kielikoe";
        ValintakoeDTO kielikoe = new ValintakoeDTO();
        kielikoe.setAktiivinen(true);
        kielikoe.setLahetetaankoKoekutsut(true);
        kielikoe.setKuvaus(kielikoeNimi);
        kielikoe.setNimi(kielikoeNimi);
        kielikoe.setTunniste(KIELIKOE_TUNNISTE);
        kielikoe.setLaskentakaavaId(kielikokeenLaskentakaava.getId());

        valintakoeService.lisaaValintakoeValinnanVaiheelle(kielikoevalinnanVaihe.getOid(), kielikoe);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        final String urheilijaLisapisteNimi = "Urheilijalisäpiste";
        ValintakoeDTO urheilijaLisapiste = new ValintakoeDTO();
        urheilijaLisapiste.setAktiivinen(true);
        urheilijaLisapiste.setLahetetaankoKoekutsut(false);
        urheilijaLisapiste.setKuvaus(urheilijaLisapisteNimi);
        urheilijaLisapiste.setNimi(urheilijaLisapisteNimi);
        urheilijaLisapiste.setTunniste(URHEILIJA_LISAPISTE_TUNNISTE);
        urheilijaLisapiste.setLaskentakaavaId(urheilijaLisapisteenMahdollisuusLaskentakaava.getId());

        valintakoeService.lisaaValintakoeValinnanVaiheelle(kielikoevalinnanVaihe.getOid(), urheilijaLisapiste);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO peruskouluVr = new ValintaryhmaDTO();
        peruskouluVr.setNimi("Peruskoulupohjaiset");

        peruskouluVr = modelMapper.map(valintaryhmaService.insert(peruskouluVr, ammatillinenKoulutusVr.getOid()),
                ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO lukioVr = new ValintaryhmaDTO();
        lukioVr.setNimi("Lukiopohjaiset");

        lukioVr = modelMapper.map(valintaryhmaService.insert(lukioVr, ammatillinenKoulutusVr.getOid()),
                ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : pkAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, peruskouluVr.getOid());

        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava pkPohjainenKaikkienAineidenKeskiarvo = asetaValintaryhmaJaTallennaKantaan(
                PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenKaikkienAineidenKeskiarvo,
                        "Yleinen koulumenestys pisteytysmalli, PK"), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava pk_pohjakoulutuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkPohjaiset.luoPohjakoulutuspisteytysmalli(), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava pk_ilmanKoulutuspaikkaaPisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava hakutoivejarjestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava tyokokemuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Laskentakaava sukupuolipisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoSukupuolipisteytysmalli(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava urheilijanLisapiste = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(PkJaYoPohjaiset.urheilijaLisapisteTunniste),
                ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Pk koostava iso kaava
        Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(
                PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(pk_painotettavatKeskiarvotLaskentakaava,
                        pk_yleinenkoulumenestyspisteytysmalli, pk_pohjakoulutuspisteytysmalli,
                        pk_ilmanKoulutuspaikkaaPisteytysmalli, hakutoivejarjestyspisteytysmalli,
                        tyokokemuspisteytysmalli, sukupuolipisteytysmalli, urheilijanLisapiste), peruskouluVr.getOid());
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : yoAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, lukioVr.getOid());

        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava lk_paattotodistuksenkeskiarvo = asetaValintaryhmaJaTallennaKantaan(
                YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), lukioVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo,
                        "Yleinen koulumenestys pisteytysmalli, LK"), lukioVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(
                YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                        tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli,
                        urheilijanLisapiste), lukioVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Tasasijakriteerit on
        //
        // pk-pohjaiseen ammatilliseen:
        //
        // 1. Hakutoivejärjestys (eli jos on vaikka 1.sijainen ja 2. sijainen
        // hakija samoilla pisteillä, valitaan se 1. sijainen hakija)
        // 2. Mahd. pääsy- ja soveltuvuuskokeen pistemäärä
        // 3. Yleinen koulumenestys (eli se sama kaava josta saa pisteet,
        // kaikkien aineiden keskiarvo)
        // 4. painotettavat arvosanat (tästäkin olemassa kaava)
        // 5. arvonta

        // yo-pohjaisessa samat kriteerit paitsi kohta 4 jää pois kun yo
        // -pohjaisessa ei ole noita painotettavia
        // arvosanoja.

        Laskentakaava hakutoivejarjestystasapistekaava = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava(), ammatillinenKoulutusVr.getOid());

        transactionManager.commit(tx);

        Laskentakaava[] pkTasasijakriteerit = new Laskentakaava[] { hakutoivejarjestystasapistekaava,
                pk_yleinenkoulumenestyspisteytysmalli, pk_painotettavatKeskiarvotLaskentakaava };
        Laskentakaava[] lkTasasijakriteerit = new Laskentakaava[] { hakutoivejarjestystasapistekaava,
                lk_yleinenkoulumenestyspisteytysmalli };

        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Laskentakaava pkYhdistettyPeruskaavaJaKielikoekaava = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(toisenAsteenPeruskoulupohjainenPeruskaava,
                        kielikokeenLaskentakaava), peruskouluVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava lkYhdistettyPeruskaavaJaKielikoekaava = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(toisenAsteenYlioppilaspohjainenPeruskaava,
                        kielikokeenLaskentakaava), peruskouluVr.getOid());
        transactionManager.commit(tx);

        lisaaHakukohdekoodit(peruskouluVr, lukioVr, pkYhdistettyPeruskaavaJaKielikoekaava,
                lkYhdistettyPeruskaavaJaKielikoekaava, pkTasasijakriteerit, lkTasasijakriteerit,
                kielikokeenLaskentakaava);

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

        Laskentakaava painotettuKeskiarvo = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.painotettuLukuaineidenKeskiarvo(), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava hakutoivejarjestystasapistekaava = asetaValintaryhmaJaTallennaKantaan(
                YhteisetKaavat.luoHakutoivejarjestysTasapistekaava("Hakutoivejärjestystasapistetilanne, lukiokoulutus"), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        LukionPkAineet pkAineet = new LukionPkAineet();

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        for (Laskentakaava kaava : pkAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, lukioKoulutusVr.getOid());
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        }

        Laskentakaava keskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.luoKaikkienAineidenKeskiarvo(pkAineet), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava paasykoe = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.paasykoeLukuarvo(PAASYKOE_TUNNISTE), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava lisanaytto = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.lisanayttoLukuarvo(LISANAYTTO_TUNNISTE), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava paasykoeJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.paasykoeJaLisanaytto(paasykoe, lisanaytto), lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Laskentakaava ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt = asetaValintaryhmaJaTallennaKantaan(
                PkJaYoPohjaiset.luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt(),
                lukioKoulutusVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO esivalinnanVaihe = new ValinnanVaiheDTO();
        esivalinnanVaihe.setAktiivinen(true);
        esivalinnanVaihe.setKuvaus("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe.setNimi("Harkinnanvaraisten käsittelyvaihe");
        esivalinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        esivalinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                lukioKoulutusVr.getOid(), esivalinnanVaihe, null), ValinnanVaiheDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintatapajonoDTO esijono = new ValintatapajonoDTO();

        esijono.setAktiivinen(true);
        esijono.setAloituspaikat(0);
        esijono.setKuvaus("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setNimi("Harkinnanvaraisten käsittelyvaiheen valintatapajono");
        esijono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);
        esijono.setSiirretaanSijoitteluun(false);

        esijono = modelMapper.map(
                valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(esivalinnanVaihe.getOid(), esijono, null),
                ValintatapajonoDTO.class);

        JarjestyskriteeriDTO esijk = new JarjestyskriteeriDTO();
        esijk.setAktiivinen(true);
        esijk.setMetatiedot(ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(esijono.getOid(), esijk, null,
                ulkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO paasykoeValinnanVaihe = new ValinnanVaiheDTO();
        paasykoeValinnanVaihe.setAktiivinen(false);
        paasykoeValinnanVaihe.setNimi("Pääsykokeen ja/tai lisäpisteen pakollisuus");
        paasykoeValinnanVaihe.setKuvaus("Pääsykokeen ja/tai lisäpisteen pakollisuus");
        paasykoeValinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.VALINTAKOE);
        paasykoeValinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                lukioKoulutusVr.getOid(), paasykoeValinnanVaihe, esivalinnanVaihe.getOid()), ValinnanVaiheDTO.class);

        ValintakoeDTO valintakoePaasykoe = new ValintakoeDTO();
        valintakoePaasykoe.setNimi("Pääsykoe");
        valintakoePaasykoe.setKuvaus("Pääsykoe");
        valintakoePaasykoe.setAktiivinen(false);
        valintakoePaasykoe.setLahetetaankoKoekutsut(true);
        valintakoePaasykoe.setTunniste("{{hakukohde." + PAASYKOE_TUNNISTE + "}}");

        valintakoeService.lisaaValintakoeValinnanVaiheelle(paasykoeValinnanVaihe.getOid(), valintakoePaasykoe);

        ValintakoeDTO valintakoeLisanaytto = new ValintakoeDTO();
        valintakoeLisanaytto.setNimi("Lisänäyttö");
        valintakoeLisanaytto.setKuvaus("Lisänäyttö");
        valintakoeLisanaytto.setAktiivinen(false);
        valintakoeLisanaytto.setLahetetaankoKoekutsut(false);
        valintakoeLisanaytto.setTunniste("{{hakukohde." + LISANAYTTO_TUNNISTE + "}}");

        valintakoeService.lisaaValintakoeValinnanVaiheelle(paasykoeValinnanVaihe.getOid(), valintakoeLisanaytto);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaiheDTO valinnanVaihe = new ValinnanVaiheDTO();
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setKuvaus("Varsinainen valinnanvaihe");
        valinnanVaihe.setNimi("Varsinainen valinnanvaihe");
        valinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        valinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                lukioKoulutusVr.getOid(), valinnanVaihe, paasykoeValinnanVaihe.getOid()), ValinnanVaiheDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintatapajonoDTO jono = new ValintatapajonoDTO();

        jono.setAktiivinen(true);
        jono.setAloituspaikat(0);
        jono.setKuvaus("Varsinaisen valinnanvaiheen valintatapajono");
        jono.setNimi("Varsinaisen valinnanvaiheen valintatapajono");
        jono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);
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

        painotettuKeskiarvoVr = modelMapper.map(
                valintaryhmaService.insert(painotettuKeskiarvoVr, lukioKoulutusVr.getOid()), ValintaryhmaDTO.class);

        Laskentakaava laskentakaavaPainotettuKeskiarvo = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.painotettuLukuaineidenKeskiarvo(), painotettuKeskiarvoVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoVr.getOid()).get(2);
        Valintatapajono valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid())
                .get(0);
        JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavaPainotettuKeskiarvo.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                laskentakaavaPainotettuKeskiarvo.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(hakutoivejarjestystasapistekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                hakutoivejarjestystasapistekaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(keskiarvotLaskentakaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                keskiarvotLaskentakaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeVr = new ValintaryhmaDTO();
        painotettuKeskiarvoJaPaasykoeVr.setNimi("Painotettu keskiarvo ja paasykoe");
        painotettuKeskiarvoJaPaasykoeVr = modelMapper.map(
                valintaryhmaService.insert(painotettuKeskiarvoJaPaasykoeVr, lukioKoulutusVr.getOid()),
                ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeVr.getOid(),
                paasykoeKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaPaasykoe = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaPaasykoe(painotettuKeskiarvo, paasykoe),
                painotettuKeskiarvoJaPaasykoeVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaPaasykoe.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                laskentakaavapainotettuKeskiarvoJaPaasykoe.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(hakutoivejarjestystasapistekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                hakutoivejarjestystasapistekaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(keskiarvotLaskentakaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                keskiarvotLaskentakaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValinnanVaihe valinnanVaihe0 = valinnanVaiheService
                .findByValintaryhma(painotettuKeskiarvoJaPaasykoeVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(),
                modelMapper.map(valinnanVaihe0, ValinnanVaiheCreateDTO.class));
        List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());
        for (Valintakoe k : valintakokeet) {
            if (k.getTunniste().contains(PAASYKOE_TUNNISTE)) {
                k.setAktiivinen(true);
                ValintakoeDTO dto = new ValintakoeDTO();
                dto.setAktiivinen(true);
                dto.setNimi(k.getNimi());
                dto.setKuvaus(k.getKuvaus());
                dto.setTunniste(k.getTunniste());
                dto.setLaskentakaavaId(k.getLaskentakaavaId());
                dto.setLahetetaankoKoekutsut(true);
                valintakoeService.update(k.getOid(), dto);
            }
        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO painotettuKeskiarvoJaLisanayttoVr = new ValintaryhmaDTO();
        painotettuKeskiarvoJaLisanayttoVr.setNimi("Painotettu keskiarvo ja lisänäyttö");

        painotettuKeskiarvoJaLisanayttoVr = modelMapper.map(
                valintaryhmaService.insert(painotettuKeskiarvoJaLisanayttoVr, lukioKoulutusVr.getOid()),
                ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(painotettuKeskiarvoJaLisanayttoVr.getOid(),
                lisanayttoKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaLisanaytto(painotettuKeskiarvo, lisanaytto),
                painotettuKeskiarvoJaLisanayttoVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaLisanayttoVr.getOid()).get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaLisanaytto.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                laskentakaavapainotettuKeskiarvoJaLisanaytto.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(hakutoivejarjestystasapistekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                hakutoivejarjestystasapistekaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(keskiarvotLaskentakaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                keskiarvotLaskentakaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaLisanayttoVr.getOid()).get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(),
                modelMapper.map(valinnanVaihe0, ValinnanVaiheCreateDTO.class));
        valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());
        for (Valintakoe k : valintakokeet) {
            if (k.getTunniste().contains(LISANAYTTO_TUNNISTE)) {
                k.setAktiivinen(true);
                ValintakoeDTO dto = new ValintakoeDTO();
                dto.setAktiivinen(true);
                dto.setNimi(k.getNimi());
                dto.setKuvaus(k.getKuvaus());
                dto.setTunniste(k.getTunniste());
                dto.setLaskentakaavaId(k.getLaskentakaavaId());
                dto.setLahetetaankoKoekutsut(false);
                valintakoeService.update(k.getOid(), dto);
            }
        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = new ValintaryhmaDTO();
        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.setNimi("Painotettu keskiarvo, pääsykoe ja lisänäyttö");

        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = modelMapper.map(
                valintaryhmaService.insert(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr, lukioKoulutusVr.getOid()),
                ValintaryhmaDTO.class);

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(
                painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), lisanayttoKoodi);
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(
                painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), paasykoeKoodi);

        Laskentakaava laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto = asetaValintaryhmaJaTallennaKantaan(
                LukionValintaperusteet.painotettuLukuaineidenKeskiarvoJaPaasykoeJaLisanaytto(painotettuKeskiarvo,
                        paasykoeJaLisanaytto), painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe1 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid())
                .get(2);
        valintatapajono = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe1.getOid()).get(0);

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                laskentakaavapainotettuKeskiarvoJaPaasykoeJaLisanaytto.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(hakutoivejarjestystasapistekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                hakutoivejarjestystasapistekaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        jk = new JarjestyskriteeriDTO();
        jk.setAktiivinen(true);
        jk.setMetatiedot(keskiarvotLaskentakaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajono.getOid(), jk, null,
                keskiarvotLaskentakaava.getId());

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        valinnanVaihe0 = valinnanVaiheService.findByValintaryhma(painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid())
                .get(1);
        valinnanVaihe0.setAktiivinen(true);
        valinnanVaiheService.update(valinnanVaihe0.getOid(),
                modelMapper.map(valinnanVaihe0, ValinnanVaiheCreateDTO.class));
        valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valinnanVaihe0.getOid());

        for (Valintakoe k : valintakokeet) {
            k.setAktiivinen(true);
            ValintakoeDTO dto = new ValintakoeDTO();
            dto.setAktiivinen(true);
            dto.setNimi(k.getNimi());
            dto.setKuvaus(k.getKuvaus());
            dto.setTunniste(k.getTunniste());
            dto.setLaskentakaavaId(k.getLaskentakaavaId());
            if (k.getTunniste().contains(PAASYKOE_TUNNISTE)) {
                dto.setLahetetaankoKoekutsut(true);
            } else {
                dto.setLahetetaankoKoekutsut(false);
            }
            valintakoeService.update(k.getOid(), dto);
        }

        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource(
                    "classpath:hakukohdekoodit/lukiohakukohdekoodit.csv").getInputStream(), Charset.forName("UTF-8")));
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

                hakukohdekoodiService
                        .lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoVr.getOid(), hakukohdekoodi);
                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoJaLisanayttoVr.getOid(),
                        hakukohdekoodi);
                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(painotettuKeskiarvoJaPaasykoeVr.getOid(),
                        hakukohdekoodi);
                hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(
                        painotettuKeskiarvoJaPaasykoeJaLisanayttoVr.getOid(), hakukohdekoodi);

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
            Laskentakaava pkPeruskaava, Laskentakaava lkPeruskaava, Laskentakaava[] pkTasasijakriteerit,
            Laskentakaava[] lkTasasijakriteerit, Laskentakaava kielikoeLaskentakaava) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource(
                    "classpath:hakukohdekoodit/ammatillinenkoulutushakukohdekoodit.csv").getInputStream(),
                    Charset.forName("UTF-8")));

            SpringExtProvider.get(actorSystem).initialize(applicationContext);

            // Luetaan otsikkorivi pois
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(CSV_DELIMITER);
                String arvo = splitted[0];
                String uri = "hakukohteet_" + arvo;
                String nimi = splitted[1].replace("\"", "");
                String nimiSV = splitted[2].replace("\"", "");

                LuoValintaperuste peruste = new LuoValintaperuste(arvo, uri, nimi, nimiSV, nimi,
                        peruskouluVr.getOid(), lukioVr.getOid(), pkPeruskaava, pkTasasijakriteerit,
                        lkPeruskaava, lkTasasijakriteerit, kielikoeLaskentakaava);

                ActorRef master = actorSystem.actorOf(
                        SpringExtProvider.get(actorSystem).props("LuoValintaperusteetActorBean").withDispatcher("thread-pool-dispatcher"), UUID.randomUUID()
                        .toString());
                master.tell(peruste, ActorRef.noSender());


            }
        } finally {
            if (reader != null) {
                reader.close();
            }

        }
    }


    private Laskentakaava asetaValintaryhmaJaTallennaKantaan(Laskentakaava kaava, String valintaryhmaOid) {
        Laskentakaava laskentakaava = laskentakaavaService.insert(kaava, null, valintaryhmaOid);
        return laskentakaava;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
