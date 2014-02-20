package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.LokalisoituTeksti;
import fi.vm.sade.service.valintaperusteet.schema.KieliTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.LokalisoituTekstiTyyppi;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jussi Jartamo
 *
 */
@Component("LokalisoituTekstiKonvertteri")
public class LokalisoituTekstiToLokalisoituTekstiTyyppiConverter implements Converter<LokalisoituTeksti, LokalisoituTekstiTyyppi> {

    public LokalisoituTekstiTyyppi convert(LokalisoituTeksti source) {
        LokalisoituTekstiTyyppi target = new LokalisoituTekstiTyyppi();
        target.setKieli(KieliTyyppi.valueOf(source.getKieli().name()));
        target.setTeksti(source.getTeksti());
        return target;
    }

}
