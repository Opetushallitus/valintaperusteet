package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.List;

/**
 * User: tommiha Date: 1/22/13 Time: 2:27 PM
 */
public interface ValinnanVaiheService {
    ValinnanVaihe readByOid(String oid);

    List<ValinnanVaihe> findByHakukohde(String oid);

    List<ValinnanVaihe> findByValintaryhma(String oid);

    ValinnanVaihe lisaaValinnanVaiheValintaryhmalle(String valintaryhmaOid, ValinnanVaihe valinnanVaihe,
                                                    String edellinenValinnanVaiheOid);

    ValinnanVaihe lisaaValinnanVaiheHakukohteelle(String hakukohdeOid, ValinnanVaiheCreateDTO valinnanVaihe,
                                                  String edellinenValinnanVaiheOid);

    void deleteByOid(String oid);

    List<ValinnanVaihe> jarjestaValinnanVaiheet(List<String> valinnanVaiheOidit);

    void kopioiValinnanVaiheetParentilta(Valintaryhma valintaryhma, Valintaryhma parentValintaryhma);

    void kopioiValinnanVaiheetParentilta(HakukohdeViite hakukohde, Valintaryhma parentValintaryhma);

    boolean kuuluuSijoitteluun(String oid);

    void deleteByOid(String oid, boolean skipInheritedCheck);

    void delete(ValinnanVaihe valinnanVaihe);

    ValinnanVaihe update(String oid, ValinnanVaiheCreateDTO dto);
}
