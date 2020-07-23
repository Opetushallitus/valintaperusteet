package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValintakoettaEiOleOlemassaException extends RuntimeException {
  public ValintakoettaEiOleOlemassaException() {}

  public ValintakoettaEiOleOlemassaException(String message) {
    super(message);
  }

  public ValintakoettaEiOleOlemassaException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValintakoettaEiOleOlemassaException(Throwable cause) {
    super(cause);
  }
}
