package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Arvokonvertterihylkays.class, name = "Arvokonvertterihylkays"),
  @JsonSubTypes.Type(value = Arvovalikonvertterihylkays.class, name = "Arvovalikonvertterihylkays"),
  @JsonSubTypes.Type(
      value = PakollinenValintaperusteHylkays.class,
      name = "PakollinenValintaperusteHylkays"),
  @JsonSubTypes.Type(value = EiOsallistunutHylkays.class, name = "EiOsallistunutHylkays"),
  @JsonSubTypes.Type(
      value = HylkaaFunktionSuorittamaHylkays.class,
      name = "HylkaaFunktionSuorittamaHylkays"),
})
public abstract class HylattyMetatieto {
  public static enum Hylattymetatietotyyppi {
    ARVOKONVERTTERIHYLKAYS(Arvokonvertterihylkays.class),
    ARVOVALIKONVERTTERIHYLKAYS(Arvovalikonvertterihylkays.class),
    PAKOLLINEN_VALINTAPERUSTE_HYLKAYS(PakollinenValintaperusteHylkays.class),
    EI_OSALLISTUNUT_HYLKAYS(EiOsallistunutHylkays.class),
    HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS(HylkaaFunktionSuorittamaHylkays.class);

    private Class<? extends HylattyMetatieto> tyyppi;

    Hylattymetatietotyyppi(Class<? extends HylattyMetatieto> tyyppi) {
      this.tyyppi = tyyppi;
    }
  }

  public HylattyMetatieto(Hylattymetatietotyyppi metatietotyyppi) {
    this.metatietotyyppi = metatietotyyppi;
  }

  private Hylattymetatietotyyppi metatietotyyppi;

  public Hylattymetatietotyyppi getMetatietotyyppi() {
    return metatietotyyppi;
  }
}
