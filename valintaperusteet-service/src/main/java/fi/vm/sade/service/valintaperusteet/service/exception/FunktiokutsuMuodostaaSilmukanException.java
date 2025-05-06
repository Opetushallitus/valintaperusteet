package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;

public class FunktiokutsuMuodostaaSilmukanException extends Exception {
  private Funktionimi funktionimi;
  private Long laskentakaavaId;

  public FunktiokutsuMuodostaaSilmukanException(Funktionimi funktionimi, Long laskentakaavaId) {
    this.funktionimi = funktionimi;
    this.laskentakaavaId = laskentakaavaId;
  }

  public FunktiokutsuMuodostaaSilmukanException(
      String message, Funktionimi funktionimi, Long laskentakaavaId) {
    super(message);
    this.funktionimi = funktionimi;
    this.laskentakaavaId = laskentakaavaId;
  }

  public FunktiokutsuMuodostaaSilmukanException(
      String message, Throwable cause, Funktionimi funktionimi, Long laskentakaavaId) {
    super(message, cause);
    this.funktionimi = funktionimi;
    this.laskentakaavaId = laskentakaavaId;
  }

  public FunktiokutsuMuodostaaSilmukanException(
      Throwable cause, Funktionimi funktionimi, Long laskentakaavaId) {
    super(cause);
    this.funktionimi = funktionimi;
    this.laskentakaavaId = laskentakaavaId;
  }

  public Funktionimi getFunktionimi() {
    return funktionimi;
  }

  public Long getLaskentakaavaId() {
    return laskentakaavaId;
  }
}
