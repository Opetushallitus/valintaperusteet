package fi.vm.sade.service.valintaperusteet.service.exception;

public class LaskentakaavaEiOleOlemassaException extends RuntimeException {
    private Long laskentakaavaOid;

    public LaskentakaavaEiOleOlemassaException(Long laskentakaavaOid) {
        this.laskentakaavaOid = laskentakaavaOid;
    }

    public LaskentakaavaEiOleOlemassaException(String message, Long laskentakaavaOid) {
        super(message);
        this.laskentakaavaOid = laskentakaavaOid;
    }

    public LaskentakaavaEiOleOlemassaException(String message, Throwable cause, Long laskentakaavaOid) {
        super(message, cause);
        this.laskentakaavaOid = laskentakaavaOid;
    }

    public LaskentakaavaEiOleOlemassaException(Throwable cause, Long laskentakaavaOid) {
        super(cause);
        this.laskentakaavaOid = laskentakaavaOid;
    }
}
