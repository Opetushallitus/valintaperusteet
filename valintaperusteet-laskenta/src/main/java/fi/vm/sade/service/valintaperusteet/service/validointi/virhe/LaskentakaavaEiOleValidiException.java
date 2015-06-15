package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

public class LaskentakaavaEiOleValidiException extends RuntimeException {
    public LaskentakaavaEiOleValidiException() {
    }

    public LaskentakaavaEiOleValidiException(String message) {
        super(message);
    }

    public LaskentakaavaEiOleValidiException(String message, Throwable cause) {
        super(message, cause);
    }

    public LaskentakaavaEiOleValidiException(Throwable cause) {
        super(cause);
    }
}
