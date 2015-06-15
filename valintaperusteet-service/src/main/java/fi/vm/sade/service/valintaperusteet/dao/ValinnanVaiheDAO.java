package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.List;
import java.util.Set;

public interface ValinnanVaiheDAO extends JpaDAO<ValinnanVaihe, Long> {
    ValinnanVaihe readByOid(String oid);

    List<ValinnanVaihe> findByHakukohde(String oid);

    ValinnanVaihe haeValintaryhmanViimeinenValinnanVaihe(String oid);

    List<ValinnanVaihe> findByValintaryhma(String oid);

    List<ValinnanVaihe> readByOids(Set<String> oids);

    ValinnanVaihe haeHakukohteenViimeinenValinnanVaihe(String hakukohdeOid);

    Set<String> findValinnanVaiheOidsByValintaryhma(String valintaryhmaOid);

    Set<String> findValinnanVaiheOidsByHakukohde(String hakukohdeOid);

    List<ValinnanVaihe> haeKopiot(String oid);

    boolean kuuluuSijoitteluun(String oid);

    List<ValinnanVaihe> ilmanLaskentaaOlevatHakukohteelle(String hakukohdeOid);

    List<ValinnanVaihe> valinnanVaiheetJaJonot(String hakukohdeOid);
}
