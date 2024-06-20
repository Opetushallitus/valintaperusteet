package fi.vm.sade.service.valintaperusteet.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.PoistettuDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.PoistetutDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.model.Poistettu;
import fi.vm.sade.service.valintaperusteet.service.SiirtotiedostoService;
import fi.vm.sade.service.valintaperusteet.service.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.util.SiirtotiedostoS3Client;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiirtotiedostoServiceImpl implements SiirtotiedostoService {
  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;
  @Autowired private ValintaperusteService valintaperusteService;
  @Autowired private PoistettuDAO poistettuDAO;

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
    PoistetutDTO poistetut = findPoistetut(startDatetime, endDatetime);
    if (!poistetut.getPoistetut().isEmpty()) {
      siirtotiedostoKeys.add(
          siirtotiedostoS3Client.createSiirtotiedosto(
              poistetut.getPoistetut(), operationId, siirtotiedostoKeys.size() + 1));
    }

    logger.info(
        "Kirjoitettiin yhteensä {} hakukohteen valintaperusteet {} siirtotiedostoon, operaatioId: {}",
        oids.size() + poistetut.getPoistetut().size(),
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

  private PoistetutDTO findPoistetut(LocalDateTime startDatetime, LocalDateTime endDatetime) {
    List<Poistettu> hakukohdeViitteet =
        poistettuDAO.findPoistetutHakukohdeViitteet(startDatetime, endDatetime);
    List<Poistettu> valinnanVaiheet =
        poistettuDAO.findPoistetutValinnanvaiheet(startDatetime, endDatetime);
    List<Poistettu> valintatapaJonot =
        poistettuDAO.findPoistetutValintatapajonot(startDatetime, endDatetime);
    List<Poistettu> valintaKokeet =
        poistettuDAO.findPoistetutValintakokeet(startDatetime, endDatetime);
    List<Poistettu> valintaPerusteet =
        poistettuDAO.findPoistetutValintaperusteet(startDatetime, endDatetime);

    Set<Long> missingHakukohdeviiteIds = new HashSet<>();
    Set<Long> missingValinnanvaiheIds = new HashSet<>();

    missingHakukohdeviiteIds.addAll(valinnanVaiheet.stream().map(Poistettu::getParentId).toList());
    missingHakukohdeviiteIds.addAll(valintaPerusteet.stream().map(Poistettu::getParentId).toList());
    missingHakukohdeviiteIds.removeAll(hakukohdeViitteet.stream().map(Poistettu::getId).toList());

    missingValinnanvaiheIds.addAll(valintatapaJonot.stream().map(Poistettu::getParentId).toList());
    missingValinnanvaiheIds.addAll(valintaKokeet.stream().map(Poistettu::getParentId).toList());
    missingValinnanvaiheIds.removeAll(valinnanVaiheet.stream().map(Poistettu::getId).toList());

    List<Poistettu> additionalParentHakukohdeviitteet =
        poistettuDAO.findHakukohdeviitteetFromHistory(missingHakukohdeviiteIds);
    List<Poistettu> additionalParentValinnanvaiheet =
        poistettuDAO.findValinnanvaiheetFromHistory(missingHakukohdeviiteIds);

    Map<Long, String> hakukohdeViiteParents =
        new HashMap<>(
            hakukohdeViitteet.stream().collect(toMap(Poistettu::getId, Poistettu::getTunniste)));
    hakukohdeViiteParents.putAll(
        additionalParentHakukohdeviitteet.stream()
            .collect(toMap(Poistettu::getId, Poistettu::getTunniste)));

    Map<Long, String> valinnanvaiheParents =
        new HashMap<>(
            valinnanVaiheet.stream().collect(toMap(Poistettu::getId, Poistettu::getTunniste)));
    valinnanvaiheParents.putAll(
        additionalParentValinnanvaiheet.stream()
            .collect(toMap(Poistettu::getId, Poistettu::getTunniste)));

    // TODO Lisää dto:hon kaikki muut poistetut entiteetit parentteineen
    List<PoistetutDTO.HakukohdeViite> poistetutViitteet =
        hakukohdeViitteet.stream()
            .map(
                viite -> {
                  PoistetutDTO.HakukohdeViite hakukohdeViite =
                      new PoistetutDTO.HakukohdeViite().setHakukohdeOid(viite.getTunniste());
                  return hakukohdeViite;
                })
            .toList();
    PoistetutDTO dto = new PoistetutDTO();
    dto.setPoistetut(poistetutViitteet);
    return dto;
  }
}
