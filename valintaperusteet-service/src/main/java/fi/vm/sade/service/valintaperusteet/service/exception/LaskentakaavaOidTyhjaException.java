package fi.vm.sade.service.valintaperusteet.service.exception;

public class LaskentakaavaOidTyhjaException extends RuntimeException {
  public LaskentakaavaOidTyhjaException() {}

  public LaskentakaavaOidTyhjaException(String message) {
    super(message);
  }

  public LaskentakaavaOidTyhjaException(String message, Throwable cause) {
    super(message, cause);
  }

  public LaskentakaavaOidTyhjaException(Throwable cause) {
    super(cause);
  }
}
