package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class HakijaryhmaValintatapajonoDAOImpl extends AbstractJpaDAOImpl<HakijaryhmaValintatapajono, Long> implements HakijaryhmaValintatapajonoDAO {
    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

    @Override
    public HakijaryhmaValintatapajono readByOid(String oid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QValintatapajono v = QValintatapajono.valintatapajono;

        return from(hv)
                .where(hv.oid.eq(oid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.valintatapajono, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(v.valinnanVaihe).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .singleResult(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByValintatapajono(String oid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QValintatapajono v = QValintatapajono.valintatapajono;

        return from(hv).where(hv.valintatapajono.oid.eq(oid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.valintatapajono, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(v.valinnanVaihe).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);

    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHakijaryhma(String hakijaryhmaOid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QValintatapajono v = QValintatapajono.valintatapajono;

        return from(hv).where(hv.hakijaryhma.oid.eq(hakijaryhmaOid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.valintatapajono, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(v.valinnanVaihe).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHakukohde(String oid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

        return from(hv).where(hv.hakukohdeViite.oid.eq(oid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.hakukohdeViite, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> oids) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

        return from(hv).where(hv.hakukohdeViite.oid.in(oids))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.hakukohdeViite, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHaku(String hakuOid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

        return from(hv).where(hv.hakukohdeViite.hakuoid.eq(hakuOid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.hakukohdeViite, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);
    }

    @Override
    public HakijaryhmaValintatapajono haeHakukohteenViimeinenHakijaryhma(String hakukohdeOid) {
        QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
        QHakijaryhmaValintatapajono hakijaryhmajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
        QHakijaryhma h = QHakijaryhma.hakijaryhma;

        HakijaryhmaValintatapajono lastValinnanVaihe = from(hakukohde)
                .leftJoin(hakukohde.hakijaryhmat, hakijaryhmajono)
                .leftJoin(hakijaryhmajono.hakijaryhma, h)
                .where(hakijaryhmajono.id.notIn(
                        subQuery().from(hakijaryhmajono)
                                .where(hakijaryhmajono.edellinen.isNotNull())
                                .list(hakijaryhmajono.edellinen.id)
                )
                        .and(hakukohde.oid.eq(hakukohdeOid)))
                .singleResult(hakijaryhmajono);

        return lastValinnanVaihe;
    }

    @Override
    public HakijaryhmaValintatapajono haeValintatapajononViimeinenHakijaryhma(String valintatapajonoOid) {
        QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
        QHakijaryhmaValintatapajono hakijaryhmajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
        QHakijaryhma h = QHakijaryhma.hakijaryhma;

        return from(valintatapajono)
                .leftJoin(valintatapajono.hakijaryhmat, hakijaryhmajono)
                .leftJoin(hakijaryhmajono.hakijaryhma, h)
                .where(hakijaryhmajono.id.notIn(
                        subQuery().from(hakijaryhmajono)
                                .where(hakijaryhmajono.edellinen.isNotNull())
                                .list(hakijaryhmajono.edellinen.id)
                )
                        .and(valintatapajono.oid.eq(valintatapajonoOid)))
                .singleResult(hakijaryhmajono);
    }
}
