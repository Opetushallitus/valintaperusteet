package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.*;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LaskentakaavaDAOImpl extends AbstractJpaDAOImpl<Laskentakaava, Long>
    implements LaskentakaavaDAO {

  @Autowired EntityManager em;

  @Override
  public Laskentakaava getLaskentakaava(Long id) {
    QLaskentakaava lk = QLaskentakaava.laskentakaava;

    return queryFactory()
        .selectFrom(lk)
        .setHint("org.hibernate.cacheable", Boolean.TRUE)
        .where(lk.id.eq(id))
        .distinct()
        .fetchFirst();
  }

  @Override
  public Laskentakaava getLaskentakaavaValintaryhma(Long id) {
    QLaskentakaava lk = QLaskentakaava.laskentakaava;
    QValintaryhma v = QValintaryhma.valintaryhma;

    return queryFactory()
        .selectFrom(lk)
        .leftJoin(lk.valintaryhma, v)
        .fetchJoin()
        .where(lk.id.eq(id))
        .distinct()
        .fetchFirst();
  }

  @Override
  public List<Tuple> findLaskentakaavatByHakukohde(List<String> oids) {
    if (oids == null || oids.size() < 1) {
      return null;
    }
    QLaskentakaava lk = QLaskentakaava.laskentakaava;
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QValintatapajono vtj = QValintatapajono.valintatapajono;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QHakukohdeViite hkv1 = QHakukohdeViite.hakukohdeViite;

    return queryFactory()
        .select(hkv1.oid, vv.oid, lk)
        .from(lk)
        .setHint("org.hibernate.cacheable", Boolean.TRUE)
        .leftJoin(lk.jarjestyskriteerit, jk)
        .leftJoin(jk.valintatapajono, vtj)
        .leftJoin(vtj.valinnanVaihe, vv)
        .leftJoin(vv.hakukohdeViite, hkv1)
        .where(hkv1.oid.in(oids))
        .distinct()
        .fetch();
  }

  @Override
  public List<Laskentakaava> findKaavas(
      boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi) {
    QLaskentakaava lk = QLaskentakaava.laskentakaava;
    JPAQuery<Laskentakaava> query = queryFactory().selectFrom(lk);
    BooleanBuilder builder = new BooleanBuilder();
    if (!all) {
      builder.and(lk.onLuonnos.isFalse());
    }
    builder.and(
        valintaryhmaOid != null
            ? lk.valintaryhma.oid.eq(valintaryhmaOid)
            : lk.valintaryhma.isNull());
    builder.and(hakukohdeOid != null ? lk.hakukohde.oid.eq(hakukohdeOid) : lk.hakukohde.isNull());
    if (tyyppi != null) {
      builder.and(lk.tyyppi.eq(tyyppi));
    }
    return query.setHint("org.hibernate.cacheable", Boolean.TRUE).where(builder).fetch();
  }

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

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public boolean isReferencedByOtherLaskentakaavas(Long laskentakaavaId) {
    return !getEntityManager()
        .createNativeQuery(
            "SELECT id FROM laskentakaava WHERE jsonb_path_query_array(funktiokutsu, 'strict $.**.laskentakaavaChild.id') @@ '$[*]=="
                + laskentakaavaId.longValue()
                + "';")
        .getResultList()
        .isEmpty();
  }

  @Override
  public void flush() {
    getEntityManager().flush();
  }
}
