package fi.vm.sade.service.valintaperusteet.dao.impl;

import java.util.List;

import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.QTuple;

import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;

/**
 * User: tommiha Date: 1/14/13 Time: 4:07 PM
 */
@Repository
public class LaskentakaavaDAOImpl extends AbstractJpaDAOImpl<Laskentakaava, Long> implements LaskentakaavaDAO {

    @Override
    public Laskentakaava getLaskentakaava(Long id) {
        QLaskentakaava lk = QLaskentakaava.laskentakaava;

        Laskentakaava laskentakaava = from(lk).setHint("org.hibernate.cacheable", Boolean.TRUE)
                .where(lk.id.eq(id)).distinct().singleResult(lk);

        return laskentakaava;
    }

    @Override
    public Laskentakaava getLaskentakaavaValintaryhma(Long id) {
        QLaskentakaava lk = QLaskentakaava.laskentakaava;
        QValintaryhma v = QValintaryhma.valintaryhma;

        Laskentakaava laskentakaava = from(lk)
                .leftJoin(lk.valintaryhma, v).fetch()
                .where(lk.id.eq(id)).distinct().singleResult(lk);

        return laskentakaava;
    }

    @Override
    public List<Tuple> findLaskentakaavatByHakukohde(List<String> oids) {
        if (oids == null || oids.size() < 1) {
            return null;
        }
        QLaskentakaava lk = QLaskentakaava.laskentakaava;
        QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
        QValintatapajono vtj = QValintatapajono.valintatapajono;
        QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
        QHakukohdeViite hkv1 = QHakukohdeViite.hakukohdeViite;

        return from(lk).setHint("org.hibernate.cacheable", Boolean.TRUE).leftJoin(lk.jarjestyskriteerit, jk)
                .leftJoin(jk.valintatapajono, vtj).leftJoin(vtj.valinnanVaihe, vv).leftJoin(vv.hakukohdeViite, hkv1)
                .where(hkv1.oid.in(oids)).distinct().list(new QTuple(hkv1.oid, vv.oid, lk));
    }

    @Override
    public List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi) {
        QLaskentakaava lk = QLaskentakaava.laskentakaava;

        JPAQuery query = from(lk);

        BooleanBuilder builder = new BooleanBuilder();
        if (!all) {
            builder.and(lk.onLuonnos.isFalse());
        }

        builder.and(valintaryhmaOid != null ? lk.valintaryhma.oid.eq(valintaryhmaOid) : lk.valintaryhma.isNull());
        builder.and(hakukohdeOid != null ? lk.hakukohde.oid.eq(hakukohdeOid) : lk.hakukohde.isNull());

        if (tyyppi != null) {
            builder.and(lk.tyyppi.eq(tyyppi));
        }

        return query.setHint("org.hibernate.cacheable", Boolean.TRUE).where(builder).list(lk);
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

}
