package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValintatapajonoaEiVoiLisataException extends RuntimeException {
  public ValintatapajonoaEiVoiLisataException() {}

  public ValintatapajonoaEiVoiLisataException(String message) {
    super(message);
  }

  public ValintatapajonoaEiVoiLisataException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValintatapajonoaEiVoiLisataException(Throwable cause) {
    super(cause);
  }
}
