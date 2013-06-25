package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;

import java.util.List;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 15.40
 */
public interface ValintakoekoodiService {

    void lisaaValintakoekoodiValintaryhmalle(String valintaryhmaOid, Valintakoekoodi valintakoekoodi);

    void updateValintaryhmanValintakoekoodit(String valintaryhmaOid, List<Valintakoekoodi> valintakoekoodit);
}
