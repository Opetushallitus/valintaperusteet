package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.schema.FunktioargumenttiTyyppi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 7.3.2013
 * Time: 13.50
 * To change this template use File | Settings | File Templates.
 */
public class FunktioargumenttiToFunktioargumenttiTyyppi implements Converter<Funktioargumentti, FunktioargumenttiTyyppi> {

    @Autowired
    FunktiokutsuToFunktiokutsuTyyppi funktiokutsuToFunktiokutsuTyyppi;

    @Override
    public FunktioargumenttiTyyppi convert(Funktioargumentti funktioargumentti) {
        FunktioargumenttiTyyppi tyyppi = new FunktioargumenttiTyyppi();

        tyyppi.setIndeksi(funktioargumentti.getIndeksi());

        Funktiokutsu funktiokutsu = null;
        if (funktioargumentti.getLaskentakaavaChild() != null) {
            funktiokutsu = funktioargumentti.getLaajennettuKaava();
        } else {
            funktiokutsu = funktioargumentti.getFunktiokutsuChild();
        }

        tyyppi.setFunktiokutsu(funktiokutsuToFunktiokutsuTyyppi.convert(funktiokutsu));

        return tyyppi;
    }
}
