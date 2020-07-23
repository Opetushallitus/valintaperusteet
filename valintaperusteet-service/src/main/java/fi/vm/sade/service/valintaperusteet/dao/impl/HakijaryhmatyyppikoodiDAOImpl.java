package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
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

  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  @Override
  public Hakijaryhmatyyppikoodi readByUri(String koodiUri) {
    QHakijaryhmatyyppikoodi koodi = QHakijaryhmatyyppikoodi.hakijaryhmatyyppikoodi;
    return from(koodi).where(koodi.uri.eq(koodiUri)).singleResult(koodi);
  }

  @Override
  public List<Hakijaryhmatyyppikoodi> findByUris(String... koodiUris) {
    if (koodiUris == null || koodiUris.length == 0) {
      return new ArrayList<Hakijaryhmatyyppikoodi>();
    }
    QHakijaryhmatyyppikoodi koodi = QHakijaryhmatyyppikoodi.hakijaryhmatyyppikoodi;
    return from(koodi).where(koodi.uri.in(koodiUris)).list(koodi);
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
