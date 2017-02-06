package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;

import java.util.List;
import java.util.Optional;

public interface HakijaryhmaService {
    void deleteByOid(String oid, boolean skipInheritedCheck);

    List<Hakijaryhma> findByHakukohde(String oid);

    List<Hakijaryhma> findByValintaryhma(String oid);

    Hakijaryhma readByOid(String oid);

    void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid);

    Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, HakijaryhmaCreateDTO hakijaryhma, String edellinenHakijaryhmaOid);

    Hakijaryhma update(String oid, HakijaryhmaCreateDTO entity);

    Optional<Hakijaryhma> siirra(HakijaryhmaSiirraDTO dto);

    void kopioiHakijaryhmatMasterValintaryhmalta(String parentValintaryhmaOid, String childValintaryhmaoid, JuureenKopiointiCache kopiointiCache);

    List<Hakijaryhma> jarjestaHakijaryhmat(List<String> oids);
}
