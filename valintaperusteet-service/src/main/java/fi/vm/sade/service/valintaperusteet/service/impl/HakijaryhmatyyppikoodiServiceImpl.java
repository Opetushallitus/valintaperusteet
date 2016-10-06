package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmatyyppikoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.KoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Koodi;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class HakijaryhmatyyppikoodiServiceImpl implements HakijaryhmatyyppikoodiService {

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private HakijaryhmatyyppikoodiDAO hakijaryhmatyyppikoodiDAO;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public void updateHakijaryhmanTyyppikoodi(String hakijaryhmaOid, KoodiDTO hakijaryhmatyyppikoodi) {
        Hakijaryhma hakijaryhma = hakijaryhmaService.readByOid(hakijaryhmaOid);
        if(hakijaryhmatyyppikoodi == null) {
            hakijaryhma.setHakijaryhmatyyppikoodi(null);
        } else {
            Hakijaryhmatyyppikoodi haettu = hakijaryhmatyyppikoodiDAO.readByUri(hakijaryhmatyyppikoodi.getUri());
            if (haettu == null) {
                haettu = hakijaryhmatyyppikoodiDAO.insert(modelMapper.map(hakijaryhmatyyppikoodi, Hakijaryhmatyyppikoodi.class));
            }
            haettu.setArvo(hakijaryhmatyyppikoodi.getArvo());
            haettu.setNimiEn(hakijaryhmatyyppikoodi.getNimiEn());
            haettu.setNimiFi(hakijaryhmatyyppikoodi.getNimiFi());
            haettu.setNimiSv(hakijaryhmatyyppikoodi.getNimiSv());
            hakijaryhma.setHakijaryhmatyyppikoodi(haettu);
        }
    }

    @Override
    public void updateHakijaryhmaValintatapajononTyyppikoodi(String hakijaryhmaOid, KoodiDTO hakijaryhmatyyppikoodi) {
        HakijaryhmaValintatapajono hakijaryhma = hakijaryhmaValintatapajonoService.readByOid(hakijaryhmaOid);
        if(hakijaryhmatyyppikoodi == null) {
            hakijaryhma.setHakijaryhmatyyppikoodi(null);
        } else {
            Hakijaryhmatyyppikoodi haettu = hakijaryhmatyyppikoodiDAO.readByUri(hakijaryhmatyyppikoodi.getUri());
            if (haettu == null) {
                haettu = hakijaryhmatyyppikoodiDAO.insert(modelMapper.map(hakijaryhmatyyppikoodi, Hakijaryhmatyyppikoodi.class));
            }
            haettu.setArvo(hakijaryhmatyyppikoodi.getArvo());
            haettu.setNimiEn(hakijaryhmatyyppikoodi.getNimiEn());
            haettu.setNimiFi(hakijaryhmatyyppikoodi.getNimiFi());
            haettu.setNimiSv(hakijaryhmatyyppikoodi.getNimiSv());
            hakijaryhma.setHakijaryhmatyyppikoodi(haettu);
        }
    }
}
