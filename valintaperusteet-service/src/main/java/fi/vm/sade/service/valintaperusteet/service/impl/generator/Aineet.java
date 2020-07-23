package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Aineet {
  public static final String aidinkieliJaKirjallisuus1 = "AI";
  public static final String aidinkieliJaKirjallisuus2 = "AI2";

  public static final String historia = "HI";
  public static final String yhteiskuntaoppi = "YH";
  public static final String matematiikka = "MA";
  public static final String fysiikka = "FY";
  public static final String kemia = "KE";
  public static final String biologia = "BI";
  public static final String kuvataide = "KU";
  public static final String musiikki = "MU";
  public static final String maantieto = "GE";
  public static final String liikunta = "LI";
  public static final String terveystieto = "TE";
  public static final String uskonto = "KT";

  // A1-kieliä voi olla kolme
  public static final String a11Kieli = "A1";
  public static final String a12Kieli = "A12";
  public static final String a13Kieli = "A13";

  // A2-kieliä voi olla kolme
  public static final String a21Kieli = "A2";
  public static final String a22Kieli = "A22";
  public static final String a23Kieli = "A23";

  // B1-kieliä voi olla yksi
  public static final String b1Kieli = "B1";

  // B2-kieliä voi olla kolme
  public static final String b21Kieli = "B2";
  public static final String b22Kieli = "B22";
  public static final String b23Kieli = "B23";

  // B3-kieliä voi olla kolme
  public static final String b31Kieli = "B3";
  public static final String b32Kieli = "B32";
  public static final String b33Kieli = "B33";

  private enum Aine {
    AI_1(aidinkieliJaKirjallisuus1, "1. Äidinkieli ja kirjallisuus"),
    AI_2(aidinkieliJaKirjallisuus2, "2. Äidinkieli ja kirjallisuus"),
    HI(historia, "Historia"),
    YH(yhteiskuntaoppi, "Yhteiskuntaoppi"),
    MA(matematiikka, "Matematiikka"),
    FY(fysiikka, "Fysiikka"),
    KE(kemia, "Kemia"),
    BI(biologia, "Biologia"),
    KU(kuvataide, "Kuvataide"),
    MU(musiikki, "Musiikki"),
    GE(maantieto, "Maantieto"),
    LI(liikunta, "Liikunta"),
    TE(terveystieto, "Terveystieto"),
    KT(uskonto, "Uskonto tai elämänkatsomustieto"),
    A1_1(a11Kieli, "1. A1-kieli"),
    A1_2(a12Kieli, "2. A1-kieli"),
    A1_3(a13Kieli, "3. A1-kieli"),
    A2_1(a21Kieli, "1. A2-kieli"),
    A2_2(a22Kieli, "2. A2-kieli"),
    A2_3(a23Kieli, "3. A2-kieli"),
    B1_1(b1Kieli, "B1 kieli"),
    B2_1(b21Kieli, "1. B2-kieli"),
    B2_2(b22Kieli, "2. B2-kieli"),
    B2_3(b23Kieli, "3. B2-kieli"),
    B3_1(b31Kieli, "1. B3-kieli"),
    B3_2(b32Kieli, "2. B3-kieli"),
    B3_3(b33Kieli, "3. B3-kieli");

    Aine(String tunniste, String kuvaus) {
      this.tunniste = tunniste;
      this.kuvaus = kuvaus;
    }

    private String tunniste;
    private String kuvaus;
  }

  private Map<String, String> aineet = new HashMap<String, String>();
  private Map<String, Laskentakaava> kaavat = new HashMap<String, Laskentakaava>();

  public Aineet() {
    for (Aine a : Aine.values()) {
      aineet.put(a.tunniste, a.kuvaus);
    }
  }

  protected Map<String, String> getAineet() {
    return aineet;
  }

  protected Map<String, Laskentakaava> getKaavat() {
    return kaavat;
  }

  public Laskentakaava getLaskentakaava(String tunniste) {
    return kaavat.get(tunniste);
  }

  public Collection<Laskentakaava> getLaskentakaavat() {
    return kaavat.values();
  }
}
