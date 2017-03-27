package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.List;

@Named("PoistaOrvotActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class PoistaOrvotActorBean extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    public PoistaOrvotActorBean() {

    }

    public void onReceive(Object message) throws Exception {
        if (message != null) {
            log.info("Poistetaan orvot funktiokutsut");
            try {
                long n = funktiokutsuDAO.deleteOrphans();
                log.info(String.format("Poistettiin %d orpoa", n));
            } catch (Exception e) {
                log.error(e, "Orpojen poisto epäonnistui");
            }
        } else {
            unhandled(message);
            getContext().stop(getSelf());
        }
    }
}
