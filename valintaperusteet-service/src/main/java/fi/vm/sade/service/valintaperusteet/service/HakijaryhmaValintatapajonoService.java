package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;

import java.util.List;

public interface HakijaryhmaValintatapajonoService {

    void deleteByOid(String oid, boolean skipInheritedCheck);

    List<HakijaryhmaValintatapajono> findHakijaryhmaByJono(String oid);

    HakijaryhmaValintatapajono readByOid(String oid);

    List<HakijaryhmaValintatapajono> findByHakijaryhma(String hakijaryhmaOid);

    HakijaryhmaValintatapajono insert(HakijaryhmaValintatapajono entity);

    // CRUD
    HakijaryhmaValintatapajono update(String oid, HakijaryhmaValintatapajonoDTO dto);

    void delete(HakijaryhmaValintatapajono entity);
}
