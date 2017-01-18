package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;

import java.util.List;
import java.util.Set;

public interface ValinnanVaiheService {
    ValinnanVaihe readByOid(String oid);

    List<ValinnanVaihe> findByHakukohde(String oid);

    List<ValinnanVaihe> findByValintaryhma(String oid);

    ValinnanVaihe lisaaValinnanVaiheValintaryhmalle(String valintaryhmaOid, ValinnanVaiheCreateDTO valinnanVaihe, String edellinenValinnanVaiheOid);

    ValinnanVaihe lisaaValinnanVaiheHakukohteelle(String hakukohdeOid, ValinnanVaiheCreateDTO valinnanVaihe, String edellinenValinnanVaiheOid);

    void deleteByOid(String oid);

    List<ValinnanVaihe> jarjestaValinnanVaiheet(List<String> valinnanVaiheOidit);

    void kopioiValinnanVaiheetParentilta(Valintaryhma valintaryhma, Valintaryhma parentValintaryhma, JuureenKopiointiCache kopiointiCache);

    void kopioiValinnanVaiheetParentilta(HakukohdeViite hakukohde, Valintaryhma parentValintaryhma, JuureenKopiointiCache kopiointiCache);

    boolean kuuluuSijoitteluun(String oid);

    void deleteByOid(String oid, boolean skipInheritedCheck);

    void delete(ValinnanVaihe valinnanVaihe);

    ValinnanVaihe update(String oid, ValinnanVaiheCreateDTO dto);

    Set<String> getValintaryhmaOids(String oid);
}
