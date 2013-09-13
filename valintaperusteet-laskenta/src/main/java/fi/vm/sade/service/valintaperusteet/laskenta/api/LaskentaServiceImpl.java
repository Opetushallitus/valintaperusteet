package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.laskenta.Esiprosessori;
import fi.vm.sade.service.valintaperusteet.laskenta.Laskin;
import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;
import fi.vm.sade.service.valintaperusteet.laskenta.Totuusarvofunktio;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * User: kwuoti Date: 24.2.2013 Time: 21.33
 */
public class LaskentaServiceImpl implements LaskentaService {

    public Laskentatulos<BigDecimal> suoritaLasku(Hakukohde hakukohde, Hakemus hakemus,
                                                  Collection<Hakemus> kaikkiHakemukset, Lukuarvofunktio laskettava, StringBuffer historia) {
        Hakemus prosessoituHakemus = Esiprosessori.esiprosessoi(hakukohde, kaikkiHakemukset, hakemus, laskettava);
        return Laskin.suoritaLasku(hakukohde, prosessoituHakemus, laskettava, historia);
    }

    public Laskentatulos<Boolean> suoritaLasku(Hakukohde hakukohde, Hakemus hakemus, Totuusarvofunktio laskettava,
                                               StringBuffer historia) {
        return Laskin.suoritaLasku(hakukohde, hakemus, laskettava, historia);
    }

}
