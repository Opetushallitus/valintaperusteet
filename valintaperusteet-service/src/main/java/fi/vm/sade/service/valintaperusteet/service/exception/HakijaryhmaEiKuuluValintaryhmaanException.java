package fi.vm.sade.service.valintaperusteet.service.exception;

public class HakijaryhmaEiKuuluValintaryhmaanException extends RuntimeException {
    private final String hakijaryhmaOid;
    private final String valintaryhmaOid;

    public HakijaryhmaEiKuuluValintaryhmaanException(String message, String hakijaryhmaOid, String valintaryhmaOid) {
        super(message);
        this.hakijaryhmaOid = hakijaryhmaOid;
        this.valintaryhmaOid = valintaryhmaOid;
    }
}
