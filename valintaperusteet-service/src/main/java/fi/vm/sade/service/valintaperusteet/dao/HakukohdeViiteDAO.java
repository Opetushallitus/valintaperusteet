package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.List;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 15.1.2013
 * Time: 17.20
 * To change this template use File | Settings | File Templates.
 */
public interface HakukohdeViiteDAO extends JpaDAO<HakukohdeViite, Long> {

    List<HakukohdeViite> findRoot();

    List<HakukohdeViite> haunHakukohteet(String hakuOid);

    HakukohdeViite readByOid(String oid);

    List<HakukohdeViite> readByOids(List<String> oids);

    HakukohdeViite readForImport(String oid);

    List<HakukohdeViite> findByValintaryhmaOid(String oid);

    List<HakukohdeViite> findByValintaryhmaOidForValisijoittelu(String oid);

    boolean kuuluuSijoitteluun(String oid);

    void flush();

    List<HakukohdeViite> search(String hakuOid, List<String> tila, String searchString);

    List<HakukohdeViite> readByHakukohdekoodiUri(String koodiUri);

    Optional<Valintaryhma> findValintaryhmaByHakukohdeOid(String oid);
}
