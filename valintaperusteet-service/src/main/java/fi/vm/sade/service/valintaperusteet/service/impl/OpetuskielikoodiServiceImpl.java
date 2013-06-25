package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.OpetuskielikoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);

        Opetuskielikoodi haettu = opetuskielikoodiDAO.readByUri(opetuskielikoodi.getUri());
        if (haettu != null) {
            haettu.setUri(opetuskielikoodi.getUri());
            haettu.setNimiFi(opetuskielikoodi.getNimiFi());
            haettu.setNimiSv(opetuskielikoodi.getNimiSv());
            haettu.setNimiEn(opetuskielikoodi.getNimiEn());
            haettu.setArvo(opetuskielikoodi.getArvo());
        } else {
            haettu = opetuskielikoodiDAO.insert(opetuskielikoodi);
        }
        valintaryhma.getOpetuskielikoodit().add(haettu);
    }

}
