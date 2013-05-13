package fi.vm.sade.service.valintaperusteet.dao.impl;

import java.util.*;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

/**
 * User: tommiha Date: 1/17/13 Time: 12:51 PM
 */
@Repository
public class ValintaryhmaDAOImpl extends AbstractJpaDAOImpl<Valintaryhma, Long> implements ValintaryhmaDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<Valintaryhma> findChildrenByParentOid(String id) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

        BooleanExpression eq = null;
        if (id == null) {
            eq = valintaryhma.ylavalintaryhma.isNull();
        } else {
            eq = valintaryhma.ylavalintaryhma.oid.eq(id);
        }
        return from(valintaryhma).leftJoin(valintaryhma.alavalintaryhmat).fetch()
                .leftJoin(valintaryhma.hakukohdeViitteet).fetch().where(eq).distinct().list(valintaryhma);
    }

    @Override
    public Valintaryhma readByOid(String oid) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

        return from(valintaryhma).leftJoin(valintaryhma.alavalintaryhmat).fetch()
                .leftJoin(valintaryhma.hakukohdeViitteet).fetch()
                .leftJoin(valintaryhma.hakukohdekoodit).fetch()
                .where(valintaryhma.oid.eq(oid))
                .singleResult(valintaryhma);
    }

    @Override
    public List<Valintaryhma> readHierarchy(String childOid) {
        List<Valintaryhma> set = new LinkedList<Valintaryhma>();
        set.add(readByOid(childOid));
        Valintaryhma parent = null;
        String currentOid = childOid;
        do {
            parent = findParent(currentOid);
            if(parent != null) {
                currentOid = parent.getOid();
                set.add(parent);
            }
        } while (parent != null);
        return set;
    }

    private Valintaryhma findParent(String oid) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

        Valintaryhma current = from(valintaryhma)
                .where(valintaryhma.oid.eq(oid))
                .singleResult(valintaryhma);

        return current.getYlavalintaryhma();
    }
}
