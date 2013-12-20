package fi.vm.sade.service.valintaperusteet.service.lukio;

import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.kaava.Laskentadomainkonvertteri;
import fi.vm.sade.kaava.Laskentakaavavalidaattori;
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

    private static final String OPPIAINE_POSTFIX = "_OPPIAINE";


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
        put("paasykoe_tunniste", "pk");
        put("lisanaytto_tunniste", "l");
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
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.AIDINKIELI_JA_KIRJALLISUUS1, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.B1KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.B1KIELI + OPPIAINE_POSTFIX, LukionValintaperusteet.SAKSA),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.A11KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.A11KIELI + OPPIAINE_POSTFIX, LukionValintaperusteet.LATVIA),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.USKONTO, "6.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.HISTORIA, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.YHTEISKUNTAOPPI, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.MATEMATIIKKA, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.FYSIIKKA, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.KEMIA, "6.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.BIOLOGIA, "10.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.TERVEYSTIETO, "6.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.MAANTIETO, "5.0")
        ));

        Hakukohde hakukohde = new Hakukohde(HAKUKOHDE_OID1, yhdistaMapit(
                valintaperuste(LukionValintaperusteet.AIDINKIELI_JA_KIRJALLISUUS1 + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.5"),
                valintaperuste(LukionValintaperusteet.B1KIELI + "_" + LukionValintaperusteet.SAKSA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.5"),
                valintaperuste(LukionValintaperusteet.A11KIELI + "_" + LukionValintaperusteet.LATVIA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.5"),
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
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.AIDINKIELI_JA_KIRJALLISUUS1, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.B1KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.B1KIELI + OPPIAINE_POSTFIX, LukionValintaperusteet.SAKSA),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.A11KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.A11KIELI + OPPIAINE_POSTFIX, LukionValintaperusteet.LATVIA),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.B21KIELI, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.B21KIELI + OPPIAINE_POSTFIX, LukionValintaperusteet.RANSKA),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.USKONTO, "6.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.HISTORIA, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.YHTEISKUNTAOPPI, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.MATEMATIIKKA, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.FYSIIKKA, "7.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.KEMIA, "6.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.BIOLOGIA, "10.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.TERVEYSTIETO, "6.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.MAANTIETO, "5.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.LIIKUNTA, "8.0"),
                valintaperuste(LukionValintaperusteet.AINE_PREFIX+LukionValintaperusteet.KUVATAIDE, "10.0")
//                valintaperuste("PK_KU_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_KU_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_TE", "10"),
//                valintaperuste("PK_KS", "10"),
//                valintaperuste("PK_KT", "10"),
//                valintaperuste("PK_KU", "10"),
//                valintaperuste("PK_BI_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_KO", "10"),
//                valintaperuste("PK_BI_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_FY", "10"),
//                valintaperuste("PK_MU_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_MU_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_BI", "10"),
//                valintaperuste("PK_A1_OPPIAINE", "SV"),
//                valintaperuste("PK_AI_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_MA_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_AI_VAL1", "Ei arvosanaa"),
//                valintaperuste("vaiheId", "osaaminen"),
//                valintaperuste("PK_A1_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_A1_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_MA_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_HI_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_B1_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_HI_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_B1_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_KO_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_KO_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_GE", "10"),
//                valintaperuste("PK_KE", "10"),
//                valintaperuste("PK_AI_OPPIAINE", "FI"),
//                valintaperuste("PK_MU", "10"),
//                valintaperuste("PK_FY_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_FY_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_KS_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_YH_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_HI", "10"),
//                valintaperuste("PK_YH_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_B1_OPPIAINE", "EL"),
//                valintaperuste("PK_A1", "9"),
//                valintaperuste("PK_KE_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_KE_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_LI_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_KS_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_LI_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_AI", "10"),
//                valintaperuste("PK_LI", "10"),
//                valintaperuste("PK_YH", "10"),
//                valintaperuste("PK_GE_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_GE_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_MA", "9"),
//                valintaperuste("PK_TE_VAL1", "Ei arvosanaa"),
//                valintaperuste("PK_KT_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_B1", "9"),
//                valintaperuste("PK_TE_VAL2", "Ei arvosanaa"),
//                valintaperuste("PK_KT_VAL1", "Ei arvosanaa")
        ));

        Hakukohde hakukohde = new Hakukohde(HAKUKOHDE_OID1, yhdistaMapit(
                valintaperuste(LukionValintaperusteet.B1KIELI + "_" + LukionValintaperusteet.SAKSA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.0"),
                valintaperuste(LukionValintaperusteet.A11KIELI + "_" + LukionValintaperusteet.LATVIA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "1.0"),
                valintaperuste(LukionValintaperusteet.A11KIELI + "_" + LukionValintaperusteet.RANSKA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "5.0"),
                valintaperuste(LukionValintaperusteet.LIIKUNTA + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "2.0"),
                valintaperuste(LukionValintaperusteet.KUVATAIDE + LukionValintaperusteet.PAINOKERROIN_POSTFIX, "2.5"),
                mustacheMap
//                valintaperuste("A12_DE_painokerroin","1.0"),
//                valintaperuste("A12_EL_painokerroin","1.0"),
//                valintaperuste("A12_EN_painokerroin","1.0"),
//                valintaperuste("A12_ES_painokerroin","1.0"),
//                valintaperuste("A12_ET_painokerroin","1.0"),
//                valintaperuste("A12_FI_painokerroin","1.0"),
//                valintaperuste("A12_FR_painokerroin","1.0"),
//                valintaperuste("A12_IT_painokerroin","1.0"),
//                valintaperuste("A12_JA_painokerroin","1.0"),
//                valintaperuste("A12_LA_painokerroin","1.0"),
//                valintaperuste("A12_LT_painokerroin","1.0"),
//                valintaperuste("A12_LV_painokerroin","1.0"),
//                valintaperuste("A12_PT_painokerroin","1.0"),
//                valintaperuste("A12_RU_painokerroin","1.0"),
//                valintaperuste("A12_SE_painokerroin","1.0"),
//                valintaperuste("A12_SV_painokerroin","1.0"),
//                valintaperuste("A12_VK_painokerroin","1.0"),
//                valintaperuste("A12_ZH_painokerroin","1.0"),
//                valintaperuste("A13_DE_painokerroin","1.0"),
//                valintaperuste("A13_EL_painokerroin","1.0"),
//                valintaperuste("A13_EN_painokerroin","1.0"),
//                valintaperuste("A13_ES_painokerroin","1.0"),
//                valintaperuste("A13_ET_painokerroin","1.0"),
//                valintaperuste("A13_FI_painokerroin","1.0"),
//                valintaperuste("A13_FR_painokerroin","1.0"),
//                valintaperuste("A13_IT_painokerroin","1.0"),
//                valintaperuste("A13_JA_painokerroin","1.0"),
//                valintaperuste("A13_LA_painokerroin","1.0"),
//                valintaperuste("A13_LT_painokerroin","1.0"),
//                valintaperuste("A13_LV_painokerroin","1.0"),
//                valintaperuste("A13_PT_painokerroin","1.0"),
//                valintaperuste("A13_RU_painokerroin","1.0"),
//                valintaperuste("A13_SE_painokerroin","1.0"),
//                valintaperuste("A13_SV_painokerroin","1.0"),
//                valintaperuste("A13_VK_painokerroin","1.0"),
//                valintaperuste("A13_ZH_painokerroin","1.0"),
//                valintaperuste("A1_DE_painokerroin","1.0"),
//                valintaperuste("A1_EL_painokerroin","1.0"),
//                valintaperuste("A1_EN_painokerroin","1.0"),
//                valintaperuste("A1_ES_painokerroin","1.0"),
//                valintaperuste("A1_ET_painokerroin","1.0"),
//                valintaperuste("A1_FI_painokerroin","1.0"),
//                valintaperuste("A1_FR_painokerroin","1.0"),
//                valintaperuste("A1_IT_painokerroin","1.0"),
//                valintaperuste("A1_JA_painokerroin","1.0"),
//                valintaperuste("A1_LA_painokerroin","1.0"),
//                valintaperuste("A1_LT_painokerroin","1.0"),
//                valintaperuste("A1_LV_painokerroin","1.0"),
//                valintaperuste("A1_PT_painokerroin","1.0"),
//                valintaperuste("A1_RU_painokerroin","1.0"),
//                valintaperuste("A1_SE_painokerroin","1.0"),
//                valintaperuste("A1_SV_painokerroin","1.0"),
//                valintaperuste("A1_VK_painokerroin","1.0"),
//                valintaperuste("A1_ZH_painokerroin","1.0"),
//                valintaperuste("A22_DE_painokerroin","1.0"),
//                valintaperuste("A22_EL_painokerroin","1.0"),
//                valintaperuste("A22_EN_painokerroin","1.0"),
//                valintaperuste("A22_ES_painokerroin","1.0"),
//                valintaperuste("A22_ET_painokerroin","1.0"),
//                valintaperuste("A22_FI_painokerroin","1.0"),
//                valintaperuste("A22_FR_painokerroin","1.0"),
//                valintaperuste("A22_IT_painokerroin","1.0"),
//                valintaperuste("A22_JA_painokerroin","1.0"),
//                valintaperuste("A22_LA_painokerroin","1.0"),
//                valintaperuste("A22_LT_painokerroin","1.0"),
//                valintaperuste("A22_LV_painokerroin","1.0"),
//                valintaperuste("A22_PT_painokerroin","1.0"),
//                valintaperuste("A22_RU_painokerroin","1.0"),
//                valintaperuste("A22_SE_painokerroin","1.0"),
//                valintaperuste("A22_SV_painokerroin","1.0"),
//                valintaperuste("A22_VK_painokerroin","1.0"),
//                valintaperuste("A22_ZH_painokerroin","1.0"),
//                valintaperuste("A23_DE_painokerroin","1.0"),
//                valintaperuste("A23_EL_painokerroin","1.0"),
//                valintaperuste("A23_EN_painokerroin","1.0"),
//                valintaperuste("A23_ES_painokerroin","1.0"),
//                valintaperuste("A23_ET_painokerroin","1.0"),
//                valintaperuste("A23_FI_painokerroin","1.0"),
//                valintaperuste("A23_FR_painokerroin","1.0"),
//                valintaperuste("A23_IT_painokerroin","1.0"),
//                valintaperuste("A23_JA_painokerroin","1.0"),
//                valintaperuste("A23_LA_painokerroin","1.0"),
//                valintaperuste("A23_LT_painokerroin","1.0"),
//                valintaperuste("A23_LV_painokerroin","1.0"),
//                valintaperuste("A23_PT_painokerroin","1.0"),
//                valintaperuste("A23_RU_painokerroin","1.0"),
//                valintaperuste("A23_SE_painokerroin","1.0"),
//                valintaperuste("A23_SV_painokerroin","1.0"),
//                valintaperuste("A23_VK_painokerroin","1.0"),
//                valintaperuste("A23_ZH_painokerroin","1.0"),
//                valintaperuste("A2_DE_painokerroin","1.0"),
//                valintaperuste("A2_EL_painokerroin","1.0"),
//                valintaperuste("A2_EN_painokerroin","1.0"),
//                valintaperuste("A2_ES_painokerroin","1.0"),
//                valintaperuste("A2_ET_painokerroin","1.0"),
//                valintaperuste("A2_FI_painokerroin","1.0"),
//                valintaperuste("A2_FR_painokerroin","1.0"),
//                valintaperuste("A2_IT_painokerroin","1.0"),
//                valintaperuste("A2_JA_painokerroin","1.0"),
//                valintaperuste("A2_LA_painokerroin","1.0"),
//                valintaperuste("A2_LT_painokerroin","1.0"),
//                valintaperuste("A2_LV_painokerroin","1.0"),
//                valintaperuste("A2_PT_painokerroin","1.0"),
//                valintaperuste("A2_RU_painokerroin","1.0"),
//                valintaperuste("A2_SE_painokerroin","1.0"),
//                valintaperuste("A2_SV_painokerroin","1.0"),
//                valintaperuste("A2_VK_painokerroin","1.0"),
//                valintaperuste("A2_ZH_painokerroin","1.0"),
//                valintaperuste("AI2_painokerroin","1.0"),
//                valintaperuste("AI_painokerroin","1.0"),
//                valintaperuste("B1_DE_painokerroin","1.0"),
//                valintaperuste("B1_EL_painokerroin","1.0"),
//                valintaperuste("B1_EN_painokerroin","1.0"),
//                valintaperuste("B1_ES_painokerroin","1.0"),
//                valintaperuste("B1_ET_painokerroin","1.0"),
//                valintaperuste("B1_FI_painokerroin","1.0"),
//                valintaperuste("B1_FR_painokerroin","1.0"),
//                valintaperuste("B1_IT_painokerroin","1.0"),
//                valintaperuste("B1_JA_painokerroin","1.0"),
//                valintaperuste("B1_LA_painokerroin","1.0"),
//                valintaperuste("B1_LT_painokerroin","1.0"),
//                valintaperuste("B1_LV_painokerroin","1.0"),
//                valintaperuste("B1_PT_painokerroin","1.0"),
//                valintaperuste("B1_RU_painokerroin","1.0"),
//                valintaperuste("B1_SE_painokerroin","1.0"),
//                valintaperuste("B1_SV_painokerroin","1.0"),
//                valintaperuste("B1_VK_painokerroin","1.0"),
//                valintaperuste("B1_ZH_painokerroin","1.0"),
//                valintaperuste("B22_DE_painokerroin","1.0"),
//                valintaperuste("B22_EL_painokerroin","1.0"),
//                valintaperuste("B22_EN_painokerroin","1.0"),
//                valintaperuste("B22_ES_painokerroin","1.0"),
//                valintaperuste("B22_ET_painokerroin","1.0"),
//                valintaperuste("B22_FI_painokerroin","1.0"),
//                valintaperuste("B22_FR_painokerroin","1.0"),
//                valintaperuste("B22_IT_painokerroin","1.0"),
//                valintaperuste("B22_JA_painokerroin","1.0"),
//                valintaperuste("B22_LA_painokerroin","1.0"),
//                valintaperuste("B22_LT_painokerroin","1.0"),
//                valintaperuste("B22_LV_painokerroin","1.0"),
//                valintaperuste("B22_PT_painokerroin","1.0"),
//                valintaperuste("B22_RU_painokerroin","1.0"),
//                valintaperuste("B22_SE_painokerroin","1.0"),
//                valintaperuste("B22_SV_painokerroin","1.0"),
//                valintaperuste("B22_VK_painokerroin","1.0"),
//                valintaperuste("B22_ZH_painokerroin","1.0"),
//                valintaperuste("B23_DE_painokerroin","1.0"),
//                valintaperuste("B23_EL_painokerroin","1.0"),
//                valintaperuste("B23_EN_painokerroin","1.0"),
//                valintaperuste("B23_ES_painokerroin","1.0"),
//                valintaperuste("B23_ET_painokerroin","1.0"),
//                valintaperuste("B23_FI_painokerroin","1.0"),
//                valintaperuste("B23_FR_painokerroin","1.0"),
//                valintaperuste("B23_IT_painokerroin","1.0"),
//                valintaperuste("B23_JA_painokerroin","1.0"),
//                valintaperuste("B23_LA_painokerroin","1.0"),
//                valintaperuste("B23_LT_painokerroin","1.0"),
//                valintaperuste("B23_LV_painokerroin","1.0"),
//                valintaperuste("B23_PT_painokerroin","1.0"),
//                valintaperuste("B23_RU_painokerroin","1.0"),
//                valintaperuste("B23_SE_painokerroin","1.0"),
//                valintaperuste("B23_SV_painokerroin","1.0"),
//                valintaperuste("B23_VK_painokerroin","1.0"),
//                valintaperuste("B23_ZH_painokerroin","1.0"),
//                valintaperuste("B2_DE_painokerroin","1.0"),
//                valintaperuste("B2_EL_painokerroin","1.0"),
//                valintaperuste("B2_EN_painokerroin","1.0"),
//                valintaperuste("B2_ES_painokerroin","1.0"),
//                valintaperuste("B2_ET_painokerroin","1.0"),
//                valintaperuste("B2_FI_painokerroin","1.0"),
//                valintaperuste("B2_FR_painokerroin","1.0"),
//                valintaperuste("B2_IT_painokerroin","1.0"),
//                valintaperuste("B2_JA_painokerroin","1.0"),
//                valintaperuste("B2_LA_painokerroin","1.0"),
//                valintaperuste("B2_LT_painokerroin","1.0"),
//                valintaperuste("B2_LV_painokerroin","1.0"),
//                valintaperuste("B2_PT_painokerroin","1.0"),
//                valintaperuste("B2_RU_painokerroin","1.0"),
//                valintaperuste("B2_SE_painokerroin","1.0"),
//                valintaperuste("B2_SV_painokerroin","1.0"),
//                valintaperuste("B2_VK_painokerroin","1.0"),
//                valintaperuste("B2_ZH_painokerroin","1.0"),
//                valintaperuste("B32_DE_painokerroin","1.0"),
//                valintaperuste("B32_EL_painokerroin","1.0"),
//                valintaperuste("B32_EN_painokerroin","1.0"),
//                valintaperuste("B32_ES_painokerroin","1.0"),
//                valintaperuste("B32_ET_painokerroin","1.0"),
//                valintaperuste("B32_FI_painokerroin","1.0"),
//                valintaperuste("B32_FR_painokerroin","1.0"),
//                valintaperuste("B32_IT_painokerroin","1.0"),
//                valintaperuste("B32_JA_painokerroin","1.0"),
//                valintaperuste("B32_LA_painokerroin","1.0"),
//                valintaperuste("B32_LT_painokerroin","1.0"),
//                valintaperuste("B32_LV_painokerroin","1.0"),
//                valintaperuste("B32_PT_painokerroin","1.0"),
//                valintaperuste("B32_RU_painokerroin","1.0"),
//                valintaperuste("B32_SE_painokerroin","1.0"),
//                valintaperuste("B32_SV_painokerroin","1.0"),
//                valintaperuste("B32_VK_painokerroin","1.0"),
//                valintaperuste("B32_ZH_painokerroin","1.0"),
//                valintaperuste("B33_DE_painokerroin","1.0"),
//                valintaperuste("B33_EL_painokerroin","1.0"),
//                valintaperuste("B33_EN_painokerroin","1.0"),
//                valintaperuste("B33_ES_painokerroin","1.0"),
//                valintaperuste("B33_ET_painokerroin","1.0"),
//                valintaperuste("B33_FI_painokerroin","1.0"),
//                valintaperuste("B33_FR_painokerroin","1.0"),
//                valintaperuste("B33_IT_painokerroin","1.0"),
//                valintaperuste("B33_JA_painokerroin","1.0"),
//                valintaperuste("B33_LA_painokerroin","1.0"),
//                valintaperuste("B33_LT_painokerroin","1.0"),
//                valintaperuste("B33_LV_painokerroin","1.0"),
//                valintaperuste("B33_PT_painokerroin","1.0"),
//                valintaperuste("B33_RU_painokerroin","1.0"),
//                valintaperuste("B33_SE_painokerroin","1.0"),
//                valintaperuste("B33_SV_painokerroin","1.0"),
//                valintaperuste("B33_VK_painokerroin","1.0"),
//                valintaperuste("B33_ZH_painokerroin","1.0"),
//                valintaperuste("B3_DE_painokerroin","1.0"),
//                valintaperuste("B3_EL_painokerroin","1.0"),
//                valintaperuste("B3_EN_painokerroin","1.0"),
//                valintaperuste("B3_ES_painokerroin","1.0"),
//                valintaperuste("B3_ET_painokerroin","1.0"),
//                valintaperuste("B3_FI_painokerroin","1.0"),
//                valintaperuste("B3_FR_painokerroin","1.0"),
//                valintaperuste("B3_IT_painokerroin","1.0"),
//                valintaperuste("B3_JA_painokerroin","1.0"),
//                valintaperuste("B3_LA_painokerroin","1.0"),
//                valintaperuste("B3_LT_painokerroin","1.0"),
//                valintaperuste("B3_LV_painokerroin","1.0"),
//                valintaperuste("B3_PT_painokerroin","1.0"),
//                valintaperuste("B3_RU_painokerroin","1.0"),
//                valintaperuste("B3_SE_painokerroin","1.0"),
//                valintaperuste("B3_SV_painokerroin","1.0"),
//                valintaperuste("B3_VK_painokerroin","1.0"),
//                valintaperuste("B3_ZH_painokerroin","1.0"),
//                valintaperuste("BI_painokerroin","1.0"),
//                valintaperuste("FY_painokerroin","1.0"),
//                valintaperuste("GE_painokerroin","1.0"),
//                valintaperuste("HI_painokerroin","1.0"),
//                valintaperuste("KE_painokerroin","1.0"),
//                valintaperuste("kielikoe_tunniste","Gymnasiets bildkonstlinje_kielikoe"),
//                valintaperuste("KO_painokerroin","0.0"),
//                valintaperuste("KS_painokerroin","0.0"),
//                valintaperuste("KT_painokerroin","1.0"),
//                valintaperuste("KU_painokerroin","0.0"),
//                valintaperuste("LI_painokerroin","0.0"),
//                valintaperuste("lisanaytto_hylkays_max","0.0"),
//                valintaperuste("lisanaytto_hylkays_min","0.0"),
//                valintaperuste("lisanaytto_max","0.0"),
//                valintaperuste("lisanaytto_min","0.0"),
//                valintaperuste("lisanaytto_tunniste","Gymnasiets bildkonstlinje_lisanaytto"),
//                valintaperuste("lisapiste_tunniste","Gymnasiets bildkonstlinje_lisapiste"),
//                valintaperuste("MA_painokerroin","1.0"),
//                valintaperuste("MU_painokerroin","0.0"),
//                valintaperuste("paasykoe_hylkays_max","4.00"),
//                valintaperuste("paasykoe_hylkays_min","0.0"),
//                valintaperuste("paasykoe_ja_lisanaytto_hylkays_max","4.00"),
//                valintaperuste("paasykoe_ja_lisanaytto_hylkays_min","0.0"),
//                valintaperuste("paasykoe_max","10.00"),
//                valintaperuste("paasykoe_min","1.00"),
//                valintaperuste("paasykoe_tunniste","Gymnasiets bildkonstlinje_paasykoe"),
//                valintaperuste("painotettu_keskiarvo_hylkays_max","7"),
//                valintaperuste("painotettu_keskiarvo_hylkays_min","0.0"),
//                valintaperuste("TE_painokerroin","1.0"),
//                valintaperuste("YH_painokerroin","1.0")

        ));

        Lukuarvofunktio lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.painotettuLukuaineidenKeskiarvo()).getFunktiokutsu());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("7.6000").compareTo(tulos.getTulos()) == 0);
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

        Lukuarvofunktio lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.paasykoeLukuarvo("paasykoe_tunniste")).getFunktiokutsu());
        Laskentatulos<BigDecimal> tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("3.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemusHylattyPaasykoe, new ArrayList<Hakemus>(), lasku);
        assertTrue(new BigDecimal("1.5").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());

        lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.lisanayttoLukuarvo("lisanaytto_tunniste")).getFunktiokutsu());
        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemusHylattyLisanaytto, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("1.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());

        tulos = laskentaService.suoritaValintalaskenta(hakukohde, hakemus, new ArrayList<Hakemus>(), lasku);

        assertTrue(new BigDecimal("3.0").compareTo(tulos.getTulos()) == 0);
        assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

        lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(laajennaAlakaavat(LukionValintaperusteet.paasykoeJaLisanaytto(LukionValintaperusteet.paasykoeLukuarvo("paasykoe_tunniste"), LukionValintaperusteet.lisanayttoLukuarvo("lisanaytto_tunniste"))).getFunktiokutsu());
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
