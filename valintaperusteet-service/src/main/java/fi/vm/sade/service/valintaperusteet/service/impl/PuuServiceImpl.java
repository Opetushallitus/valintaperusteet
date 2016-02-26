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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@Transactional .. do not change this, otherwise you risk doing lazy loading here
public class PuuServiceImpl implements PuuService {
    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Override
    public List<ValintaperustePuuDTO> search(String hakuOid, List<String> tila, String searchString, boolean hakukohteet, String kohdejoukko, String valintaryhmaOid) {
        //fetch whole tree in a single query, is at least now faster than individually querying
        List<Valintaryhma> valintaryhmaList;
        if (valintaryhmaOid != null && !valintaryhmaOid.isEmpty()) {
            valintaryhmaList = Arrays.asList(valintaryhmaDAO.findAllFetchAlavalintaryhmat(valintaryhmaOid));
        } else {
            valintaryhmaList = valintaryhmaDAO.findAllFetchAlavalintaryhmat();
        }
        List<ValintaperustePuuDTO> parentList = new ArrayList<ValintaperustePuuDTO>();
        Map<Long, ValintaperustePuuDTO> dtoMap = new HashMap<Long, ValintaperustePuuDTO>();
        List<Valintaryhma> parents = new ArrayList<Valintaryhma>();
        for (Valintaryhma valintaryhma : valintaryhmaList) {
            if (kohdejoukko.isEmpty()) {
                if (valintaryhma.getYlavalintaryhma() == null) {
                    parents.add(valintaryhma);
                }
            } else {
                if (valintaryhma.getYlavalintaryhma() == null
                        && (valintaryhma.getKohdejoukko() == null || valintaryhma.getKohdejoukko().isEmpty() || valintaryhma.getKohdejoukko().equals(kohdejoukko))) {
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

    private void attach(HakukohdeViite viite, Map<Long, ValintaperustePuuDTO> map, List<ValintaperustePuuDTO> list) {
        ValintaperustePuuDTO dto = convert(viite);
        if (viite.getValintaryhma() == null) {
            list.add(dto);
        } else {
            Optional<ValintaperustePuuDTO> puu = Optional.ofNullable(map.get(viite.getValintaryhma().getId()));
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

    private ValintaperustePuuDTO convert(Valintaryhma valintaryhma, Map<Long, ValintaperustePuuDTO> dtoMap) {
        ValintaperustePuuDTO valintaperustePuuDTO = new ValintaperustePuuDTO();
        dtoMap.put(valintaryhma.getId(), valintaperustePuuDTO);
        valintaperustePuuDTO.setNimi(valintaryhma.getNimi());
        valintaperustePuuDTO.setHakuvuosi(valintaryhma.getHakuvuosi());
        valintaperustePuuDTO.setTyyppi(ValintaperustePuuTyyppi.VALINTARYHMA);
        valintaperustePuuDTO.setOid(valintaryhma.getOid());
        valintaperustePuuDTO.setKohdejoukko(valintaryhma.getKohdejoukko());
        valintaperustePuuDTO.setViimeinenKaynnistyspaiva(valintaryhma.getViimeinenKaynnistyspaiva());
        for (Organisaatio organisaatio : valintaryhma.getOrganisaatiot()) {
            valintaperustePuuDTO.getOrganisaatiot().add(convert(organisaatio));
        }
        if(valintaryhma.getVastuuorganisaatio() != null) {
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
