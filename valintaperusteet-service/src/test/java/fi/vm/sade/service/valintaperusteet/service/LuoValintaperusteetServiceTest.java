package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.kaava.Laskentadomainkonvertteri;
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus;
import fi.vm.sade.service.valintaperusteet.laskenta.api.LaskentaService;
import fi.vm.sade.service.valintaperusteet.laskenta.api.Laskentatulos;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.service.impl.LuoValintaperusteetServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: kwuoti Date: 5.3.2013 Time: 16.02
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LuoValintaperusteetServiceTest {

    @Autowired
    private LuoValintaperusteetService luoValintaperusteetService;

    @Autowired
    private LaskentaService laskentaService;

    @Test
    @Ignore
    public void testLuo() throws IOException {
        luoValintaperusteetService.luo();
    }

    private PkAineet pkAineet = new PkAineet();
    private YoAineet yoAineet = new YoAineet();

    private static final String HAKEMUS_OID = "hakemusOid";
    private static final String HAKUKOHDE_OID1 = "1.2.246.562.5.10095_02_186_0632";
    private static final String HAKUKOHDE_OID2 = "1.2.246.562.5.01403_01_186_1027";
    private static final String HAKUKOHDE_OID3 = "1.2.246.562.5.01787_01_406_1042";
    private static final String HAKUKOHDE_OID4 = "1.2.246.562.5.01787_01_406_1043";
    private static final String HAKUKOHDE_OID5 = "1.2.246.562.5.01787_01_406_1044";

    private static final Map<Integer, String> hakutoiveet;

    static {
        hakutoiveet = new HashMap<Integer, String>();
        hakutoiveet.put(1, HAKUKOHDE_OID1);
        hakutoiveet.put(2, HAKUKOHDE_OID2);
        hakutoiveet.put(3, HAKUKOHDE_OID3);
        hakutoiveet.put(4, HAKUKOHDE_OID4);
        hakutoiveet.put(5, HAKUKOHDE_OID5);
    }

    private Map<String, Object> newMap() {
        return new HashMap<String, Object>();
    }

    private Map<String, Object> yhdistaMapit(Map<String, Object>... maps) {
        Map<String, Object> map = newMap();
        for (Map<String, Object> m : maps) {
            map.putAll(m);
        }

        return map;
    }

    private Map<String, Object> valintaperuste(String avain, Object arvo) {
        Map<String, Object> map = newMap();
        map.put(avain, arvo);
        return map;
    }

    private Map<String, Object> pakollinenPkAine(String aine, Object arvo) {
        Map<String, Object> map = newMap();
        map.put(PkAineet.pakollinen(aine), arvo);
        return map;
    }

    private Map<String, Object> pakollinenPkAineJaValinnaiset(String aine, Object pakollinenArvo, Object val1Arvo,
                                                              Object val2Arvo) {
        Map<String, Object> map = newMap();
        map.putAll(pakollinenPkAine(aine, pakollinenArvo));
        map.put(PkAineet.valinnainen1(aine), val1Arvo);
        map.put(PkAineet.valinnainen2(aine), val2Arvo);
        return map;
    }

    private Map<String, Object> lkAine(String aine, Object arvo) {
        Map<String, Object> map = newMap();
        map.put(YoAineet.pakollinen(aine), arvo);
        return map;
    }

    private Hakemus hakemus(Map<String, Object> kentat) {
        Map<String, String> h = new HashMap<String, String>();

        for (Map.Entry<String, Object> e : kentat.entrySet()) {
            h.put(e.getKey(), e.getValue().toString());
        }

        return new Hakemus(HAKEMUS_OID, hakutoiveet, h);
    }

    private List<Hakemus> hakemukset(Hakemus... hs) {
        List<Hakemus> h = new ArrayList<Hakemus>();

        for (Hakemus hak : hs) {
            h.add(hak);
        }

        return h;
    }

    private Laskentakaava laajennaAlakaavat(Laskentakaava lk) {
        laajennaAlakaavat(lk.getFunktiokutsu());
        return lk;
    }

    private void laajennaAlakaavat(Funktiokutsu fk) {
        if (fk != null) {
            for (Funktioargumentti fa : fk.getFunktioargumentit()) {
                if (fa.getLaskentakaavaChild() != null) {
                    fa.setFunktiokutsuChild(fa.getLaskentakaavaChild().getFunktiokutsu());
                    fa.setLaskentakaavaChild(null);
                }

                laajennaAlakaavat(fa.getFunktiokutsuChild());
            }
        }
    }

    private Map<String, Object> luoPkAineet() {
        return yhdistaMapit(
                pakollinenPkAineJaValinnaiset(Aineet.aidinkieliJaKirjallisuus1, 5.0, 7.0, 6.0), // 5.75
                pakollinenPkAineJaValinnaiset(Aineet.aidinkieliJaKirjallisuus2, 8.0, 9.0, 8.0), // 8.25
                pakollinenPkAine(Aineet.historia, 7.0), // 7.0
                pakollinenPkAine(Aineet.yhteiskuntaoppi, 8.0), // 8.0
                pakollinenPkAine(Aineet.matematiikka, 7.0), // 7.0
                pakollinenPkAineJaValinnaiset(Aineet.fysiikka, 8.0, 9.0, 9.0), // 8.5
                pakollinenPkAine(Aineet.kemia, 7.0), // 7.0
                pakollinenPkAine(Aineet.biologia, 5.0), // 5.0
                pakollinenPkAine(Aineet.kuvataide, 8.0), // 8.0
                pakollinenPkAineJaValinnaiset(PkAineet.musiikki, 9.0, 9.0, 10.0), // 9.25
                pakollinenPkAine(Aineet.maantieto, 7.0), // 7.0
                pakollinenPkAineJaValinnaiset(PkAineet.kasityo, 7.0, 7.0, 8.0), // 7.25
                pakollinenPkAine(PkAineet.kotitalous, 6.0), // 6.0
                pakollinenPkAine(Aineet.liikunta, 8.0), // 8.0
                pakollinenPkAine(Aineet.terveystieto, 9.0), // 9.0
                pakollinenPkAine(Aineet.uskonto, 5.0), // 5.0
                pakollinenPkAine(Aineet.a11Kieli, 10.0), // 10.0
                pakollinenPkAine(Aineet.b1Kieli, 8.0), // 8.0
                pakollinenPkAine(Aineet.b31Kieli, 7.0) // 7.0
        );
    }

    private Map<String, Object> luoLkAineet() {
        return yhdistaMapit(lkAine(Aineet.aidinkieliJaKirjallisuus1, 7.0),
                lkAine(Aineet.aidinkieliJaKirjallisuus2, 8.0), lkAine(Aineet.historia, 9.0),
                lkAine(Aineet.yhteiskuntaoppi, 6.0), lkAine(Aineet.matematiikka, 7.0), lkAine(Aineet.fysiikka, 7.0),
                lkAine(Aineet.kemia, 6.0), lkAine(Aineet.biologia, 9.0), lkAine(Aineet.kuvataide, 10.0),
                lkAine(Aineet.musiikki, 5.0), lkAine(Aineet.maantieto, 7.0), lkAine(YoAineet.filosofia, 9.0),
                lkAine(Aineet.liikunta, 8.0), lkAine(Aineet.terveystieto, 5.0), lkAine(Aineet.uskonto, 9.0),
                lkAine(Aineet.a11Kieli, 10.0), lkAine(Aineet.b1Kieli, 7.0), lkAine(Aineet.b31Kieli, 6.0));
    }

    private Hakemus luoPerushakemus() {
        Hakemus hakemus = hakemus(luoPkAineet());

        return hakemus;
    }

    @Test
    public void testPkPainotettavatKeskiarvot() {
        Hakemus hakemus = luoPerushakemus();

        // Musiikki: 9.25
        // Kuvaamataito: 8.0
        // Käsityö: 7.25
        // Kotitalous: 6.0
        // Liikunta: 8.0

        // Kolmen parhaan keskiarvo: (9.25 + 8.0 + 7.25) / 3 = 8.16667
        // Asettuu välille 8.0 - 8.5 ===> 5 pistettä
        final BigDecimal odotettuTulos = new BigDecimal("5.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPkPohjainenLukuaineidenKeskiarvo() {
        Hakemus hakemus = luoPerushakemus();

        // Kaikkien aineiden keskiarvo: 7.421

        final BigDecimal odotettuTulos = new BigDecimal("7.4211");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pkAineet));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    public Laskentakaava luoVakiokaava(BigDecimal arvo) {
        Funktiokutsu funktiokutsu = GenericHelper.luoLukuarvo(arvo.doubleValue());
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(funktiokutsu, "vakio");
    }

    @Test
    public void testPkYleinenKoulumenestysPisteytysmalliRajaarvoMin() {
        Hakemus hakemus = luoPerushakemus();

        final BigDecimal odotettuTulos = new BigDecimal("7.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                luoVakiokaava(new BigDecimal(7.0)), "nimi"));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPkYleinenKoulumenestysPisteytysmalliRajaarvoMax() {
        Hakemus hakemus = luoPerushakemus();

        final BigDecimal odotettuTulos = new BigDecimal("8.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                luoVakiokaava(new BigDecimal(7.25)), "nimi"));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPkYleinenKoulumenestysPisteytysmalli() {
        Hakemus hakemus = luoPerushakemus();

        // Kaikkien aineiden keskiarvo: 7.421
        // Asettuu välille 7.25 - 7.50 ==> 8 pistettä

        final BigDecimal odotettuTulos = new BigDecimal("8.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                PkPohjaiset.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pkAineet), "nimi"));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPkPohjakoulutusPisteytysmalliPohjakoulutusOnPerusopetus() {
        Hakemus hakemus = hakemus(yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi)));

        final BigDecimal odotettuTulos = new BigDecimal("6.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPkPohjakoulutusPisteytysmalliLisapistekoulutusSuoritettu() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkPohjaiset.kymppiluokka, Boolean.TRUE.toString())));

        final BigDecimal odotettuTulos = new BigDecimal("6.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPkPohjakoulutusPisteytysmalliPohjakoulutusMuu() {
        Hakemus hakemus = hakemus(yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.lukionPaattotodistus),
                valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi)));

        final BigDecimal odotettuTulos = new BigDecimal("0.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testIlmanKoulutuspaikkaaPisteytysmalliOnKoulutuspaikka() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon,
                Boolean.TRUE.toString())));

        final BigDecimal odotettuTulos = new BigDecimal("0.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testIlmanKoulutuspaikkaaPisteytysmalliEiOleKoulutuspaikkaa() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon,
                Boolean.FALSE.toString())));

        final BigDecimal odotettuTulos = new BigDecimal("8.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestysPisteytysmalliEnsisijainenHakutoive() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("2.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestysPisteytysmalliToissijainenHakutoive() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("0.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID2, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testTyokokemusPisteytysmalliAlleKolme() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 2.0)));

        final BigDecimal odotettuTulos = new BigDecimal("0.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testTyokokemusPisteytysmalliKolme() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 3.0)));

        final BigDecimal odotettuTulos = new BigDecimal("1.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testTyokokemusPisteytysmalliViisi() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 5.0)));

        final BigDecimal odotettuTulos = new BigDecimal("1.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testTyokokemusPisteytysmalliKuusi() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 6.0)));

        final BigDecimal odotettuTulos = new BigDecimal("2.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testTyokokemusPisteytysmalliYksitoista() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 11.0)));

        final BigDecimal odotettuTulos = new BigDecimal("2.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testTyokokemusPisteytysmalliKaksitoista() {
        Hakemus hakemus = hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 12.0)));

        final BigDecimal odotettuTulos = new BigDecimal("3.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testSukupuolipisteytysmalliAlle30Prosenttia() {
        Hakemus[] hakemukset = new Hakemus[]{hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n")))};

        final BigDecimal odotettuTulos = new BigDecimal("2.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemukset[0],
                Arrays.asList(hakemukset), Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()),
                new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testSukupuolipisteytysmalliYli30Prosenttia() {
        Hakemus[] hakemukset = new Hakemus[]{hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n")))};

        final BigDecimal odotettuTulos = new BigDecimal("0.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemukset[0],
                Arrays.asList(hakemukset), Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()),
                new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testToisenAsteenPeruskoulupohjainenPeruskaava() {
        Hakemus[] hakemukset = new Hakemus[]{
                hakemus(yhdistaMapit(
                        luoPkAineet(),
                        valintaperuste(PkPohjaiset.pohjakoulutusAvain,
                                PkPohjaiset.perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara),
                        valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi),
                        valintaperuste(PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon, Boolean.FALSE.toString()),
                        valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 7),
                        valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),};

        final BigDecimal odotettuTulos = new BigDecimal("31.0");

        Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet),
                PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                        PkPohjaiset.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pkAineet), "nimi"),
                PkPohjaiset.luoPohjakoulutuspisteytysmalli(), PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(),
                PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(), PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
                PkJaYoPohjaiset.luoSukupuolipisteytysmalli()));
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemukset[0],
                Arrays.asList(hakemukset), Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()),
                new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testLkPaattotodistuksenKeskiarvo() {
        Hakemus hakemus = hakemus(yhdistaMapit(luoLkAineet()));

        // Kaikkien aineiden keskiarvo: 7.5
        final BigDecimal odotettuTulos = new BigDecimal("7.5");

        Laskentakaava kaava = laajennaAlakaavat(YoPohjaiset
                .luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos.compareTo(tulos.getTulos()), 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testLkYleinenKoulumenestyspisteytysmalli() {
        Hakemus hakemus = hakemus(yhdistaMapit(luoLkAineet()));

        // Kaikkien aineiden keskiarvo: 7.5
        // Asettuu arvovälille 7.5 - 7.75

        final BigDecimal odotettuTulos = new BigDecimal("9.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), "nimi"));

        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testToisenAsteenYlioppilaspohjainenPeruskaava() {
        Hakemus[] hakemukset = new Hakemus[]{
                hakemus(yhdistaMapit(luoLkAineet(), valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 7),
                        valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),};

        final BigDecimal odotettuTulos = new BigDecimal("13.0");

        Laskentakaava kaava = laajennaAlakaavat(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(
                PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
                PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
                PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
                PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                        YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), "nimi")));
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemukset[0],
                Arrays.asList(hakemukset), Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()),
                new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestystasapistekaavaEnsimmainen() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("5.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestystasapistekaavaToinen() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("4.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID2, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestystasapistekaavaKolmas() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("3.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID3, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestystasapistekaavaNeljas() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("2.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID4, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestystasapistekaavaViides() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("1.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID5, hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHakutoivejarjestystasapistekaavaEiHakenut() {
        Hakemus hakemus = luoPerushakemus();
        final BigDecimal odotettuTulos = new BigDecimal("0.0");

        Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaLasku("ei-olemassa", hakemus, hakemukset(hakemus),
                Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()), new StringBuffer());

        assertEquals(odotettuTulos, tulos.getTulos());
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testAidinkieliOnOpetuskieliSuomi() {
        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, "FI")))
        };

        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, "SV"))),
                hakemus(new HashMap<String, Object>())
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoAidinkieliOnOpetuskieliFunktiokutsu(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);
        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }

    @Test
    public void testAidinkieliOnOpetuskieliRuotsi() {
        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, "SV")))
        };

        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, "FI"))),
                hakemus(new HashMap<String, Object>())
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoAidinkieliOnOpetuskieliFunktiokutsu(LuoValintaperusteetServiceImpl.Kielikoodi.RUOTSI);
        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }

    @Test
    public void testKielikoeSuoritettu() {
        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.kielikoePrefix + "fi", "true")))
        };

        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.kielikoePrefix + "fi", "false"))),
                hakemus(new HashMap<String, Object>())
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoeSuoritettuFunktiokutsu(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);

        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }

    @Test
    public void testKielikoekriteeri1() {
        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.perustopetuksenKieli, "fi")))
        };
        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.perustopetuksenKieli, "sv"))),
                hakemus(new HashMap<String, Object>())
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri1(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);
        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }

    @Test
    public void testKielikoekriteeri2() {
        String[] aineet = {
                Aineet.a11Kieli,
                Aineet.a12Kieli,
                Aineet.a13Kieli,
                Aineet.a21Kieli,
                Aineet.a22Kieli,
                Aineet.a23Kieli
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri2(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);
        for (String aine : aineet) {
            Hakemus[] odotettuTulosTrue = {
                    hakemus(yhdistaMapit(
                            valintaperuste("PK_" + aine + "_OPPIAINE", "fi"),
                            valintaperuste("PK_" + aine, "7.0")
                    ))
            };

            Hakemus[] odotettuTulosFalse = {
                    hakemus(yhdistaMapit(
                            valintaperuste("PK_" + aine + "_OPPIAINE", "fi"),
                            valintaperuste("PK_" + aine, "6.0")
                    )),
                    hakemus(new HashMap<String, Object>())
            };

            for (Hakemus h : odotettuTulosFalse) {
                Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                        Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
                assertFalse(tulos.getTulos());
                assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
            }

            for (Hakemus h : odotettuTulosTrue) {
                Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                        Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

                assertTrue(tulos.getTulos());
                assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
            }
        }
    }

    @Test
    public void testKielikoekriteeri3() {
        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(
                        valintaperuste("PK_AI_OPPIAINE", "fi_2"),
                        valintaperuste("PK_AI", "7.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("PK_AI2_OPPIAINE", "fi_2"),
                        valintaperuste("PK_AI2", "7.0")
                ))
        };

        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(
                        valintaperuste("PK_AI_OPPIAINE", "fi_2"),
                        valintaperuste("PK_AI", "6.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("PK_AI2_OPPIAINE", "fi_2"),
                        valintaperuste("PK_AI2", "6.0")
                )),
                hakemus(new HashMap<String, Object>())
        };
        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri3(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);

        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }

    @Test
    public void testKielikoekriteeri4() {
        Hakemus fiHakemus = hakemus(yhdistaMapit(
                valintaperuste("lukion_kieli", "fi")));
        Hakemus tyhjaHakemus = hakemus(new HashMap<String, Object>());

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri4(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);
        Laskentatulos<Boolean> fiTulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, fiHakemus,
                Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
        Laskentatulos<Boolean> tyhjaTulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, tyhjaHakemus,
                Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

        assertTrue(fiTulos.getTulos());
        assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, fiTulos.getTila().getTilatyyppi());

        assertFalse(tyhjaTulos.getTulos());
        assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tyhjaTulos.getTila().getTilatyyppi());
    }

    @Test
    public void testKielkoekriteeri5() {
        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi"),
                        valintaperuste("LK_AI", "4.0"))),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi"),
                        valintaperuste("LK_AI2", "4.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi_2"),
                        valintaperuste("LK_AI", "4.0"))),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi_2"),
                        valintaperuste("LK_AI2", "4.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi_SE"),
                        valintaperuste("LK_AI", "4.0"))),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi_SE"),
                        valintaperuste("LK_AI2", "4.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi_VK"),
                        valintaperuste("LK_AI", "4.0"))),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi_VK"),
                        valintaperuste("LK_AI2", "4.0")
                )),
                hakemus(new HashMap<String, Object>())
        };

        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi"),
                        valintaperuste("LK_AI", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi"),
                        valintaperuste("LK_AI2", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi_2"),
                        valintaperuste("LK_AI", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi_2"),
                        valintaperuste("LK_AI2", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi_SE"),
                        valintaperuste("LK_AI", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi_SE"),
                        valintaperuste("LK_AI2", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI_OPPIAINE", "fi_VK"),
                        valintaperuste("LK_AI", "5.0")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("LK_AI2_OPPIAINE", "fi_VK"),
                        valintaperuste("LK_AI2", "5.0")
                ))
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri5(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);

        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }

    @Test
    public void testKielikoekriteeri6() {
        String[] aineet = {
                Aineet.a11Kieli,
                Aineet.a12Kieli,
                Aineet.a13Kieli,
                Aineet.a21Kieli,
                Aineet.a22Kieli,
                Aineet.a23Kieli,
                Aineet.b1Kieli
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri6(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);
        for (String aine : aineet) {
            Hakemus[] odotettuTulosTrue = {
                    hakemus(yhdistaMapit(
                            valintaperuste("LK_" + aine + "_OPPIAINE", "fi"),
                            valintaperuste("LK_" + aine, "5.0")
                    ))
            };

            Hakemus[] odotettuTulosFalse = {
                    hakemus(yhdistaMapit(
                            valintaperuste("LK_" + aine + "_OPPIAINE", "fi"),
                            valintaperuste("LK_" + aine, "4.0")
                    )),
                    hakemus(new HashMap<String, Object>())
            };

            for (Hakemus h : odotettuTulosFalse) {
                Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                        Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
                assertFalse(tulos.getTulos());
                assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
            }

            for (Hakemus h : odotettuTulosTrue) {
                Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                        Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

                assertTrue(tulos.getTulos());
                assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
            }
        }
    }

    @Test
    public void testKielikoekriteeri7() {
        Hakemus[] odotettuTulosTrue = {
                hakemus(yhdistaMapit(
                        valintaperuste("yleinen_kielitutkinto_fi", "true")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("valtionhallinnon_kielitutkinto_fi", "true")
                ))};

        Hakemus[] odotettuTulosFalse = {
                hakemus(yhdistaMapit(
                        valintaperuste("yleinen_kielitutkinto_fi", "false")
                )),
                hakemus(yhdistaMapit(
                        valintaperuste("valtionhallinnon_kielitutkinto_fi", "false")
                )),
                hakemus(new HashMap<String, Object>())
        };

        Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri7(LuoValintaperusteetServiceImpl.Kielikoodi.SUOMI);
        for (Hakemus h : odotettuTulosFalse) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());
            assertFalse(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
            Laskentatulos<Boolean> tulos = laskentaService.suoritaLasku(HAKUKOHDE_OID1, h,
                    Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava), new StringBuffer());

            assertTrue(tulos.getTulos());
            assertEquals(Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
    }
}
