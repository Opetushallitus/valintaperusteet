package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.Status;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiValintaperusteRekursio;

/**
 * Created with IntelliJ IDEA. User: kjsaila Date: 17/12/13 Time: 13:10 To
 * change this template use File | Settings | File Templates.
 */

@Named("HaeValintaperusteetRekursiivisestiActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class HaeValintaperusteetRekursiivisestiActorBean extends UntypedActor {

    private int funktiokutsuLapset = 0;

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    private Funktiokutsu original;
    private Map<String, ValintaperusteDTO> valintaperusteet;
    private Map<String, String> hakukohteenValintaperusteet;

    private ActorRef actorParent = null;

    public HaeValintaperusteetRekursiivisestiActorBean() {

    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(5, Duration.create("10 seconds"), new Function<Throwable, Directive>() {
            public Directive apply(Throwable cause) {
                cause.printStackTrace();
                return SupervisorStrategy.restart();
            }
        });
    }

    private String haeTunniste(String mustache, Map<String, String> hakukohteenValintaperusteet) {
        String r = "\\{\\{([A-Za-z0–9\\-_]+)\\.([A-Za-z0–9\\-_]+)\\}\\}";
        Pattern pattern = Pattern.compile(r);
        final Matcher m = pattern.matcher(mustache);

        String avain = null;
        while (m.find()) {
            if (!m.group(1).isEmpty() && m.group(1).contentEquals("hakukohde") && !m.group(2).isEmpty()) {
                avain = m.group(2);
            }
        }
        if (avain == null) {
            return mustache;
        } else {
            String arvo = hakukohteenValintaperusteet.get(avain);
            return arvo;
        }

    }

    private Funktiokutsu kasitteleLoppuun(Funktiokutsu funktiokutsu) {
        for (ValintaperusteViite vp : funktiokutsu.getValintaperusteviitteet()) {
            if (Valintaperustelahde.SYOTETTAVA_ARVO.equals(vp.getLahde())
                    || fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO
                            .equals(vp.getLahde())) {

                ValintaperusteDTO valintaperuste = new ValintaperusteDTO();
                valintaperuste.setFunktiotyyppi(new ModelMapper().map(funktiokutsu.getFunktionimi().getTyyppi(),
                        fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.class));
                valintaperuste.setTunniste(vp.getTunniste());
                valintaperuste.setKuvaus(vp.getKuvaus());

                valintaperuste
                        .setLahde(fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde.SYOTETTAVA_ARVO);
                valintaperuste.setOnPakollinen(vp.getOnPakollinen());
                valintaperuste.setOsallistuminenTunniste(vp.getOsallistuminenTunniste());

                if (vp.getEpasuoraViittaus() != null && vp.getEpasuoraViittaus()) {
                    valintaperuste.setTunniste(hakukohteenValintaperusteet.get(vp.getTunniste()));
                    valintaperuste.setOsallistuminenTunniste(valintaperuste.getTunniste()
                            + ValintaperusteViite.OSALLISTUMINEN_POSTFIX);
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
                            BigDecimal current = new BigDecimal(haeTunniste(av.getMinValue(),
                                    hakukohteenValintaperusteet));
                            if (min == null || current.compareTo(min) < 0) {
                                min = current;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Cannot convert min value "+av.getMinValue()+" to BigDecimal");
                            e.printStackTrace();
                        }

                        try {
                            BigDecimal current = new BigDecimal(haeTunniste(av.getMaxValue(),
                                    hakukohteenValintaperusteet));
                            if (max == null || current.compareTo(max) > 0) {
                                max = current;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(("Cannot convert max value "+av.getMaxValue()+" to BigDecimal");
                            e.printStackTrace();
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
        } else if (message instanceof UusiValintaperusteRekursio) {
            actorParent = sender();
            UusiValintaperusteRekursio viesti = (UusiValintaperusteRekursio) message;

            original = funktiokutsuDAO.getFunktiokutsu(viesti.getId());
            valintaperusteet = viesti.getValintaperusteet();
            hakukohteenValintaperusteet = viesti.getHakukohteenValintaperusteet();

            if (original.getFunktioargumentit() == null || original.getFunktioargumentit().size() == 0) {
                self().tell(original, getSelf());
            } else {
                funktiokutsuLapset = original.getFunktioargumentit().size();

                for (Funktioargumentti arg : original.getFunktioargumentit()) {
                    ActorSystem system = getContext().system();
                    ActorRef child = getContext().actorOf(
                            SpringExtProvider.get(system).props("HaeValintaperusteetRekursiivisestiActorBean"),
                            UUID.randomUUID().toString().replaceAll("-", ""));
                    if (arg.getFunktiokutsuChild() != null) {
                        child.tell(
                                new UusiValintaperusteRekursio(arg.getFunktiokutsuChild().getId(), viesti
                                        .getValintaperusteet(), viesti.getHakukohteenValintaperusteet()), self());
                    } else if (arg.getLaskentakaavaChild() != null) {
                        child.tell(new UusiValintaperusteRekursio(
                                arg.getLaskentakaavaChild().getFunktiokutsu().getId(), viesti.getValintaperusteet(),
                                viesti.getHakukohteenValintaperusteet()), self());
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
