package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.List;

/**
 * User: kkammone
 * Date: 14.1.2013
 * Time: 9:15
 */
public interface ValintaryhmaService {
    List<Valintaryhma> findValintaryhmasByParentOid(String oid);

    Valintaryhma readByOid(String oid);

    Valintaryhma insert(ValintaryhmaCreateDTO valintaryhma, String parentOid);

    List<Valintaryhma> findParentHierarchyFromOid(String oid);

    Valintaryhma copyAsChild(String sourceOid, String parentOid, String name);

    Valintaryhma insert(ValintaryhmaCreateDTO valintaryhma);

    Valintaryhma update(String oid, ValintaryhmaCreateDTO valintaryhma);

    void delete(String oid);
}
