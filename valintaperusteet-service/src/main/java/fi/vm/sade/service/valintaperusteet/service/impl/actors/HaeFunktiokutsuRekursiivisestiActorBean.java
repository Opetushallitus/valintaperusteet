package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;

import akka.japi.pf.DeciderBuilder;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.UusiRekursio;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.*;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

/**
 * Created with IntelliJ IDEA. User: kjsaila Date: 17/12/13 Time: 13:10 To
 * change this template use File | Settings | File Templates.
 */

@Named("HaeFunktiokutsuRekursiivisestiActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class HaeFunktiokutsuRekursiivisestiActorBean extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private int funktiokutsuLapset = 0;

	@Autowired
	private FunktiokutsuDAO funktiokutsuDAO;

	private Funktiokutsu original;

	private Long id;
	private boolean laajennaAlakaavat;
	private Set<Long> laskentakaavaIds;
	private ActorRef actorParent = null;

	public HaeFunktiokutsuRekursiivisestiActorBean() {

	}

	public void onReceive(Object message) throws Exception {

		if (message instanceof Funktiokutsu) {

			Funktiokutsu response = (Funktiokutsu) message;
			FunktiokutsuMuodostaaSilmukanException silmukka = null;
			for (Funktioargumentti arg : original.getFunktioargumentit()) {
				if (arg.getFunktiokutsuChild() != null
						&& arg.getFunktiokutsuChild().getId() == response
								.getId()) {
					arg.setFunktiokutsuChild(response);
				} else if (laajennaAlakaavat
						&& arg.getLaskentakaavaChild() != null
						&& arg.getLaskentakaavaChild().getFunktiokutsu()
								.getId() == response.getId()) {
					if (laskentakaavaIds.contains(arg.getLaskentakaavaChild()
							.getId())) {
						silmukka = new FunktiokutsuMuodostaaSilmukanException(
								"Funktiokutsu " + id + " muodostaa silmukan "
										+ "laskentakaavaan "
										+ arg.getLaskentakaavaChild().getId(),
								id, original.getFunktionimi(), arg
										.getLaskentakaavaChild().getId());
					}
					arg.getLaskentakaavaChild().setFunktiokutsu(response);
					arg.setLaajennettuKaava(response);
				} else if (arg.getLaskentakaavaChild() != null
						&& arg.getLaskentakaavaChild().getFunktiokutsu()
								.getId() == response.getId()) {
					arg.getLaskentakaavaChild().setFunktiokutsu(response);
				}
			}
			funktiokutsuLapset--;
			if (silmukka != null) {
				self().tell(silmukka, getSelf());
			} else if (funktiokutsuLapset <= 0) {
				ActorRef par = getContext().parent();
				if (par.equals(actorParent)) {
					par.tell(original, getSelf());
				} else {
					actorParent.tell(original, getSelf());
				}
				getContext().stop(self());
			}
		} else if (message instanceof UusiRekursio) {
			actorParent = sender();
			UusiRekursio viesti = (UusiRekursio) message;
			id = viesti.getId();
			laajennaAlakaavat = viesti.isLaajennaAlakaavat();
			laskentakaavaIds = viesti.getLaskentakaavaIds();

			original = funktiokutsuDAO.getFunktiokutsu(viesti.getId());
			if (original == null) {
				self().tell(
						new FunktiokutsuEiOleOlemassaException("Funktiokutsu ("
								+ id + ") ei ole olemassa", id), getSelf());
			} else {
				if (original.getFunktioargumentit() == null
						|| original.getFunktioargumentit().size() == 0) {
					self().tell(original, getSelf());
				} else {
					funktiokutsuLapset = original.getFunktioargumentit().size();
					for (Funktioargumentti fa : original.getFunktioargumentit()) {
						ActorSystem system = getContext().system();
						ActorRef child = getContext()
								.actorOf(
										SpringExtProvider
												.get(system)
												.props("HaeFunktiokutsuRekursiivisestiActorBean"),
										UUID.randomUUID().toString()
												.replaceAll("-", ""));
						if (fa.getFunktiokutsuChild() != null) {
							child.tell(
									new UusiRekursio(fa.getFunktiokutsuChild()
											.getId(), viesti
											.isLaajennaAlakaavat(), viesti
											.getLaskentakaavaIds()), self());
						} else if (viesti.isLaajennaAlakaavat()
								&& fa.getLaskentakaavaChild() != null) {
							Set<Long> newLaskentakaavaIds = new HashSet<Long>(
									viesti.getLaskentakaavaIds());
							newLaskentakaavaIds.add(fa.getLaskentakaavaChild()
									.getId());
							child.tell(new UusiRekursio(fa
									.getLaskentakaavaChild().getFunktiokutsu()
									.getId(), viesti.isLaajennaAlakaavat(),
									newLaskentakaavaIds), self());

						} else {
							self().tell(original, getSelf());

						}

					}

				}
			}

		} else if (message instanceof Exception) {
			ActorRef par = getContext().parent();
			if (par.equals(actorParent)) {
				par.tell(message, ActorRef.noSender());
			} else {
				Throwable ex;
				if (message instanceof FunktiokutsuMuodostaaSilmukanException) {
					FunktiokutsuMuodostaaSilmukanException exp = (FunktiokutsuMuodostaaSilmukanException) message;
					ex = new FunktiokutsuMuodostaaSilmukanException(
							exp.getMessage(), exp.getFunktiokutsuId(),
							exp.getFunktionimi(), exp.getLaskentakaavaId());
				} else if (message instanceof FunktiokutsuEiOleOlemassaException) {
					ex = new FunktiokutsuEiOleOlemassaException(
							"Funktiokutsu (" + id + ") ei ole olemassa", id);
				} else {
					ex = (Exception) message;
				}
				actorParent.tell(new Status.Failure(ex), ActorRef.noSender());
			}
			getContext().stop(self());
		} else {
			unhandled(message);
			getContext().stop(getSelf());
		}

	}
}
