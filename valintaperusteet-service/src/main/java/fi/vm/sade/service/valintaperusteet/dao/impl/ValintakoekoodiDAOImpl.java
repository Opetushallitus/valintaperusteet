package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.QValintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ValintakoekoodiDAOImpl extends AbstractJpaDAOImpl<Valintakoekoodi, Long>
    implements ValintakoekoodiDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public Set<Valintakoekoodi> findByValintaryhma(String valintaryhmaOid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;
    return new HashSet<>(
        queryFactory()
            .select(valintakoekoodi)
            .from(valintaryhma)
            .join(valintaryhma.valintakoekoodit, valintakoekoodi)
            .where(valintaryhma.oid.eq(valintaryhmaOid))
            .fetch());
  }

  @Override
  public Valintakoekoodi readByUri(String uri) {
    QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;
    return queryFactory()
        .selectFrom(valintakoekoodi)
        .where(valintakoekoodi.uri.eq(uri))
        .fetchFirst();
  }

  @Override
  public List<Valintakoekoodi> findByUris(String[] koodiUris) {
    if (koodiUris == null || koodiUris.length == 0) {
      return new ArrayList<>();
    }
    QValintakoekoodi koodi = QValintakoekoodi.valintakoekoodi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.in(koodiUris)).fetch();
  }

  @Override
  public Valintakoekoodi insertOrUpdate(Valintakoekoodi koodi) {
    Optional<Valintakoekoodi> haettu = Optional.ofNullable(readByUri(koodi.getUri()));
    return haettu
        .map(
            k -> {
              update(k);
              return k;
            })
        .orElse(insert(koodi));
  }
}
