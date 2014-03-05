package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.LokalisoituTeksti;
import fi.vm.sade.service.valintaperusteet.schema.ArvovalikonvertteriparametriTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.TekstiRyhmaTyyppi;
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

        tyyppi.setHylkaysperuste(arvovalikonvertteriparametri.getHylkaysperuste());
        tyyppi.setPaluuarvo(arvovalikonvertteriparametri.getPaluuarvo());

        LokalisoituTekstiToLokalisoituTekstiTyyppiConverter converter = new LokalisoituTekstiToLokalisoituTekstiTyyppiConverter();
        TekstiRyhmaTyyppi ryhma = new TekstiRyhmaTyyppi();
        if (arvovalikonvertteriparametri.getKuvaukset() != null && arvovalikonvertteriparametri.getKuvaukset().getTekstit() != null) {
            for (LokalisoituTeksti k : arvovalikonvertteriparametri.getKuvaukset().getTekstit()) {
                ryhma.getTekstit().add(converter.convert(k));
            }
        }
        tyyppi.setKuvaukset(ryhma);

        return tyyppi;
    }
}
