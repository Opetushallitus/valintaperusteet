package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.util.ArrayList;
import java.util.List;

public class LukionValintaperusteet {
  public static final String PAINOKERROIN_POSTFIX = "_painokerroin";
  public static final String AINE_PREFIX = "PK_";
  public static final String KYMPPILUOKKA_SUFFIX = "_10";

  public static final String AIDINKIELI_JA_KIRJALLISUUS1 = "AI";
  public static final String AIDINKIELI_JA_KIRJALLISUUS2 = "AI2";

  // A1-kieliä voi olla kolme
  public static final String A11KIELI = "A1";
  public static final String A12KIELI = "A12";
  public static final String A13KIELI = "A13";

  // A2-kieliä voi olla kolme
  public static final String A21KIELI = "A2";
  public static final String A22KIELI = "A22";
  public static final String A23KIELI = "A23";

  // B1-kieliä voi olla yksi
  public static final String B1KIELI = "B1";

  // B2-kieliä voi olla kolme
  public static final String B21KIELI = "B2";
  public static final String B22KIELI = "B22";
  public static final String B23KIELI = "B23";

  // B3-kieliä voi olla kolme
  public static final String B31KIELI = "B3";
  public static final String B32KIELI = "B32";
  public static final String B33KIELI = "B33";

  public static final String USKONTO = "KT";
  public static final String HISTORIA = "HI";
  public static final String YHTEISKUNTAOPPI = "YH";
  public static final String MATEMATIIKKA = "MA";
  public static final String FYSIIKKA = "FY";
  public static final String KEMIA = "KE";
  public static final String BIOLOGIA = "BI";
  public static final String TERVEYSTIETO = "TE";
  public static final String MAANTIETO = "GE";

  public static final String LIIKUNTA = "LI";
  public static final String KASITYO = "KS";
  public static final String KOTITALOUS = "KO";
  public static final String MUSIIKKI = "MU";
  public static final String KUVATAIDE = "KU";

  public static final String SAKSA = "DE";
  public static final String KREIKKA = "EL";
  public static final String ENGLANTI = "EN";
  public static final String ESPANJA = "ES";
  public static final String EESTI = "ET";
  public static final String SUOMI = "FI";
  public static final String RANSKA = "FR";
  public static final String ITALIA = "IT";
  public static final String JAPANI = "JA";
  public static final String LATINA = "LA";
  public static final String LIETTUA = "LT";
  public static final String LATVIA = "LV";
  public static final String PORTUGALI = "PT";
  public static final String VENAJA = "RU";
  public static final String SAAME = "SE";
  public static final String RUOTSI = "SV";
  public static final String VIITTOMAKIELI = "VK";
  public static final String KIINA = "ZH";
  public static final String MUUKIELI = "XX";

  public static final String[] LUKUAINEET = {
    AIDINKIELI_JA_KIRJALLISUUS1,
    AIDINKIELI_JA_KIRJALLISUUS2,
    USKONTO,
    HISTORIA,
    YHTEISKUNTAOPPI,
    MATEMATIIKKA,
    FYSIIKKA,
    KEMIA,
    BIOLOGIA,
    TERVEYSTIETO,
    MAANTIETO
  };

  public static final String[] KIELET = {
    A11KIELI, A12KIELI, A13KIELI, A21KIELI, A22KIELI, A23KIELI, B1KIELI, B21KIELI, B22KIELI,
    B23KIELI, B31KIELI, B32KIELI, B33KIELI
  };

  public static final String[] KIELIKOODIT = {
    SAKSA,
    KREIKKA,
    ENGLANTI,
    ESPANJA,
    EESTI,
    SUOMI,
    RANSKA,
    ITALIA,
    JAPANI,
    LATINA,
    LIETTUA,
    LATVIA,
    PORTUGALI,
    VENAJA,
    SAAME,
    RUOTSI,
    VIITTOMAKIELI,
    KIINA,
    MUUKIELI
  };

  public static final String[] TAITO_JA_TAIDEAINEET = {
    LIIKUNTA, KASITYO, KOTITALOUS, MUSIIKKI, KUVATAIDE
  };

