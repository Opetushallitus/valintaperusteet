package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuTyyppi;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.PuuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 16.1.2013
 * Time: 10.37
 * To change this template use File | Settings | File Templates.
 */
@Service
//@Transactional .. do not change this, otherwise you risk lazy init stuff
public class PuuServiceImpl implements PuuService {

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;


    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;


    @Override
    public List<ValintaperustePuuDTO> search(String hakuOid, List<String> tila, String searchString) {
        //fetch whole tree in a single query, is at least now faster than individually querying

        List<Valintaryhma>  list = valintaryhmaDAO.findAllByHakuoid(hakuOid);
        List<HakukohdeViite> hakukohdeList = hakukohdeViiteDAO.search(hakuOid,tila,searchString);

        List<ValintaperustePuuDTO> dtoList = new ArrayList<ValintaperustePuuDTO>();


        //parse parents
        List<Valintaryhma>  parents = new ArrayList<Valintaryhma>();
        for(Valintaryhma valintaryhma : parents) {
            if(valintaryhma.getYlavalintaryhma() == null) {
                parents.add(valintaryhma);
            }
        }
        for(Valintaryhma valintaryhma : list) {
            dtoList.add(convert(valintaryhma));
        }
        for(HakukohdeViite hakukohdeViite : hakukohdeList) {
            dtoList.add(convert(hakukohdeViite));
        }



        return dtoList;
    }

    private ValintaperustePuuDTO convert(HakukohdeViite viite) {
        ValintaperustePuuDTO dto = new ValintaperustePuuDTO();
        dto.setTyyppi(ValintaperustePuuTyyppi.HAKUKOHDE);
        dto.setHakuOid(viite.getHakuoid());
        dto.setOid(viite.getOid());
        dto.setTarjoajaOid(viite.getTarjoajaOid());
        dto.setTila(viite.getTila());

        return dto;
    }

    private ValintaperustePuuDTO convert(Valintaryhma valintaryhma) {
        ValintaperustePuuDTO valintaperustePuuDTO = new ValintaperustePuuDTO();
        valintaperustePuuDTO.setNimi(valintaryhma.getNimi());
        valintaperustePuuDTO.setTyyppi(ValintaperustePuuTyyppi.VALINTARYHMA);
        valintaperustePuuDTO.setOid(valintaryhma.getOid());

        for(Valintaryhma valintaryhma1: valintaryhma.getAlavalintaryhmat()){
            valintaperustePuuDTO.getAlavalintaryhmat().add(convert(valintaryhma1));
        }


        return valintaperustePuuDTO;
    }


}
