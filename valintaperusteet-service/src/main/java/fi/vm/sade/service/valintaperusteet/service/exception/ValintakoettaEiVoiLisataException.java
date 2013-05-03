package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.33
 */
public class ValintakoettaEiVoiLisataException extends RuntimeException {
    private String valintatapajonoOid;

    public ValintakoettaEiVoiLisataException(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public ValintakoettaEiVoiLisataException(String message, String valintatapajonoOid) {
        super(message);
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public ValintakoettaEiVoiLisataException(String message, Throwable cause, String valintatapajonoOid) {
        super(message, cause);
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public ValintakoettaEiVoiLisataException(Throwable cause, String valintatapajonoOid) {
        super(cause);
        this.valintatapajonoOid = valintatapajonoOid;
    }
}
