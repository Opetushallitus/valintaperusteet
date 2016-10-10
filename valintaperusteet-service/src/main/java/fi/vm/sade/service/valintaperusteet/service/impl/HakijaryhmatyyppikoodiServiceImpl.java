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
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmatyyppikoodiOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class HakijaryhmatyyppikoodiServiceImpl implements HakijaryhmatyyppikoodiService {

    @Autowired
    private HakijaryhmatyyppikoodiDAO hakijaryhmatyyppikoodiDAO;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public Hakijaryhmatyyppikoodi getOrCreateHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi) {
        if (hakijaryhmatyyppikoodi == null || hakijaryhmatyyppikoodi.getUri() == null) {
            throw new HakijaryhmatyyppikoodiOnTyhjaException("hakijaryhman tyyppikoodi on tyhjä");
        }
        Hakijaryhmatyyppikoodi haettu = hakijaryhmatyyppikoodiDAO.readByUri(hakijaryhmatyyppikoodi.getUri());
        if (haettu == null) {
            haettu = hakijaryhmatyyppikoodiDAO.insert(modelMapper.map(hakijaryhmatyyppikoodi, Hakijaryhmatyyppikoodi.class));
        }
        hakijaryhmatyyppikoodiDAO.detach(haettu);
        return haettu;
    }
}
