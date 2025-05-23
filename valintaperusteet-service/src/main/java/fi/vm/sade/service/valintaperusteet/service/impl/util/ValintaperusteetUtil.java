package fi.vm.sade.service.valintaperusteet.service.impl.util;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.service.impl.LaskentakaavaServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValintaperusteetUtil {
  private static Logger LOG = LoggerFactory.getLogger(ValintaperusteetUtil.class);

  private static String haeTunniste(
      String mustache, Map<String, String> hakukohteenValintaperusteet) {
    final Matcher m = LaskentakaavaServiceImpl.pattern.matcher(mustache);
    String avain = null;
    while (m.find()) {
      if (!m.group(1).isEmpty() && m.group(1).contentEquals("hakukohde") && !m.group(2).isEmpty()) {
        avain = m.group(2);
      }
    }
    if (avain == null) {
      return mustache;
    } else {
      return hakukohteenValintaperusteet.get(avain);
    }
  }

  public static Map<String, ValintaperusteDTO> haeAvaimet(
      Funktiokutsu funktiokutsu, Map<String, String> hakukohteenValintaperusteet) {
    Map<String, ValintaperusteDTO> valintaperusteet = new HashMap<>();
    for (ValintaperusteViite vp : funktiokutsu.getValintaperusteviitteet()) {
      if (Valintaperustelahde.SYOTETTAVA_ARVO.equals(vp.getLahde())
          || fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde
              .HAKUKOHTEEN_SYOTETTAVA_ARVO
              .equals(vp.getLahde())) {

        ValintaperusteDTO valintaperuste = new ValintaperusteDTO();
        valintaperuste.setFunktiotyyppi(
            new ModelMapper()
                .map(
                    funktiokutsu.getFunktionimi().getTyyppi(),
                    fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.class));
        valintaperuste.setTunniste(vp.getTunniste());
        valintaperuste.setVaatiiOsallistumisen(vp.getVaatiiOsallistumisen());
        valintaperuste.setSyotettavissaKaikille(vp.getSyotettavissaKaikille());
        valintaperuste.setKuvaus(vp.getKuvaus());
        valintaperuste.setLahde(
            fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde.SYOTETTAVA_ARVO);
        valintaperuste.setOnPakollinen(vp.getOnPakollinen());
        valintaperuste.setOsallistuminenTunniste(vp.getOsallistuminenTunniste());
        valintaperuste.setTilastoidaan(vp.getTilastoidaan());
        if (null != vp.getSyotettavanarvontyyppi()) {
          valintaperuste.setSyötettavanArvonTyyppi(
              new ModelMapper()
                  .map(
                      vp.getSyotettavanarvontyyppi(),
                      fi.vm.sade.service.valintaperusteet.dto.KoodiDTO.class));
        }

        if (vp.getEpasuoraViittaus() != null && vp.getEpasuoraViittaus()) {
          valintaperuste.setTunniste(hakukohteenValintaperusteet.get(vp.getTunniste()));
          valintaperuste.setOsallistuminenTunniste(
              valintaperuste.getTunniste() + ValintaperusteViite.OSALLISTUMINEN_POSTFIX);
        }

        if (funktiokutsu.getArvokonvertteriparametrit() != null
            && funktiokutsu.getArvokonvertteriparametrit().size() > 0) {
          List<String> arvot = new ArrayList<String>();
          for (Arvokonvertteriparametri ap : funktiokutsu.getArvokonvertteriparametrit()) {
            arvot.add(haeTunniste(ap.getArvo(), hakukohteenValintaperusteet));
          }
          valintaperuste.setArvot(arvot);
        } else if (funktiokutsu.getArvovalikonvertteriparametrit() != null
            && funktiokutsu.getArvovalikonvertteriparametrit().size() > 0) {
          BigDecimal min = null;
          BigDecimal max = null;
          for (Arvovalikonvertteriparametri av : funktiokutsu.getArvovalikonvertteriparametrit()) {
            try {
              BigDecimal current =
                  new BigDecimal(
                      (haeTunniste(av.getMinValue(), hakukohteenValintaperusteet))
                          .replace(',', '.'));
              if (min == null || current.compareTo(min) < 0) {
                min = current;
              }
            } catch (NumberFormatException e) {
              LOG.error("Cannot convert min value {} to BigDecimal", av.getMinValue());
            }

            try {
              BigDecimal current =
                  new BigDecimal(
                      (haeTunniste(av.getMaxValue(), hakukohteenValintaperusteet))
                          .replace(',', '.'));
              if (max == null || current.compareTo(max) > 0) {
                max = current;
              }
            } catch (NumberFormatException e) {
              LOG.error("Cannot convert max value {} to BigDecimal", av.getMaxValue());
            }
          }
          valintaperuste.setMin(min != null ? min.toString() : null);
          valintaperuste.setMax(max != null ? max.toString() : null);
        }
        valintaperusteet.put(valintaperuste.getTunniste(), valintaperuste);
      }
    }
    return valintaperusteet;
  }
}
