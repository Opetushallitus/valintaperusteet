package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class PakollinenValintaperusteHylkays extends HylattyMetatieto {
    public PakollinenValintaperusteHylkays(String valintaperustetunniste) {
        super(Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    public PakollinenValintaperusteHylkays() {
        super(Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS);
    }

    private String valintaperustetunniste;

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
