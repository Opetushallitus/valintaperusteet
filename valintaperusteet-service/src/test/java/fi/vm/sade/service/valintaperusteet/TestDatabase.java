package fi.vm.sade.service.valintaperusteet;

import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Testikanta, joka käynnistetään kerran koko testiajolle ensimmäisen sitä tarvitsevan testin
 * yhteydessä. Testcontainers (Ryuk) poistaa kontin, kun testi-JVM sammuu.
 */
public final class TestDatabase {
  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("valintaperusteet_tests")
          .withUsername("valintaperusteet_tests")
          .withPassword("testikannan_salasana")
          .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));

  private TestDatabase() {}

  public static synchronized PostgreSQLContainer<?> container() {
    if (!POSTGRES.isRunning()) {
      POSTGRES.start();
    }
    return POSTGRES;
  }
}
