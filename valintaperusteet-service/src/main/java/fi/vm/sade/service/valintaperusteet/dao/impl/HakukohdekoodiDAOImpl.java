package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdekoodi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class HakukohdekoodiDAOImpl extends AbstractJpaDAOImpl<Hakukohdekoodi, Long>
    implements HakukohdekoodiDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public Hakukohdekoodi readByUri(String koodiUri) {
    QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.eq(koodiUri)).fetchFirst();
  }

  @Override
  public Hakukohdekoodi findByHakukohdeOid(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QHakukohdekoodi hakukohdekoodi = QHakukohdekoodi.hakukohdekoodi;
    return queryFactory()
        .select(hakukohdekoodi)
        .from(hakukohde)
        .innerJoin(hakukohde.hakukohdekoodi, hakukohdekoodi)
        .where(hakukohde.oid.eq(hakukohdeOid))
        .fetchFirst();
  }

  @Override
  public Hakukohdekoodi findByHakukohdeOidAndKoodiUri(String hakukohdeOid, String koodiUri) {
    QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    return queryFactory()
        .selectFrom(koodi)
        .where(koodi.uri.eq(koodiUri).and(hakukohde.oid.eq(hakukohdeOid)))
        .fetchFirst();
  }

  @Override
  public List<Hakukohdekoodi> findByUris(String... koodiUris) {
    if (koodiUris == null || koodiUris.length == 0) {
      return new ArrayList<Hakukohdekoodi>();
    }
    QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.in(koodiUris)).fetch();
  }

  @Override
  public Hakukohdekoodi insertOrUpdate(Hakukohdekoodi koodi) {
    Optional<Hakukohdekoodi> haettu = Optional.ofNullable(readByUri(koodi.getUri()));
    return haettu
        .map(
            k -> {
              update(k);
              return k;
            })
        .orElse(insert(koodi));
  }
}
