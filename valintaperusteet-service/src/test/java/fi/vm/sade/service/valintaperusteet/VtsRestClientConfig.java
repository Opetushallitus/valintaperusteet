package fi.vm.sade.service.valintaperusteet;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fi.vm.sade.service.valintaperusteet.util.VtsRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;

@Configuration
@ContextConfiguration(classes = TestConfiguration.class)
@Profile("vtsConfig")
public class VtsRestClientConfig {

  @Bean
  @Primary
  VtsRestClient vtsRestClient() {
    VtsRestClient mock = mock(VtsRestClient.class);
    try {
      when(mock.isJonoSijoiteltu(eq("26"))).thenReturn(true);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return mock;
  }
}
