package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.OrganisaatioDAO;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;
import fi.vm.sade.service.valintaperusteet.model.QOrganisaatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 26.11.2013
 * Time: 13.16
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class OrganisaatioDAOImpl extends AbstractJpaDAOImpl<Organisaatio, Long> implements OrganisaatioDAO {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioDAOImpl.class);

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public Organisaatio readByOid(String oid) {
        QOrganisaatio o = QOrganisaatio.organisaatio;

        return from(o)
                .where(o.oid.eq(oid))
                .singleResult(o);
    }

    @Override
    public List<Organisaatio> readByOidList(Set<String> oids) {
        QOrganisaatio organisaatio = QOrganisaatio.organisaatio;

        return from(organisaatio)
                .where(organisaatio.oid.in(oids))
                .list(organisaatio);
    }
}
