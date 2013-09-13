package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
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

        return tyyppi;
    }
}