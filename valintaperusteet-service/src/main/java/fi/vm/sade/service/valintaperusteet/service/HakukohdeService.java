package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 10.1.2013
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */
public interface HakukohdeService  extends CRUDService<HakukohdeViite, Long, String> {

    List<HakukohdeViite> findAll();

    HakukohdeViite readByOid(String oid);

    List<HakukohdeViite> findRoot();

    List<HakukohdeViite> findByValintaryhmaOid(String oid);

    HakukohdeViite insert(HakukohdeViiteDTO hakukohdeViite);

    HakukohdeViite update(String oid, HakukohdeViiteDTO incoming) throws Exception;

    HakukohdeViite insert(HakukohdeViite hakukohde, String valintaryhmaOid);

    boolean kuuluuSijoitteluun(String oid);
}
