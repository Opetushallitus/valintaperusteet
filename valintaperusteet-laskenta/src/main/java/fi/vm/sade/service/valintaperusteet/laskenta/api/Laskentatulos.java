package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila;

/**
 * User: kwuoti
 * Date: 24.2.2013
 * Time: 19.21
 */
public class Laskentatulos<T> {


    public Laskentatulos(Tila tila, T tulos, StringBuffer historia) {
        this.tila = tila;
        this.tulos = tulos;
        this.historia = historia;
    }

    private Tila tila;
    private T tulos;
    private StringBuffer historia;

    public Tila getTila() {
        return tila;
    }

    public T getTulos() {
        return tulos;
    }

    public StringBuffer getHistoria() {
        return historia;
    }
}
