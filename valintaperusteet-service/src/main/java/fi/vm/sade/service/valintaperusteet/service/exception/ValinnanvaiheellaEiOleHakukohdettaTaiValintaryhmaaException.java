package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 12.47
 */
public class ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException extends RuntimeException {

    private String valintaryhmaOid;

    public ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException(String message, String valintaryhmaOid) {
        super(message);
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException(String message, Throwable cause, String valintaryhmaOid) {
        super(message, cause);
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException(Throwable cause, String valintaryhmaOid) {
        super(cause);
        this.valintaryhmaOid = valintaryhmaOid;
    }
}
