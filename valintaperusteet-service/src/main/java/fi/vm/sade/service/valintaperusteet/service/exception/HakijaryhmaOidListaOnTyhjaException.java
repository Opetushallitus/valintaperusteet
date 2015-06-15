package fi.vm.sade.service.valintaperusteet.service.exception;

public class HakijaryhmaOidListaOnTyhjaException extends RuntimeException {
    public HakijaryhmaOidListaOnTyhjaException() {
    }

    public HakijaryhmaOidListaOnTyhjaException(String message) {
        super(message);
    }

    public HakijaryhmaOidListaOnTyhjaException(String message, Throwable cause) {
        super(message, cause);
    }

    public HakijaryhmaOidListaOnTyhjaException(Throwable cause) {
        super(cause);
    }
}
