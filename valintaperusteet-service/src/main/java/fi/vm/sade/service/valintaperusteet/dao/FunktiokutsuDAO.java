package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;

import java.util.List;

public interface FunktiokutsuDAO extends JpaDAO<Funktiokutsu, Long> {
    Funktiokutsu getFunktiokutsu(Long id);

    Funktiokutsu getFunktiokutsunValintaperusteet(Long id);

    List<Long> getOrphans();

    void deleteOrphans();

    void deleteOrphan(Long id);

    List<Funktiokutsu> findFunktiokutsuByHakukohdeOids(String hakukohdeOid);

    List<Funktioargumentti> findByLaskentakaavaChild(Long laskentakaavaId);

    void flush();
}
