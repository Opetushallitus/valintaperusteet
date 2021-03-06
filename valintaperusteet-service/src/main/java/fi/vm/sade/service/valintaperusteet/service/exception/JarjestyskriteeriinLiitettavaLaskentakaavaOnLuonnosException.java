package fi.vm.sade.service.valintaperusteet.service.exception;

public class JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException extends RuntimeException {
  private Long laskentakaavaOid;

  public JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException(Long laskentakaavaOid) {
    this.laskentakaavaOid = laskentakaavaOid;
  }

  public JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException(
      String message, Long laskentakaavaOid) {
    super(message);
    this.laskentakaavaOid = laskentakaavaOid;
  }

  public JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException(
      String message, Throwable cause, Long laskentakaavaOid) {
    super(message, cause);
    this.laskentakaavaOid = laskentakaavaOid;
  }

  public JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException(
      Throwable cause, Long laskentakaavaOid) {
    super(cause);
    this.laskentakaavaOid = laskentakaavaOid;
  }
}
