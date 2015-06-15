package fi.vm.sade.service.valintaperusteet.service.exception;

public class ValinnanVaihettaEiVoiPoistaaException extends RuntimeException {
    public ValinnanVaihettaEiVoiPoistaaException() {
    }

    public ValinnanVaihettaEiVoiPoistaaException(String message) {
        super(message);
    }

    public ValinnanVaihettaEiVoiPoistaaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValinnanVaihettaEiVoiPoistaaException(Throwable cause) {
        super(cause);
    }
}
