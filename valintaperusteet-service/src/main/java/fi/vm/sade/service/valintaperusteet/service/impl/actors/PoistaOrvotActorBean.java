package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiRekursio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

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
            funktiokutsuDAO.deleteOrphans();
            getContext().stop(self());
        } else {
            unhandled(message);
            getContext().stop(getSelf());
        }

    }
}
