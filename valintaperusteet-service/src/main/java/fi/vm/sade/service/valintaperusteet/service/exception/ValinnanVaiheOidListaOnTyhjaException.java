package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValinnanVaiheOidListaOnTyhjaException extends RuntimeException {
  public ValinnanVaiheOidListaOnTyhjaException() {}

  public ValinnanVaiheOidListaOnTyhjaException(String message) {
    super(message);
  }

  public ValinnanVaiheOidListaOnTyhjaException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValinnanVaiheOidListaOnTyhjaException(Throwable cause) {
    super(cause);
  }
}
