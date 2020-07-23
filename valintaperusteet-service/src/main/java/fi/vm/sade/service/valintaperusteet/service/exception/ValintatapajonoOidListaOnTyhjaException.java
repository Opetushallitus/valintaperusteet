package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValintatapajonoOidListaOnTyhjaException extends RuntimeException {
  public ValintatapajonoOidListaOnTyhjaException() {}

  public ValintatapajonoOidListaOnTyhjaException(String message) {
    super(message);
  }

  public ValintatapajonoOidListaOnTyhjaException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValintatapajonoOidListaOnTyhjaException(Throwable cause) {
    super(cause);
  }
}
