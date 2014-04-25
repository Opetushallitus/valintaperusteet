package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.schema.TasasijasaantoTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.ValintatapajonoTyyppi;
import org.springframework.core.convert.converter.Converter;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.28
 */
public class JonoToValintatapajonoTyyppi implements Converter<Valintatapajono, ValintatapajonoTyyppi> {
    @Override
    public ValintatapajonoTyyppi convert(Valintatapajono jono) {
        ValintatapajonoTyyppi converted = new ValintatapajonoTyyppi();
        converted.setAloituspaikat(jono.getAloituspaikat());
        converted.setKuvaus(jono.getKuvaus());
        converted.setNimi(jono.getNimi());
        converted.setOid(jono.getOid());
        converted.setSiirretaanSijoitteluun(jono.getSiirretaanSijoitteluun());
        converted.setKaikkiEhdonTayttavatHyvaksytaan(jono.getKaikkiEhdonTayttavatHyvaksytaan());
        converted.setEiVarasijatayttoa(jono.getEiVarasijatayttoa());
        converted.setTasasijasaanto(TasasijasaantoTyyppi.fromValue(jono.getTasapistesaanto().name()));
        return converted;
    }
}
