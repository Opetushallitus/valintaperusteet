package fi.vm.sade.service.valintaperusteet.service.impl.util.koodi;

import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;

/**
 * User: wuoti
 * Date: 25.6.2013
 * Time: 13.29
 */
public class OpetuskielikoodiHandler extends KoodiHandler<Opetuskielikoodi> {
    public OpetuskielikoodiHandler(ValintaryhmaService valintaryhmaService, OpetuskielikoodiDAO koodiDAO) {
        super(valintaryhmaService, koodiDAO);
    }

    @Override
    protected void clearValintaryhmaKoodis(Valintaryhma valintaryhma) {
        valintaryhma.getOpetuskielikoodit().clear();
    }

    @Override
    protected void addKoodiToValintaryhma(Valintaryhma valintaryhma, Opetuskielikoodi koodi) {
        valintaryhma.getOpetuskielikoodit().add(koodi);
    }
}
