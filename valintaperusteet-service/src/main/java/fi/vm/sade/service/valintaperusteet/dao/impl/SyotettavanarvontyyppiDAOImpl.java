package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.SyotettavanarvontyyppiDAO;
import fi.vm.sade.service.valintaperusteet.model.Syotettavanarvontyyppi;
import fi.vm.sade.service.valintaperusteet.model.QSyotettavanarvontyyppi;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SyotettavanarvontyyppiDAOImpl extends AbstractJpaDAOImpl<Syotettavanarvontyyppi, Long> implements SyotettavanarvontyyppiDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public Syotettavanarvontyyppi readByUri(String koodiUri) {
        QSyotettavanarvontyyppi koodi = QSyotettavanarvontyyppi.syotettavanarvontyyppi;
        return from(koodi)
                .where(koodi.uri.eq(koodiUri))
                .singleResult(koodi);
    }

    @Override
    public List<Syotettavanarvontyyppi> findByUris(String... koodiUris) {
        if (koodiUris == null || koodiUris.length == 0) {
            return new ArrayList<Syotettavanarvontyyppi>();
        }
        QSyotettavanarvontyyppi koodi = QSyotettavanarvontyyppi.syotettavanarvontyyppi;
        return from(koodi)
                .where(koodi.uri.in(koodiUris))
                .list(koodi);
    }

    @Override
    public Syotettavanarvontyyppi insertOrUpdate(Syotettavanarvontyyppi koodi) {
        Optional<Syotettavanarvontyyppi> haettu = Optional.ofNullable(readByUri(koodi.getUri()));
        return haettu.map(k -> {
            update(k);
            return k;
        }).orElse(insert(koodi));
    }
}
