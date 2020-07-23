package fi.vm.sade.service.valintaperusteet.service.exception;

public class HakijaryhmatyyppikoodiOnTyhjaException extends RuntimeException {
  public HakijaryhmatyyppikoodiOnTyhjaException() {}

  public HakijaryhmatyyppikoodiOnTyhjaException(String message) {
    super(message);
  }

  public HakijaryhmatyyppikoodiOnTyhjaException(String message, Throwable cause) {
    super(message, cause);
  }

  public HakijaryhmatyyppikoodiOnTyhjaException(Throwable cause) {
    super(cause);
  }
}
