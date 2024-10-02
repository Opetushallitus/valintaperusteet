package fi.vm.sade.service.valintaperusteet.ovara;

import fi.vm.sade.service.valintaperusteet.ovara.ajastus.SiirtotiedostoAjastusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
      "fi.vm.sade.service.valintaperusteet.ovara",
      "fi.vm.sade.service.valintaperusteet.ovara.ajastus",
      "fi.vm.sade.service.valintaperusteet.dao",
      "fi.vm.sade.service.valintaperusteet.service.impl.actors",
      "fi.vm.sade.service.valintaperusteet.service",
      "fi.vm.sade.service.valintaperusteet.util"
    })
public class OvaraApp implements CommandLineRunner {
  private static final Logger logger = LoggerFactory.getLogger(OvaraApp.class.getName());

  final SiirtotiedostoAjastusService siirtotiedostoAjastusService;

  public OvaraApp(SiirtotiedostoAjastusService siirtotiedostoAjastusService) {
    this.siirtotiedostoAjastusService = siirtotiedostoAjastusService;
  }

  public static void main(String[] args) {
    logger.info("Hello, ovara world!");
    try {
      SpringApplication application = new SpringApplication(OvaraApp.class);
      application.setWebApplicationType(WebApplicationType.NONE);
      application.run(args);
      System.exit(0);

    } catch (Exception e) {
      logger.error("Virhe: ", e);
      System.exit(1);
    }
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("Running!");
    siirtotiedostoAjastusService.createNextSiirtotiedosto();
    logger.info("Done running!");
  }
}
