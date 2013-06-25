package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;
import fi.vm.sade.service.valintaperusteet.service.OpetuskielikoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.impl.util.koodi.OpetuskielikoodiHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 14.25
 */
@Service
@Transactional
public class OpetuskielikoodiServiceImpl implements OpetuskielikoodiService {

    @Autowired
    private OpetuskielikoodiDAO opetuskielikoodiDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Override
    public void lisaaOpetuskielikoodiValintaryhmalle(String valintaryhmaOid, Opetuskielikoodi opetuskielikoodi) {
        new OpetuskielikoodiHandler(valintaryhmaService, opetuskielikoodiDAO)
                .lisaaKoodiValintaryhmalle(valintaryhmaOid, opetuskielikoodi);
    }

    @Override
    public void updateValintaryhmaOpetuskielikoodit(String valintaryhmaOid, Set<Opetuskielikoodi> opetuskielikoodit) {
        new OpetuskielikoodiHandler(valintaryhmaService, opetuskielikoodiDAO)
                .paivitaValintaryhmanKoodit(valintaryhmaOid, opetuskielikoodit);
    }

}
