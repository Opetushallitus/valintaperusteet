package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.QTuple;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: tommiha Date: 1/14/13 Time: 4:07 PM
 */
@Repository
public class LaskentakaavaDAOImpl extends AbstractJpaDAOImpl<Laskentakaava, Long> implements LaskentakaavaDAO {

    @Override
    public Laskentakaava getLaskentakaava(Long id) {
        QLaskentakaava lk = QLaskentakaava.laskentakaava;
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;

        Laskentakaava laskentakaava = from(lk).leftJoin(lk.funktiokutsu, fk).fetch()
                .leftJoin(fk.arvokonvertteriparametrit).fetch().leftJoin(fk.arvovalikonvertteriparametrit).fetch()
                .leftJoin(fk.syoteparametrit).fetch().leftJoin(fk.funktioargumentit).fetch()
                .leftJoin(fk.valintaperuste).fetch().where(lk.id.eq(id)).distinct().singleResult(lk);

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

        return from(lk).leftJoin(lk.jarjestyskriteerit, jk).leftJoin(jk.valintatapajono, vtj)
                .leftJoin(vtj.valinnanVaihe, vv).leftJoin(vv.hakukohdeViite, hkv1).where(hkv1.oid.in(oids)).distinct()
                .list(new QTuple(hkv1.oid, vv.oid, lk));
    }

    @Override
    public List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, Funktiotyyppi tyyppi) {
        QLaskentakaava lk = QLaskentakaava.laskentakaava;

        JPAQuery query = from(lk);

        if(!all) {
            query.where(lk.onLuonnos.isFalse());
        }

        query.where(valintaryhmaOid != null ?
                lk.valintaryhma.oid.eq(valintaryhmaOid) : lk.valintaryhma.isNull());

        if(tyyppi != null) {
            query.where(lk.tyyppi.eq(tyyppi));
        }

        return query.list(lk);
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

}
