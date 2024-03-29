package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Lists;
import fi.vm.sade.kaava.Laskentadomainkonvertteri;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.laskenta.Laskin;
import fi.vm.sade.service.valintaperusteet.laskenta.api.*;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HylattyMetatieto;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hylattytila;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.VirheMetatieto;
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Virhetila;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.Aineet;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.GenericHelper;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkAineet;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkJaYoPohjaiset;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkPohjaiset;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.YoAineet;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.YoPohjaiset;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** User: kwuoti Date: 5.3.2013 Time: 16.02 */
public class LuoValintaperusteetServiceTest extends WithSpringBoot {

  @Autowired private LuoValintaperusteetService luoValintaperusteetService;

  @Autowired private LaskentaService laskentaService;

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Test
  @Disabled
  public void testLuo() throws IOException {
    luoValintaperusteetService.luo();
    int ryhmienMaara = valintaryhmaDAO.findAll().size();
    assertEquals(ryhmienMaara, 944);
  }

  private PkAineet pkAineet = new PkAineet();
  private YoAineet yoAineet = new YoAineet();

  private static boolean korkeakouluhaku = false;

  private static final String HAKEMUS_OID = "hakemusOid";
  private static final String HAKUKOHDE_OID1 = "1.2.246.562.5.10095_02_186_0632";
  private static final Hakukohde HAKUKOHDE1 =
      new Hakukohde(HAKUKOHDE_OID1, new HashMap<String, String>(), korkeakouluhaku);

  private static final String HAKUKOHDE_OID2 = "1.2.246.562.5.01403_01_186_1027";
  private static final Hakukohde HAKUKOHDE2 =
      new Hakukohde(HAKUKOHDE_OID2, new HashMap<String, String>(), korkeakouluhaku);

  private static final String HAKUKOHDE_OID3 = "1.2.246.562.5.01787_01_406_1042";
  private static final Hakukohde HAKUKOHDE3 =
      new Hakukohde(HAKUKOHDE_OID3, new HashMap<String, String>(), korkeakouluhaku);

  private static final String HAKUKOHDE_OID4 = "1.2.246.562.5.01787_01_406_1043";
  private static final Hakukohde HAKUKOHDE4 =
      new Hakukohde(HAKUKOHDE_OID4, new HashMap<String, String>(), korkeakouluhaku);

  private static final String HAKUKOHDE_OID5 = "1.2.246.562.5.01787_01_406_1044";
  private static final Hakukohde HAKUKOHDE5 =
      new Hakukohde(HAKUKOHDE_OID5, new HashMap<String, String>(), korkeakouluhaku);

  private static final Map<Integer, Hakutoive> hakutoiveet;
  private static final String[] KIELET = new String[] {"fi", "sv"};

