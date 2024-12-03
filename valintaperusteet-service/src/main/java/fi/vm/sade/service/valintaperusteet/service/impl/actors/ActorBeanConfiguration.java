package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActorBeanConfiguration {

  @Bean("PoistaOrvotActorBean")
  public PoistaOrvotActorBean poistaOrvotActorBean() {
    return new PoistaOrvotActorBean();
  }
}
