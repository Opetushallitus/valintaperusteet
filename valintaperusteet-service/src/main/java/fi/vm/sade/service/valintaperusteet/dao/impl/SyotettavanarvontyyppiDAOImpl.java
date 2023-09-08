package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.SyotettavanarvontyyppiDAO;
import fi.vm.sade.service.valintaperusteet.model.QSyotettavanarvontyyppi;
import fi.vm.sade.service.valintaperusteet.model.Syotettavanarvontyyppi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SyotettavanarvontyyppiDAOImpl extends AbstractJpaDAOImpl<Syotettavanarvontyyppi, Long>
    implements SyotettavanarvontyyppiDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public Syotettavanarvontyyppi readByUri(String koodiUri) {
    QSyotettavanarvontyyppi koodi = QSyotettavanarvontyyppi.syotettavanarvontyyppi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.eq(koodiUri)).fetchFirst();
  }

  @Override
  public List<Syotettavanarvontyyppi> findByUris(String... koodiUris) {
    if (koodiUris == null || koodiUris.length == 0) {
      return new ArrayList<Syotettavanarvontyyppi>();
    }
    QSyotettavanarvontyyppi koodi = QSyotettavanarvontyyppi.syotettavanarvontyyppi;
    return queryFactory().selectFrom(koodi).where(koodi.uri.in(koodiUris)).fetch();
  }

  @Override
  public Syotettavanarvontyyppi insertOrUpdate(Syotettavanarvontyyppi koodi) {
    Optional<Syotettavanarvontyyppi> haettu = Optional.ofNullable(readByUri(koodi.getUri()));
    return haettu
        .map(
            k -> {
              update(k);
              return k;
            })
        .orElse(insert(koodi));
  }
}
