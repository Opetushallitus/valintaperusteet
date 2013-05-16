package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: wuoti
 * Date: 16.5.2013
 * Time: 10.44
 */
public class HakukohdekoodiOnLiitettyToiseenValintaryhmaanException extends RuntimeException {

    public HakukohdekoodiOnLiitettyToiseenValintaryhmaanException() {
    }

    public HakukohdekoodiOnLiitettyToiseenValintaryhmaanException(String message) {
        super(message);
    }

    public HakukohdekoodiOnLiitettyToiseenValintaryhmaanException(String message, Throwable cause) {
        super(message, cause);
    }

    public HakukohdekoodiOnLiitettyToiseenValintaryhmaanException(Throwable cause) {
        super(cause);
    }
}
