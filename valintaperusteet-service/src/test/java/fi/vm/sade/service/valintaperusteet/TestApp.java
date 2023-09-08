package fi.vm.sade.service.valintaperusteet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = false)
@EnableWebSecurity(debug = true)
@ContextConfiguration(classes = TestConfiguration.class)
public class TestApp {
  public static void main(String[] args) {
    SpringApplication.run(TestApp.class);
  }
}
