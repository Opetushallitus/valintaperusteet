package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: tommiha Date: 1/17/13 Time: 12:51 PM
 */
@Repository
public class JarjestyskriteeriDAOImpl extends AbstractJpaDAOImpl<Jarjestyskriteeri, Long> implements
        JarjestyskriteeriDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

    @Override
    public List<Jarjestyskriteeri> findByJono(String oid) {
        QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;

        return from(jk).leftJoin(jk.edellinen).fetch().leftJoin(jk.master).fetch().leftJoin(jk.laskentakaava).fetch()
                .leftJoin(jk.valintatapajono).fetch().where(jk.valintatapajono.oid.eq(oid)).list(jk);
    }

    @Override
    public List<Jarjestyskriteeri> findByHakukohde(String oid) {
        QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
        QValintatapajono vtj = QValintatapajono.valintatapajono;
        QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;

        QValintaryhma vr = QValintaryhma.valintaryhma;

        QHakukohdeViite hkv1 = new QHakukohdeViite("hkv1");

        return from(jk).leftJoin(jk.valintatapajono, vtj).leftJoin(vtj.valinnanVaihe, vv)
                .leftJoin(vv.hakukohdeViite, hkv1).leftJoin(vv.valintaryhma, vr).where(hkv1.oid.eq(oid)).distinct()
                .list(jk);
    }

    @Override
    public Jarjestyskriteeri readByOid(String oid) {
        QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;

        return from(jk).where(jk.oid.eq(oid)).singleResult(jk);
    }

    @Override
    public Jarjestyskriteeri haeValintatapajononViimeinenJarjestyskriteeri(String valintatapajonoOid) {
        QValintatapajono jono = QValintatapajono.valintatapajono;
        QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;

        return from(jono)
                .leftJoin(jono.jarjestyskriteerit, jk)
                .where(jk.id.notIn(subQuery().from(jk).where(jk.edellinen.isNotNull()).list(jk.edellinen.id)).and(
                        jono.oid.eq(valintatapajonoOid))).singleResult(jk);
    }

}
