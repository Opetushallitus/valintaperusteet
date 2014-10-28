package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 1.10.2013
 * Time: 15.15
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class HakijaryhmaDAOImpl extends AbstractJpaDAOImpl<Hakijaryhma, Long> implements HakijaryhmaDAO {
    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

    @Override
    public Hakijaryhma readByOid(String oid) {
        QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
        QHakijaryhmaValintatapajono hakijaryhmaValintatapajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(hakijaryhma).where(hakijaryhma.oid.eq(oid))
                .leftJoin(hakijaryhma.jonot, hakijaryhmaValintatapajono).fetch()
                .leftJoin(hakijaryhmaValintatapajono.valintatapajono).fetch()
                .leftJoin(hakijaryhmaValintatapajono.hakukohdeViite).fetch()
                .leftJoin(hakijaryhma.valintaryhma).fetch()
                .leftJoin(hakijaryhma.laskentakaava).fetch()
                .singleResult(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findByValintatapajono(String oid) {
        QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;

        return from(hakijaryhma).where(hakijaryhma.jonot.any().valintatapajono.oid.eq(oid))
                .leftJoin(hakijaryhma.jonot).fetch()
                .leftJoin(hakijaryhma.valintaryhma).fetch()
                .leftJoin(hakijaryhma.laskentakaava).fetch()
                .listDistinct(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findByHakukohde(String oid) {
        QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;

        return from(hakijaryhma).where(hakijaryhma.jonot.any().hakukohdeViite.oid.eq(oid))
                .leftJoin(hakijaryhma.jonot).fetch()
                .leftJoin(hakijaryhma.valintaryhma).fetch()
                .leftJoin(hakijaryhma.laskentakaava).fetch()
                .listDistinct(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findByValintaryhma(String oid) {
        QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
//        QHakijaryhmaValintatapajono hakijaryhmaValintatapajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(hakijaryhma).where(hakijaryhma.valintaryhma.oid.eq(oid))
//                .leftJoin(hakijaryhma.jonot, hakijaryhmaValintatapajono).fetch()
//                .leftJoin(hakijaryhmaValintatapajono.valintatapajono).fetch()
//                .leftJoin(hakijaryhmaValintatapajono.hakukohdeViite).fetch()
                .leftJoin(hakijaryhma.valintaryhma).fetch()
                .leftJoin(hakijaryhma.laskentakaava).fetch()
                .listDistinct(hakijaryhma);
    }

    @Override
    public Hakijaryhma haeValintaryhmanViimeinenHakijaryhma(String valintaryhmaOid) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;

        Hakijaryhma lastValinnanVaihe = from(valintaryhma)
                .leftJoin(valintaryhma.hakijaryhmat, hakijaryhma)
//                .where(hakijaryhma.id.notIn(
//                        subQuery().from(hakijaryhma)
//                                .where(hakijaryhma.edellinen.isNotNull())
//                                .list(hakijaryhma.edellinen.id)
//                )
//                .where(valintaryhma.oid.eq(valintaryhmaOid)))
                .where(valintaryhma.oid.eq(valintaryhmaOid))
                .singleResult(hakijaryhma);

        return lastValinnanVaihe;
    }

    @Override
    public List<Hakijaryhma> findByLaskentakaava(long id) {
        QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
        return from(hakijaryhma)
                .leftJoin(hakijaryhma.laskentakaava)
                .where(hakijaryhma.laskentakaava.id.eq(id))
                .list(hakijaryhma);
    }
}
