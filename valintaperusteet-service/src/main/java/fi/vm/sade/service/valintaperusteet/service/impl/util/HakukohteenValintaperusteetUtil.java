package fi.vm.sade.service.valintaperusteet.service.impl.util;

import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import java.util.ArrayList;
import java.util.List;

public class HakukohteenValintaperusteetUtil {

  public static Funktiokutsu haeAvaimet(
      Funktiokutsu funktiokutsu, HakukohteenValintaperusteAvaimetDTO valintaperusteet) {
    List<String> tunnisteet = new ArrayList<String>();
    List<String> arvot = new ArrayList<String>();
    List<String> hylkaysperusteet = new ArrayList<String>();
    List<String> minimit = new ArrayList<String>();
    List<String> maksimit = new ArrayList<String>();
    List<String> palautaHaetutArvot = new ArrayList<String>();
    for (ValintaperusteViite vp : funktiokutsu.getValintaperusteviitteet()) {
      Valintaperustelahde lahde = vp.getLahde();
      if (lahde.equals(Valintaperustelahde.HAKUKOHTEEN_ARVO)
          || lahde.equals(Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO)) {
        tunnisteet.add(vp.getTunniste());
      }
      if (funktiokutsu.getArvokonvertteriparametrit() != null
          && funktiokutsu.getArvokonvertteriparametrit().size() > 0) {
        for (Arvokonvertteriparametri ap : funktiokutsu.getArvokonvertteriparametrit()) {
          if (ap.getArvo().contains("hakukohde") && ap.getArvo().startsWith("{{")) {
            arvot.add(ap.getArvo());
          }
          if (ap.getHylkaysperuste().contains("hakukohde")
              && ap.getHylkaysperuste().startsWith("{{")) {
            hylkaysperusteet.add(ap.getHylkaysperuste());
          }
        }
      } else if (funktiokutsu.getArvovalikonvertteriparametrit() != null
          && funktiokutsu.getArvovalikonvertteriparametrit().size() > 0) {
        for (Arvovalikonvertteriparametri ap : funktiokutsu.getArvovalikonvertteriparametrit()) {
          if (ap.getMinValue().contains("hakukohde") && ap.getMinValue().startsWith("{{")) {
            minimit.add(ap.getMinValue());
          }
          if (ap.getMaxValue().contains("hakukohde") && ap.getMaxValue().startsWith("{{")) {
            maksimit.add(ap.getMaxValue());
          }
          if (ap.getPalautaHaettuArvo().contains("hakukohde")
              && ap.getPalautaHaettuArvo().startsWith("{{")) {
            palautaHaetutArvot.add(ap.getPalautaHaettuArvo());
          }
        }
      }
    }
    if (tunnisteet.size() > 0) {
      if (valintaperusteet.getTunnisteet() == null) {
        valintaperusteet.setTunnisteet(tunnisteet);
      } else {
        List<String> temp = valintaperusteet.getTunnisteet();
        temp.addAll(tunnisteet);
        valintaperusteet.setTunnisteet(temp);
      }
    }

    if (arvot.size() > 0) {
      if (valintaperusteet.getArvot() == null) {
        valintaperusteet.setArvot(arvot);
      } else {
        List<String> temp = valintaperusteet.getArvot();
        temp.addAll(arvot);
        valintaperusteet.setArvot(temp);
      }
    }

    if (hylkaysperusteet.size() > 0) {
      if (valintaperusteet.getHylkaysperusteet() == null) {
        valintaperusteet.setHylkaysperusteet(hylkaysperusteet);
      } else {
        List<String> temp = valintaperusteet.getHylkaysperusteet();
        temp.addAll(hylkaysperusteet);
        valintaperusteet.setHylkaysperusteet(temp);
      }
    }

    if (minimit.size() > 0) {
      if (valintaperusteet.getMinimit() == null) {
        valintaperusteet.setMinimit(minimit);
      } else {
        List<String> temp = valintaperusteet.getMinimit();
        temp.addAll(minimit);
        valintaperusteet.setMinimit(temp);
      }
    }

    if (maksimit.size() > 0) {
      if (valintaperusteet.getMaksimit() == null) {
        valintaperusteet.setMaksimit(maksimit);
      } else {
        List<String> temp = valintaperusteet.getMaksimit();
        temp.addAll(maksimit);
        valintaperusteet.setMaksimit(temp);
      }
    }

    if (palautaHaetutArvot.size() > 0) {
      if (valintaperusteet.getPalautaHaettutArvot() == null) {
        valintaperusteet.setPalautaHaettutArvot(palautaHaetutArvot);
      } else {
        List<String> temp = valintaperusteet.getPalautaHaettutArvot();
        temp.addAll(palautaHaetutArvot);
        valintaperusteet.setPalautaHaettutArvot(temp);
      }
    }

    return funktiokutsu;
  }
}
