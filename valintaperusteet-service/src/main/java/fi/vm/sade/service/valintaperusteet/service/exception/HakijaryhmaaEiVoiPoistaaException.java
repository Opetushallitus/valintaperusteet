package fi.vm.sade.service.valintaperusteet.service.exception;

public class HakijaryhmaaEiVoiPoistaaException extends RuntimeException {
    public HakijaryhmaaEiVoiPoistaaException() {
    }

    public HakijaryhmaaEiVoiPoistaaException(String message) {
        super(message);
    }

    public HakijaryhmaaEiVoiPoistaaException(String message, Throwable cause) {
        super(message, cause);
    }

    public HakijaryhmaaEiVoiPoistaaException(Throwable cause) {
        super(cause);
    }
}
