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
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    public PoistaOrvotActorBean() {

    }

    private void poistaOrvot() {
        List<Long> orphans = funktiokutsuDAO.getOrphans();
        orphans.forEach(laskentakaavaService::poistaOrpoFunktiokutsu);
        if (orphans.size() > 0) {
            poistaOrvot();
        }
    }

    public void onReceive(Object message) throws Exception {
        if (message != null) {
            log.info("Ajastettu orpojen poistaminen päällä");
            this.poistaOrvot();
            log.info("Orvot poistettu");
        } else {
            unhandled(message);
            getContext().stop(getSelf());
        }
    }
}
