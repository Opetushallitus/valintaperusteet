package fi.vm.sade.service.valintaperusteet.service.exception;

public class JarjestyskriteeriaEiVoiPoistaaException extends RuntimeException {
  public JarjestyskriteeriaEiVoiPoistaaException() {}

  public JarjestyskriteeriaEiVoiPoistaaException(String message) {
    super(message);
  }

  public JarjestyskriteeriaEiVoiPoistaaException(String message, Throwable cause) {
    super(message, cause);
  }

  public JarjestyskriteeriaEiVoiPoistaaException(Throwable cause) {
    super(cause);
  }
}
