package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

import java.util.Collection;

public interface LaskentaService {

    public Laskentatulos<Double> suoritaLasku(String hakukohde, Hakemus hakemus,
                                              Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava);

    public Laskentatulos<Boolean> suoritaLasku(String hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava);
}
