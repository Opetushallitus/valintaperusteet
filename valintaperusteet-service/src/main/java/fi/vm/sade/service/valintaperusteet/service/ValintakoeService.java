package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;

import java.util.List;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.05
 */
public interface ValintakoeService extends CRUDService<Valintakoe, Long, String> {
    Valintakoe readByOid(String oid);

    List<Valintakoe> findValintakoeByValinnanVaihe(String oid);

    Valintakoe lisaaValintakoeValinnanVaiheelle(String valinnanVaiheOid, ValintakoeDTO koe);
}
