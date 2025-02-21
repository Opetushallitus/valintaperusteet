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

  @PostConstruct
  private void initActorSystemAndSchedulers() {
    actorSystem = ActorSystem.create("ValintaperusteetActorSystem");
    SpringExtProvider.get(actorSystem).initialize(applicationContext);
  }

  @PreDestroy
  public void tearDownActorSystem() {
    try {
      Await.result(actorSystem.terminate(), Duration.create(1, TimeUnit.MINUTES));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
