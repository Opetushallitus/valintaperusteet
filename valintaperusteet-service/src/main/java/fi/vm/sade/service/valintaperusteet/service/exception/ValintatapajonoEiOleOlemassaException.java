package fi.vm.sade.service.valintaperusteet.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValintatapajonoEiOleOlemassaException extends ResponseStatusException {
  private final String valintatapajonoOid;

  public ValintatapajonoEiOleOlemassaException(String message, String valintatapajonoOid) {
    super(HttpStatus.NOT_FOUND, message);
    this.valintatapajonoOid = valintatapajonoOid;
  }
}
