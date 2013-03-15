package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;
import fi.vm.sade.service.valintaperusteet.schema.SyoteparametriTyyppi;
import org.springframework.core.convert.converter.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 7.3.2013
 * Time: 13.57
 * To change this template use File | Settings | File Templates.
 */
public class SyoteparametriToSyoteparametriTyyppi implements Converter<Syoteparametri, SyoteparametriTyyppi> {
    @Override
    public SyoteparametriTyyppi convert(Syoteparametri syoteparametri) {
        SyoteparametriTyyppi tyyppi = new SyoteparametriTyyppi();

        tyyppi.setArvo(syoteparametri.getArvo());
        tyyppi.setAvain(syoteparametri.getAvain());

        return tyyppi;
    }
}
