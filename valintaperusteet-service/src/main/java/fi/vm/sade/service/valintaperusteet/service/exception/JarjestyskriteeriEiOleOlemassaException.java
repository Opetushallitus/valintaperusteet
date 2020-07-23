package fi.vm.sade.service.valintaperusteet.service.exception;

public class JarjestyskriteeriEiOleOlemassaException extends RuntimeException {
  private String jarjestyskriteeriOid;

  public JarjestyskriteeriEiOleOlemassaException(String jarjestyskriteeriOid) {
    this.jarjestyskriteeriOid = jarjestyskriteeriOid;
  }

  public JarjestyskriteeriEiOleOlemassaException(String message, String jarjestyskriteeriOid) {
    super(message);
    this.jarjestyskriteeriOid = jarjestyskriteeriOid;
  }

  public JarjestyskriteeriEiOleOlemassaException(
      String message, Throwable cause, String jarjestyskriteeriOid) {
    super(message, cause);
    this.jarjestyskriteeriOid = jarjestyskriteeriOid;
  }

  public JarjestyskriteeriEiOleOlemassaException(Throwable cause, String jarjestyskriteeriOid) {
    super(cause);
    this.jarjestyskriteeriOid = jarjestyskriteeriOid;
  }
}
