package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;

import java.util.List;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.59
 */
public interface HakukohdekoodiDAO extends JpaDAO<Hakukohdekoodi, Long> {
    Hakukohdekoodi findByKoodiUri(String koodiUri);

    Hakukohdekoodi findByHakukohdeOid(String hakukohdeOid);

    Hakukohdekoodi findByHakukohdeOidAndKoodiUri(String hakukohdeOid, String koodiUri);

    List<Hakukohdekoodi> findByUris(String... koodiUris);
}
