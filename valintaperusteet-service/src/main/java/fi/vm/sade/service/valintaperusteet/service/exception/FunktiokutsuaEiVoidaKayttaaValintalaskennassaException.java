package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.model.FunktiokutsuId;

public class FunktiokutsuaEiVoidaKayttaaValintalaskennassaException extends RuntimeException {
  private FunktiokutsuId id;
  private Funktionimi funktionimi;

  public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(FunktiokutsuId id, Funktionimi funktionimi) {
    this.id = id;
    this.funktionimi = funktionimi;
  }

  public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(
          String message, FunktiokutsuId id, Funktionimi funktionimi) {
    super(message);
    this.id = id;
    this.funktionimi = funktionimi;
  }

  public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(
      String message, Throwable cause, FunktiokutsuId id, Funktionimi funktionimi) {
    super(message, cause);
    this.id = id;
    this.funktionimi = funktionimi;
  }

  public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(
      Throwable cause, FunktiokutsuId id, Funktionimi funktionimi) {
    super(cause);
    this.id = id;
    this.funktionimi = funktionimi;
  }
}
