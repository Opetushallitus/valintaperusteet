package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
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

  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  @Override
  public Set<Valintakoekoodi> findByValintaryhma(String valintaryhmaOid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;
    return new HashSet<>(
        from(valintaryhma)
            .join(valintaryhma.valintakoekoodit, valintakoekoodi)
            .where(valintaryhma.oid.eq(valintaryhmaOid))
            .list(valintakoekoodi));
  }

  @Override
  public Valintakoekoodi readByUri(String uri) {
    QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;
    return from(valintakoekoodi).where(valintakoekoodi.uri.eq(uri)).singleResult(valintakoekoodi);
  }

  @Override
  public List<Valintakoekoodi> findByUris(String[] koodiUris) {
    if (koodiUris == null || koodiUris.length == 0) {
      return new ArrayList<Valintakoekoodi>();
    }
    QValintakoekoodi koodi = QValintakoekoodi.valintakoekoodi;
    return from(koodi).where(koodi.uri.in(koodiUris)).list(koodi);
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
