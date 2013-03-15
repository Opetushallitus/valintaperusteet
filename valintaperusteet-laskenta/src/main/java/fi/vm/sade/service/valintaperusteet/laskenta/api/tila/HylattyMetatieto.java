package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.07
 */
public abstract class HylattyMetatieto {
    public static enum Hylattymetatietotyyppi {
        ARVOKONVERTTERIHYLKAYS(Arvokonvertterihylkays.class),
        ARVOVALIKONVERTTERIHYLKAYS(Arvovalikonvertterihylkays.class),
        PAKOLLINEN_VALINTAPERUSTE_HYLKAYS(PakollinenValintaperusteHylkays.class);

        private Class<? extends HylattyMetatieto> tyyppi;

        Hylattymetatietotyyppi(Class<? extends  HylattyMetatieto> tyyppi) {
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
