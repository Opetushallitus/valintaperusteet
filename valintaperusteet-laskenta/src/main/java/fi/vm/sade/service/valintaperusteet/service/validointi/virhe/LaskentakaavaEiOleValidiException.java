package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

/**
 * User: wuoti
 * Date: 4.9.2013
 * Time: 16.10
 */
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
