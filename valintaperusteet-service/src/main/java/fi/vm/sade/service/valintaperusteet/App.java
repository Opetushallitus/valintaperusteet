package fi.vm.sade.service.valintaperusteet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
  public static final String CONTEXT_PATH = "/valintaperusteet-service";

  public static void main(String[] args) {
    System.setProperty("server.servlet.context-path", CONTEXT_PATH);
    SpringApplication.run(App.class, args);
  }
}
