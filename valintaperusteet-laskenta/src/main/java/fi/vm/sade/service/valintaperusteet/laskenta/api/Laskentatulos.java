package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila;

import java.util.Map;

public class Laskentatulos<T> {
    public Laskentatulos(Tila tila, T tulos, String historia, Map<String, SyotettyArvo> syotetytArvot, Map<String, FunktioTulos> funktioTulokset) {
        this.tila = tila;
        this.tulos = tulos;
        this.historia = historia;
        this.syotetytArvot = syotetytArvot;
        this.funktioTulokset = funktioTulokset;
    }

    private Tila tila;
    private T tulos;
    private String historia;
    private Map<String, SyotettyArvo> syotetytArvot;
    private Map<String, FunktioTulos> funktioTulokset;

    public Tila getTila() {
        return tila;
    }

    public T getTulos() {
        return tulos;
    }

    public String getHistoria() {
        return historia;
    }

    public Map<String, SyotettyArvo> getSyotetytArvot() {
        return syotetytArvot;
    }

    public Map<String, FunktioTulos> getFunktioTulokset() {
        return funktioTulokset;
    }
}
