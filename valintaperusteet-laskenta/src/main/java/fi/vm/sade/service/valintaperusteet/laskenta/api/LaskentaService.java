package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;

import java.util.Collection;

public interface LaskentaService {

    public Laskentatulos<Double> suoritaLasku(String hakukohde, Hakemus hakemus,
                                              Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava);
}
