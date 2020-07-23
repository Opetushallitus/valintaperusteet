package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValintatapajonoaEiVoiPoistaaException extends RuntimeException {
  public ValintatapajonoaEiVoiPoistaaException() {}

  public ValintatapajonoaEiVoiPoistaaException(String message) {
    super(message);
  }

  public ValintatapajonoaEiVoiPoistaaException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValintatapajonoaEiVoiPoistaaException(Throwable cause) {
    super(cause);
  }
}
