package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import fi.vm.sade.service.valintaperusteet.service.ValintakoekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.impl.util.koodi.ValintakoekoodiHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValintakoekoodiServiceImpl implements ValintakoekoodiService {

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValintakoekoodiDAO valintakoekoodiDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void lisaaValintakoekoodiValintaryhmalle(
      String valintaryhmaOid, KoodiDTO valintakoekoodi) {
    new ValintakoekoodiHandler(valintaryhmaService, valintakoekoodiDAO)
        .lisaaKoodiValintaryhmalle(
            valintaryhmaOid, modelMapper.map(valintakoekoodi, Valintakoekoodi.class));
  }

  @Override
  public void updateValintaryhmanValintakoekoodit(
      String valintaryhmaOid, List<KoodiDTO> valintakoekoodit) {
    new ValintakoekoodiHandler(valintaryhmaService, valintakoekoodiDAO)
        .paivitaValintaryhmanKoodit(
            valintaryhmaOid, modelMapper.mapList(valintakoekoodit, Valintakoekoodi.class));
  }
}
