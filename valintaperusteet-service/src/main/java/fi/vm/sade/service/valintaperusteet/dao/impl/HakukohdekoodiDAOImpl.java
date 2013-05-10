package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdekoodi;
import org.springframework.stereotype.Repository;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.59
 */
@Repository
public class HakukohdekoodiDAOImpl extends AbstractJpaDAOImpl<Hakukohdekoodi, Long> implements HakukohdekoodiDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public Hakukohdekoodi findByKoodiUri(String koodiUri) {
        QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;

        return from(koodi)
                .leftJoin(koodi.hakukohde).fetch()
                .leftJoin(koodi.valintaryhma).fetch()
                .where(koodi.uri.eq(koodiUri))
                .singleResult(koodi);
    }

    @Override
    public Hakukohdekoodi findByHakukohdeOid(String hakukohdeOid) {
        QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
        QHakukohdekoodi hakukohdekoodi = QHakukohdekoodi.hakukohdekoodi;

        return from(hakukohde)
                .innerJoin(hakukohde.hakukohdekoodi, hakukohdekoodi)
                .leftJoin(hakukohdekoodi.hakukohde).fetch()
                .leftJoin(hakukohdekoodi.valintaryhma).fetch()
                .where(hakukohde.oid.eq(hakukohdeOid))
                .singleResult(hakukohdekoodi);
    }

}
