package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;

public class FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException extends RuntimeException {
  private Funktionimi funktionimi;

  public FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(
      String message, Funktionimi funktionimi) {
    super(message);
    this.funktionimi = funktionimi;
  }
}
