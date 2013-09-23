package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Laskin;
import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * User: kwuoti Date: 24.2.2013 Time: 21.33
 */
public class LaskentaServiceImpl implements LaskentaService {

    public Laskentatulos<BigDecimal> suoritaValintalaskenta(Hakukohde hakukohde, Hakemus hakemus,
                                                            Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava) {
        return Laskin.suoritaValintalaskenta(hakukohde, hakemus, kaikkiHakemukset, laskettava);
    }

    public Laskentatulos<Boolean> suoritaValintakoelaskenta(Hakukohde hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava) {
        return Laskin.suoritaValintakoelaskenta(hakukohde, hakemus, laskettava);
    }

}
