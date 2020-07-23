package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import java.math.BigDecimal;

public class Arvovalikonvertterihylkays extends HylattyMetatieto {
  public Arvovalikonvertterihylkays(
      BigDecimal arvo, BigDecimal arvovaliMin, BigDecimal arvovaliMax) {
    super(Hylattymetatietotyyppi.ARVOVALIKONVERTTERIHYLKAYS);
    this.arvo = arvo;
    this.arvovaliMin = arvovaliMin;
    this.arvovaliMax = arvovaliMax;
  }

  public Arvovalikonvertterihylkays() {
    super(Hylattymetatietotyyppi.ARVOVALIKONVERTTERIHYLKAYS);
  }

  private BigDecimal arvo;
  private BigDecimal arvovaliMin;
  private BigDecimal arvovaliMax;

  public BigDecimal getArvo() {
    return arvo;
  }

  public BigDecimal getArvovaliMax() {
    return arvovaliMax;
  }

  public BigDecimal getArvovaliMin() {
    return arvovaliMin;
  }
}
