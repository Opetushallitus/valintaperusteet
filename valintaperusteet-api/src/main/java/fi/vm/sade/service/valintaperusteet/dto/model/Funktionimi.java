package fi.vm.sade.service.valintaperusteet.dto.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Funktionimi {
  LUKUARVO(
      "Lukuarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  TOTUUSARVO(
      "Totuusarvo",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  SUMMA(
      "Summa",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  MAKSIMI(
      "Maksimi",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  MINIMI(
      "Minimi",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  TULO(
      "Tulo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  KESKIARVO(
      "Keskiarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  MEDIAANI(
      "Mediaani",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  KESKIARVONPARASTA(
      "Keskiarvo N-parasta",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  SUMMANPARASTA(
      "Summa N-parasta",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  TULONPARASTA(
      "Tulo N-parasta",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  NMAKSIMI(
      "N-maksimi",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  NMINIMI(
      "N-minimi",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  JA(
      "Ja",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  TAI(
      "Tai",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAELUKUARVO(
      "Hae lukuarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAELUKUARVOEHDOLLA(
      "Hae Lukuarvo Ehdolla",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAETOTUUSARVO(
      "Hae totuusarvo",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAEMERKKIJONOJAKONVERTOILUKUARVOKSI(
      "Hae merkkijono ja konvertoi lukuarvoksi",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAETOTUUSARVOJAKONVERTOILUKUARVOKSI(
      "Hae totuusarvo ja konvertoi lukuarvoksi",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI(
      "Hae merkkijono ja konvertoi totuusarvoksi",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  NEGAATIO(
      "Negaatio",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  OSAMAARA(
      "Osamäärä",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  JOS(
      "Jos",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  SUUREMPI(
      "Suurempi",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  PIENEMPI(
      "Pienempi",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  SUUREMPITAIYHTASUURI(
      "Suurempi tai yhtäsuuri",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  PIENEMPITAIYHTASUURI(
      "Pienempi tai yhtäsuuri",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  YHTASUURI(
      "Yhtäsuuri",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  EI(
      "Ei",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  TYHJA("Tyhjä", Funktiotyyppi.EI_VALIDI),
  NIMETTYTOTUUSARVO(
      "Nimetty totuusarvo",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  NIMETTYLUKUARVO(
      "Nimetty lukuarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  KONVERTOILUKUARVO(
      "Konvertoitulukuarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAKUTOIVE(
      "Hakutoive",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAKUTOIVERYHMASSA(
      "HakutoiveRyhmassa",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAKUKELPOISUUS(
      "Hakukelpoisuus",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  DEMOGRAFIA("Demografia", Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA),
  PYORISTYS(
      "Pyöristys",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HAEMERKKIJONOJAVERTAAYHTASUURUUS(
      "Hae merkkijono ja vertaa yhtasuuruus",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HYLKAA(
      "Hylkää",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTAKOELASKENTA,
      Laskentamoodi.VALINTALASKENTA),
  HYLKAAARVOVALILLA(
      "Hylkää arvovälillä",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  SKAALAUS("Skaalaus", Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA),
  PAINOTETTUKESKIARVO(
      "Painotettu keskiarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEYOARVOSANA(
      "Hae YO-arvosana",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEOSAKOEARVOSANA(
      "Hae YO-osakoearvosana",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  VALINTAPERUSTEYHTASUURUUS(
      "Valintaperusteyhtasuuruus",
      Funktiotyyppi.TOTUUSARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLINENYTOARVOSANA(
      "Ammatillisen tutkinnon YTO:n arvosana",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLINENYTOARVIOINTIASTEIKKO(
      "Ammatillisen tutkinnon YTO:n arviointiasteikko",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  ITEROIAMMATILLISETTUTKINNOT(
      "Iteroi ammatilliset tutkinnot",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  ITEROIAMMATILLISETOSAT(
      "Iteroi ammatilliset tutkinnon osat",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  ITEROIAMMATILLISETYTOOSAALUEET(
      "Iteroi ammatillisen tutkinnon YTO:n osa-alueet",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLISENOSANLAAJUUS(
      "Ammatillisen tutkinnon osan laajuus",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLISENOSANARVOSANA(
      "Ammatillisen tutkinnon osan arvosana",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLISENYTOOSAALUEENLAAJUUS(
      "Ammatillisen tutkinnon YTO:n osa-alueen laajuus",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLISENYTOOSAALUEENARVOSANA(
      "Ammatillisen tutkinnon YTO:n osa-alueen arvosana",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLISENTUTKINNONKESKIARVO(
      "Ammatillisen tutkinnon tallennettu keskiarvo",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA),
  HAEAMMATILLISENTUTKINNONSUORITUSTAPA(
      "Ammatillisen tutkinnon suoritustapa",
      Funktiotyyppi.LUKUARVOFUNKTIO,
      Laskentamoodi.VALINTALASKENTA,
      Laskentamoodi.VALINTAKOELASKENTA);

  public static final String JOS_LAISKA_PARAMETRI = "laskeLaiskasti";

  private final String kuvaus;
  private Funktiotyyppi tyyppi;
  private Set<Laskentamoodi> laskentamoodit;

  public static final String ITEROIAMMATILLISETTUTKINNOT_VALMISTUMIS_PARAMETRI =
      "valmistumisenTakarajaPvm";
  public static final String ITEROIAMMATILLISETTUTKINNOT_LEIKKURIPVM_PARAMETRI =
      "koskessaViimeistaanPvm";

  Funktionimi(String kuvaus, Funktiotyyppi tyyppi, Laskentamoodi... laskentamoodit) {
    this.kuvaus = kuvaus;
    this.tyyppi = tyyppi;
    this.laskentamoodit = new HashSet<Laskentamoodi>(Arrays.asList(laskentamoodit));
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public Funktiotyyppi getTyyppi() {
    return this.tyyppi;
  }

  public Set<Laskentamoodi> getLaskentamoodit() {
    return laskentamoodit;
  }

  public static final Set<Funktionimi> ammatillistenArvosanojenFunktionimet = new HashSet<>();

  static {
    ammatillistenArvosanojenFunktionimet.add(ITEROIAMMATILLISETTUTKINNOT);
    ammatillistenArvosanojenFunktionimet.add(ITEROIAMMATILLISETOSAT);
    ammatillistenArvosanojenFunktionimet.add(ITEROIAMMATILLISETYTOOSAALUEET);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLINENYTOARVIOINTIASTEIKKO);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLINENYTOARVOSANA);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLISENOSANLAAJUUS);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLISENOSANARVOSANA);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLISENYTOOSAALUEENLAAJUUS);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLISENYTOOSAALUEENARVOSANA);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLISENTUTKINNONSUORITUSTAPA);
    ammatillistenArvosanojenFunktionimet.add(HAEAMMATILLISENTUTKINNONKESKIARVO);
  }
}
