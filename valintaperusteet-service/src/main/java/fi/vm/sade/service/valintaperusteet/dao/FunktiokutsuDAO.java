package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 15.1.2013
 * Time: 17.20
 * To change this template use File | Settings | File Templates.
 */
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
