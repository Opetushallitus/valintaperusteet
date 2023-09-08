package fi.vm.sade.service.valintaperusteet.config;

import akka.actor.ActorSystem;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = {"fi.vm.sade.service.valintaperusteet"})
public class ValintaperusteetConfiguration implements WebMvcConfigurer {
  @Bean
  public ValintaperusteetModelMapper modelMapper() {
    return new ValintaperusteetModelMapper();
  }

  @Bean(destroyMethod = "terminate")
  public ActorSystem actorSystem() {
    return ActorSystem.create();
  }

  @Override
  public void configurePathMatch(final PathMatchConfigurer configurer) {
    configurer.setUseTrailingSlashMatch(true);
  }
}
