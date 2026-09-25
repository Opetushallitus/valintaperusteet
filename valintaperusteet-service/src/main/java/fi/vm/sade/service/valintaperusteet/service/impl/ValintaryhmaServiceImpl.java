package fi.vm.sade.service.valintaperusteet.service.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dto.OrganisaatioDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaaEiVoidaKopioida;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ValintaryhmaServiceImpl implements ValintaryhmaService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaServiceImpl.class);
  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Lazy @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private OrganisaatioDAO organisaatioDAO;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private OidService oidService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private ValintaryhmaKopiointiDAO valintaryhmaKopiointiDAO;

  public List<Valintaryhma> findValintaryhmasByParentOid(String id) {
    return valintaryhmaDAO.findChildrenByParentOid(id);
  }

  @Override
  public List<Valintaryhma> findValintaryhmasByParentOidPlain(String id) {
    return valintaryhmaDAO.findChildrenByParentOidPlain(id);
  }

  @Override
  public Valintaryhma readByOid(String oid) {
    return haeValintaryhma(oid);
  }

  @Override
  public Valintaryhma readPlainByOid(String oid) {
    Valintaryhma valintaryhma = valintaryhmaDAO.readPlainByOid(oid);
    if (valintaryhma == null) {
      throw new ValintaryhmaEiOleOlemassaException(
          "Valintaryhma (" + oid + ") ei ole olemassa.", oid);
    }
    return valintaryhma;
  }

  private Valintaryhma haeValintaryhma(String oid) {
    Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(oid);
    if (valintaryhma == null) {
      throw new ValintaryhmaEiOleOlemassaException(
          "Valintaryhma (" + oid + ") ei ole olemassa.", oid);
    }
    return valintaryhma;
  }

  @Override
  public Valintaryhma insert(ValintaryhmaCreateDTO dto, String parentOid) {
    Valintaryhma valintaryhma = modelMapper.map(dto, Valintaryhma.class);
    valintaryhma.setOid(oidService.haeValintaryhmaOid());
    Valintaryhma parent = haeValintaryhma(parentOid);
    valintaryhma.setYlavalintaryhma(parent);
    setOrganisaatiot(valintaryhma, dto);
    valintaryhma.setViimeinenKaynnistyspaiva(dto.getViimeinenKaynnistyspaiva());
    Valintaryhma inserted = valintaryhmaDAO.insert(valintaryhma);
    valinnanVaiheService.kopioiValinnanVaiheetParentilta(inserted, parent);
    hakijaryhmaService.kopioiHakijaryhmatMasterValintaryhmalta(parentOid, inserted.getOid());
    return inserted;
  }

  @Override
  public List<Valintaryhma> findParentHierarchyFromOid(String oid) {
    return valintaryhmaDAO.readHierarchy(oid);
  }

  @Override
  public Valintaryhma update(String oid, ValintaryhmaCreateDTO incoming) {
    Valintaryhma managedObject = haeValintaryhma(oid);
    managedObject.setNimi(incoming.getNimi());
    managedObject.setKohdejoukko(incoming.getKohdejoukko());
    managedObject.setHakuoid(incoming.getHakuoid());
    managedObject.setViimeinenKaynnistyspaiva(incoming.getViimeinenKaynnistyspaiva());
    if (managedObject.getHakuvuosi() == null
        || !managedObject.getHakuvuosi().equals(incoming.getHakuvuosi())) {
      managedObject.setHakuvuosi(incoming.getHakuvuosi());
      asetaHakuvuosiAlaryhmille(managedObject.getOid(), incoming.getHakuvuosi());
    }
    setOrganisaatiot(managedObject, incoming);
    return managedObject;
  }

  private void asetaHakuvuosiAlaryhmille(String oid, String hakuvuosi) {
    final List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOidPlain(oid);
    children.forEach(
        v -> {
          v.setHakuvuosi(hakuvuosi);
          asetaHakuvuosiAlaryhmille(v.getOid(), hakuvuosi);
        });
  }

  private Set<Organisaatio> getOrganisaatios(Set<OrganisaatioDTO> incoming) {
    Set<Organisaatio> organisaatiot = new HashSet<Organisaatio>();
    for (OrganisaatioDTO organisaatio : incoming) {
      Organisaatio temp = organisaatioDAO.readByOid(organisaatio.getOid());
      // TODO: OidPath pitäis varmaan päivittää joskus vanoille kanssa.
      if (temp == null) {
        temp = organisaatioDAO.insert(modelMapper.map(organisaatio, Organisaatio.class));
      }
      organisaatiot.add(temp);
    }
    return organisaatiot;
  }

  private Valintaryhma setOrganisaatiot(Valintaryhma entity, ValintaryhmaCreateDTO dto) {
    Set<Organisaatio> organisaatiot = getOrganisaatios(dto.getOrganisaatiot());
    entity.setOrganisaatiot(organisaatiot);
    Organisaatio vastuuorganisaatio = null;
    if (!StringUtils.isEmpty(dto.getVastuuorganisaatioOid())) {
      for (Organisaatio organisaatio : organisaatiot) {
        if (dto.getVastuuorganisaatioOid().equals(organisaatio.getOid())) {
          vastuuorganisaatio = organisaatio;
        }
      }
    }
    entity.setVastuuorganisaatio(vastuuorganisaatio);
    return entity;
  }

  private boolean isChildOf(String childOid, String parentOid) {
    return valintaryhmaDAO.readHierarchy(childOid).stream()
        .anyMatch(vr -> vr.getOid().equals(parentOid));
  }

  public Valintaryhma copyAsChild(String sourceOid, String parentOid, String name) {
    if (parentOid != null) {
      // Tarkistetaan, että parent ei ole sourcen jälkeläinen
      if (isChildOf(parentOid, sourceOid)) {
        throw new ValintaryhmaaEiVoidaKopioida(
            "Valintaryhmä (" + parentOid + ") on kohderyhmän (" + sourceOid + ") lapsi",
            sourceOid,
            parentOid);
      }
      // Tarkistetaan sisarusten nimet
      List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOidPlain(parentOid);
      if (children.stream().anyMatch(vr -> vr.getNimi().equals(name))) {
        throw new ValintaryhmaaEiVoidaKopioida(
            "Valintaryhmällä (" + parentOid + ") on jo \"" + name + "\" niminen lapsi",
            sourceOid,
            parentOid);
      }
    }
    haeValintaryhma(sourceOid);
    String kopionOid =
        parentOid == null
            ? valintaryhmaKopiointiDAO.kopioiJuureen(sourceOid, name)
            : valintaryhmaKopiointiDAO.kopioiVanhemmanAlle(sourceOid, parentOid, name);
    return haeValintaryhma(kopionOid);
  }

  @Override
  public Valintaryhma insert(ValintaryhmaCreateDTO dto) {
    Valintaryhma entity = modelMapper.map(dto, Valintaryhma.class);
    entity.setOid(oidService.haeValintaryhmaOid());
    setOrganisaatiot(entity, dto);
    return valintaryhmaDAO.insert(entity);
  }

  @Override
  public void delete(String oid) {
    LOGGER.info(String.format("Aloitetaan valintaryhmän %s poistaminen", oid));
    Optional<Valintaryhma> managedObject = Optional.ofNullable(haeValintaryhma(oid));
    if (managedObject.isPresent()) {
      LOGGER.info(
          String.format(
              "Poistetaan valintaryhmän %s %s laskentakaavat",
              managedObject.get().getOid(), managedObject.get().getNimi()));
      managedObject.get().getLaskentakaava().forEach(lk -> laskentakaavaService.poista(lk.getId()));
      LOGGER.info(
          String.format(
              "Poistetaan valintaryhmä %s %s",
              managedObject.get().getOid(), managedObject.get().getNimi()));
      valintaryhmaDAO.remove(managedObject.get());
    }
  }

  @Override
  public Set<String> findHakukohdesRecursive(Set<String> oids) {
    Set<String> hakukohdeOids = Sets.newHashSet();
    for (String oid : oids) {
      addHakukohdeOids(oid, hakukohdeOids);
      getChildrenRecursive(oid, hakukohdeOids);
    }
    return hakukohdeOids;
  }

  @Override
  public Boolean onkoHaullaValintaryhmia(String hakuOid) {
    return !valintaryhmaDAO.haunJaHaunHakukohteenValintaryhmat(hakuOid).isEmpty();
  }

  private void getChildrenRecursive(String oid, Set<String> hakukohdeOids) {
    for (Valintaryhma child : findValintaryhmasByParentOid(oid)) {
      if (child.getLapsihakukohde()) {
        addHakukohdeOids(child.getOid(), hakukohdeOids);
      }
      if (child.getLapsivalintaryhma()) {
        getChildrenRecursive(child.getOid(), hakukohdeOids);
      }
    }
  }

  private void addHakukohdeOids(String oid, Set<String> hakukohdeOids) {
    List<HakukohdeViite> childHakukohdes = hakukohdeService.findByValintaryhmaOid(oid);
    hakukohdeOids.addAll(
        childHakukohdes.stream().map(HakukohdeViite::getOid).collect(Collectors.toList()));
  }
}
