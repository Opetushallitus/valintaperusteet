package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.SiirtotiedostoResult;
import java.time.LocalDateTime;

public interface SiirtotiedostoService {
  SiirtotiedostoResult createSiirtotiedostot(
      LocalDateTime startDatetime, LocalDateTime endDatatime);

  SiirtotiedostoResult createSiirtotiedostotForAvaimet(
      LocalDateTime startDatetime, LocalDateTime endDatatime);
}
