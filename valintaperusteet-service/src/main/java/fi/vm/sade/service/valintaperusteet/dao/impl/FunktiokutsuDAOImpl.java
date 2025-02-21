package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class FunktiokutsuDAOImpl extends AbstractJpaDAOImpl<Funktiokutsu, Long>
    implements FunktiokutsuDAO {

  @Override
  public List<Funktiokutsu> findFunktiokutsuByHakukohdeOid(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri kriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    QLaskentakaava kaava = QLaskentakaava.laskentakaava;

    return queryFactory()
        .select(kaava)
        .from(hakukohde)
        .innerJoin(hakukohde.valinnanvaiheet, vaihe)
        .innerJoin(vaihe.jonot, jono)
        .innerJoin(jono.jarjestyskriteerit, kriteeri)
        .innerJoin(kriteeri.laskentakaava, kaava)
        .where(
            hakukohde
                .oid
                .eq(hakukohdeOid)
                .and(vaihe.aktiivinen.isTrue())
                .and(jono.aktiivinen.isTrue())
                .and(kriteeri.aktiivinen.isTrue()))
        .distinct()
        .fetch()
        .stream()
        .map(k -> k.getFunktiokutsu())
        .toList();
  }

  @Override
  public Map<String, List<Funktiokutsu>> findFunktiokutsuByHakukohdeOids(
      List<String> hakukohdeOidit) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri kriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    QLaskentakaava kaava = QLaskentakaava.laskentakaava;

    Map<String, List<Funktiokutsu>> result = new HashMap<>();
    queryFactory()
        .select(hakukohde.oid, kaava)
        .from(hakukohde)
        .innerJoin(hakukohde.valinnanvaiheet, vaihe)
        .innerJoin(vaihe.jonot, jono)
        .innerJoin(jono.jarjestyskriteerit, kriteeri)
        .innerJoin(kriteeri.laskentakaava, kaava)
        .where(
            hakukohde
                .oid
                .in(hakukohdeOidit)
                .and(vaihe.aktiivinen.isTrue())
                .and(jono.aktiivinen.isTrue())
                .and(kriteeri.aktiivinen.isTrue()))
        .distinct()
        .fetch()
        .forEach(
            r -> {
              String rHakukohdeOid = r.get(0, String.class);
              Laskentakaava rLaskentakaava = r.get(1, Laskentakaava.class);
              if (!result.containsKey(rHakukohdeOid)) {
                result.put(rHakukohdeOid, new ArrayList<>());
              }
              result.get(rHakukohdeOid).add(rLaskentakaava.getFunktiokutsu());
            });
    return result;
  }

  @Override
  public boolean isReferencedByOtherLaskentakaavas(Long laskentakaavaId) {
    return !getEntityManager()
        .createNativeQuery(
            "SELECT id FROM laskentakaava WHERE jsonb_path_query_array(kaava, 'strict $.**.laskentakaavaChild.id') @@ '$[*]=="
                + laskentakaavaId.longValue()
                + "';")
        .getResultList()
        .isEmpty();
  }

  @Override
  public void flush() {
    getEntityManager().flush();
  }

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }
}
