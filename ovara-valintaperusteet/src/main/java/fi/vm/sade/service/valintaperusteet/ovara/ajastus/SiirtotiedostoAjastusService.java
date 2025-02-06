package fi.vm.sade.service.valintaperusteet.ovara.ajastus;

import fi.vm.sade.service.valintaperusteet.ovara.ajastus.impl.SiirtotiedostoProsessiRepositoryImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.SiirtotiedostoServiceImpl;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiirtotiedostoAjastusService {

  private static final Logger logger = LoggerFactory.getLogger(SiirtotiedostoAjastusService.class);

  private final SiirtotiedostoServiceImpl siirtotiedostoServiceImpl;

  private final SiirtotiedostoProsessiRepositoryImpl siirtotiedostoProsessiRepositoryImpl;

  public SiirtotiedostoAjastusService(
      SiirtotiedostoServiceImpl siirtotiedostoServiceImpl,
      SiirtotiedostoProsessiRepositoryImpl siirtotiedostoProsessiRepositoryImpl) {
    this.siirtotiedostoServiceImpl = siirtotiedostoServiceImpl;
    this.siirtotiedostoProsessiRepositoryImpl = siirtotiedostoProsessiRepositoryImpl;
  }

  public String createNextSiirtotiedosto() {
    logger.info("Creating siirtotiedosto by ajastus!");
    SiirtotiedostoProsessi latest = siirtotiedostoProsessiRepositoryImpl.findLatestSuccessful();
    logger.info("Latest: {}", latest);
    SiirtotiedostoProsessi uusi = latest.createNewProcessBasedOnThis();
    logger.info("New process: {}", uusi);
    siirtotiedostoProsessiRepositoryImpl.persist(uusi);

    try {
      String resultInfo =
          siirtotiedostoServiceImpl.createSiirtotiedostot(
              uusi.getWindowStart().toLocalDateTime(), uusi.getWindowEnd().toLocalDateTime());
      uusi.setInfo(resultInfo);
      uusi.setSuccess(true);
      uusi.setRunEnd(OffsetDateTime.now());
      siirtotiedostoProsessiRepositoryImpl.persist(uusi);
    } catch (Exception e) {
      logger.error(
          "{} Tapahtui virhe muodostettaessa ajastettua siirtotiedostoa:",
          uusi.getExecutionUuid(),
          e);
      uusi.setInfo("{}");
      uusi.setErrorMessage(e.getMessage());
      uusi.setSuccess(false);
      uusi.setRunEnd(OffsetDateTime.now());
      siirtotiedostoProsessiRepositoryImpl.persist(uusi);
    }

    return "DONE";
  }
}
