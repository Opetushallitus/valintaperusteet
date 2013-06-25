package fi.vm.sade.service.valintaperusteet.service.impl.util.koodi;

import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;

/**
 * User: wuoti
 * Date: 25.6.2013
 * Time: 13.53
 */
public class ValintakoekoodiHandler extends KoodiHandler<Valintakoekoodi> {

    public ValintakoekoodiHandler(ValintaryhmaService valintaryhmaService, ValintakoekoodiDAO koodiDAO) {
        super(valintaryhmaService, koodiDAO);
    }

    @Override
    protected void clearValintaryhmaKoodis(Valintaryhma valintaryhma) {
        valintaryhma.getValintakoekoodit().clear();
    }

    @Override
    protected void addKoodiToValintaryhma(Valintaryhma valintaryhma, Valintakoekoodi koodi) {
        valintaryhma.getValintakoekoodit().add(koodi);
    }
}
