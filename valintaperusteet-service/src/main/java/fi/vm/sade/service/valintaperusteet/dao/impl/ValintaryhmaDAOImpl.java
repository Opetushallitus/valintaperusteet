package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.QValintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * User: tommiha Date: 1/17/13 Time: 12:51 PM
 */
@Repository
public class ValintaryhmaDAOImpl extends AbstractJpaDAOImpl<Valintaryhma, Long> implements ValintaryhmaDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ValintaryhmaDAOImpl.class);

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
        return from(valintaryhma)
                .leftJoin(valintaryhma.alavalintaryhmat).fetch()
                .leftJoin(valintaryhma.hakukohdeViitteet).fetch()
                .leftJoin(valintaryhma.hakukohdekoodit).fetch()
                .leftJoin(valintaryhma.valintakoekoodit).fetch()
                .leftJoin(valintaryhma.organisaatiot).fetch()
                .where(eq).distinct().orderBy(valintaryhma.nimi.asc()).list(valintaryhma);
    }

    @Override
    public Valintaryhma readByOid(String oid) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

        Valintaryhma vr = from(valintaryhma).leftJoin(valintaryhma.alavalintaryhmat).fetch()
                .leftJoin(valintaryhma.hakukohdeViitteet).fetch()
                .leftJoin(valintaryhma.hakukohdekoodit).fetch()
                .leftJoin(valintaryhma.organisaatiot).fetch()
                .where(valintaryhma.oid.eq(oid))
                .singleResult(valintaryhma);


        if (vr != null) {
            // Initialisoidaan valintakoekoodien haku
            vr.getValintakoekoodit().size();
        }

        return vr;

    }

    @Override
    public List<Valintaryhma> readHierarchy(String childOid) {
        List<Valintaryhma> set = new LinkedList<Valintaryhma>();
        set.add(readByOid(childOid));
        Valintaryhma parent = null;
        String currentOid = childOid;
        do {
            parent = findParent(currentOid);
            if (parent != null) {
                currentOid = parent.getOid();
                set.add(parent);
            }
        } while (parent != null);
        return set;
    }

    @Override
    public List<Valintaryhma> findAllFetchAlavalintaryhmat() {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

        return from(valintaryhma)
                .leftJoin(valintaryhma.alavalintaryhmat).fetch()
                .leftJoin(valintaryhma.organisaatiot).fetch()
                .distinct().list(valintaryhma);
    }


    @Override
    public List<Valintaryhma> haeHakukohdekoodinJaValintakoekoodienMukaan(String hakukohdekoodiUri,
                                                                          Collection<String> valintakoekoodiUrit) {
        LOG.info("hakukohdekoodi: {}, valintakoekoodit: {}",
                new Object[]{hakukohdekoodiUri, valintakoekoodiUrit});

        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        QHakukohdekoodi hakukohdekoodi = QHakukohdekoodi.hakukohdekoodi;
        QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(hakukohdekoodi.uri.eq(hakukohdekoodiUri));

        if (!valintakoekoodiUrit.isEmpty()) {
            booleanBuilder.and(valintakoekoodi.uri.in(valintakoekoodiUrit));
        }

        return from(valintaryhma)
                .join(valintaryhma.hakukohdekoodit, hakukohdekoodi).fetch()
                .leftJoin(valintaryhma.valintakoekoodit, valintakoekoodi).fetch()
                .where(booleanBuilder)
                .distinct()
                .list(valintaryhma);
    }

    private Valintaryhma findParent(String oid) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

        Valintaryhma current = from(valintaryhma)
                .where(valintaryhma.oid.eq(oid))
                .singleResult(valintaryhma);

        return current.getYlavalintaryhma();
    }

    @Override
    public List<Valintaryhma> readByHakukohdekoodiUri(String koodiUri) {
        QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
        QValintaryhma vr = QValintaryhma.valintaryhma;

        return from(vr)
                .innerJoin(vr.hakukohdekoodit, koodi)
                .where(koodi.uri.eq(koodiUri))
                .distinct().list(vr);

    }

    @Override
    public List<Valintaryhma> readByHakuoid(String hakuoid) {
        QValintaryhma vr = QValintaryhma.valintaryhma;

        return from(vr)
                .where(vr.hakuoid.eq(hakuoid))
                .distinct().list(vr);
    }

}
