package fi.vm.sade.service.valintaperusteet.laskenta.api;

import java.math.BigDecimal;
import java.util.Collection;

import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

public interface LaskentaService {

    public Laskentatulos<BigDecimal> suoritaLasku(String hakukohde, Hakemus hakemus,
            Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava, StringBuffer historia);

    public Laskentatulos<Boolean> suoritaLasku(String hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava,
            StringBuffer historia);
}
