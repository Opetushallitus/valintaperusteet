package fi.vm.sade.service.valintaperusteet.service.exception;

public class JarjestyskriteeriOidListaOnTyhjaException extends RuntimeException {
  public JarjestyskriteeriOidListaOnTyhjaException() {}

  public JarjestyskriteeriOidListaOnTyhjaException(String message) {
    super(message);
  }

  public JarjestyskriteeriOidListaOnTyhjaException(String message, Throwable cause) {
    super(message, cause);
  }

  public JarjestyskriteeriOidListaOnTyhjaException(Throwable cause) {
    super(cause);
  }
}
