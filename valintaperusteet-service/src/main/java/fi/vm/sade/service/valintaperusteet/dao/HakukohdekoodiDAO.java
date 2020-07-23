package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import java.util.List;

public interface HakukohdekoodiDAO extends JpaDAO<Hakukohdekoodi, Long>, KoodiDAO<Hakukohdekoodi> {
  Hakukohdekoodi findByHakukohdeOid(String hakukohdeOid);

  Hakukohdekoodi findByHakukohdeOidAndKoodiUri(String hakukohdeOid, String koodiUri);

  Hakukohdekoodi readByUri(String koodiUri);

  List<Hakukohdekoodi> findByUris(String... koodiUris);

  Hakukohdekoodi insertOrUpdate(Hakukohdekoodi koodi);
}
