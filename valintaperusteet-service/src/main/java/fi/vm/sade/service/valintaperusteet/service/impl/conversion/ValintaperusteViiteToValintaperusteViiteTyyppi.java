package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.schema.ValintaperusteviiteTyyppi;
import org.springframework.core.convert.converter.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 7.3.2013
 * Time: 14.03
 * To change this template use File | Settings | File Templates.
 */
public class ValintaperusteViiteToValintaperusteViiteTyyppi implements Converter<ValintaperusteViite, ValintaperusteviiteTyyppi> {
    @Override
    public ValintaperusteviiteTyyppi convert(ValintaperusteViite valintaperusteViite) {
        ValintaperusteviiteTyyppi tyyppi = new ValintaperusteviiteTyyppi();

        tyyppi.setTunniste(valintaperusteViite.getTunniste());
        tyyppi.setOnPakollinen(valintaperusteViite.getOnPakollinen());

        return tyyppi;
    }
}
