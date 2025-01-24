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
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public void flush() {
    getEntityManager().flush();
  }

  @Override
  @Transactional
  public Optional<Long> migrateLaskentakaavat() {
    QLaskentakaava lk = QLaskentakaava.laskentakaava;
    Collection<Laskentakaava> laskentakaavat =
        queryFactory()
            .selectFrom(lk)
            .where(lk.kaava.isNull())
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .setHint("jakarta.persistence.lock.timeout", -2)
            .limit(1)
            .fetch();
    if (laskentakaavat.size() == 0) {
      return Optional.empty();
    }
    laskentakaavat.forEach(kaava -> kaava.migrateKaava());

    return Optional.of(laskentakaavat.iterator().next().getId());
  }
}
