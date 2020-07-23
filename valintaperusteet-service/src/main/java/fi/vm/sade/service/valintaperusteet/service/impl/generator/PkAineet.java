package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import java.util.Map;

public class PkAineet extends Aineet {
  public static final String PK_Valinnainen1 = "_VAL1";
  public static final String PK_Valinnainen2 = "_VAL2";
  public static final String PK_etuliite = "PK_";
  public static final String PK_kymppiluokka = "_10";
  public static final String PK_kuvausjalkiliite = ", PK päättötodistus, mukaanlukien valinnaiset";

  public static final String kotitalous = "KO";
  public static final String kasityo = "KS";

  public static final String PK_OPPIAINE_TEMPLATE = PK_etuliite + "%s_OPPIAINE";

  private enum PkAine {
    KO(kotitalous, "Kotitalous"),
    KS(kasityo, "Käsityö");

    PkAine(String tunniste, String kuvaus) {
      this.tunniste = tunniste;
      this.kuvaus = kuvaus;
    }

    String tunniste;
    String kuvaus;
  }

  public PkAineet() {
    for (PkAine aine : PkAine.values()) {
      getAineet().put(aine.tunniste, aine.kuvaus);
    }

    for (Map.Entry<String, String> aine : getAineet().entrySet()) {
      String ainetunniste = aine.getKey();
      String ainekuvaus = aine.getValue();

      getKaavat().put(ainetunniste, luoPKAine(ainetunniste, ainekuvaus));
    }
  }

  public static String pakollinen(String ainetunniste) {
    return PK_etuliite + ainetunniste;
  }

  public static String kymppi(String ainetunniste) {
    return PK_etuliite + ainetunniste + PK_kymppiluokka;
  }

  public static String valinnainen1(String ainetunniste) {
    return PK_etuliite + ainetunniste + PK_Valinnainen1;
  }

  public static String valinnainen1kymppi(String ainetunniste) {
    return PK_etuliite + ainetunniste + PK_Valinnainen1 + PK_kymppiluokka;
  }

  public static String valinnainen2(String ainetunniste) {
    return PK_etuliite + ainetunniste + PK_Valinnainen2;
  }

  public static String valinnainen2kymppi(String ainetunniste) {
    return PK_etuliite + ainetunniste + PK_Valinnainen2 + PK_kymppiluokka;
  }

  private Laskentakaava luoPKAine(String ainetunniste, String kuvaus) {
    Funktiokutsu aine =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                pakollinen(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO));
    Funktiokutsu aine_kymppiluokka =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                kymppi(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO));
    Funktiokutsu aine_max = GenericHelper.luoMaksimi(aine, aine_kymppiluokka);
    Funktiokutsu aineValinnainen1 =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                valinnainen1(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO));
    Funktiokutsu aineValinnainen1_kymppiluokka =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                valinnainen1kymppi(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO));
    Funktiokutsu valinnainen1_max =
        GenericHelper.luoMaksimi(aineValinnainen1, aineValinnainen1_kymppiluokka);
    Funktiokutsu aineValinnainen2 =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                valinnainen2(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO));
    Funktiokutsu aineValinnainen2_kymppiluokka =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                valinnainen2kymppi(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO));
    Funktiokutsu valinnainen2_max =
        GenericHelper.luoMaksimi(aineValinnainen2, aineValinnainen2_kymppiluokka);
    Funktiokutsu valinnainenKeskiarvo =
        GenericHelper.luoKeskiarvo(valinnainen1_max, valinnainen2_max);
    Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(aine_max, valinnainenKeskiarvo);
    Laskentakaava laskentakaava =
        GenericHelper.luoLaskentakaavaJaNimettyFunktio(keskiarvo, kuvaus + PK_kuvausjalkiliite);
    return laskentakaava;
  }

  public static String oppiaine(String ainetunniste) {
    return String.format(PK_OPPIAINE_TEMPLATE, ainetunniste);
  }
}