  public static Laskentakaava painotettuLukuaineidenKeskiarvoJaPaasykoe(
      Laskentakaava ka, Laskentakaava paasykoe) {
    Funktiokutsu summa = GenericHelper.luoSumma(ka, paasykoe);
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        summa, "Lukion valintaperusteet, painotettu keskiarvo ja pääsykoe");
  }

  public static Laskentakaava painotettuLukuaineidenKeskiarvoJaLisanaytto(
      Laskentakaava ka, Laskentakaava lisanaytto) {
    Funktiokutsu summa = GenericHelper.luoSumma(ka, lisanaytto);
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        summa, "Lukion valintaperusteet, painotettu keskiarvo ja lisänäyttö");
  }

  public static Laskentakaava painotettuLukuaineidenKeskiarvoJaPaasykoeJaLisanaytto(
      Laskentakaava ka, Laskentakaava paasykoeJaLisanaytto) {
    Funktiokutsu summa = GenericHelper.luoSumma(ka, paasykoeJaLisanaytto);
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        summa, "Lukion valintaperusteet, painotettu keskiarvo, pääsykoe ja lisänäyttö");
  }

  public static Laskentakaava painotettuLukuaineidenKeskiarvo() {
    String minimi = "{{hakukohde.painotettu_keskiarvo_hylkays_min}}";
    String maksimi = "{{hakukohde.painotettu_keskiarvo_hylkays_max}}";
    List<GenericHelper.Painotus> painotukset = new ArrayList<GenericHelper.Painotus>();
    for (String aine : LUKUAINEET) {
      Funktiokutsu arvo =
          GenericHelper.luoHaeLukuarvo(
              GenericHelper.luoValintaperusteViite(
                  AINE_PREFIX + aine, false, Valintaperustelahde.HAETTAVA_ARVO));
      Funktiokutsu arvo_kymppiluokka =
          GenericHelper.luoHaeLukuarvo(
              GenericHelper.luoValintaperusteViite(
                  AINE_PREFIX + aine + KYMPPILUOKKA_SUFFIX,
                  false,
                  Valintaperustelahde.HAETTAVA_ARVO));
      Funktiokutsu max = GenericHelper.luoMaksimi(arvo, arvo_kymppiluokka);
      Funktiokutsu painokerroin =
          GenericHelper.luoHaeLukuarvo(
              GenericHelper.luoValintaperusteViite(
                  aine + PAINOKERROIN_POSTFIX, false, Valintaperustelahde.HAKUKOHTEEN_ARVO),
              1.0);
      painotukset.add(new GenericHelper.Painotus(painokerroin, max));
    }
    for (String aine : KIELET) {
      for (String koodi : KIELIKOODIT) {
        String avain = "{{" + AINE_PREFIX + aine + "_OPPIAINE." + koodi + "}}";
        ValintaperusteViite vp =
            GenericHelper.luoValintaperusteViite(avain, false, Valintaperustelahde.HAETTAVA_ARVO);
        Funktiokutsu arvo =
            GenericHelper.luoHaeLukuarvoEhdolla(
                GenericHelper.luoValintaperusteViite(
                    AINE_PREFIX + aine, false, Valintaperustelahde.HAETTAVA_ARVO),
                vp);
        Funktiokutsu arvo_kymppiluokka =
            GenericHelper.luoHaeLukuarvoEhdolla(
                GenericHelper.luoValintaperusteViite(
                    AINE_PREFIX + aine + KYMPPILUOKKA_SUFFIX,
                    false,
                    Valintaperustelahde.HAETTAVA_ARVO),
                vp);
        Funktiokutsu max = GenericHelper.luoMaksimi(arvo, arvo_kymppiluokka);
        Funktiokutsu painokerroin =
            GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(
                    aine + "_" + koodi + PAINOKERROIN_POSTFIX,
                    false,
                    Valintaperustelahde.HAKUKOHTEEN_ARVO),
                1.0);
        painotukset.add(new GenericHelper.Painotus(painokerroin, max));
      }
    }
    for (String aine : TAITO_JA_TAIDEAINEET) {
      Funktiokutsu arvo =
          GenericHelper.luoHaeLukuarvo(
              GenericHelper.luoValintaperusteViite(
                  AINE_PREFIX + aine, false, Valintaperustelahde.HAETTAVA_ARVO));
      Funktiokutsu arvo_kymppiluokka =
          GenericHelper.luoHaeLukuarvo(
              GenericHelper.luoValintaperusteViite(
                  AINE_PREFIX + aine + KYMPPILUOKKA_SUFFIX,
                  false,
                  Valintaperustelahde.HAETTAVA_ARVO));
      Funktiokutsu max = GenericHelper.luoMaksimi(arvo, arvo_kymppiluokka);
      Funktiokutsu painokerroin =
          GenericHelper.luoHaeLukuarvo(
              GenericHelper.luoValintaperusteViite(
                  aine + PAINOKERROIN_POSTFIX, false, Valintaperustelahde.HAKUKOHTEEN_ARVO));
      painotukset.add(new GenericHelper.Painotus(painokerroin, max));
    }
    Funktiokutsu painotuksetFunktio =
        GenericHelper.luoPainotettuKeskiarvo(
            painotukset.toArray(new GenericHelper.Painotus[painotukset.size()]));
    Funktiokutsu pyoristetty = GenericHelper.luoPyoristys(painotuksetFunktio, 2);
    pyoristetty.setTallennaTulos(true);
    pyoristetty.setTulosTunniste("painotettu_keskiarvo");
    pyoristetty.setTulosTekstiFi("Keskiarvo");
    pyoristetty.setTulosTekstiSv("Medeltalet");
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        GenericHelper.luoHylkaaArvovalilla(
            pyoristetty,
            "Lukion asettama keskiarvoraja ei ylity",
            "Uppnår inte av gymnasiet fastställda lägsta godkända medeltalsgräns",
            minimi,
            maksimi),
        "Lukion valintaperusteet, painotettu keskiarvo");
  }

  public static Laskentakaava paasykoeJaLisanaytto(
      Laskentakaava paasykoe, Laskentakaava lisanaytto) {
    Funktiokutsu summa = GenericHelper.luoSumma(paasykoe, lisanaytto);
    String minimi = "{{hakukohde.paasykoe_ja_lisanaytto_hylkays_min}}";
    String maksimi = "{{hakukohde.paasykoe_ja_lisanaytto_hylkays_max}}";
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        GenericHelper.luoHylkaaArvovalilla(
            summa,
            "Pääsykokeen ja lisänäytön alin hyväksyttävä yhteispistemäärä ei ylity",
            "Uppnår inte lägsta godkända sammanlagda poängantal för inträdesprov och tilläggsprestation",
            minimi,
            maksimi),
        "Lukion valintaperusteet, pääsykoe ja lisänäyttö");
  }

  public static Laskentakaava paasykoeLukuarvo(String paasykoeTunniste) {
    String minimi = "{{hakukohde.paasykoe_hylkays_min}}";
    String maksimi = "{{hakukohde.paasykoe_hylkays_max}}";
    String alaraja = "{{hakukohde.paasykoe_min}}";
    String ylaraja = "{{hakukohde.paasykoe_max}}";
    List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
    konvs.add(GenericHelper.luoArvovalikonvertteriparametri(alaraja, ylaraja));
    TekstiRyhma kuvaukset = new TekstiRyhma();
    LokalisoituTeksti fi = new LokalisoituTeksti();
    fi.setKieli(Kieli.FI);
    fi.setTeksti("Ei ole osallistunut pääsykokeeseen");
    kuvaukset.getTekstit().add(fi);
    LokalisoituTeksti sv = new LokalisoituTeksti();
    sv.setKieli(Kieli.SV);
    sv.setTeksti("Har inte deltagit i inträdesprov");
    kuvaukset.getTekstit().add(sv);
    Funktiokutsu funktiokutsu =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                paasykoeTunniste,
                true,
                Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO,
                "Pääsykoe",
                true,
                kuvaukset),
            konvs);
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        GenericHelper.luoHylkaaArvovalilla(
            funktiokutsu,
            "Pääsykokeen alin hyväksyttävä pistemäärä ei ylity",
            "Uppnår inte lägsta godkända poängantal för inträdesprov",
            minimi,
            maksimi),
        "Lukion valintaperusteet, pääsykoe");
  }

  public static Laskentakaava lisanayttoLukuarvo(String lisanayttoTunniste) {
    String minimi = "{{hakukohde.lisanaytto_hylkays_min}}";
    String maksimi = "{{hakukohde.lisanaytto_hylkays_max}}";
    String alaraja = "{{hakukohde.lisanaytto_min}}";
    String ylaraja = "{{hakukohde.lisanaytto_max}}";
    List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
    konvs.add(GenericHelper.luoArvovalikonvertteriparametri(alaraja, ylaraja));
    TekstiRyhma kuvaukset = new TekstiRyhma();
    LokalisoituTeksti fi = new LokalisoituTeksti();
    fi.setKieli(Kieli.FI);
    fi.setTeksti("Ei ole osallistunut lisänäyttöön");
    kuvaukset.getTekstit().add(fi);
    LokalisoituTeksti sv = new LokalisoituTeksti();
    sv.setKieli(Kieli.SV);
    sv.setTeksti("Har inte deltagit i tilläggsprestation");
    kuvaukset.getTekstit().add(sv);
    Funktiokutsu funktiokutsu =
        GenericHelper.luoHaeLukuarvo(
            GenericHelper.luoValintaperusteViite(
                lisanayttoTunniste,
                true,
                Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO,
                "Lisänäyttö",
                true,
                kuvaukset),
            konvs);
    return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
        GenericHelper.luoHylkaaArvovalilla(
            funktiokutsu,
            "Lisänäytön alin hyväksyttävä pistemäärä ei ylity",
            "Uppnår inte lägsta godkända poängantal för tilläggsprestation",
            minimi,
            maksimi),
        "Lukion valintaperusteet, lisänäyttö");
  }

  public static Laskentakaava luoKaikkienAineidenKeskiarvo(LukionPkAineet pkAineet) {
    Laskentakaava[] args =
        new Laskentakaava[] {
          pkAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus1),
          pkAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus2),
          pkAineet.getLaskentakaava(Aineet.historia),
          pkAineet.getLaskentakaava(Aineet.yhteiskuntaoppi),
          pkAineet.getLaskentakaava(Aineet.matematiikka),
          pkAineet.getLaskentakaava(Aineet.fysiikka),
          pkAineet.getLaskentakaava(Aineet.kemia),
          pkAineet.getLaskentakaava(Aineet.biologia),
          pkAineet.getLaskentakaava(Aineet.kuvataide),
          pkAineet.getLaskentakaava(Aineet.musiikki),
          pkAineet.getLaskentakaava(Aineet.maantieto),
          pkAineet.getLaskentakaava(PkAineet.kasityo),
          pkAineet.getLaskentakaava(PkAineet.kotitalous),
          pkAineet.getLaskentakaava(Aineet.liikunta),
          pkAineet.getLaskentakaava(Aineet.terveystieto),
          pkAineet.getLaskentakaava(Aineet.uskonto),
          pkAineet.getLaskentakaava(Aineet.a11Kieli),
          pkAineet.getLaskentakaava(Aineet.a12Kieli),
          pkAineet.getLaskentakaava(Aineet.a13Kieli),
          pkAineet.getLaskentakaava(Aineet.a21Kieli),
          pkAineet.getLaskentakaava(Aineet.a22Kieli),
          pkAineet.getLaskentakaava(Aineet.a23Kieli),
          pkAineet.getLaskentakaava(Aineet.b1Kieli),
          pkAineet.getLaskentakaava(Aineet.b21Kieli),
          pkAineet.getLaskentakaava(Aineet.b22Kieli),
          pkAineet.getLaskentakaava(Aineet.b23Kieli),
          pkAineet.getLaskentakaava(Aineet.b31Kieli),
          pkAineet.getLaskentakaava(Aineet.b32Kieli),
          pkAineet.getLaskentakaava(Aineet.b33Kieli)
        };
    Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(args);
    Funktiokutsu pyoristetty = GenericHelper.luoPyoristys(keskiarvo, 2);
    Laskentakaava laskentakaava =
        GenericHelper.luoLaskentakaavaJaNimettyFunktio(
            pyoristetty, "Kaikkien aineiden keskiarvo, lukiokoulutus");
    return laskentakaava;
  }
}
