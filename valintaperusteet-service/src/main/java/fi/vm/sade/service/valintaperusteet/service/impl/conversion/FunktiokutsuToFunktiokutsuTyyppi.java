package fi.vm.sade.service.valintaperusteet.service.impl.conversion;

import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.28
 */
public class FunktiokutsuToFunktiokutsuTyyppi implements Converter<Funktiokutsu, FunktiokutsuTyyppi> {

    @Autowired
    private ArvokonvertteriparametriToArvokonvertteriparametriTyyppi arvokonvertteriparametriToArvokonvertteriparametriTyyppi;

    @Autowired
    private ArvovalikonvertteriparametriToArvovalikonvertteriparametriTyyppi arvovalikonvertteriparametriToArvovalikonvertteriparametriTyyppi;

    @Autowired
    private FunktioargumenttiToFunktioargumenttiTyyppi funktioargumenttiToFunktioargumenttiTyyppi;

    @Autowired
    private SyoteparametriToSyoteparametriTyyppi syoteparametriToSyoteparametriTyyppi;

    @Autowired
    private ValintaperusteViiteToValintaperusteViiteTyyppi valintaperusteViiteToValintaperusteViiteTyyppi;

    @Override
    public FunktiokutsuTyyppi convert(Funktiokutsu funktiokutsu) {
        FunktiokutsuTyyppi tyyppi = new FunktiokutsuTyyppi();

        tyyppi.setFunktionimi(funktiokutsu.getFunktionimi().name());
        tyyppi.setOid(funktiokutsu.getId());

        for (Arvokonvertteriparametri arvokonvertteriparametri : funktiokutsu.getArvokonvertteriparametrit()) {
            ArvokonvertteriparametriTyyppi convert = arvokonvertteriparametriToArvokonvertteriparametriTyyppi.convert(arvokonvertteriparametri);
            tyyppi.getArvokonvertteriparametrit().add(convert);
        }

        for (Arvovalikonvertteriparametri arvovalikonvertteriparametri : funktiokutsu.getArvovalikonvertteriparametrit()) {
            ArvovalikonvertteriparametriTyyppi convert = arvovalikonvertteriparametriToArvovalikonvertteriparametriTyyppi.convert(arvovalikonvertteriparametri);
            tyyppi.getArvovalikonvertteriparametrit().add(convert);
        }

        for (Funktioargumentti funktioargumentti : funktiokutsu.getFunktioargumentit()) {
            FunktioargumenttiTyyppi convert = funktioargumenttiToFunktioargumenttiTyyppi.convert(funktioargumentti);
            tyyppi.getFunktioargumentit().add(convert);
        }

        for (Syoteparametri syoteparametri : funktiokutsu.getSyoteparametrit()) {
            SyoteparametriTyyppi convert = syoteparametriToSyoteparametriTyyppi.convert(syoteparametri);
            tyyppi.getSyoteparametrit().add(convert);
        }

        if (funktiokutsu.getValintaperuste() != null) {
            ValintaperusteviiteTyyppi convert = valintaperusteViiteToValintaperusteViiteTyyppi.convert(funktiokutsu.getValintaperuste());
            tyyppi.setValintaperusteviite(convert);
        }


        return tyyppi;
    }
}
