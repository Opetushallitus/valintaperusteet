package fi.vm.sade.service.valintaperusteet.ovara.ajastus;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.dto.SiirtotiedostoResult;
import fi.vm.sade.service.valintaperusteet.ovara.ajastus.impl.SiirtotiedostoProsessiRepositoryImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.SiirtotiedostoServiceImpl;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiirtotiedostoAjastusService {

  private static final Logger logger = LoggerFactory.getLogger(SiirtotiedostoAjastusService.class);

  private final SiirtotiedostoServiceImpl siirtotiedostoServiceImpl;

  private final SiirtotiedostoProsessiRepositoryImpl siirtotiedostoProsessiRepositoryImpl;

  private final ObjectMapper objectMapper;

  public SiirtotiedostoAjastusService(
      SiirtotiedostoServiceImpl siirtotiedostoServiceImpl,
      SiirtotiedostoProsessiRepositoryImpl siirtotiedostoProsessiRepositoryImpl,
      ObjectMapper objectMapper) {
    this.siirtotiedostoServiceImpl = siirtotiedostoServiceImpl;
    this.siirtotiedostoProsessiRepositoryImpl = siirtotiedostoProsessiRepositoryImpl;
    this.objectMapper = objectMapper;
  }

  public String createNextSiirtotiedosto() {
    logger.info("Creating siirtotiedosto by ajastus!");
    SiirtotiedostoProsessi latest = siirtotiedostoProsessiRepositoryImpl.findLatestSuccessful();
    logger.info("Latest: {}", latest);
    SiirtotiedostoProsessi uusi = latest.createNewProcessBasedOnThis();
    logger.info("New process: {}", uusi);
    siirtotiedostoProsessiRepositoryImpl.persist(uusi);

    try {
      LocalDateTime start = uusi.getWindowStart().toLocalDateTime();
      LocalDateTime end = uusi.getWindowEnd().toLocalDateTime();
      SiirtotiedostoResult result = siirtotiedostoServiceImpl.createSiirtotiedostot(start, end);
      SiirtotiedostoResult avaimetResult = new SiirtotiedostoResult(List.of(), 0);
      // testataan ensin ilman ajastettuja luonteja
      // siirtotiedostoServiceImpl.createSiirtotiedostotForAvaimet(start, end);

      uusi.setInfo(
          objectMapper.writeValueAsString(
              new SiirtotiedostoInfo(result.total(), avaimetResult.total())));
      uusi.setSuccess(true);
    } catch (Exception e) {
      logger.error(
          "{} Tapahtui virhe muodostettaessa ajastettua siirtotiedostoa:",
          uusi.getExecutionUuid(),
          e);
      uusi.setInfo("{}");
      uusi.setErrorMessage(e.getMessage());
      uusi.setSuccess(false);
    } finally {
      uusi.setRunEnd(OffsetDateTime.now());
      siirtotiedostoProsessiRepositoryImpl.persist(uusi);
    }

    return "DONE";
  }
}
