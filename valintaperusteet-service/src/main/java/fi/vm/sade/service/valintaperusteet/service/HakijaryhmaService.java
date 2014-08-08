package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.List;

public interface HakijaryhmaService {

    void deleteByOid(String oid, boolean skipInheritedCheck);

    List<Hakijaryhma> findByHakukohde(String oid);

    List<Hakijaryhma> findByValintaryhma(String oid);

    Hakijaryhma readByOid(String oid);

    void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid);

    Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, HakijaryhmaCreateDTO hakijaryhma);

//    Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, HakijaryhmaCreateDTO hakijaryhma);

//    List<Hakijaryhma> jarjestaHakijaryhmat(List<String> oids);
//
//    void kopioiHakijaryhmatParentilta(Valintaryhma inserted, Valintaryhma parent);
//
//    void kopioiHakijaryhmatParentilta(HakukohdeViite inserted, Valintaryhma parent);

    Hakijaryhma insert(Hakijaryhma entity);

    void delete(Hakijaryhma entity);

    Hakijaryhma update(String oid, HakijaryhmaCreateDTO entity);
}
