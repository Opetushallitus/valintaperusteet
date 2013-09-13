package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

import java.math.BigDecimal;
import java.util.Collection;

public interface LaskentaService {

    public Laskentatulos<BigDecimal> suoritaLasku(Hakukohde hakukohde, Hakemus hakemus,
                                                  Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava, StringBuffer historia);

    public Laskentatulos<Boolean> suoritaLasku(Hakukohde hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava,
                                               StringBuffer historia);
}
