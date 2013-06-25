package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;

import java.util.List;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 14.19
 */
public interface OpetuskielikoodiDAO extends JpaDAO<Opetuskielikoodi, Long>, KoodiDAO<Opetuskielikoodi> {
    Opetuskielikoodi readByUri(String uri);

    List<Opetuskielikoodi> findByUris(String[] koodiUris);
}
