package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import java.util.Map;

public class Virhetila extends Tila {

  public Virhetila(Map<String, String> kuvaus, VirheMetatieto metatieto) {
    super(Tilatyyppi.VIRHE);
    this.kuvaus = kuvaus;
    this.metatieto = metatieto;
  }

  public Virhetila() {
    super(Tilatyyppi.VIRHE);
  }

  private Map<String, String> kuvaus;
  private VirheMetatieto metatieto;

  public Map<String, String> getKuvaus() {
    return kuvaus;
  }

  public VirheMetatieto getMetatieto() {
    return metatieto;
  }
}
