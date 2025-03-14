package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import static fi.vm.sade.service.valintaperusteet.service.impl.actors.creators.SpringExtension.SpringExtProvider;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

@Service
public class ActorService {
  @Autowired ApplicationContext applicationContext;

  private ActorSystem actorSystem;

  private ActorRef deleteOrphans;

  private Cancellable cancellable;

  private FiniteDuration duration;

  @PostConstruct
  private void initActorSystemAndSchedulers() {
    actorSystem = ActorSystem.create("ValintaperusteetActorSystem");
    SpringExtProvider.get(actorSystem).initialize(applicationContext);
    initOrphanRemovalActor();
    duration = Duration.create(6, TimeUnit.HOURS);
    runScheduler(duration, deleteOrphans);
  }

  @PreDestroy
  public void tearDownActorSystem() {
    try {
      Await.result(actorSystem.terminate(), Duration.create(1, TimeUnit.MINUTES));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void initOrphanRemovalActor() {
    deleteOrphans =
        actorSystem.actorOf(
            SpringExtProvider.get(actorSystem).props("PoistaOrvotActorBean"), "OrphanRemovalActor");
  }

  private void runScheduler(FiniteDuration duration, ActorRef actor) {
    cancellable =
        actorSystem
            .scheduler()
            .schedule(
                Duration.Zero(),
                duration,
                actor,
                "run",
                actorSystem.dispatcher(),
                ActorRef.noSender());
  }

  public ActorSystem getActorSystem() {
    return actorSystem;
  }

  public void runSchedulerIfNotRunning() {
    if (deleteOrphans.isTerminated()) {
      if (!cancellable.isCancelled()) {
        cancellable.cancel();
      }
      initOrphanRemovalActor();
      runScheduler(duration, deleteOrphans);
    } else if (cancellable.isCancelled()) {
      runScheduler(duration, deleteOrphans);
    }
  }

  public void runOnce() {
    deleteOrphans.tell("run", ActorRef.noSender());
  }
}
