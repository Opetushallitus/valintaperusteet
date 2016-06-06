package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

import java.math.BigDecimal;
import java.util.Collection;

public interface LaskentaService {
    Laskentatulos<BigDecimal> suoritaValintalaskenta(Hakukohde hakukohde, Hakemus hakemus, Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava);

    Laskentatulos<Boolean> suoritaValintalaskenta(Hakukohde hakukohde, Hakemus hakemus, Collection<Hakemus> kaikkiHakemukset, Totuusarvofunktio laskettava);

    Laskentatulos<BigDecimal> suoritaValintakoelaskenta(Hakukohde hakukohde, Hakemus hakemus, Lukuarvofunktio laskettava);

    Laskentatulos<Boolean> suoritaValintakoelaskenta(Hakukohde hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava);
}
