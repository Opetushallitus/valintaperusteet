package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;

import java.util.Set;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 14.26
 */
public interface OpetuskielikoodiService {
    void lisaaOpetuskielikoodiValintaryhmalle(String valintaryhmaOid, Opetuskielikoodi opetuskielikoodi);

    void updateValintaryhmaOpetuskielikoodit(String valintaryhmaOid, Set<Opetuskielikoodi> opetuskielikoodit);
}
