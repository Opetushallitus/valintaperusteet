package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.ValintaryhmaId;

import java.util.List;
import java.util.Set;

public interface ValintaryhmaService {
  List<Valintaryhma> findValintaryhmasByParentOid(String oid);

  Valintaryhma read(ValintaryhmaId valintaryhmaId);

  Valintaryhma readByOid(String oid);

  Valintaryhma insert(ValintaryhmaCreateDTO valintaryhma, String parentOid);

  List<Valintaryhma> findParentHierarchyFromOid(String oid);

  Valintaryhma copyAsChild(String sourceOid, String parentOid, String name);

  Valintaryhma insert(ValintaryhmaCreateDTO valintaryhma);

  Valintaryhma update(String oid, ValintaryhmaCreateDTO valintaryhma);

  void delete(String oid);

  Set<String> findHakukohdesRecursive(Set<String> oids);
}
