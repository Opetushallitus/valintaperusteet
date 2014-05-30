package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.LokalisoituTeksti;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.schema.TekstiRyhmaTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintaperustelahdeTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintaperusteviiteTyyppi;
import org.springframework.core.convert.converter.Converter;

/**
 * User: jukais
 * Date: 7.3.2013
 * Time: 14.03
 */
public class ValintaperusteViiteToValintaperusteViiteTyyppi implements Converter<ValintaperusteViite, ValintaperusteviiteTyyppi> {
    @Override
    public ValintaperusteviiteTyyppi convert(ValintaperusteViite valintaperusteViite) {
        ValintaperusteviiteTyyppi tyyppi = new ValintaperusteviiteTyyppi();

        tyyppi.setTunniste(valintaperusteViite.getTunniste());
        tyyppi.setOnPakollinen(valintaperusteViite.getOnPakollinen());
        tyyppi.setLahde(ValintaperustelahdeTyyppi.valueOf(valintaperusteViite.getLahde().name()));
        tyyppi.setIndeksi(valintaperusteViite.getIndeksi());
        tyyppi.setEpasuoraViittaus(valintaperusteViite.getEpasuoraViittaus());
        tyyppi.setKuvaus(valintaperusteViite.getKuvaus());

        LokalisoituTekstiToLokalisoituTekstiTyyppiConverter converter = new LokalisoituTekstiToLokalisoituTekstiTyyppiConverter();
        TekstiRyhmaTyyppi ryhma = new TekstiRyhmaTyyppi();
        if (valintaperusteViite.getKuvaukset() != null && valintaperusteViite.getKuvaukset().getTekstit() != null) {
            for (LokalisoituTeksti k : valintaperusteViite.getKuvaukset().getTekstit()) {
                ryhma.getTekstit().add(converter.convert(k));
            }
        }
        tyyppi.setKuvaukset(ryhma);

        return tyyppi;
    }
}