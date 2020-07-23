package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import java.util.Map;

public class YoAineet extends Aineet {
  public static final String filosofia = "FI";
  public static final String psykologia = "PS";
  public static final String LK_ETULIITE = "LK_";
  public static final String LK_KUVAUSJALKILIITE = ", LK päättötodistus";
  public static final String LK_OPPIAINE_TEMPLATE = LK_ETULIITE + "%s_OPPIAINE";

  private enum YoAine {
    FI(filosofia, "Filosofia"),
    PS(psykologia, "Psykologia");

    YoAine(String tunniste, String kuvaus) {
      this.tunniste = tunniste;
      this.kuvaus = kuvaus;
    }

    String tunniste;
    String kuvaus;
  }

  public YoAineet() {
    for (YoAine aine : YoAine.values()) {
      getAineet().put(aine.tunniste, aine.kuvaus);
    }
    for (Map.Entry<String, String> aine : getAineet().entrySet()) {
      String ainetunniste = aine.getKey();
      String ainekuvaus = aine.getValue();
      getKaavat().put(ainetunniste, luoYOAine(ainetunniste, ainekuvaus));
    }
  }

  public static String pakollinen(String ainetunniste) {
    return LK_ETULIITE + ainetunniste;
  }

  public Laskentakaava luoYOAine(String ainetunniste, String ainekuvaus) {
    Funktiokutsu aine =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                pakollinen(ainetunniste),
                false,
                fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde.HAETTAVA_ARVO));
    Laskentakaava laskentakaava =
        GenericHelper.luoLaskentakaavaJaNimettyFunktio(aine, ainekuvaus + LK_KUVAUSJALKILIITE);
    return laskentakaava;
  }

  public static String oppiaine(String ainetunniste) {
    return String.format(LK_OPPIAINE_TEMPLATE, ainetunniste);
  }
}
