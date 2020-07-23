package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.Collection;
import java.util.List;

public interface ValintaryhmaDAO extends JpaDAO<Valintaryhma, Long> {
  List<Valintaryhma> findChildrenByParentOid(String oid);

  List<Valintaryhma> findChildrenByParentOidPlain(String oid);

  Valintaryhma readByOid(String oid);

  /** Hakee valintaryhmähierarkian annetusta lapsesta ylöspäin. Lapsi tulee mukana. */
  List<Valintaryhma> readHierarchy(String childOid);

  List<Valintaryhma> findAllFetchAlavalintaryhmat();

  Valintaryhma findAllFetchAlavalintaryhmat(String oid);

  List<Valintaryhma> haeHakukohdekoodinJaValintakoekoodienMukaan(
      String hakukohdekoodiUri, Collection<String> valintakoekoodiUrit);

  List<Valintaryhma> readByHakukohdekoodiUri(String koodiUri);

  List<Valintaryhma> readByHakuoid(String hakuoid);
}
