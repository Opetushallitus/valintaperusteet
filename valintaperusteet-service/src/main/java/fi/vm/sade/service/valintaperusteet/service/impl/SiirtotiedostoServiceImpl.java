package fi.vm.sade.service.valintaperusteet.service.impl;

import static java.util.stream.Collectors.*;

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
    logger.info("Creating siirtotiedostot for window {} - {}", startDatetime, endDatetime);
    List<String> oids = hakukohdeViiteDAO.findNewOrChangedHakukohdeOids(startDatetime, endDatetime);
    List<String> siirtotiedostoKeys = new ArrayList<>();
    String operationId = UUID.randomUUID().toString();
    if (!oids.isEmpty()) {
      Iterator<List<String>> partitionIterator =
          Lists.partition(oids, siirtotiedostoS3Client.getMaxHakukohdeCountInFile()).iterator();
      while (partitionIterator.hasNext()) {
        List<HakuparametritDTO> dtoList =
            partitionIterator.next().stream().map(this::createDto).collect(toList());
        List<ValintaperusteetDTO> valintaperusteet =
            valintaperusteService.haeValintaperusteet(dtoList);
        siirtotiedostoKeys.add(
            siirtotiedostoS3Client.createSiirtotiedosto(
                valintaperusteet, operationId, siirtotiedostoKeys.size() + 1));
      }
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
    result.addProperty("total", oids.size() + poistetut.getPoistetut().size());
    result.addProperty("success", true);
    return result.toString();
  }

  private PoistetutDTO findPoistetut(LocalDateTime startDatetime, LocalDateTime endDatetime) {
    List<Poistettu> poistetutHakukohdeViitteet =
        poistettuDAO.findPoistetutHakukohdeViitteet(startDatetime, endDatetime);
    List<Poistettu> poistetutValinnanvaiheet =
        poistettuDAO.findPoistetutValinnanvaiheet(startDatetime, endDatetime);
    List<Poistettu> valintatapaJonot =
        poistettuDAO.findPoistetutValintatapajonot(startDatetime, endDatetime);
    List<Poistettu> valintaKokeet =
        poistettuDAO.findPoistetutValintakokeet(startDatetime, endDatetime);
    List<Poistettu> valintaPerusteet =
        poistettuDAO.findPoistetutValintaperusteet(startDatetime, endDatetime);

    List<Long> requiredParentValinnanvaiheIds =
        new ArrayList<>(valintatapaJonot.stream().map(Poistettu::getParentId).toList());
    requiredParentValinnanvaiheIds.addAll(
        valintaKokeet.stream().map(Poistettu::getParentId).toList());
    List<Poistettu> valinnanVaiheet =
        findAllRequiredParents(
            ParentType.VALINNAN_VAIHE, poistetutValinnanvaiheet, requiredParentValinnanvaiheIds);

    List<Long> requiredParentHakukohdeviiteIds =
        new ArrayList<>(valinnanVaiheet.stream().map(Poistettu::getParentId).toList());
    requiredParentHakukohdeviiteIds.addAll(
        valintaPerusteet.stream().map(Poistettu::getParentId).toList());
    List<Poistettu> hakukohdeViitteet =
        findAllRequiredParents(
            ParentType.HAKUKOHDE_VIITE,
            poistetutHakukohdeViitteet,
            requiredParentHakukohdeviiteIds);

    List<PoistetutDTO.HakukohdeViite> poistetutViitteet =
        hakukohdeViitteet.stream()
            .map(
                viite -> {
                  PoistetutDTO.HakukohdeViite hakukohdeViite =
                      new PoistetutDTO.HakukohdeViite().setHakukohdeOid(viite.getTunniste());
                  hakukohdeViite.setPoistettuItself(viite.isDeletedItself());
                  if (!viite.isDeletedItself()) {
                    // Valinnanvaiheet, hakukohdeviitteellä on vain yksi valinnanvaihe
                    List<Poistettu> viiteChildren = findChildren(valinnanVaiheet, viite);
                    if (!viiteChildren.isEmpty()) {
                      Poistettu vaihe = viiteChildren.iterator().next();
                      PoistetutDTO.ValinnanVaihe valinnanVaihe =
                          new PoistetutDTO.ValinnanVaihe().setValinnanVaiheOid(vaihe.getTunniste());
                      valinnanVaihe.setPoistettuItself(vaihe.isDeletedItself());
                      if (!vaihe.isDeletedItself()) {
                        // Valintatapajonot
                        List<Poistettu> vaiheChildren = findChildren(valintatapaJonot, vaihe);
                        List<PoistetutDTO.PoistettuOid> jonot =
                            vaiheChildren.stream()
                                .map(
                                    jono ->
                                        new PoistetutDTO.PoistettuOid().setOid(jono.getTunniste()))
                                .toList();
                        // Valintakokeet
                        vaiheChildren = findChildren(valintaKokeet, vaihe);
                        List<PoistetutDTO.PoistettuOid> kokeet =
                            vaiheChildren.stream()
                                .map(
                                    koe ->
                                        new PoistetutDTO.PoistettuOid().setOid(koe.getTunniste()))
                                .toList();
                        valinnanVaihe.setValintatapajono(jonot.isEmpty() ? null : jonot);
                        valinnanVaihe.setValintakoe(kokeet.isEmpty() ? null : kokeet);
                      }
                      hakukohdeViite.setValinnanVaihe(valinnanVaihe);
                    }
                    // Valintaperusteet
                    viiteChildren = findChildren(valintaPerusteet, viite);
                    List<PoistetutDTO.Valintaperuste> perusteet =
                        viiteChildren.stream()
                            .map(
                                peruste ->
                                    new PoistetutDTO.Valintaperuste()
                                        .setTunniste(peruste.getTunniste()))
                            .toList();
                    hakukohdeViite.setHakukohteenValintaperuste(
                        perusteet.isEmpty() ? null : perusteet);
                  }
                  return hakukohdeViite;
                })
            .toList();
    PoistetutDTO dto = new PoistetutDTO();
    dto.setPoistetut(poistetutViitteet);
    return dto;
  }

  private List<Poistettu> findAllRequiredParents(
      ParentType parentType, List<Poistettu> poistetutParents, List<Long> requiredParentIds) {
    List<Poistettu> allRequiredParents = new ArrayList<>(poistetutParents);
    Set<Long> remainingParentIds = new HashSet<>(requiredParentIds);

    idList(allRequiredParents).forEach(remainingParentIds::remove);
    if (parentType == ParentType.VALINNAN_VAIHE) {
      allRequiredParents.addAll(poistettuDAO.findParentValinnanvaiheet(remainingParentIds));
    } else {
      allRequiredParents.addAll(poistettuDAO.findParentHakukohdeviitteet(remainingParentIds));
    }
    idList(allRequiredParents).forEach(remainingParentIds::remove);
    if (!remainingParentIds.isEmpty()) {
      if (parentType == ParentType.VALINNAN_VAIHE) {
        allRequiredParents.addAll(
            poistettuDAO.findParentValinnanvaiheetFromHistory(remainingParentIds));
      } else {
        allRequiredParents.addAll(
            poistettuDAO.findParentHakukohdeviitteetFromHistory(remainingParentIds));
      }
    }

    return allRequiredParents;
  }

  private List<Long> idList(List<Poistettu> items) {
    return items.stream().map(Poistettu::getId).toList();
  }

  private List<Poistettu> findChildren(List<Poistettu> allChildren, Poistettu parent) {
    return allChildren.stream()
        .filter(child -> Objects.equals(child.getParentId(), parent.getId()))
        .toList();
  }

  protected static enum ParentType {
    HAKUKOHDE_VIITE,
    VALINNAN_VAIHE
  }
}
