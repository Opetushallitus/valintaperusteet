package fi.vm.sade.service.valintaperusteet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
  @Bean
  public OpenAPI valintaperusteetAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Valintaperusteet API")
                .description("Valintaperusteet")
                .version("v1.0"));
  }
}
