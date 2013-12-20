package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiRekursio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

@Named("HaeFunktiokutusRekursiivisestiActor")
@org.springframework.context.annotation.Scope("prototype")
@Transactional
public class HaeFunktiokutusRekursiivisestiActor extends UntypedActor {

    private int funktiokutsuLapset = 0;


    final FunktiokutsuDAO funktiokutsuDAO;

    @Autowired
    private FunktiokutsuDAO afunktiokutsuDAO;

    //private Funktiokutsu funktiokutsu;

    private Long id;
    private boolean laajennaAlakaavat;
    private Set<Long> laskentakaavaIds;
    private ActorRef actorParent = null;

    @Inject
    public HaeFunktiokutusRekursiivisestiActor(@Named("funktiokutsuDAOImpl") FunktiokutsuDAO funktiokutsuDAO) {
        this.funktiokutsuDAO = funktiokutsuDAO;
    }

    @Transactional
    public void onReceive(Object message) throws FunktiokutsuMuodostaaSilmukanException {

        if (message instanceof Funktiokutsu) {
            if(this.funktiokutsuLapset > 1) {
                this.funktiokutsuLapset--;
            } else {
                Funktiokutsu response = (Funktiokutsu)message;
                this.actorParent.tell(response, self());
            }
        } else if(message instanceof UusiRekursio) {
            this.actorParent = getSender();
            UusiRekursio viesti = (UusiRekursio)message;
            this.setId(viesti.getId());
            this.setLaajennaAlakaavat(viesti.isLaajennaAlakaavat());
            this.setLaskentakaavaIds(viesti.getLaskentakaavaIds());
            System.out.println("############ In Transaction #############");
            Funktiokutsu funktiokutsu = afunktiokutsuDAO.getFunktiokutsu(id);

            if (funktiokutsu == null) {
                throw new FunktiokutsuEiOleOlemassaException("Funktiokutsu (" + this.id + ") ei ole olemassa", this.id);
            }

            if(funktiokutsu.getFunktioargumentit() == null || funktiokutsu.getFunktioargumentit().size() == 0) {
                getSender().tell(funktiokutsu, self());
            } else {
                this.funktiokutsuLapset = funktiokutsu.getFunktioargumentit().size();
                for (Funktioargumentti fa : funktiokutsu.getFunktioargumentit()) {

                    if (fa.getFunktiokutsuChild() != null) {
                        ActorSystem system = getContext().system();
                        long childId = new Date().getTime();
                        ActorRef child = getContext().actorOf(
                                SpringExtProvider.get(system).props("HaeFunktiokutusRekursiivisestiActor"),
                                id+"_child_"+fa.getFunktiokutsuChild().getId()+"_"+fa.getId()+childId
                        );
                        child.tell(new UusiRekursio(fa.getFunktiokutsuChild().getId(), laajennaAlakaavat, laskentakaavaIds), self());
                    } else if (this.laajennaAlakaavat && fa.getLaskentakaavaChild() != null) {
                        if (this.laskentakaavaIds.contains(fa.getLaskentakaavaChild().getId())) {
                            throw new FunktiokutsuMuodostaaSilmukanException("Funktiokutsu " + this.id + " muodostaa silmukan " +
                                    "laskentakaavaan " + fa.getLaskentakaavaChild().getId(), id,
                                    funktiokutsu.getFunktionimi(), fa.getLaskentakaavaChild().getId());
                        }
                        Set<Long> newLaskentakaavaIds = new HashSet<Long>(this.laskentakaavaIds);
                        newLaskentakaavaIds.add(fa.getLaskentakaavaChild().getId());
                        Timeout timeout = new Timeout(Duration.create(50, "seconds"));
                        ActorSystem system = getContext().system();
                        ActorRef child = getContext().actorOf(
                                SpringExtProvider.get(system).props("HaeFunktiokutusRekursiivisestiActor"),
                                id+"_child_"+fa.getLaskentakaavaChild().getFunktiokutsu()+"_"+fa.getId()
                        );
                        Future<Object> future = Patterns.ask(child, new UusiRekursio(fa.getLaskentakaavaChild().getFunktiokutsu().getId(), laajennaAlakaavat, newLaskentakaavaIds), timeout);
                        Funktiokutsu fk = null;
                        try {
                            fk = (Funktiokutsu) Await.result(future, timeout.duration());
                        } catch (Exception e) {
                            throw new FunktiokutsuEiOleOlemassaException("Funktiokutsu (" + fa.getLaskentakaavaChild().getFunktiokutsu().getId() + ") ei ole olemassa", fa.getLaskentakaavaChild().getFunktiokutsu().getId());
                        }
                        //Funktiokutsu fk = haeFunktiokutsuRekursiivisesti(fa.getLaskentakaavaChild().getFunktiokutsu().getId(),
                        //laajennaAlakaavat, newLaskentakaavaIds);
                        fa.setLaajennettuKaava(fk);

                    } else {
                        ActorSystem system = getContext().system();
                        ActorRef child = getContext().actorOf(
                                SpringExtProvider.get(system).props("HaeFunktiokutusRekursiivisestiActor"),
                                id+"_child_"+fa.getLaskentakaavaChild().getFunktiokutsu()+"_"+fa.getId()
                        );
                        child.tell(new UusiRekursio(fa.getLaskentakaavaChild().getFunktiokutsu().getId(), laajennaAlakaavat, laskentakaavaIds), self());
                    }

                }
                //ActorRef par = getSender();
                //par.tell(funktiokutsu, self());
            }


        } else {
            unhandled(message);
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLaajennaAlakaavat() {
        return laajennaAlakaavat;
    }

    public void setLaajennaAlakaavat(boolean laajennaAlakaavat) {
        this.laajennaAlakaavat = laajennaAlakaavat;
    }

    public Set<Long> getLaskentakaavaIds() {
        return laskentakaavaIds;
    }

    public void setLaskentakaavaIds(Set<Long> laskentakaavaIds) {
        this.laskentakaavaIds = laskentakaavaIds;
    }
}
