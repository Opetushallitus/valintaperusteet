package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HakijaryhmaService {
    void deleteByOid(String oid, boolean skipInheritedCheck);

    List<Hakijaryhma> findByHakukohde(String oid);

    List<Hakijaryhma> findByHakukohteet(Collection<String> hakukohdeOids);

    List<Hakijaryhma> findByHaku(String hakuOid);

    List<Hakijaryhma> findByValintaryhma(String oid);

    Hakijaryhma readByOid(String oid);

    void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid);

    Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, HakijaryhmaCreateDTO hakijaryhma);

    Hakijaryhma insert(Hakijaryhma entity);

    void delete(Hakijaryhma entity);

    Hakijaryhma update(String oid, HakijaryhmaCreateDTO entity);

    Optional<Hakijaryhma> siirra(HakijaryhmaSiirraDTO dto);

    void kopioiHakijaryhmatMasterValintaryhmalta(String parentValintaryhmaOid, String childValintaryhmaoid);
}
