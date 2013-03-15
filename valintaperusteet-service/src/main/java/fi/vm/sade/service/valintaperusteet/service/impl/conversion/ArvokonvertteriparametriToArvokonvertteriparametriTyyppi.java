package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.schema.ArvokonvertteriparametriTyyppi;
import org.springframework.core.convert.converter.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 7.3.2013
 * Time: 13.26
 * To change this template use File | Settings | File Templates.
 */
public class ArvokonvertteriparametriToArvokonvertteriparametriTyyppi implements Converter<Arvokonvertteriparametri, ArvokonvertteriparametriTyyppi> {

    @Override
    public ArvokonvertteriparametriTyyppi convert(Arvokonvertteriparametri arvokonvertteriparametri) {
        ArvokonvertteriparametriTyyppi tyyppi = new ArvokonvertteriparametriTyyppi();

        tyyppi.setArvo(arvokonvertteriparametri.getArvo());
        tyyppi.setHylkaysperuste(arvokonvertteriparametri.getHylkaysperuste());
        tyyppi.setPaluuarvo(arvokonvertteriparametri.getPaluuarvo());

        return tyyppi;
    }
}
