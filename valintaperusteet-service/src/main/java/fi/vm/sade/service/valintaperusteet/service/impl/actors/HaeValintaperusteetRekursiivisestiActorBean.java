package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.service.impl.LaskentakaavaServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiValintaperusteRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.util.FunktiokutsuCache;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import javax.inject.Named;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Named("HaeValintaperusteetRekursiivisestiActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class HaeValintaperusteetRekursiivisestiActorBean extends UntypedAbstractActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  private int funktiokutsuLapset = 0;

  @Autowired private FunktiokutsuDAO funktiokutsuDAO;

  private Funktiokutsu original;
  private Map<String, ValintaperusteDTO> valintaperusteet;
  private Map<String, String> hakukohteenValintaperusteet;

  private ActorRef actorParent = null;

  @Autowired private FunktiokutsuCache funktiokutsuCache;

  public HaeValintaperusteetRekursiivisestiActorBean() {}

  private String haeTunniste(String mustache, Map<String, String> hakukohteenValintaperusteet) {
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

  private Funktiokutsu kasitteleLoppuun(Funktiokutsu funktiokutsu) {
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
              log.error("Cannot convert min value {} to BigDecimal", av.getMinValue());
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
              log.error("Cannot convert max value {} to BigDecimal", av.getMaxValue());
            }
          }
          valintaperuste.setMin(min != null ? min.toString() : null);
          valintaperuste.setMax(max != null ? max.toString() : null);
        }
        valintaperusteet.put(valintaperuste.getTunniste(), valintaperuste);
      }
    }
    return funktiokutsu;
  }

  public void onReceive(Object message) {
    if (message instanceof Funktiokutsu) {
      Funktiokutsu response = (Funktiokutsu) message;
      for (Funktioargumentti arg : original.getFunktioargumentit()) {
        if (arg.getFunktiokutsuChild() != null
            && arg.getFunktiokutsuChild().getId().equals(response.getId())) {
          arg.setFunktiokutsuChild(response);
        } else if (arg.getLaskentakaavaChild() != null
            && arg.getLaskentakaavaChild().getFunktiokutsu() == null) {
          arg.getLaskentakaavaChild().setFunktiokutsu(response);
        }
      }
      funktiokutsuLapset--;
      if (funktiokutsuLapset <= 0) {
        original = kasitteleLoppuun(original);
        ActorRef par = getContext().parent();
        if (par.equals(actorParent)) {
          par.tell(original, getSelf());
        } else {
          actorParent.tell(original, getSelf());
        }
        getContext().stop(self());
      }
    } else if (message instanceof UusiValintaperusteRekursio) {
      actorParent = sender();
      UusiValintaperusteRekursio viesti = (UusiValintaperusteRekursio) message;
      original = funktiokutsuCache.get(viesti.getId());
      if (null == original) {
        original = funktiokutsuDAO.getFunktiokutsunValintaperusteet(viesti.getId());
        funktiokutsuCache.add(viesti.getId(), original);
      }
      valintaperusteet = viesti.getValintaperusteet();
      hakukohteenValintaperusteet = viesti.getHakukohteenValintaperusteet();
      if (original.getFunktioargumentit() == null || original.getFunktioargumentit().size() == 0) {
        self().tell(original, getSelf());
      } else {
        funktiokutsuLapset = original.getFunktioargumentit().size();

        for (Funktioargumentti arg : original.getFunktioargumentit()) {
          ActorSystem system = getContext().system();
          ActorRef child =
              getContext()
                  .actorOf(
                      SpringExtProvider.get(system)
                          .props("HaeValintaperusteetRekursiivisestiActorBean"),
                      UUID.randomUUID().toString().replaceAll("-", ""));
          if (arg.getFunktiokutsuChild() != null) {
            child.tell(
                new UusiValintaperusteRekursio(
                    arg.getFunktiokutsuChild().getId(),
                    viesti.getValintaperusteet(),
                    viesti.getHakukohteenValintaperusteet()),
                self());
          } else if (arg.getLaskentakaavaChild() != null) {
            child.tell(
                new UusiValintaperusteRekursio(
                    arg.getLaskentakaavaChild().getFunktiokutsu().getId(),
                    viesti.getValintaperusteet(),
                    viesti.getHakukohteenValintaperusteet()),
                self());
          }
        }
      }
    } else if (message instanceof Exception) {
      ActorRef par = getContext().parent();
      if (par.equals(actorParent)) {
        par.tell(message, ActorRef.noSender());
      } else {
        Throwable ex = (Throwable) message;
        actorParent.tell(new Status.Failure(ex), ActorRef.noSender());
      }
      getContext().stop(self());
    } else {
      unhandled(message);
      getContext().stop(getSelf());
    }
  }
}
