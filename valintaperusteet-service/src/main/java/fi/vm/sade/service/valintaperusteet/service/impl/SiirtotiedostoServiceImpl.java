package fi.vm.sade.service.valintaperusteet.service.impl;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.service.SiirtotiedostoService;
import fi.vm.sade.service.valintaperusteet.service.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.util.SiirtotiedostoS3Client;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiirtotiedostoServiceImpl implements SiirtotiedostoService {
  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;
  @Autowired private ValintaperusteService valintaperusteService;

  private static final Logger logger =
      LoggerFactory.getLogger(ValinnanVaiheServiceImpl.class.getName());
  private final SiirtotiedostoS3Client siirtotiedostoS3Client;

  @Autowired
  public SiirtotiedostoServiceImpl(final SiirtotiedostoS3Client siirtotiedostoS3Client) {
    this.siirtotiedostoS3Client = siirtotiedostoS3Client;
  }

  private HakuparametritDTO createDto(String hakukohdeOid) {
    HakuparametritDTO dto = new HakuparametritDTO();
    dto.setHakukohdeOid(hakukohdeOid);
    dto.haetaankoLaskukaavat(false);
    dto.haetaankoPaivitysAikaleimat(true);
    return dto;
  }

  public String createSiirtotiedostot(LocalDateTime startDatetime, LocalDateTime endDatetime) {
    List<String> oids = hakukohdeViiteDAO.findNewOrChangedHakukohdeOids(startDatetime, endDatetime);
    Iterator<List<String>> partitionIterator =
        Lists.partition(oids, siirtotiedostoS3Client.getMaxHakukohdeCountInFile()).iterator();
    List<String> siirtotiedostoKeys = new ArrayList<>();
    String operationId = UUID.randomUUID().toString();
    while (partitionIterator.hasNext()) {
      List<HakuparametritDTO> dtoList =
          partitionIterator.next().stream().map(this::createDto).collect(toList());
      List<ValintaperusteetDTO> valintaperusteet =
          valintaperusteService.haeValintaperusteet(dtoList);
      siirtotiedostoKeys.add(
          siirtotiedostoS3Client.createSiirtotiedosto(
              valintaperusteet, operationId, siirtotiedostoKeys.size() + 1));
    }
    logger.info(
        "Kirjoitettiin yhteensä {} hakukohteen valintaperusteet {} siirtotiedostoon, operaatioId: {}",
        oids.size(),
        siirtotiedostoKeys.size(),
        operationId);

    JsonObject result = new JsonObject();
    JsonArray keyJson = new JsonArray();
    siirtotiedostoKeys.forEach(keyJson::add);
    result.add("keys", keyJson);
    result.addProperty("total", oids.size());
    result.addProperty("success", true);
    return result.toString();
  }
}
