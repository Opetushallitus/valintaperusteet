package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValintakoettaEiVoiPoistaaException extends RuntimeException {

  public ValintakoettaEiVoiPoistaaException() {}

  public ValintakoettaEiVoiPoistaaException(String message) {
    super(message);
  }

  public ValintakoettaEiVoiPoistaaException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValintakoettaEiVoiPoistaaException(Throwable cause) {
    super(cause);
  }
}
