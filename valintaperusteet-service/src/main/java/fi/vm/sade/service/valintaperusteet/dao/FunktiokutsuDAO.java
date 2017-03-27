package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;

import java.util.List;

public interface FunktiokutsuDAO extends JpaDAO<Funktiokutsu, Long> {
    Funktiokutsu getFunktiokutsu(Long id);

    Funktiokutsu getFunktiokutsunValintaperusteet(Long id);

    void deleteOrphans();

    List<Funktiokutsu> findFunktiokutsuByHakukohdeOids(String hakukohdeOid);

    List<Funktioargumentti> findByLaskentakaavaChild(Long laskentakaavaId);

    void flush();
}
