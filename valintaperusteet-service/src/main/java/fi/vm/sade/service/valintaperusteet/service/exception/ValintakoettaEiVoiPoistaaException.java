package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: wuoti
 * Date: 10.5.2013
 * Time: 10.15
 *
 * */
public class ValintakoettaEiVoiPoistaaException extends  RuntimeException{

    public ValintakoettaEiVoiPoistaaException() {
    }

    public ValintakoettaEiVoiPoistaaException(String message) {
        super(message);
    }

    public ValintakoettaEiVoiPoistaaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValintakoettaEiVoiPoistaaException(Throwable cause) {
        super(cause);
    }
}
