package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.List;
import java.util.Set;

public interface ValintaryhmaService {
  List<Valintaryhma> findValintaryhmasByParentOid(String oid);

  /**
   * Kuten {@link #findValintaryhmasByParentOid(String)}, mutta ilman raskaita fetch joineja.
   * Käytetään kun lapsista tarvitaan vain itse valintaryhmä, ei sen kokoelmia.
   */
  List<Valintaryhma> findValintaryhmasByParentOidPlain(String oid);

  Valintaryhma readByOid(String oid);

  /** Kuten {@link #readByOid(String)}, mutta ilman raskaita fetch joineja. */
  Valintaryhma readPlainByOid(String oid);

  Valintaryhma insert(ValintaryhmaCreateDTO valintaryhma, String parentOid);

  List<Valintaryhma> findParentHierarchyFromOid(String oid);

  Valintaryhma copyAsChild(String sourceOid, String parentOid, String name);

  Valintaryhma insert(ValintaryhmaCreateDTO valintaryhma);

  Valintaryhma update(String oid, ValintaryhmaCreateDTO valintaryhma);

  void delete(String oid);

  Set<String> findHakukohdesRecursive(Set<String> oids);

  Boolean onkoHaullaValintaryhmia(String hakuOid);
}
