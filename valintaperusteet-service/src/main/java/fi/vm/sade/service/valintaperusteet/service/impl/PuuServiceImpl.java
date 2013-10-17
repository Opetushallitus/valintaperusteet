package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuTyyppi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.PuuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class PuuServiceImpl implements PuuService {

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;


    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;


    @Override
    public List<ValintaperustePuuDTO> search(String hakuOid, List<String> tila, String oid) {
        //fetch whole tree in a single query, is at least now faster than individually querying

        List<Valintaryhma>  list = valintaryhmaDAO.findAllByHakuoid(oid);

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
            return null;
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
