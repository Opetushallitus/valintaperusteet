package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValintatapajonoEiOleOlemassaException extends RuntimeException {
  private String valintatapajonoOid;

  public ValintatapajonoEiOleOlemassaException(String valintatapajonoOid) {
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public ValintatapajonoEiOleOlemassaException(String message, String valintatapajonoOid) {
    super(message);
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public ValintatapajonoEiOleOlemassaException(
      String message, Throwable cause, String valintatapajonoOid) {
    super(message, cause);
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public ValintatapajonoEiOleOlemassaException(Throwable cause, String valintatapajonoOid) {
    super(cause);
    this.valintatapajonoOid = valintatapajonoOid;
  }
}
