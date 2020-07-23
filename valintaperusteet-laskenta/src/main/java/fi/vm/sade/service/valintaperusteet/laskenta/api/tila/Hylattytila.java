package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import java.util.Map;

public class Hylattytila extends Tila {

  public Hylattytila() {
    super(Tilatyyppi.HYLATTY);
  }

  public Hylattytila(Map<String, String> kuvaus, HylattyMetatieto metatieto) {
    super(Tilatyyppi.HYLATTY);
    this.kuvaus = kuvaus;
    this.metatieto = metatieto;
  }

  public Hylattytila(
      Map<String, String> kuvaus, HylattyMetatieto metatieto, String tekninenKuvaus) {
    super(Tilatyyppi.HYLATTY);
    this.kuvaus = kuvaus;
    this.metatieto = metatieto;
    this.tekninenKuvaus = tekninenKuvaus;
  }

  private Map<String, String> kuvaus;

  private HylattyMetatieto metatieto;

  private String tekninenKuvaus;

  public String getTekninenKuvaus() {
    return tekninenKuvaus;
  }

  public void setTekninenKuvaus(String tekninenKuvaus) {
    this.tekninenKuvaus = tekninenKuvaus;
  }

  public Map<String, String> getKuvaus() {
    return kuvaus;
  }

  public HylattyMetatieto getMetatieto() {
    return metatieto;
  }
}
