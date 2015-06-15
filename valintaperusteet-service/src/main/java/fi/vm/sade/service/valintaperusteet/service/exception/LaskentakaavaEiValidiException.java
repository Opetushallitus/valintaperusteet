package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

public class LaskentakaavaEiValidiException extends RuntimeException {

    private Laskentakaava validoituLaskentakaava;

    public LaskentakaavaEiValidiException(Laskentakaava validoituLaskentakaava) {
        this.validoituLaskentakaava = validoituLaskentakaava;
    }

    public LaskentakaavaEiValidiException(String message, Laskentakaava validoituLaskentakaava) {
        super(message);
        this.validoituLaskentakaava = validoituLaskentakaava;
    }

    public LaskentakaavaEiValidiException(String message, Throwable cause, Laskentakaava validoituLaskentakaava) {
        super(message, cause);
        this.validoituLaskentakaava = validoituLaskentakaava;
    }

    public LaskentakaavaEiValidiException(Throwable cause, Laskentakaava validoituLaskentakaava) {
        super(cause);
        this.validoituLaskentakaava = validoituLaskentakaava;
    }

    public Laskentakaava getValidoituLaskentakaava() {
        return validoituLaskentakaava;
    }

    public void setValidoituLaskentakaava(Laskentakaava validoituLaskentakaava) {
        this.validoituLaskentakaava = validoituLaskentakaava;
    }
}
