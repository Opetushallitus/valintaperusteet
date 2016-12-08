package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;

import java.util.Collection;
import java.util.List;

public interface HakijaryhmaValintatapajonoService {
    void deleteByOid(String oid, boolean skipInheritedCheck);

    List<HakijaryhmaValintatapajono> findHakijaryhmaByJono(String oid);

    List<HakijaryhmaValintatapajono> findHakijaryhmaByJonos(List<String> oid);

    HakijaryhmaValintatapajono readByOid(String oid);

    List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> hakukohdeOids);

    HakijaryhmaValintatapajono update(String oid, HakijaryhmaValintatapajonoDTO dto);

    void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid);

    void liitaHakijaryhmaHakukohteelle(String hakukohdeOid, String hakijaryhmaOid);

    Hakijaryhma lisaaHakijaryhmaValintatapajonolle(String valintatapajonoOid, HakijaryhmaCreateDTO dto);

    Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, HakijaryhmaCreateDTO hakijaryhma);

    List<HakijaryhmaValintatapajono> findByHakukohde(String oid);
}
