package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila;

/**
 * User: kwuoti
 * Date: 24.2.2013
 * Time: 19.21
 */
public class Laskentatulos<T> {

    public Laskentatulos(Tila tila, T tulos) {
        this.tila = tila;
        this.tulos = tulos;
    }

    private Tila tila;
    private T tulos;

    public Tila getTila() {
        return tila;
    }

    public void setTila(Tila tila) {
        this.tila = tila;
    }

    public T getTulos() {
        return tulos;
    }

    public void setTulos(T tulos) {
        this.tulos = tulos;
    }
}
