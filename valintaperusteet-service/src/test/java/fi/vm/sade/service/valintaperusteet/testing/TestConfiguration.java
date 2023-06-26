package fi.vm.sade.service.valintaperusteet.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
  @Autowired private ServerProperties serverProperties;

  @Bean("serverPort")
  public Integer serverPort() {
    return serverProperties.getPort();
  }
}
