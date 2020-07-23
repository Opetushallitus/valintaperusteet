package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import java.util.List;
import java.util.Map;

public interface FunktiokutsuDAO extends JpaDAO<Funktiokutsu, Long> {
  Funktiokutsu getFunktiokutsu(Long id);

  Funktiokutsu getFunktiokutsunValintaperusteet(Long id);

  long deleteOrphans();

  List<Funktiokutsu> findFunktiokutsuByHakukohdeOid(String hakukohdeOid);

  Map<String, List<Funktiokutsu>> findFunktiokutsuByHakukohdeOids(List<String> hakukohdeOidit);

  List<Funktioargumentti> findByLaskentakaavaChild(Long laskentakaavaId);

  void flush();
}