  static {
    hakutoiveet = new HashMap<>();
    hakutoiveet.put(1, new Hakutoive(HAKUKOHDE_OID1, Lists.newArrayList()));
    hakutoiveet.put(2, new Hakutoive(HAKUKOHDE_OID2, Lists.newArrayList()));
    hakutoiveet.put(3, new Hakutoive(HAKUKOHDE_OID3, Lists.newArrayList()));
    hakutoiveet.put(4, new Hakutoive(HAKUKOHDE_OID4, Lists.newArrayList()));
    hakutoiveet.put(5, new Hakutoive(HAKUKOHDE_OID5, Lists.newArrayList()));
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

  private Map<String, String> valintaperuste(String avain, Object arvo) {
    Map<String, String> map = newMap();
    map.put(avain, arvo.toString());
    return map;
  }

  private Map<String, String> pakollinenPkAine(String aine, Object arvo) {
    Map<String, String> map = newMap();
    map.put(PkAineet.pakollinen(aine), arvo.toString());
    return map;
  }

  private Map<String, String> pakollinenPkAineJaValinnaiset(
      String aine, Object pakollinenArvo, Object val1Arvo, Object val2Arvo) {
    Map<String, String> map = newMap();
    map.putAll(pakollinenPkAine(aine, pakollinenArvo.toString()));
    map.put(PkAineet.valinnainen1(aine), val1Arvo.toString());
    map.put(PkAineet.valinnainen2(aine), val2Arvo.toString());
    return map;
  }

  private Map<String, String> lkAine(String aine, Object arvo) {
    Map<String, String> map = newMap();
    map.put(YoAineet.pakollinen(aine), arvo.toString());
    return map;
  }

  private Hakemus hakemus(Map<String, String> kentat) {
    Map<String, String> h = new HashMap<String, String>();

    for (Map.Entry<String, String> e : kentat.entrySet()) {
      h.put(e.getKey(), e.getValue().toString());
    }

    return new Hakemus(HAKEMUS_OID, hakutoiveet, h, new HashMap<>());
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

  private Map<String, String> luoPkAineet() {
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

  private Map<String, String> luoLkAineet() {
    return yhdistaMapit(
        lkAine(Aineet.aidinkieliJaKirjallisuus1, 7.0),
        lkAine(Aineet.aidinkieliJaKirjallisuus2, 8.0),
        lkAine(Aineet.historia, 9.0),
        lkAine(Aineet.yhteiskuntaoppi, 6.0),
        lkAine(Aineet.matematiikka, 7.0),
        lkAine(Aineet.fysiikka, 7.0),
        lkAine(Aineet.kemia, 6.0),
        lkAine(Aineet.biologia, 9.0),
        lkAine(Aineet.kuvataide, 10.0),
        lkAine(Aineet.musiikki, 5.0),
        lkAine(Aineet.maantieto, 7.0),
        lkAine(YoAineet.filosofia, 9.0),
        lkAine(Aineet.liikunta, 8.0),
        lkAine(Aineet.terveystieto, 5.0),
        lkAine(Aineet.uskonto, 9.0),
        lkAine(Aineet.a11Kieli, 10.0),
        lkAine(Aineet.b1Kieli, 7.0),
        lkAine(Aineet.b31Kieli, 6.0));
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

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaavaIlmanKonvertteria(pkAineet)));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPkPohjainenKaikkienAineidenKeskiarvo() {
    Hakemus hakemus = luoPerushakemus();

    // Kaikkien aineiden keskiarvo: 7.421

    final BigDecimal odotettuTulos = new BigDecimal("7.4211");

    Laskentakaava kaava =
        laajennaAlakaavat(PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

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

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                luoVakiokaava(new BigDecimal(7.0)),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPkYleinenKoulumenestysPisteytysmalliRajaarvoMax() {
    Hakemus hakemus = luoPerushakemus();

    final BigDecimal odotettuTulos = new BigDecimal("8.0");

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                luoVakiokaava(new BigDecimal(7.25)),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPkYleinenKoulumenestysPisteytysmalli() {
    Hakemus hakemus = luoPerushakemus();

    // Kaikkien aineiden keskiarvo: 7.421
    // Asettuu välille 7.25 - 7.50 ==> 8 pistettä

    final BigDecimal odotettuTulos = new BigDecimal("8.0");

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPkPohjakoulutusPisteytysmalliPohjakoulutusOnPerusopetus() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi)));

    final BigDecimal odotettuTulos = new BigDecimal("6.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPkPohjakoulutusPisteytysmalliLisapistekoulutusSuoritettu() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkPohjaiset.kymppiluokka, Boolean.TRUE.toString())));

    final BigDecimal odotettuTulos = new BigDecimal("6.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPkPohjakoulutusPisteytysmalliPohjakoulutusMuu() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.lukionPaattotodistus),
                valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi)));

    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testIlmanKoulutuspaikkaaPisteytysmalliOnKoulutuspaikka() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon, Boolean.TRUE.toString())));

    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testIlmanKoulutuspaikkaaPisteytysmalliEiOleKoulutuspaikkaa() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon, Boolean.FALSE.toString())));

    final BigDecimal odotettuTulos = new BigDecimal("8.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestysPisteytysmalliEnsisijainenHakutoive() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("2.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestysPisteytysmalliToissijainenHakutoive() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE2,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testTyokokemusPisteytysmalliAlleKolme() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 2.0)));

    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testTyokokemusPisteytysmalliKolme() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 3.0)));

    final BigDecimal odotettuTulos = new BigDecimal("1.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testTyokokemusPisteytysmalliViisi() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 5.0)));

    final BigDecimal odotettuTulos = new BigDecimal("1.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testTyokokemusPisteytysmalliKuusi() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 6.0)));

    final BigDecimal odotettuTulos = new BigDecimal("2.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testTyokokemusPisteytysmalliYksitoista() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 11.0)));

    final BigDecimal odotettuTulos = new BigDecimal("2.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testTyokokemusPisteytysmalliKaksitoista() {
    Hakemus hakemus =
        hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 12.0)));

    final BigDecimal odotettuTulos = new BigDecimal("3.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testSukupuolipisteytysmalliAlle30Prosenttia() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n")))
        };

    final BigDecimal odotettuTulos = new BigDecimal("2.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testSukupuolipisteytysmalliYli30Prosenttia() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n")))
        };

    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testToisenAsteenPeruskoulupohjainenPeruskaava() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  luoPkAineet(),
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain,
                      PkPohjaiset.perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara),
                  valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi),
                  valintaperuste(
                      PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon, Boolean.FALSE.toString()),
                  valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 7),
                  valintaperuste("urheilija_lisapiste", 2),
                  valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
        };

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.urheilijaLisapisteTunniste, "urheilija_lisapiste")),
            korkeakouluhaku);

    final BigDecimal odotettuTulos = new BigDecimal("33.0");

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(
                    PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaavaIlmanKonvertteria(pkAineet)),
                PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                    PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet),
                    "nimi",
                    "koulumenestys",
                    "Yleinen koulumenestys",
                    "Allmän skolframgång"),
                PkPohjaiset.luoPohjakoulutuspisteytysmalli(),
                PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(),
                PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
                PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
                PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
                PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(
                    PkJaYoPohjaiset.urheilijaLisapisteTunniste)));
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testLkPaattotodistuksenKeskiarvo() {
    Hakemus hakemus = hakemus(yhdistaMapit(luoLkAineet()));

    // Kaikkien aineiden keskiarvo: 7.5
    final BigDecimal odotettuTulos = new BigDecimal("7.5");

    Laskentakaava kaava =
        laajennaAlakaavat(
            YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos.compareTo(tulos.getTulos()), 0);
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testLkYleinenKoulumenestyspisteytysmalli() {
    Hakemus hakemus = hakemus(yhdistaMapit(luoLkAineet()));

    // Kaikkien aineiden keskiarvo: 7.5
    // Asettuu arvovälille 7.5 - 7.75

    final BigDecimal odotettuTulos = new BigDecimal("9.0");

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testToisenAsteenYlioppilaspohjainenPeruskaava() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  luoLkAineet(),
                  valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 7),
                  valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
        };

    final BigDecimal odotettuTulos = new BigDecimal("13.0");

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.urheilijaLisapisteTunniste, "urheilija_lisapiste")),
            korkeakouluhaku);

    Laskentakaava kaava =
        laajennaAlakaavat(
            YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(
                PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
                PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
                PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
                PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                    YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet),
                    "nimi",
                    "koulumenestys",
                    "Yleinen koulumenestys",
                    "Allmän skolframgång"),
                PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(
                    PkJaYoPohjaiset.urheilijaLisapisteTunniste)));
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestystasapistekaavaEnsimmainen() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("5.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestystasapistekaavaToinen() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("4.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE2,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestystasapistekaavaKolmas() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("3.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE3,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestystasapistekaavaNeljas() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("2.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE4,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestystasapistekaavaViides() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("1.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE5,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testHakutoivejarjestystasapistekaavaEiHakenut() {
    Hakemus hakemus = luoPerushakemus();
    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoHakutoivejarjestysTasapistekaava());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            new Hakukohde("ei-olemassa", new HashMap<String, String>(), korkeakouluhaku),
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testYhdistettyPeruskaavaJaKielikoekaavaHylatty() {

    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  luoPkAineet(),
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain,
                      PkPohjaiset.perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara),
                  valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi),
                  valintaperuste(
                      PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon, Boolean.FALSE.toString()),
                  valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 7),
                  valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
        };

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi"),
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.urheilijaLisapisteTunniste, "urheilija_lisapiste")),
            korkeakouluhaku);

    final BigDecimal odotettuTulos = new BigDecimal("31.0");

    Laskentakaava peruskaava =
        PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(
            PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaavaIlmanKonvertteria(pkAineet)),
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"),
            PkPohjaiset.luoPohjakoulutuspisteytysmalli(),
            PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(),
            PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
            PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
            PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
            PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(PkJaYoPohjaiset.urheilijaLisapisteTunniste));

    Laskentakaava yhdistetty =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                peruskaava, PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(yhdistetty.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
    assertEquals(
        HylattyMetatieto.Hylattymetatietotyyppi.HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS,
        ((Hylattytila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
  }

  @Test
  public void testYhdistettyPeruskaavaJaKielikoekaavaHyvaksyttavissa() {

    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  luoPkAineet(),
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain,
                      PkPohjaiset.perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara),
                  valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi),
                  valintaperuste(
                      PkPohjaiset.koulutuspaikkaAmmatilliseenTutkintoon, Boolean.FALSE.toString()),
                  valintaperuste(PkJaYoPohjaiset.tyokokemuskuukaudet, 7),
                  valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"),
                  valintaperuste("kielikoe_fi", Boolean.TRUE.toString()))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "m"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
          hakemus(yhdistaMapit(valintaperuste(PkJaYoPohjaiset.sukupuoli, "n"))),
        };

    final BigDecimal odotettuTulos = new BigDecimal("31.0");
    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi"),
                valintaperuste(PkJaYoPohjaiset.urheilijaLisapisteTunniste, "urheilija_lisapiste")),
            korkeakouluhaku);

    Laskentakaava peruskaava =
        PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(
            PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaavaIlmanKonvertteria(pkAineet)),
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"),
            PkPohjaiset.luoPohjakoulutuspisteytysmalli(),
            PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(),
            PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
            PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
            PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
            PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(PkJaYoPohjaiset.urheilijaLisapisteTunniste));

    Laskentakaava yhdistetty =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                peruskaava, PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(yhdistetty.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPohjakoulutusOnUlkomaillaSuoritettuKoulutus() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.ulkomaillaSuoritettuKoulutus),
                valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi)));

    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPohjakoulutusOnOppivelvollisuudenSuorittaminenKeskeytynyt() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.pohjakoulutusAvain,
                    PkPohjaiset.oppivelvollisuudenSuorittaminenKeskeytynyt),
                valintaperuste(PkPohjaiset.todistuksenSaantivuosi, PkPohjaiset.kuluvaVuosi)));

    final BigDecimal odotettuTulos = new BigDecimal("0.0");

    Laskentakaava kaava = laajennaAlakaavat(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testUlkomaillaSuoritettuKoulutus() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.ulkomaillaSuoritettuKoulutus)));

    Laskentakaava kaava =
        PkJaYoPohjaiset
            .luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt();
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testOppivelvollisuudenSuorittaminenKeskeytynyt() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.pohjakoulutusAvain,
                    PkPohjaiset.oppivelvollisuudenSuorittaminenKeskeytynyt)));

    Laskentakaava kaava =
        PkJaYoPohjaiset
            .luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt();
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPeruskoulupohjakoulutus() {
    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(
                    PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara)));

    Laskentakaava kaava =
        PkJaYoPohjaiset
            .luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt();
    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            hakemukset(hakemus),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testUrheilijaLisapisteMahdollisuus() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste + 1 + PkJaYoPohjaiset.koulutusIdTunniste,
                      HAKUKOHDE_OID1),
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste
                          + 1
                          + PkJaYoPohjaiset.urheilijanAmmatillisenKoulutuksenLisakysymysTunniste,
                      Boolean.TRUE.toString()))),
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste + 3 + PkJaYoPohjaiset.koulutusIdTunniste,
                      HAKUKOHDE_OID1),
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste
                          + 3
                          + PkJaYoPohjaiset.urheilijanAmmatillisenKoulutuksenLisakysymysTunniste,
                      Boolean.FALSE.toString()))),
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste + 3 + PkJaYoPohjaiset.koulutusIdTunniste,
                      HAKUKOHDE_OID1),
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste
                          + 5
                          + PkJaYoPohjaiset.urheilijanAmmatillisenKoulutuksenLisakysymysTunniste,
                      Boolean.TRUE.toString()))),
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste + 3 + PkJaYoPohjaiset.koulutusIdTunniste,
                      HAKUKOHDE_OID2),
                  valintaperuste(
                      PkJaYoPohjaiset.preferenceTunniste
                          + 3
                          + PkJaYoPohjaiset.urheilijanAmmatillisenKoulutuksenLisakysymysTunniste,
                      Boolean.TRUE.toString())))
        };

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.hakukohteenOid, HAKUKOHDE_OID1),
                valintaperuste(PkJaYoPohjaiset.urheilijaHakuSallittu, Boolean.TRUE.toString())),
            korkeakouluhaku);

    final Hakukohde hakukohde2 =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.hakukohteenOid, HAKUKOHDE_OID1),
                valintaperuste(PkJaYoPohjaiset.urheilijaHakuSallittu, Boolean.FALSE.toString())),
            korkeakouluhaku);

    final Hakukohde hakukohde3 =
        new Hakukohde(
            HAKUKOHDE_OID2,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.hakukohteenOid, HAKUKOHDE_OID2),
                valintaperuste(PkJaYoPohjaiset.urheilijaHakuSallittu, Boolean.TRUE.toString())),
            korkeakouluhaku);

    Laskentakaava kaava = laajennaAlakaavat(PkJaYoPohjaiset.luoUrheilijaLisapisteenMahdollisuus());

    Laskentatulos<Boolean> tulos =
        Laskin.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava.getFunktiokutsu()));

    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

    tulos =
        Laskin.suoritaValintalaskenta(
            hakukohde,
            hakemukset[1],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava.getFunktiokutsu()));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

    tulos =
        Laskin.suoritaValintalaskenta(
            hakukohde2,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava.getFunktiokutsu()));
    // assertFalse(tulos.getTulos());
    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

    tulos =
        Laskin.suoritaValintalaskenta(
            hakukohde,
            hakemukset[2],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava.getFunktiokutsu()));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

    tulos =
        Laskin.suoritaValintalaskenta(
            hakukohde3,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava.getFunktiokutsu()));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());

    tulos =
        Laskin.suoritaValintalaskenta(
            hakukohde,
            hakemukset[3],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava.getFunktiokutsu()));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoettaEiSuoritettuHylataanLkPeruskaava() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                  valintaperuste("kielikoe_fi", Boolean.FALSE.toString()))),
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                  valintaperuste("kielikoe_fi", Boolean.TRUE.toString())))
        };

    final BigDecimal odotettuTulos = new BigDecimal("2.0");
    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi"),
                valintaperuste(PkJaYoPohjaiset.urheilijaLisapisteTunniste, "urheilija_lisapiste")),
            korkeakouluhaku);

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(
                    PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
                    PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
                    PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
                    PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                        YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet),
                        "nimi",
                        "koulumenestys_pk",
                        "Yleinen koulumenestys",
                        "Allmän skolframgång"),
                    PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(
                        PkJaYoPohjaiset.urheilijaLisapisteTunniste)),
                PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
    assertEquals(
        HylattyMetatieto.Hylattymetatietotyyppi.HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS,
        ((Hylattytila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
    assertEquals("Hylätty kielikoetulos", ((Hylattytila) tulos.getTila()).getKuvaus().get("FI"));

    Laskentatulos<BigDecimal> tulos2 =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[1],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos2.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos2.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoettaEiSuoritettuHylataanPkPeruskaava() {
    Hakemus[] hakemukset =
        new Hakemus[] {
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                  valintaperuste("kielikoe_fi", Boolean.FALSE.toString()))),
          hakemus(
              yhdistaMapit(
                  valintaperuste(
                      PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                  valintaperuste("kielikoe_fi", Boolean.TRUE.toString())))
        };

    final BigDecimal odotettuTulos = new BigDecimal("10.0");
    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi"),
                valintaperuste(PkJaYoPohjaiset.urheilijaLisapisteTunniste, "urheilija_lisapiste")),
            korkeakouluhaku);

    Laskentakaava peruskaava =
        PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(
            PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(
                PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaavaIlmanKonvertteria(pkAineet)),
            PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(
                PkPohjaiset.luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(pkAineet),
                "nimi",
                "koulumenestys",
                "Yleinen koulumenestys",
                "Allmän skolframgång"),
            PkPohjaiset.luoPohjakoulutuspisteytysmalli(),
            PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(),
            PkJaYoPohjaiset.luoHakutoivejarjestyspisteytysmalli(),
            PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(),
            PkJaYoPohjaiset.luoSukupuolipisteytysmalli(),
            PkJaYoPohjaiset.urheilijaLisapisteLukuarvo(PkJaYoPohjaiset.urheilijaLisapisteTunniste));

    Laskentakaava yhdistetty =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaKielikoekaava(
                peruskaava, PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[0],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(yhdistetty.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
    assertEquals(
        HylattyMetatieto.Hylattymetatietotyyppi.HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS,
        ((Hylattytila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
    assertEquals("Hylätty kielikoetulos", ((Hylattytila) tulos.getTila()).getKuvaus().get("FI"));

    Laskentatulos<BigDecimal> tulos2 =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemukset[1],
            Arrays.asList(hakemukset),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(yhdistetty.getFunktiokutsu()));

    assertEquals(odotettuTulos, tulos2.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos2.getTila().getTilatyyppi());
  }

  @Test
  public void testPoikkeavanValintaryhmanLaskentakaavaHyvaksyttavissa() {
    final String valintakoetunniste = "tunniste";

    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                valintaperuste(valintakoetunniste, "5.0"),
                valintaperuste(valintakoetunniste + "-OSALLISTUMINEN", "OSALLISTUI"),
                valintaperuste(PkJaYoPohjaiset.aidinkieli, "FI")));

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi")),
            korkeakouluhaku);

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoPoikkeavanValintaryhmanLaskentakaava(
                PkJaYoPohjaiset.luoValintakoekaava(
                    valintakoetunniste, Valintaperustelahde.SYOTETTAVA_ARVO),
                PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemus,
            Arrays.asList(new Hakemus[] {hakemus}),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));
    final BigDecimal odotettuTulos = new BigDecimal("5.0");
    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testPoikkeavanValintaryhmanLaskentakaavaKielikoeSuorittamatta() {
    final String valintakoetunniste = "tunniste";

    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                valintaperuste(valintakoetunniste, "5.0"),
                valintaperuste(valintakoetunniste + "-OSALLISTUMINEN", "OSALLISTUI"),
                valintaperuste(PkJaYoPohjaiset.aidinkieli, "SV")));

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi")),
            korkeakouluhaku);

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoPoikkeavanValintaryhmanLaskentakaava(
                PkJaYoPohjaiset.luoValintakoekaava(
                    valintakoetunniste, Valintaperustelahde.SYOTETTAVA_ARVO),
                PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemus,
            Arrays.asList(new Hakemus[] {hakemus}),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));
    final BigDecimal odotettuTulos = new BigDecimal("5.0");
    assertEquals(odotettuTulos, tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
    assertEquals(
        HylattyMetatieto.Hylattymetatietotyyppi.HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS,
        ((Hylattytila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
  }

  @Test
  public void testPoikkeavanValintaryhmanLaskentakaavaValintakokeeseenEiOsallistuttu() {
    final String valintakoetunniste = "tunniste";

    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                valintaperuste(valintakoetunniste, "5.0"),
                valintaperuste(valintakoetunniste + "-OSALLISTUMINEN", "EI_OSALLISTUNUT"),
                valintaperuste(PkJaYoPohjaiset.aidinkieli, "FI")));

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.opetuskieli, "fi"),
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, "kielikoe_fi")),
            korkeakouluhaku);

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoPoikkeavanValintaryhmanLaskentakaava(
                PkJaYoPohjaiset.luoValintakoekaava(
                    valintakoetunniste, Valintaperustelahde.SYOTETTAVA_ARVO),
                PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            hakukohde,
            hakemus,
            Arrays.asList(new Hakemus[] {hakemus}),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertNull(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYLATTY, tulos.getTila().getTilatyyppi());
    assertEquals(
        HylattyMetatieto.Hylattymetatietotyyppi.EI_OSALLISTUNUT_HYLKAYS,
        ((Hylattytila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
  }

  @Test
  public void
      testPoikkeavanValintaryhmanLaskentakaavaValintakokeeseenOsallistuminenMerkitsematta() {
    final String valintakoetunniste = "tunniste";

    Hakemus hakemus =
        hakemus(
            yhdistaMapit(
                valintaperuste(PkPohjaiset.pohjakoulutusAvain, PkPohjaiset.perusopetuksenOppimaara),
                valintaperuste(valintakoetunniste, "5.0"),
                valintaperuste(valintakoetunniste + "-OSALLISTUMINEN", "MERKITSEMATTA"),
                valintaperuste(PkJaYoPohjaiset.aidinkieli, "FI")));

    Laskentakaava kaava =
        laajennaAlakaavat(
            PkJaYoPohjaiset.luoPoikkeavanValintaryhmanLaskentakaava(
                PkJaYoPohjaiset.luoValintakoekaava(
                    valintakoetunniste, Valintaperustelahde.SYOTETTAVA_ARVO),
                PkJaYoPohjaiset.luoKielikokeenPakollisuudenLaskentakaava()));

    Laskentatulos<BigDecimal> tulos =
        laskentaService.suoritaValintalaskenta(
            HAKUKOHDE1,
            hakemus,
            Arrays.asList(new Hakemus[] {hakemus}),
            Laskentadomainkonvertteri.muodostaLukuarvolasku(kaava.getFunktiokutsu()));

    assertNull(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.VIRHE, tulos.getTila().getTilatyyppi());
    assertEquals(
        VirheMetatieto.VirheMetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA,
        ((Virhetila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
  }

  /**
   * Tästä alaspäin on uuden mallin mukaisien funktioiden testejä (hakukohteiden valintaperusteet
   * yms)
   */
  @Test
  public void testKielikoeSuoritettuFunktiokutsuTrue() {
    final String hakemuksenKielikoetunniste = "kielikoeSuoritettu";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, hakemuksenKielikoetunniste)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(hakemuksenKielikoetunniste, true)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoeSuoritettuFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoeSuoritettuFunktiokutsuFalse() {
    final String hakemuksenKielikoetunniste = "kielikoeSuoritettu";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, hakemuksenKielikoetunniste)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(hakemuksenKielikoetunniste, false)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoeSuoritettuFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoeSuoritettuFunktiokutsuTunnistettaEiOleHakemuksella() {
    final String hakemuksenKielikoetunniste = "kielikoeSuoritettu";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(
                valintaperuste(PkJaYoPohjaiset.kielikoetunniste, hakemuksenKielikoetunniste)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(HAKEMUS_OID, new HashMap<>(), new HashMap<String, String>(), new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoeSuoritettuFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoeSuoritettuFunktiokutsuTunnistettaEiOleHakukohteella() {
    final Hakukohde hakukohde =
        new Hakukohde(HAKUKOHDE_OID1, new HashMap<String, String>(), korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(HAKEMUS_OID, new HashMap<>(), new HashMap<String, String>(), new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoeSuoritettuFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.VIRHE, tulos.getTila().getTilatyyppi());
    assertEquals(
        VirheMetatieto.VirheMetatietotyyppi.HAKUKOHTEEN_VALINTAPERUSTE_MAARITTELEMATTA_VIRHE,
        ((Virhetila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
  }

  @Test
  public void testAidinkieliOnOpetuskieliFunktiokutsuTrue() {
    final String kieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, kieli)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoAidinkieliOnOpetuskieliFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testAidinkieliOnOpetuskieliFunktiokutsuFalse() {
    final String opetuskieli = "fi";
    final String aidinkieli = "en";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, aidinkieli)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoAidinkieliOnOpetuskieliFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testAidinkieliOnOpetuskieliFunktiokutsuAidinkieltaEiOleHakemuksella() {
    final String opetuskieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(HAKEMUS_OID, new HashMap<>(), new HashMap<String, String>(), new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoAidinkieliOnOpetuskieliFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testAidinkieliOnOpetuskieliFunktiokutsuOpetuskieltaEiOleHakukohteella() {
    final String aidinkieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(HAKUKOHDE_OID1, new HashMap<String, String>(), korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.aidinkieli, aidinkieli)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoAidinkieliOnOpetuskieliFunktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.VIRHE, tulos.getTila().getTilatyyppi());
    assertEquals(
        VirheMetatieto.VirheMetatietotyyppi.HAKUKOHTEEN_VALINTAPERUSTE_MAARITTELEMATTA_VIRHE,
        ((Virhetila) tulos.getTila()).getMetatieto().getMetatietotyyppi());
  }

  @Test
  public void testKielikoekriteeri1FunktiokutsuTrue() {
    final String kieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.perustopetuksenKieli, kieli)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri1Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoekriteeri1FunktiokutsuFalse() {
    final String opetuskieli = "fi";
    final String perusopetuksenKieli = "en";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.perustopetuksenKieli, perusopetuksenKieli)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri1Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoekriteeri1FunktiokutsuPerusopetuksenKieliPuuttuuHakemukselta() {
    final String opetuskieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(HAKEMUS_OID, new HashMap<>(), new HashMap<String, String>(), new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri1Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoekriteeri2Funktiokutsu() {
    for (String kieli : KIELET) {
      String[] aineet = {
        Aineet.a11Kieli,
        Aineet.a12Kieli,
        Aineet.a13Kieli,
        Aineet.a21Kieli,
        Aineet.a22Kieli,
        Aineet.a23Kieli
      };

      Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri2Funktiokutsu();
      final Hakukohde hakukohde =
          new Hakukohde(
              HAKUKOHDE_OID1,
              yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
              korkeakouluhaku);

      for (String aine : aineet) {
        Hakemus[] odotettuTulosTrue = {
          hakemus(
              yhdistaMapit(
                  valintaperuste("PK_" + aine + "_OPPIAINE", kieli),
                  valintaperuste("PK_" + aine, "7.0")))
        };

        Hakemus[] odotettuTulosFalse = {
          hakemus(
              yhdistaMapit(
                  valintaperuste("PK_" + aine + "_OPPIAINE", kieli),
                  valintaperuste("PK_" + aine, "6.0"))),
          hakemus(new HashMap<String, String>())
        };

        for (Hakemus h : odotettuTulosFalse) {
          Laskentatulos<Boolean> tulos =
              laskentaService.suoritaValintakoelaskenta(
                  hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));
          assertFalse(tulos.getTulos());
          assertEquals(
              Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
          Laskentatulos<Boolean> tulos =
              laskentaService.suoritaValintakoelaskenta(
                  hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));

          assertTrue(tulos.getTulos());
          assertEquals(
              Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
      }
    }
  }

  @Test
  public void testKielikoekriteeri3Funktiokutsu() {
    for (String kieli : KIELET) {
      Hakemus[] odotettuTulosTrue = {
        hakemus(
            yhdistaMapit(
                valintaperuste("PK_AI_OPPIAINE", kieli + "_2"), valintaperuste("PK_AI", "7.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("PK_AI2_OPPIAINE", kieli + "_2"), valintaperuste("PK_AI2", "7.0")))
      };

      Hakemus[] odotettuTulosFalse = {
        hakemus(
            yhdistaMapit(
                valintaperuste("PK_AI_OPPIAINE", kieli + "_2"), valintaperuste("PK_AI", "6.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("PK_AI2_OPPIAINE", kieli + "_2"), valintaperuste("PK_AI2", "6.0"))),
        hakemus(new HashMap<String, String>())
      };
      Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri3Funktiokutsu();
      final Hakukohde hakukohde =
          new Hakukohde(
              HAKUKOHDE_OID1,
              yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
              korkeakouluhaku);

      for (Hakemus h : odotettuTulosFalse) {
        Laskentatulos<Boolean> tulos =
            laskentaService.suoritaValintakoelaskenta(
                hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));
        assertFalse(tulos.getTulos());
        assertEquals(
            Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
      }

      for (Hakemus h : odotettuTulosTrue) {
        Laskentatulos<Boolean> tulos =
            laskentaService.suoritaValintakoelaskenta(
                hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));

        assertTrue(tulos.getTulos());
        assertEquals(
            Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
      }
    }
  }

  @Test
  public void testKielikoekriteeri4Funktiokutsu() {
    for (String kieli : KIELET) {
      Hakemus fiHakemus = hakemus(yhdistaMapit(valintaperuste("lukion_kieli", kieli)));
      Hakemus tyhjaHakemus = hakemus(new HashMap<String, String>());

      Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri4Funktiokutsu();
      final Hakukohde hakukohde =
          new Hakukohde(
              HAKUKOHDE_OID1,
              yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
              korkeakouluhaku);

      Laskentatulos<Boolean> fiTulos =
          laskentaService.suoritaValintakoelaskenta(
              hakukohde, fiHakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));
      Laskentatulos<Boolean> tyhjaTulos =
          laskentaService.suoritaValintakoelaskenta(
              hakukohde, tyhjaHakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));

      assertTrue(fiTulos.getTulos());
      assertEquals(
          Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, fiTulos.getTila().getTilatyyppi());

      assertFalse(tyhjaTulos.getTulos());
      assertEquals(
          Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tyhjaTulos.getTila().getTilatyyppi());
    }
  }

  @Test
  public void testKielikoekriteeri5Funktiokutsu() {
    for (String kieli : KIELET) {
      Hakemus[] odotettuTulosFalse = {
        hakemus(
            yhdistaMapit(valintaperuste("LK_AI_OPPIAINE", kieli), valintaperuste("LK_AI", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli), valintaperuste("LK_AI2", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI_OPPIAINE", kieli + "_2"), valintaperuste("LK_AI", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli + "_2"), valintaperuste("LK_AI2", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI_OPPIAINE", kieli + "_SE"), valintaperuste("LK_AI", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli + "_SE"), valintaperuste("LK_AI2", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI_OPPIAINE", kieli + "_VK"), valintaperuste("LK_AI", "4.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli + "_VK"), valintaperuste("LK_AI2", "4.0"))),
        hakemus(new HashMap<String, String>())
      };

      Hakemus[] odotettuTulosTrue = {
        hakemus(
            yhdistaMapit(valintaperuste("LK_AI_OPPIAINE", kieli), valintaperuste("LK_AI", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli), valintaperuste("LK_AI2", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI_OPPIAINE", kieli + "_2"), valintaperuste("LK_AI", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli + "_2"), valintaperuste("LK_AI2", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI_OPPIAINE", kieli + "_SE"), valintaperuste("LK_AI", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli + "_SE"), valintaperuste("LK_AI2", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI_OPPIAINE", kieli + "_VK"), valintaperuste("LK_AI", "5.0"))),
        hakemus(
            yhdistaMapit(
                valintaperuste("LK_AI2_OPPIAINE", kieli + "_VK"), valintaperuste("LK_AI2", "5.0")))
      };

      Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri5Funktiokutsu();
      final Hakukohde hakukohde =
          new Hakukohde(
              HAKUKOHDE_OID1,
              yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
              korkeakouluhaku);

      for (Hakemus h : odotettuTulosFalse) {
        Laskentatulos<Boolean> tulos =
            laskentaService.suoritaValintakoelaskenta(
                hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));
        assertFalse(tulos.getTulos());
        assertEquals(
            Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
      }

      for (Hakemus h : odotettuTulosTrue) {
        Laskentatulos<Boolean> tulos =
            laskentaService.suoritaValintakoelaskenta(
                hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));

        assertTrue(tulos.getTulos());
        assertEquals(
            Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
      }
    }
  }

  @Test
  public void testKielikoekriteeri6Funktiokutsu() {

    String[] aineet = {
      Aineet.a11Kieli,
      Aineet.a12Kieli,
      Aineet.a13Kieli,
      Aineet.a21Kieli,
      Aineet.a22Kieli,
      Aineet.a23Kieli,
      Aineet.b1Kieli
    };

    for (String kieli : KIELET) {
      Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri6Funktiokutsu();
      final Hakukohde hakukohde =
          new Hakukohde(
              HAKUKOHDE_OID1,
              yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
              korkeakouluhaku);

      for (String aine : aineet) {
        Hakemus[] odotettuTulosTrue = {
          hakemus(
              yhdistaMapit(
                  valintaperuste("LK_" + aine + "_OPPIAINE", kieli),
                  valintaperuste("LK_" + aine, "5.0")))
        };

        Hakemus[] odotettuTulosFalse = {
          hakemus(
              yhdistaMapit(
                  valintaperuste("LK_" + aine + "_OPPIAINE", kieli),
                  valintaperuste("LK_" + aine, "4.0"))),
          hakemus(new HashMap<String, String>())
        };

        for (Hakemus h : odotettuTulosFalse) {
          Laskentatulos<Boolean> tulos =
              laskentaService.suoritaValintakoelaskenta(
                  hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));
          assertFalse(tulos.getTulos());
          assertEquals(
              Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }

        for (Hakemus h : odotettuTulosTrue) {
          Laskentatulos<Boolean> tulos =
              laskentaService.suoritaValintakoelaskenta(
                  hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));

          assertTrue(tulos.getTulos());
          assertEquals(
              Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
        }
      }
    }
  }

  @Test
  public void testKielikoekriteeri7Funktiokutsu() {

    for (String kieli : KIELET) {

      Hakemus[] odotettuTulosTrue = {
        hakemus(yhdistaMapit(valintaperuste("yleinen_kielitutkinto_" + kieli, "true"))),
        hakemus(yhdistaMapit(valintaperuste("valtionhallinnon_kielitutkinto_" + kieli, "true")))
      };

      Hakemus[] odotettuTulosFalse = {
        hakemus(yhdistaMapit(valintaperuste("yleinen_kielitutkinto_" + kieli, "false"))),
        hakemus(yhdistaMapit(valintaperuste("valtionhallinnon_kielitutkinto_" + kieli, "false"))),
        hakemus(new HashMap<String, String>())
      };

      Funktiokutsu kaava = PkJaYoPohjaiset.luoKielikoekriteeri7Funktiokutsu();
      final Hakukohde hakukohde =
          new Hakukohde(
              HAKUKOHDE_OID1,
              yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
              korkeakouluhaku);

      for (Hakemus h : odotettuTulosFalse) {
        Laskentatulos<Boolean> tulos =
            laskentaService.suoritaValintakoelaskenta(
                hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));
        assertFalse(tulos.getTulos());
        assertEquals(
            Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
      }

      for (Hakemus h : odotettuTulosTrue) {
        Laskentatulos<Boolean> tulos =
            laskentaService.suoritaValintakoelaskenta(
                hakukohde, h, Laskentadomainkonvertteri.muodostaTotuusarvolasku(kaava));

        assertTrue(tulos.getTulos());
        assertEquals(
            Hyvaksyttavissatila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
      }
    }
  }

  @Test
  public void testKielikoekriteeri8FunktiokutsuTrue() {
    final String kieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, kieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(
                valintaperuste(
                    PkJaYoPohjaiset.peruskoulunPaattotodistusVahintaanSeitseman + kieli, true)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri8Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoekriteeri8FunktiokutsuPerusopetuksenKieliPuuttuuHakemukselta() {
    final String opetuskieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(HAKEMUS_OID, new HashMap<>(), new HashMap<>(), new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri8Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoekriteeri8FunktiokutsuTrueLukio() {
    final String opetuskieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(
                valintaperuste(
                    PkJaYoPohjaiset.lukionPaattotodistusVahintaanSeitseman + opetuskieli, true)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri8Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertTrue(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }

  @Test
  public void testKielikoekriteeri8FunktiokutsuPerusopetuksenKieliEriHakemukselta() {
    final String opetuskieli = "fi";

    final Hakukohde hakukohde =
        new Hakukohde(
            HAKUKOHDE_OID1,
            yhdistaMapit(valintaperuste(PkJaYoPohjaiset.opetuskieli, opetuskieli)),
            korkeakouluhaku);

    final Hakemus hakemus =
        new Hakemus(
            HAKEMUS_OID,
            new HashMap<>(),
            yhdistaMapit(
                valintaperuste(
                    PkJaYoPohjaiset.peruskoulunPaattotodistusVahintaanSeitseman + "sv", true)),
            new HashMap<>());

    Funktiokutsu funktiokutsu = PkJaYoPohjaiset.luoKielikoekriteeri8Funktiokutsu();

    Laskentatulos<Boolean> tulos =
        laskentaService.suoritaValintakoelaskenta(
            hakukohde, hakemus, Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu));
    assertFalse(tulos.getTulos());
    assertEquals(Tila.Tilatyyppi.HYVAKSYTTAVISSA, tulos.getTila().getTilatyyppi());
  }
}
