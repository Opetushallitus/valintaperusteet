package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteCreateDTO;
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
public interface HakukohdeService {

    List<HakukohdeViite> findAll();

    HakukohdeViite readByOid(String oid);

    List<HakukohdeViite> findRoot();

    List<HakukohdeViite> findByValintaryhmaOid(String oid);

    HakukohdeViite update(String oid, HakukohdeViiteCreateDTO incoming) throws Exception;

    boolean kuuluuSijoitteluun(String oid);

    void deleteByOid(String oid);

    HakukohdeViite siirraHakukohdeValintaryhmaan(String hakukohdeOid, String valintaryhmaOid,
                                                 boolean siirretaanManuaalisesti);

    HakukohdeViite insert(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid);
}
