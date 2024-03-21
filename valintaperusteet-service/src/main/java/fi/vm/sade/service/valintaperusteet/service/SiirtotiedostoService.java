package fi.vm.sade.service.valintaperusteet.service;

import java.time.LocalDateTime;

public interface SiirtotiedostoService {
  String createSiirtotiedostot(LocalDateTime startDatetime, LocalDateTime endDatatime);
}
