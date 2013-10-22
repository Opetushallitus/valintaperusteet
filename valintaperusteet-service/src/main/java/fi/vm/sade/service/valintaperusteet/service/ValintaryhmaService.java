package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 14.1.2013
 * Time: 9:15
 * To change this template use File | Settings | File Templates.
 */
public interface ValintaryhmaService extends CRUDService<Valintaryhma, Long, String> {
    List<Valintaryhma> findValintaryhmasByParentOid(String oid);

    Valintaryhma readByOid(String oid);

    Valintaryhma insert(Valintaryhma valintaryhma, String parentOid);

    List<Valintaryhma> findParentHierarchyFromOid(String oid);


}
