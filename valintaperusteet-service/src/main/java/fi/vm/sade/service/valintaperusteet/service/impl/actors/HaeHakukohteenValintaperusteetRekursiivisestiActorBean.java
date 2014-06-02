package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.*;
import akka.actor.SupervisorStrategy.Directive;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiHakukohteenValintaperusteRekursio;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiValintaperusteRekursio;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

/**
 * Created with IntelliJ IDEA. User: kjsaila Date: 17/12/13 Time: 13:10 To
 * change this template use File | Settings | File Templates.
 */

@Named("HaeHakukohteenValintaperusteetRekursiivisestiActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class HaeHakukohteenValintaperusteetRekursiivisestiActorBean extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private int funktiokutsuLapset = 0;

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    private Funktiokutsu original;
    private HakukohteenValintaperusteAvaimetDTO valintaperusteet;

    private ActorRef actorParent = null;

    public HaeHakukohteenValintaperusteetRekursiivisestiActorBean() {

    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(5, Duration.create("10 seconds"), new Function<Throwable, Directive>() {
            public Directive apply(Throwable cause) {
                log.error("Virhe valintaperusteiden haussa (HaeHakukohteenValintaperusteetRekursiivisestiActorBean). Syy: {}, viesti:{}", cause.getCause(), cause.getMessage());
                cause.printStackTrace();
                return SupervisorStrategy.restart();
            }
        });
    }

    private Funktiokutsu kasitteleLoppuun(Funktiokutsu funktiokutsu) {

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
                    if (ap.getHylkaysperuste().contains("hakukohde") && ap.getHylkaysperuste().startsWith("{{")) {
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
                    if (ap.getPalautaHaettuArvo().contains("hakukohde") && ap.getPalautaHaettuArvo().startsWith("{{")) {
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

    public void onReceive(Object message) throws Exception {

        if (message instanceof Funktiokutsu) {

            Funktiokutsu response = (Funktiokutsu) message;
            for (Funktioargumentti arg : original.getFunktioargumentit()) {
                if (arg.getFunktiokutsuChild() != null && arg.getFunktiokutsuChild().getId().equals(response.getId())) {
                    arg.setFunktiokutsuChild(response);
                } else if (arg.getLaskentakaavaChild() != null) {
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
        } else if (message instanceof UusiHakukohteenValintaperusteRekursio) {
            actorParent = sender();
            UusiHakukohteenValintaperusteRekursio viesti = (UusiHakukohteenValintaperusteRekursio) message;

            original = funktiokutsuDAO.getFunktiokutsunValintaperusteet(viesti.getId());
            valintaperusteet = viesti.getValintaperusteet();

            if (original.getFunktioargumentit() == null || original.getFunktioargumentit().size() == 0) {
                self().tell(original, getSelf());
            } else {
                funktiokutsuLapset = original.getFunktioargumentit().size();

                for (Funktioargumentti arg : original.getFunktioargumentit()) {
                    ActorSystem system = getContext().system();
                    ActorRef child = getContext().actorOf(
                            SpringExtProvider.get(system).props("HaeHakukohteenValintaperusteetRekursiivisestiActorBean"),
                            UUID.randomUUID().toString().replaceAll("-", ""));
                    if (arg.getFunktiokutsuChild() != null) {
                        child.tell(
                                new UusiHakukohteenValintaperusteRekursio(arg.getFunktiokutsuChild().getId(), viesti
                                        .getValintaperusteet()), self());
                    } else if (arg.getLaskentakaavaChild() != null) {
                        child.tell(new UusiHakukohteenValintaperusteRekursio(
                                arg.getLaskentakaavaChild().getFunktiokutsu().getId(), viesti.getValintaperusteet()), self());
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
