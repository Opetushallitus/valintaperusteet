package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 14.1.2013
 * Time: 9:15
 * To change this template use File | Settings | File Templates.
 */
public interface PuuService {

    List<ValintaperustePuuDTO> search(String hakuOid, List<String> tila, String searchString);
}
