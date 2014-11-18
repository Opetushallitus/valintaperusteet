package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * Created by teemu on 17.11.2014.
 */
public class ValintaryhmaaEiVoidaKopioida extends RuntimeException {

    private String parentOid;
    private String lahdeOid;

    public ValintaryhmaaEiVoidaKopioida(String lahdeOid, String parentOid) {
        this.lahdeOid = lahdeOid;
        this.parentOid = parentOid;
    }

    public ValintaryhmaaEiVoidaKopioida(String message, String lahdeOid, String parentOid) {
        super(message);
        this.lahdeOid = lahdeOid;
        this.parentOid = parentOid;
    }

    public ValintaryhmaaEiVoidaKopioida(String message, Throwable cause, String lahdeOid, String parentOid) {
        super(message, cause);
        this.lahdeOid = lahdeOid;
        this.parentOid = parentOid;
    }

    public ValintaryhmaaEiVoidaKopioida(Throwable cause, String lahdeOid, String parentOid) {
        super(cause);
        this.lahdeOid = lahdeOid;
        this.parentOid = parentOid;
    }
}