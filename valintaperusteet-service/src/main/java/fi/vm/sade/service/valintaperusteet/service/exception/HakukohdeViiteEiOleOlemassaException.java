package fi.vm.sade.service.valintaperusteet.service.exception;

public class HakukohdeViiteEiOleOlemassaException extends RuntimeException {

    private String hakukohdeViiteOid;

    public HakukohdeViiteEiOleOlemassaException(String hakukohdeViiteOid) {
        this.hakukohdeViiteOid = hakukohdeViiteOid;
    }

    public HakukohdeViiteEiOleOlemassaException(String message, String hakukohdeViiteOid) {
        super(message);
        this.hakukohdeViiteOid = hakukohdeViiteOid;
    }

    public HakukohdeViiteEiOleOlemassaException(String message, Throwable cause, String hakukohdeViiteOid) {
        super(message, cause);
        this.hakukohdeViiteOid = hakukohdeViiteOid;
    }

    public HakukohdeViiteEiOleOlemassaException(Throwable cause, String hakukohdeViiteOid) {
        super(cause);
        this.hakukohdeViiteOid = hakukohdeViiteOid;
    }

    private static final long serialVersionUID = 8232684877014466941L;

}
