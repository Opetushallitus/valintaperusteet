package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HakukohdeViiteDAO extends JpaDAO<HakukohdeViite, Long> {
  List<HakukohdeViite> findRoot();

  List<HakukohdeViite> haunHakukohteet(String hakuOid, Boolean vainValintakokeelliset);

  HakukohdeViite readByOid(String oid);

  List<HakukohdeViite> readByOids(List<String> oids);

  HakukohdeViite readForImport(String oid);

  List<HakukohdeViite> findByValintaryhmaOid(String oid);

  List<HakukohdeViite> findByValintaryhmaOidForValisijoittelu(String oid);

  boolean kuuluuSijoitteluun(String oid);

  void flush();

  List<HakukohdeViite> search(String hakuOid, List<String> tila, String searchString);

  List<HakukohdeViite> readByHakukohdekoodiUri(String koodiUri);

  Optional<Valintaryhma> findValintaryhmaByHakukohdeOid(String oid);

  List<String> findNewOrChangedHakukohdeOids(
      LocalDateTime startDatetime, LocalDateTime endDatetime);
}
