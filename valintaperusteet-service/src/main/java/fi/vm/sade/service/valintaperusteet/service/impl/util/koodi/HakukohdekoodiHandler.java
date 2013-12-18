package fi.vm.sade.service.valintaperusteet.service.impl.util.koodi;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;

/**
 * User: wuoti
 * Date: 25.6.2013
 * Time: 13.26
 */
public class HakukohdekoodiHandler extends KoodiHandler<Hakukohdekoodi> {
    public HakukohdekoodiHandler(ValintaryhmaService valintaryhmaService, HakukohdekoodiDAO koodiDAO) {
        super(valintaryhmaService, koodiDAO);
    }

    @Override
    protected void clearValintaryhmaKoodis(Valintaryhma valintaryhma) {
        valintaryhma.getHakukohdekoodit().clear();
    }

    @Override
    protected void addKoodiToValintaryhma(Valintaryhma valintaryhma, Hakukohdekoodi koodi) {
        valintaryhma.getHakukohdekoodit().add(koodi);
    }
}
