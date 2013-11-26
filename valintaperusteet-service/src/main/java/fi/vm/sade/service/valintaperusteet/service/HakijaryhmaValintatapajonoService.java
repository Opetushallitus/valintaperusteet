package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;

import java.util.List;

public interface HakijaryhmaValintatapajonoService extends CRUDService<HakijaryhmaValintatapajono, Long, String> {

    void deleteByOid(String oid, boolean skipInheritedCheck);

    List<HakijaryhmaValintatapajono> findHakijaryhmaByJono(String oid);

    HakijaryhmaValintatapajono readByOid(String oid);

    List<HakijaryhmaValintatapajono> findByHakijaryhma(String hakijaryhmaOid);
}
