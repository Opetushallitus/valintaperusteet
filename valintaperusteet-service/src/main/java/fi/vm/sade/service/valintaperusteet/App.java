package fi.vm.sade.service.valintaperusteet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {
  public static final String CONTEXT_PATH = "/valintaperusteet-service";

  public static void main(String[] args) {
    System.setProperty("server.servlet.context-path", CONTEXT_PATH);
    SpringApplication.run(App.class, args);
  }
}
