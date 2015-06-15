package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValinnanVaiheEpaaktiivinenException extends RuntimeException {
    public ValinnanVaiheEpaaktiivinenException() {
    }

    public ValinnanVaiheEpaaktiivinenException(String message) {
        super(message);
    }

    public ValinnanVaiheEpaaktiivinenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValinnanVaiheEpaaktiivinenException(Throwable cause) {
        super(cause);
    }
}
