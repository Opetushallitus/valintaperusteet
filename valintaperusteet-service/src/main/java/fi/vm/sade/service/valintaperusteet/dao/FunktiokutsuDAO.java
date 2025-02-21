package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import java.util.List;
import java.util.Map;

public interface FunktiokutsuDAO extends JpaDAO<Funktiokutsu, Long> {

  List<Funktiokutsu> findFunktiokutsuByHakukohdeOid(String hakukohdeOid);

  Map<String, List<Funktiokutsu>> findFunktiokutsuByHakukohdeOids(List<String> hakukohdeOidit);

  boolean isReferencedByOtherLaskentakaavas(Long laskentakaavaId);

  void flush();
}
