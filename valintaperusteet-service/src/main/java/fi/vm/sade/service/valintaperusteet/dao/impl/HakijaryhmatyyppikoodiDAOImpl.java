package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmatyyppikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmatyyppikoodi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class HakijaryhmatyyppikoodiDAOImpl extends AbstractJpaDAOImpl<Hakijaryhmatyyppikoodi, Long>
    implements HakijaryhmatyyppikoodiDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public Hakijaryhmatyyppikoodi readByUri(String koodiUri) {
    QHakijaryhmatyyppikoodi koodi = QHakijaryhmatyyppikoodi.hakijaryhmatyyppikoodi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.eq(koodiUri)).fetchFirst();
  }

  @Override
  public List<Hakijaryhmatyyppikoodi> findByUris(String... koodiUris) {
    if (koodiUris == null || koodiUris.length == 0) {
      return new ArrayList<Hakijaryhmatyyppikoodi>();
    }
    QHakijaryhmatyyppikoodi koodi = QHakijaryhmatyyppikoodi.hakijaryhmatyyppikoodi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.in(koodiUris)).fetch();
  }

  @Override
  public Hakijaryhmatyyppikoodi insertOrUpdate(Hakijaryhmatyyppikoodi koodi) {
    Optional<Hakijaryhmatyyppikoodi> haettu = Optional.ofNullable(readByUri(koodi.getUri()));
    return haettu
        .map(
            k -> {
              update(k);
              return k;
            })
        .orElse(insert(koodi));
  }
}
