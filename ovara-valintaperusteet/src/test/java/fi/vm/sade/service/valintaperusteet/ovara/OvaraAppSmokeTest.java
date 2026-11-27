package fi.vm.sade.service.valintaperusteet.ovara;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.vm.sade.service.valintaperusteet.dto.SiirtotiedostoValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.ovara.ajastus.SiirtotiedostoAjastusService;
import fi.vm.sade.service.valintaperusteet.util.SiirtotiedostoS3Client;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Käynnistää OvaraAppin Spring-kontekstin oikeaa Postgres-kantaa vasten (Testcontainers), S3
 * mockattuna. Koska OvaraApp on CommandLineRunner, kontekstin käynnistys ajaa saman
 * siirtotiedostojen muodostuksen kuin tuotannossa main-metodin kautta.
 */
@Testcontainers
@SpringBootTest(classes = OvaraApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("ovara")
public class OvaraAppSmokeTest {

  // Migraatiossa lisätyn ensimmäisen onnistuneen ajon aikaikkunan loppu
  private static final String ALKUPERAINEN_WINDOW_END = "2024-08-01T00:00:00Z";

  @Container
  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:15").withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));

  @DynamicPropertySource
  static void tietokanta(DynamicPropertyRegistry registry) {
    registry.add("valintaperusteet.postgresql.url", POSTGRES::getJdbcUrl);
    registry.add("valintaperusteet.postgresql.user", POSTGRES::getUsername);
    registry.add("valintaperusteet.postgresql.password", POSTGRES::getPassword);
  }

  @MockitoBean(name = "siirtotiedostoS3Client")
  private SiirtotiedostoS3Client siirtotiedostoS3Client;

  @Autowired private SiirtotiedostoAjastusService siirtotiedostoAjastusService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  public void kaynnistyksessaMuodostetaanSiirtotiedostoEdellisenOnnistuneenAjonJalkeen() {
    List<Map<String, Object>> ajot =
        jdbcTemplate.queryForList(
            "select success, error_message, info->>'success' as info_success from siirtotiedosto "
                + "where window_start = ?::timestamptz",
            ALKUPERAINEN_WINDOW_END);

    assertEquals(1, ajot.size());
    Map<String, Object> ajo = ajot.get(0);
    assertEquals(true, ajo.get("success"), "Virhe: " + ajo.get("error_message"));
    assertEquals("true", ajo.get("info_success"));
  }

  @Test
  public void muuttuneenHakukohteenValintaperusteetKirjoitetaanS3een() {
    when(siirtotiedostoS3Client.getMaxHakukohdeCountInFile()).thenReturn(10000);
    when(siirtotiedostoS3Client.createSiirtotiedosto(anyList(), anyString(), anyInt()))
        .thenReturn("testi-avain");

    // Edellisen ikkunan aikana muuttunut hakukohde ei saa tulla mukaan. Paljastaa
    // aikavyöhykevirheet, joissa peräkkäiset ikkunat menevät päällekkäin tai eivät kohtaa.
    // last_modified asettuu triggerillä now():ksi, joten järjestys määrää aikaleimat.
    lisaaHakukohde(999998, "1.2.246.562.20.00000000000000099998");
    jdbcTemplate.update(
        "insert into siirtotiedosto (execution_uuid, window_start, window_end, run_start, run_end, info, success) "
            + "values ('6c1d7d8e-8f5a-4c55-9a33-6f1f2b7c0a01', now() - interval '1 day', now(), now(), now(), '{}', true)");
    String hakukohdeOid = "1.2.246.562.20.00000000000000099999";
    lisaaHakukohde(999999, hakukohdeOid);

    assertEquals("DONE", siirtotiedostoAjastusService.createNextSiirtotiedosto());

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<SiirtotiedostoValintaperusteetDTO>> data =
        ArgumentCaptor.forClass(List.class);
    verify(siirtotiedostoS3Client).createSiirtotiedosto(data.capture(), anyString(), anyInt());
    assertEquals(
        List.of(hakukohdeOid), data.getValue().stream().map(d -> d.getHakukohdeOid()).toList());

    Map<String, Object> viimeisin =
        jdbcTemplate.queryForMap(
            "select success, error_message, info->'keys'->>0 as avain from siirtotiedosto "
                + "order by run_start desc limit 1");
    assertEquals(true, viimeisin.get("success"), "Virhe: " + viimeisin.get("error_message"));
    assertEquals("testi-avain", viimeisin.get("avain"));
  }

  private void lisaaHakukohde(long id, String oid) {
    jdbcTemplate.update(
        "insert into hakukohde_viite (id, version, hakuoid, nimi, oid) "
            + "values (?, 0, '1.2.246.562.29.00000000000000099999', 'Smoke-testin hakukohde', ?)",
        id,
        oid);
  }
}
