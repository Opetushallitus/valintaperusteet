package fi.vm.sade.service.valintaperusteet.service.lukio;

import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.kaava.Laskentadomainkonvertteri;
import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus;
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde;
import fi.vm.sade.service.valintaperusteet.laskenta.api.LaskentaService;
import fi.vm.sade.service.valintaperusteet.laskenta.api.Laskentatulos;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.service.impl.lukio.LukionValintaperusteet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: wuoti
 * Date: 30.9.2013
 * Time: 15.47
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LukionValintaperusteetTest {
    @Autowired
    private LaskentaService laskentaService;


    private static final String HAKEMUS_OID = "hakemusOid";
    private static final String HAKUKOHDE_OID1 = "1.2.246.562.5.10095_02_186_0632";
    private static final Hakukohde HAKUKOHDE1 = new Hakukohde(HAKUKOHDE_OID1, new HashMap<String, String>());

    private static final String HAKUKOHDE_OID2 = "1.2.246.562.5.01403_01_186_1027";
    private static final Hakukohde HAKUKOHDE2 = new Hakukohde(HAKUKOHDE_OID2, new HashMap<String, String>());

    private static final String HAKUKOHDE_OID3 = "1.2.246.562.5.01787_01_406_1042";
    private static final Hakukohde HAKUKOHDE3 = new Hakukohde(HAKUKOHDE_OID3, new HashMap<String, String>());

    private static final String HAKUKOHDE_OID4 = "1.2.246.562.5.01787_01_406_1043";
    private static final Hakukohde HAKUKOHDE4 = new Hakukohde(HAKUKOHDE_OID4, new HashMap<String, String>());

    private static final String HAKUKOHDE_OID5 = "1.2.246.562.5.01787_01_406_1044";
    private static final Hakukohde HAKUKOHDE5 = new Hakukohde(HAKUKOHDE_OID5, new HashMap<String, String>());


    private static final Map<Integer, String> hakutoiveet;

    private static final Map mustacheMap = new HashMap<String, String>() {{
        put("paasykoe_hylkays_min", "0");
        put("paasykoe_hylkays_max", "2");
        put("lisanaytto_hylkays_min", "0");
        put("lisanaytto_hylkays_max", "2");
        put("paasykoe_ja_lisanaytto_hylkays_min", "4");
        put("paasykoe_ja_lisanaytto_hylkays_max", "6");
        put("painotettu_keskiarvo_hylkays_min", "4");
        put("painotettu_keskiarvo_hylkays_max", "7.5");
        put("paasykoe_tulos", "pk");
        put("lisanaytto_tulos", "l");
    }};

    static {
        hakutoiveet = new HashMap<Integer, String>();
        hakutoiveet.put(1, HAKUKOHDE_OID1);
        hakutoiveet.put(2, HAKUKOHDE_OID2);
        hakutoiveet.put(3, HAKUKOHDE_OID3);
        hakutoiveet.put(4, HAKUKOHDE_OID4);
        hakutoiveet.put(5, HAKUKOHDE_OID5);
    }

    private Map<String, String> newMap() {
        return new HashMap<String, String>();
    }

    private Map<String, String> yhdistaMapit(Map<String, String>... maps) {
        Map<String, String> map = newMap();
        for (Map<String, String> m : maps) {
            map.putAll(m);
        }

        return map;
    }

    private Map<String, String> valintaperuste(String avain, String arvo) {
        Map<String, String> map = newMap();
        map.put(avain, arvo);
        return map;
    }

    private Hakemus hakemus(Map<String, String> kentat) {
        Map<String, String> h = new HashMap<String, String>();

        for (Map.Entry<String, String> e : kentat.entrySet()) {
            h.put(e.getKey(), e.getValue());
        }

        return new Hakemus(HAKEMUS_OID, hakutoiveet, h);
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

    @Test
    public void testPainotettavatArvosanatKielienPainotus() {
        Hakemus hakemus = hakemus(yhdistaMapit(
                valintaperuste(LukionValintaperusteet.AIDINKIELI_JA_KIRJALLISUUS1, "8.0"),
                valintaperuste(LukionValintaperusteet.B1KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.A11KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.USKONTO, "6.0"),
                valintaperuste(LukionValintaperusteet.HISTORIA, "7.0"),
                valintaperuste(LukionValintaperusteet.YHTEISKUNTAOPPI, "8.0"),
                valintaperuste(LukionValintaperusteet.MATEMATIIKKA, "8.0"),
                valintaperuste(LukionValintaperusteet.FYSIIKKA, "7.0"),
                valintaperuste(LukionValintaperusteet.KEMIA, "6.0"),
                valintaperuste(LukionValintaperusteet.BIOLOGIA, "10.0"),
                valintaperuste(LukionValintaperusteet.TERVEYSTIETO, "6.0"),
                valintaperuste(LukionValintaperusteet.MAANTIETO, "5.0")
        ));

        Hakukohde hakukohde = new Hakukohde(HAKUKOHDE_OID1, yhdistaMapit(
                valintaperuste(LukionValintaperusteet.AIDINKIELI_JA_KIRJALLISUUS1 + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.5"),
                valintaperuste(LukionValintaperusteet.B1KIELI + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.5"),
                valintaperuste(LukionValintaperusteet.A11KIELI + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.5"),
                mustacheMap
        ));

        Lukuarvofunktio lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo()).getFunktiokutsu());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("7.1111").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testPainotettavatArvosanatTaitoJaTaideaineidenPainotus() {
        Hakemus hakemus = hakemus(yhdistaMapit(
                valintaperuste(LukionValintaperusteet.AIDINKIELI_JA_KIRJALLISUUS1, "8.0"),
                valintaperuste(LukionValintaperusteet.B1KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.A11KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.USKONTO, "6.0"),
                valintaperuste(LukionValintaperusteet.HISTORIA, "7.0"),
                valintaperuste(LukionValintaperusteet.YHTEISKUNTAOPPI, "8.0"),
                valintaperuste(LukionValintaperusteet.MATEMATIIKKA, "8.0"),
                valintaperuste(LukionValintaperusteet.FYSIIKKA, "7.0"),
                valintaperuste(LukionValintaperusteet.KEMIA, "6.0"),
                valintaperuste(LukionValintaperusteet.BIOLOGIA, "10.0"),
                valintaperuste(LukionValintaperusteet.TERVEYSTIETO, "6.0"),
                valintaperuste(LukionValintaperusteet.MAANTIETO, "5.0"),
                valintaperuste(LukionValintaperusteet.LIIKUNTA, "8.0"),
                valintaperuste(LukionValintaperusteet.KUVATAIDE, "10.0")
        ));

        Hakukohde hakukohde = new Hakukohde(HAKUKOHDE_OID1, yhdistaMapit(
                valintaperuste(LukionValintaperusteet.LIIKUNTA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "2.0"),
                valintaperuste(LukionValintaperusteet.KUVATAIDE + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "2.5"),
                mustacheMap
        ));

        Lukuarvofunktio lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo()).getFunktiokutsu());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("7.6364").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
    }

    @Test
    public void testHylkaaArvovalilla() {
        Hakemus hakemus = hakemus(yhdistaMapit(
                valintaperuste("pk", "3.0"),
                valintaperuste("pk-OSALLISTUMINEN", "OSALLISTUI"),
                valintaperuste("l", "3.0"),
                valintaperuste("l-OSALLISTUMINEN", "OSALLISTUI")
        ));

        Hakemus hakemusHylattyPaasykoe = hakemus(yhdistaMapit(
                valintaperuste("pk", "1.5"),
                valintaperuste("pk-OSALLISTUMINEN", "OSALLISTUI"),
                valintaperuste("l", "4.0"),
                valintaperuste("l-OSALLISTUMINEN", "OSALLISTUI")
        ));

        Hakemus hakemusHylattyLisanaytto = hakemus(yhdistaMapit(
                valintaperuste("pk", "5.0"),
                valintaperuste("pk-OSALLISTUMINEN", "OSALLISTUI"),
                valintaperuste("l", "1.0"),
                valintaperuste("l-OSALLISTUMINEN", "OSALLISTUI")
        ));

        Hakemus hakemusHylattyMolemmat = hakemus(yhdistaMapit(
                valintaperuste("pk", "0.0"),
                valintaperuste("pk-OSALLISTUMINEN", "OSALLISTUI"),
                valintaperuste("l", "1.0"),
                valintaperuste("l-OSALLISTUMINEN", "OSALLISTUI")
        ));

        Hakukohde hakukohde = new Hakukohde(HAKUKOHDE_OID1, yhdistaMapit(
                mustacheMap
        ));

        Lukuarvofunktio lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.paasykoeLukuarvo("paasykoe_tulos")).getFunktiokutsu());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("3.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemusHylattyPaasykoe, new ArrayList<Hakemus>(), lasku);
        assertTrue(new BigDecimal("1.5").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());

        lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.lisanayttoLukuarvo("lisanaytto_tulos")).getFunktiokutsu());
        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemusHylattyLisanaytto, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("1.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());

        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("3.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

        lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.paasykoeJaLisanaytto(LukionValintaperusteet.paasykoeLukuarvo("paasykoe_tulos"), LukionValintaperusteet.lisanayttoLukuarvo("lisanaytto_tulos"))).getFunktiokutsu());
        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("6.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemusHylattyLisanaytto, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("6.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());

        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemusHylattyMolemmat, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("1.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());

    }
}
