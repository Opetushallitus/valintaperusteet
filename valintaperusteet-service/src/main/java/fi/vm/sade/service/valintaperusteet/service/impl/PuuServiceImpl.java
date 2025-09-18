package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.OrganisaatioDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuTyyppi;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.PuuService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PuuServiceImpl implements PuuService {
  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Override
  public List<ValintaperustePuuDTO> search(
      String hakuOid,
      List<String> tila,
      String searchString,
      boolean hakukohteet,
      String kohdejoukko,
      String valintaryhmaOid) {
    // fetch whole tree in a single query, is at least now faster than individually querying
    List<Valintaryhma> valintaryhmaList;
    if (valintaryhmaOid != null && !valintaryhmaOid.isEmpty()) {
      valintaryhmaList =
          Collections.singletonList(valintaryhmaDAO.findAllFetchAlavalintaryhmat(valintaryhmaOid));
    } else {
      valintaryhmaList = valintaryhmaDAO.findAllFetchAlavalintaryhmat();
    }
    List<ValintaperustePuuDTO> parentList = new ArrayList<>();
    Map<Long, ValintaperustePuuDTO> dtoMap = new HashMap<>();
    List<Valintaryhma> parents = new ArrayList<>();
    for (Valintaryhma valintaryhma : valintaryhmaList) {
      if (kohdejoukko.isEmpty()) {
        if (valintaryhma.getYlavalintaryhma() == null) {
          parents.add(valintaryhma);
        }
      } else {
        if (valintaryhma.getYlavalintaryhma() == null
            && (valintaryhma.getKohdejoukko() == null
                || valintaryhma.getKohdejoukko().isEmpty()
                || valintaryhma.getKohdejoukko().equals(kohdejoukko))) {
          parents.add(valintaryhma);
        }
      }
    }
    for (Valintaryhma valintaryhma : parents) {
      ValintaperustePuuDTO dto = convert(valintaryhma, dtoMap);
      parentList.add(dto);
    }
    if (hakukohteet) {
      List<HakukohdeViite> hakukohdeList = hakukohdeViiteDAO.search(hakuOid, tila, searchString);
      for (HakukohdeViite hakukohdeViite : hakukohdeList) {
        attach(hakukohdeViite, dtoMap, parentList);
      }
    }
    return parentList;
  }

  @Transactional
  public List<ValintaperustePuuDTO> searchByHaku(String hakuOid) {
    List<Valintaryhma> valintaryhmaList =
        valintaryhmaDAO.findAllByHakuOidFetchAlavalintaryhmat(hakuOid);
    List<HakukohdeViite> hakukohdeList = hakukohdeViiteDAO.search(hakuOid, null, "");
    List<ValintaperustePuuDTO> parentList = new ArrayList<>();
    Map<Long, ValintaperustePuuDTO> dtoMap = new HashMap<>();
    List<Valintaryhma> parents = new ArrayList<>();
    for (Valintaryhma valintaryhma : valintaryhmaList) {
      if (valintaryhma.getYlavalintaryhma() == null) {
        parents.add(valintaryhma);
      } else {
        if (valintaryhma.getYlavalintaryhma() == null
            && (valintaryhma.getKohdejoukko() == null || valintaryhma.getKohdejoukko().isEmpty())) {
          parents.add(valintaryhma);
        }
      }
    }
    for (Valintaryhma valintaryhma : parents) {
      ValintaperustePuuDTO dto = convert(valintaryhma, dtoMap);
      parentList.add(dto);
    }
    for (HakukohdeViite hakukohdeViite : hakukohdeList) {
      attach(hakukohdeViite, dtoMap, parentList);
    }
    return parentList;
  }

  private boolean containsHakukohde(Valintaryhma ryhma, Map<String, HakukohdeViite> hakukohteet) {
    boolean intersects =
        ryhma.getHakukohdeViitteet().stream().anyMatch(hv -> hakukohteet.get(hv.getOid()) != null);
    return intersects
        || ryhma.getAlavalintaryhmat().stream().anyMatch(ar -> containsHakukohde(ar, hakukohteet));
  }

  private void attach(
      HakukohdeViite viite, Map<Long, ValintaperustePuuDTO> map, List<ValintaperustePuuDTO> list) {
    ValintaperustePuuDTO dto = convert(viite);
    if (viite.getValintaryhma() == null) {
      list.add(dto);
    } else {
      Optional<ValintaperustePuuDTO> puu =
          Optional.ofNullable(map.get(viite.getValintaryhma().getId()));
      puu.map(a -> a.getHakukohdeViitteet().add(dto));
    }
  }

  private ValintaperustePuuDTO convert(HakukohdeViite viite) {
    ValintaperustePuuDTO dto = new ValintaperustePuuDTO();
    dto.setTyyppi(ValintaperustePuuTyyppi.HAKUKOHDE);
    dto.setHakuOid(viite.getHakuoid());
    dto.setOid(viite.getOid());
    dto.setNimi(viite.getNimi());
    dto.setTarjoajaOid(viite.getTarjoajaOid());
    dto.setTila(viite.getTila());
    return dto;
  }

  private ValintaperustePuuDTO convert(
      Valintaryhma valintaryhma, Map<Long, ValintaperustePuuDTO> dtoMap) {
    ValintaperustePuuDTO valintaperustePuuDTO = new ValintaperustePuuDTO();
    dtoMap.put(valintaryhma.getId(), valintaperustePuuDTO);
    valintaperustePuuDTO.setNimi(valintaryhma.getNimi());
    valintaperustePuuDTO.setHakuvuosi(valintaryhma.getHakuvuosi());
    valintaperustePuuDTO.setTyyppi(ValintaperustePuuTyyppi.VALINTARYHMA);
    valintaperustePuuDTO.setOid(valintaryhma.getOid());
    valintaperustePuuDTO.setKohdejoukko(valintaryhma.getKohdejoukko());
    valintaperustePuuDTO.setViimeinenKaynnistyspaiva(valintaryhma.getViimeinenKaynnistyspaiva());
    valintaperustePuuDTO.setHakuOid(valintaryhma.getHakuoid());
    for (Organisaatio organisaatio : valintaryhma.getOrganisaatiot()) {
      valintaperustePuuDTO.getOrganisaatiot().add(convert(organisaatio));
    }
    if (valintaryhma.getVastuuorganisaatio() != null) {
      valintaperustePuuDTO.setVastuuorganisaatio(convert(valintaryhma.getVastuuorganisaatio()));
    }
    for (Valintaryhma valintaryhma1 : valintaryhma.getAlavalintaryhmat()) {
      valintaperustePuuDTO.getAlavalintaryhmat().add(convert(valintaryhma1, dtoMap));
    }
    return valintaperustePuuDTO;
  }

  private OrganisaatioDTO convert(Organisaatio organisaatio) {
    OrganisaatioDTO orgDTO = new OrganisaatioDTO();
    orgDTO.setOid(organisaatio.getOid());
    orgDTO.setParentOidPath(organisaatio.getParentOidPath());
    return orgDTO;
  }
}
