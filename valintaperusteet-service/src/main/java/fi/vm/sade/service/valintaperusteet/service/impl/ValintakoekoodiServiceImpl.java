package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.ValintakoekoodiService;
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
public class ValintakoekoodiServiceImpl implements ValintakoekoodiService {


    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintakoekoodiDAO valintakoekoodiDAO;

    @Override
    public void lisaaValintakoekoodiValintaryhmalle(String valintaryhmaOid, Valintakoekoodi valintakoekoodi) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);

        Valintakoekoodi haettu = valintakoekoodiDAO.readByUri(valintakoekoodi.getUri());
        if (haettu != null) {
            haettu.setUri(valintakoekoodi.getUri());
            haettu.setNimiFi(valintakoekoodi.getNimiFi());
            haettu.setNimiSv(valintakoekoodi.getNimiSv());
            haettu.setNimiEn(valintakoekoodi.getNimiEn());
            haettu.setArvo(valintakoekoodi.getArvo());
        } else {
            haettu = valintakoekoodiDAO.insert(valintakoekoodi);
        }
        valintaryhma.getValintakoekoodit().add(haettu);
    }
}
