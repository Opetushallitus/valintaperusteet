package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * Created by teemu on 17.11.2014.
 */
public class ValintaryhmaaEiVoidaKopioida extends RuntimeException {

    private String valintaryhmaOid;

    public ValintaryhmaaEiVoidaKopioida(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValintaryhmaaEiVoidaKopioida(String message, String valintaryhmaOid) {
        super(message);
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValintaryhmaaEiVoidaKopioida(String message, Throwable cause, String valintaryhmaOid) {
        super(message, cause);
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValintaryhmaaEiVoidaKopioida(Throwable cause, String valintaryhmaOid) {
        super(cause);
        this.valintaryhmaOid = valintaryhmaOid;
    }
}