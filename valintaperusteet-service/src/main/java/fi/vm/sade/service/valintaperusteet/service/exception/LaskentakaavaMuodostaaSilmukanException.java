package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;

public class LaskentakaavaMuodostaaSilmukanException extends RuntimeException {
  public final LaskentakaavaId id;
  public final LaskentakaavaId takaisinViittaavanId;

  public LaskentakaavaMuodostaaSilmukanException(LaskentakaavaId id,
                                                 LaskentakaavaId takaisinViittaavanId) {
    super(String.format(
            "Laskentakaava %d muodostaa silmukan itseensä kaavan %d kautta",
            id.id,
            takaisinViittaavanId.id
    ));
    this.id = id;
    this.takaisinViittaavanId = takaisinViittaavanId;
  }
}
