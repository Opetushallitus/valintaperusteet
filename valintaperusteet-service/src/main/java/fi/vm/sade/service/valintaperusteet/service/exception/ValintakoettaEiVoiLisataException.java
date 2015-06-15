package fi.vm.sade.service.valintaperusteet.service.exception;

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
