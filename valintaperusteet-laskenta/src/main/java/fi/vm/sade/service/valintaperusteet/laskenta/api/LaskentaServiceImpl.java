package fi.vm.sade.service.valintaperusteet.laskenta.api;

import java.util.Collection;

import fi.vm.sade.service.valintaperusteet.laskenta.Esiprosessori;
import fi.vm.sade.service.valintaperusteet.laskenta.Laskin;
import fi.vm.sade.service.valintaperusteet.laskenta.Lukuarvofunktio;

/**
 * User: kwuoti Date: 24.2.2013 Time: 21.33
 */
public class LaskentaServiceImpl implements LaskentaService {

    public Laskentatulos<Double> suoritaLasku(String hakukohde, Hakemus hakemus, Collection<Hakemus> kaikkiHakemukset,
            Lukuarvofunktio laskettava) {
        Hakemus prosessoituHakemus = Esiprosessori.esiprosessoi(hakukohde, kaikkiHakemukset, hakemus, laskettava);
        return Laskin.suoritaLasku(hakukohde, prosessoituHakemus, laskettava);
    }

}
