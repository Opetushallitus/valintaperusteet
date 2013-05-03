package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 16.4.2013
 * Time: 13.15
 */
public class ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException extends RuntimeException {
    public ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException() {
    }

    public ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException(String message) {
        super(message);
    }

    public ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException(Throwable cause) {
        super(cause);
    }
}
