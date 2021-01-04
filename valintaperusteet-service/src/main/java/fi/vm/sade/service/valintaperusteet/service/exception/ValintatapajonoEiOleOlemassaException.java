package fi.vm.sade.service.valintaperusteet.service.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ValintatapajonoEiOleOlemassaException extends WebApplicationException {
  private String valintatapajonoOid;

  public ValintatapajonoEiOleOlemassaException(String valintatapajonoOid) {
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public ValintatapajonoEiOleOlemassaException(String message, String valintatapajonoOid) {
    super(message, Response.Status.NOT_FOUND);
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
