package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintakoe;

import java.util.List;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.14
 */
public interface ValintakoeDAO extends JpaDAO<Valintakoe, Long> {

    List<Valintakoe> findByValinnanVaihe(String valinnanVaiheOid);

    Valintakoe readByOid(String oid);


}
