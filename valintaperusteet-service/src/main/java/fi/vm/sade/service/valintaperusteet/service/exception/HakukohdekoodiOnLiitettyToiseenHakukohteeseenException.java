package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: wuoti
 * Date: 16.5.2013
 * Time: 10.50
 */
public class HakukohdekoodiOnLiitettyToiseenHakukohteeseenException extends RuntimeException {
    public HakukohdekoodiOnLiitettyToiseenHakukohteeseenException() {
    }

    public HakukohdekoodiOnLiitettyToiseenHakukohteeseenException(String message) {
        super(message);
    }

    public HakukohdekoodiOnLiitettyToiseenHakukohteeseenException(String message, Throwable cause) {
        super(message, cause);
    }

    public HakukohdekoodiOnLiitettyToiseenHakukohteeseenException(Throwable cause) {
        super(cause);
    }
}
