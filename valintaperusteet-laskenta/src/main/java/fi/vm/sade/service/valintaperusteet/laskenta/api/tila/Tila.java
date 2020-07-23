package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public abstract class Tila {
  public static enum Tilatyyppi {
    HYLATTY(Hylattytila.class),
    HYVAKSYTTAVISSA(Hyvaksyttavissatila.class),
    VIRHE(Virhetila.class);

    private Class<? extends Tila> tyyppi;

    Tilatyyppi(Class<? extends Tila> tyyppi) {
      this.tyyppi = tyyppi;
    }
  }

  public String toString() {
    return tilatyyppi.toString();
  }

  public Tila(Tilatyyppi tilatyyppi) {
    this.tilatyyppi = tilatyyppi;
  }

  private Tilatyyppi tilatyyppi;

  public Tilatyyppi getTilatyyppi() {
    return tilatyyppi;
  }
}
