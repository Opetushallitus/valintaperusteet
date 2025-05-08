package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.List;
import java.util.Set;

public interface ValinnanVaiheDAO extends JpaDAO<ValinnanVaihe, Long> {
  ValinnanVaihe readByOid(String oid);

  List<ValinnanVaihe> findByHakukohde(String oid);

  ValinnanVaihe haeValintaryhmanViimeinenValinnanVaihe(String oid);

  List<ValinnanVaihe> findByValintaryhma(String oid);

  List<ValinnanVaihe> readByOids(Set<String> oids);

  ValinnanVaihe haeHakukohteenViimeinenValinnanVaihe(String hakukohdeOid);

  List<ValinnanVaihe> haeKopiot(String oid);

  boolean kuuluuSijoitteluun(String oid);

  List<ValinnanVaihe> valinnanVaiheetJaJonot(String hakukohdeOid);

  List<ValinnanVaihe> jarjestaUudelleen(HakukohdeViite hakukohdeViite, List<String> uusiJarjestys);

  List<ValinnanVaihe> jarjestaUudelleen(Valintaryhma valintaryhma, List<String> uusiJarjestys);

  List<ValinnanVaihe> jarjestaUudelleenMasterJarjestyksenMukaan(
      HakukohdeViite hakukohdeViite, List<ValinnanVaihe> uusiMasterJarjestys);

  List<ValinnanVaihe> jarjestaUudelleenMasterJarjestyksenMukaan(
      Valintaryhma valintaryhma, List<ValinnanVaihe> uusiMasterJarjestys);

  ValinnanVaihe insert(ValinnanVaihe uusi);

  void delete(ValinnanVaihe valinnanVaihe);
}
