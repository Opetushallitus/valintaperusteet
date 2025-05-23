package fi.vm.sade.service.valintaperusteet.service.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.OrganisaatioDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.OrganisaatioDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaaEiVoidaKopioida;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
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

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private OidService oidService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private HakukohdekoodiDAO hakukohdekoodiDAO;

  @Autowired private ValintakoekoodiDAO valintakoekoodiDAO;

  public List<Valintaryhma> findValintaryhmasByParentOid(String id) {
    return valintaryhmaDAO.findChildrenByParentOid(id);
  }

  @Override
  public Valintaryhma readByOid(String oid) {
    return haeValintaryhma(oid);
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
    valinnanVaiheService.kopioiValinnanVaiheetParentilta(inserted, parent, null);
    hakijaryhmaService.kopioiHakijaryhmatMasterValintaryhmalta(parentOid, inserted.getOid(), null);
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

  private Valintaryhma copyAsChild(
      Valintaryhma source, Valintaryhma parent, String name, JuureenKopiointiCache kopiointiCache) {
    if (parent == null && kopiointiCache == null) {
      throw new IllegalArgumentException(
          "Parent is required if want to copy valinnanvaiheet from it");
    }
    LOGGER.info(
        "Kopioidaan valintaryhmä {} nimellä '{}' valintaryhmän {} alle", source, name, parent);
    Valintaryhma copy = new Valintaryhma();
    copy.setYlavalintaryhma(parent);
    copy.setNimi(name);
    copy.setOid(oidService.haeValintaryhmaOid());
    copy.getOrganisaatiot().addAll(source.getOrganisaatiot());
    copy.setVastuuorganisaatio(source.getVastuuorganisaatio());
    copy.setKohdejoukko(source.getKohdejoukko());
    Valintaryhma inserted = valintaryhmaDAO.insert(copy);
    copyLaskentakaavat(source, inserted, kopiointiCache);
    if (kopiointiCache == null) {
      hakijaryhmaService.kopioiHakijaryhmatMasterValintaryhmalta(
          parent.getOid(), inserted.getOid(), null);
      valinnanVaiheService.kopioiValinnanVaiheetParentilta(inserted, parent, null);
    } else {
      hakijaryhmaService.kopioiHakijaryhmatMasterValintaryhmalta(
          source.getOid(), inserted.getOid(), kopiointiCache);
      valinnanVaiheService.kopioiValinnanVaiheetParentilta(inserted, source, kopiointiCache);
    }
    copyHakukohdekoodit(source, inserted);
    copyValintakoekoodit(source, inserted);
    List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOid(source.getOid());
    children.forEach(
        (child) -> {
          Valintaryhma copiedChild = copyAsChild(child, inserted, child.getNimi(), kopiointiCache);
          inserted.getAlavalintaryhmat().add(copiedChild);
        });
    LOGGER.info(
        "Kopioitiin valintaryhmä {} nimellä '{}' valintaryhmän {} alle: {}",
        source,
        name,
        parent,
        inserted);
    return inserted;
  }

  private void copyLaskentakaavat(
      Valintaryhma source, Valintaryhma target, JuureenKopiointiCache kopiointiCache) {
    source
        .getLaskentakaava()
        .forEach(
            sourceKaava -> {
              Laskentakaava copied =
                  laskentakaavaService.kopioiJosEiJoKopioitu(
                      sourceKaava, sourceKaava.getHakukohde(), target);
              if (kopiointiCache != null) {
                kopiointiCache.kopioidutLaskentakaavat.put(sourceKaava.getId(), copied);
              }
            });
  }

  private void copyHakukohdekoodit(Valintaryhma source, Valintaryhma target) {
    source.getHakukohdekoodit().stream()
        .forEach(
            sourceKoodi -> {
              target.getHakukohdekoodit().add(sourceKoodi);
            });
  }

  private void copyValintakoekoodit(Valintaryhma source, Valintaryhma target) {
    Set<Valintakoekoodi> sourceKoodit = valintakoekoodiDAO.findByValintaryhma(source.getOid());
    if (sourceKoodit != null) {
      for (Valintakoekoodi sourceKoodi : sourceKoodit) {
        target.getValintakoekoodit().add(sourceKoodi);
      }
    }
  }

  public Valintaryhma copyAsChild(String sourceOid, String parentOid, String name) {
    Valintaryhma parent = null;
    if (parentOid != null) {
      // Tarkistetaan, että parent ei ole sourcen jälkeläinen
      if (isChildOf(parentOid, sourceOid)) {
        throw new ValintaryhmaaEiVoidaKopioida(
            "Valintaryhmä (" + parentOid + ") on kohderyhmän (" + sourceOid + ") lapsi",
            sourceOid,
            parentOid);
      }
      // Tarkistetaan sisarusten nimet
      List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOid(parentOid);
      if (children.stream().anyMatch(vr -> vr.getNimi().equals(name))) {
        throw new ValintaryhmaaEiVoidaKopioida(
            "Valintaryhmällä (" + parentOid + ") on jo \"" + name + "\" niminen lapsi",
            sourceOid,
            parentOid);
      }
      parent = valintaryhmaDAO.readByOid(parentOid);
    }
    Valintaryhma source = valintaryhmaDAO.readByOid(sourceOid);
    return copyAsChild(
        source, parent, name, parentOid == null ? new JuureenKopiointiCache() : null);
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
    return !valintaryhmaDAO.readByHakuoid(hakuOid).isEmpty();
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
