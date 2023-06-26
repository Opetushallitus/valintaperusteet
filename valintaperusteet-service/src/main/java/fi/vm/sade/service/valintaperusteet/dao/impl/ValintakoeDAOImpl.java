package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.QValintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ValintakoeDAOImpl extends AbstractJpaDAOImpl<Valintakoe, Long>
    implements ValintakoeDAO {

  @Override
  public List<Valintakoe> findByValinnanVaihe(String valinnanVaiheOid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintakoe valintakoe = QValintakoe.valintakoe;
    return queryFactory()
        .select(valintakoe)
        .from(valinnanVaihe)
        .innerJoin(valinnanVaihe.valintakokeet, valintakoe)
        .leftJoin(valintakoe.laskentakaava)
        .fetchJoin()
        .leftJoin(valintakoe.masterValintakoe)
        .fetchJoin()
        .where(valinnanVaihe.oid.eq(valinnanVaiheOid))
        .distinct()
        .fetch();
  }

  @Override
  public Valintakoe readByOid(String oid) {
    QValintakoe valintakoe = QValintakoe.valintakoe;
    return queryFactory()
        .selectFrom(valintakoe)
        .leftJoin(valintakoe.laskentakaava)
        .fetchJoin()
        .leftJoin(valintakoe.masterValintakoe)
        .fetchJoin()
        .where(valintakoe.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public List<Valintakoe> readByOids(Collection<String> oids) {
    QValintakoe valintakoe = QValintakoe.valintakoe;
    return queryFactory()
        .selectFrom(valintakoe)
        .leftJoin(valintakoe.laskentakaava)
        .fetchJoin()
        .leftJoin(valintakoe.masterValintakoe)
        .fetchJoin()
        .where(valintakoe.oid.in(oids))
        .fetch();
  }

  @Override
  public List<Valintakoe> readByTunnisteet(Collection<String> tunnisteet) {
    QValintakoe valintakoe = QValintakoe.valintakoe;
    return queryFactory()
        .selectFrom(valintakoe)
        .leftJoin(valintakoe.laskentakaava)
        .fetchJoin()
        .leftJoin(valintakoe.masterValintakoe)
        .fetchJoin()
        .where(valintakoe.tunniste.in(tunnisteet))
        .fetch();
  }

  @Override
  public List<Valintakoe> findByLaskentakaava(long id) {
    QValintakoe valintakoe = QValintakoe.valintakoe;
    return queryFactory()
        .selectFrom(valintakoe)
        .leftJoin(valintakoe.laskentakaava)
        .where(valintakoe.laskentakaava.id.eq(id))
        .fetch();
  }

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }
}
