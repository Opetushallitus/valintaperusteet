package fi.vm.sade.service.valintaperusteet.dto;

import static fi.vm.sade.service.valintaperusteet.dto.model.SiirtotiedostoConstants.SIIRTOTIEDOSTO_DATETIME_FORMATTER;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public abstract class AbstractWithModifyTimestamp {
  @Schema(description = "Luonti- tai viimeisin modifiointiaikaleima", required = false)
  protected String lastModified;

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public void setLastModified(LocalDateTime lastModified) {
    this.lastModified =
        lastModified != null ? lastModified.format(SIIRTOTIEDOSTO_DATETIME_FORMATTER) : null;
  }

  public void setLastModifiedIfDesired(
      HakuparametritDTO hakuParametrit, LocalDateTime lastModified) {
    if (hakuParametrit.haetaankoPaivitysAikaleimat()) {
      setLastModified(lastModified);
    }
  }
}
