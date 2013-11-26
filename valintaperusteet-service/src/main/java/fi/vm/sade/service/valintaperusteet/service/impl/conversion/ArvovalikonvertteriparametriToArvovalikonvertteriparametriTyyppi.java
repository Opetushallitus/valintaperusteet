package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.schema.ArvovalikonvertteriparametriTyyppi;
import org.springframework.core.convert.converter.Converter;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 7.3.2013 Time: 13.45 To change
 * this template use File | Settings | File Templates.
 */
public class ArvovalikonvertteriparametriToArvovalikonvertteriparametriTyyppi implements
        Converter<Arvovalikonvertteriparametri, ArvovalikonvertteriparametriTyyppi> {

    @Override
    public ArvovalikonvertteriparametriTyyppi convert(Arvovalikonvertteriparametri arvovalikonvertteriparametri) {
        ArvovalikonvertteriparametriTyyppi tyyppi = new ArvovalikonvertteriparametriTyyppi();

        tyyppi.setMaksimiarvo(arvovalikonvertteriparametri.getMaxValue().toString());
        tyyppi.setMinimiarvo(arvovalikonvertteriparametri.getMinValue().toString());
        tyyppi.setPalautaHaettuArvo(arvovalikonvertteriparametri.getPalautaHaettuArvo());
        tyyppi.setPaluuarvo(arvovalikonvertteriparametri.getPaluuarvo());

        return tyyppi;
    }
}
