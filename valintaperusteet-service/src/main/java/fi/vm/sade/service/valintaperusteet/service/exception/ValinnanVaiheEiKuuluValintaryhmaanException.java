package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValinnanVaiheEiKuuluValintaryhmaanException extends RuntimeException {
  private String valinnanVaiheOid;
  private String valintaryhmaOid;

  public ValinnanVaiheEiKuuluValintaryhmaanException(
      String valinnanVaiheOid, String valintaryhmaOid) {
    this.valinnanVaiheOid = valinnanVaiheOid;
    this.valintaryhmaOid = valintaryhmaOid;
  }

  public ValinnanVaiheEiKuuluValintaryhmaanException(
      String message, String valinnanVaiheOid, String valintaryhmaOid) {
    super(message);
    this.valinnanVaiheOid = valinnanVaiheOid;
    this.valintaryhmaOid = valintaryhmaOid;
  }

  public ValinnanVaiheEiKuuluValintaryhmaanException(
      String message, Throwable cause, String valinnanVaiheOid, String valintaryhmaOid) {
    super(message, cause);
    this.valinnanVaiheOid = valinnanVaiheOid;
    this.valintaryhmaOid = valintaryhmaOid;
  }

  public ValinnanVaiheEiKuuluValintaryhmaanException(
      Throwable cause, String valinnanVaiheOid, String valintaryhmaOid) {
    super(cause);
    this.valinnanVaiheOid = valinnanVaiheOid;
    this.valintaryhmaOid = valintaryhmaOid;
  }
}
