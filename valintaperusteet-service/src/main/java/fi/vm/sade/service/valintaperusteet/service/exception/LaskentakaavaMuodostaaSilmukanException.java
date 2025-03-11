package fi.vm.sade.service.valintaperusteet.service.exception;

public class LaskentakaavaMuodostaaSilmukanException extends RuntimeException {
  private Long parentLaskentakaavaId;
  private Long viitattuLaskentakaavaId;

  public LaskentakaavaMuodostaaSilmukanException(
      Long parentLaskentakaavaId, Long funktiokutsuId, Long viitattuLaskentakaavaId) {
    this.parentLaskentakaavaId = parentLaskentakaavaId;
    this.viitattuLaskentakaavaId = viitattuLaskentakaavaId;
  }

  public LaskentakaavaMuodostaaSilmukanException(
      String message, Long parentLaskentakaavaId, Long viitattuLaskentakaavaId) {
    super(message);
    this.parentLaskentakaavaId = parentLaskentakaavaId;
    this.viitattuLaskentakaavaId = viitattuLaskentakaavaId;
  }

  public LaskentakaavaMuodostaaSilmukanException(
      String message, Throwable cause, Long parentLaskentakaavaId, Long viitattuLaskentakaavaId) {
    super(message, cause);
    this.parentLaskentakaavaId = parentLaskentakaavaId;
    this.viitattuLaskentakaavaId = viitattuLaskentakaavaId;
  }

  public LaskentakaavaMuodostaaSilmukanException(
      Throwable cause, Long parentLaskentakaavaId, Long viitattuLaskentakaavaId) {
    super(cause);
    this.parentLaskentakaavaId = parentLaskentakaavaId;
    this.viitattuLaskentakaavaId = viitattuLaskentakaavaId;
  }

  public Long getParentLaskentakaavaId() {
    return parentLaskentakaavaId;
  }

  public Long getViitattuLaskentakaavaId() {
    return viitattuLaskentakaavaId;
  }
}
