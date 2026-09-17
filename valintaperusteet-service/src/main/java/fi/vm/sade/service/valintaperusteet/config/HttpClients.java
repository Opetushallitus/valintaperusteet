package fi.vm.sade.service.valintaperusteet.config;

import static fi.vm.sade.valinta.sharedutils.http.HttpResource.CSRF_VALUE;

import fi.vm.sade.javautils.nio.cas.CasClient;
import fi.vm.sade.javautils.nio.cas.CasClientBuilder;
import fi.vm.sade.javautils.nio.cas.CasConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClients {

  @Bean(name = "ValintatulosCasClient")
  CasClient getValintatulosCasClient(
      @Value("${valintaperusteet.valinta-tulos-service.username}") String username,
      @Value("${valintaperusteet.valinta-tulos-service.password}") String password,
      @Value("${cas.url}") String casUrl,
      @Value("${valintaperusteet.valinta-tulos-service.service-url}") final String serviceUrl) {

    String service = String.format("%s/auth/login", serviceUrl);
    CasConfig config =
        new CasConfig.CasConfigBuilder(
                username, password, casUrl, service, CSRF_VALUE, ConfigEnums.CALLER_ID.value(), "")
            .setJsessionName("session")
            .build();
    return CasClientBuilder.build(config);
  }
}
