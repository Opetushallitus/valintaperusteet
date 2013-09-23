package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

import java.math.BigDecimal;
import java.util.Collection;

public interface LaskentaService {

    public Laskentatulos<BigDecimal> suoritaValintalaskenta(Hakukohde hakukohde, Hakemus hakemus,
                                                            Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava);

    public Laskentatulos<Boolean> suoritaValintakoelaskenta(Hakukohde hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava);
}
