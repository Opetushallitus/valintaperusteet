package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: tommiha Date: 1/17/13 Time: 12:51 PM
 */
@Repository
public class HakukohdeViiteDAOImpl extends AbstractJpaDAOImpl<HakukohdeViite, Long> implements HakukohdeViiteDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<HakukohdeViite> findRoot() {
        QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        return from(hakukohdeViite)
                .leftJoin(hakukohdeViite.valintaryhma, valintaryhma).fetch()
                .leftJoin(hakukohdeViite.hakukohdekoodi).fetch()
                .where(valintaryhma.isNull())
                .list(hakukohdeViite);
    }

    @Override
    public List<HakukohdeViite> findAll() {
        QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        return from(hakukohdeViite)
                .leftJoin(hakukohdeViite.valintaryhma, valintaryhma).fetch()
                .leftJoin(hakukohdeViite.hakukohdekoodi).fetch()
                .list(hakukohdeViite);
    }

    @Override
    public HakukohdeViite readByOid(String oid) {
        QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        return from(hakukohdeViite)
                .leftJoin(hakukohdeViite.valintaryhma, valintaryhma).fetch()
                .leftJoin(hakukohdeViite.hakukohdekoodi).fetch()
                .leftJoin(hakukohdeViite.valintakokeet).fetch()
                .leftJoin(hakukohdeViite.hakukohteenValintaperusteet).fetch()
                .where(hakukohdeViite.oid.eq(oid))
                .singleResult(hakukohdeViite);
    }

    @Override
    public List<HakukohdeViite> findByValintaryhmaOid(String oid) {
        QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        return from(hakukohdeViite)
                .leftJoin(hakukohdeViite.valintaryhma, valintaryhma).fetch()
                .leftJoin(hakukohdeViite.hakukohdekoodi).fetch()
                .where(valintaryhma.oid.eq(oid)).list(hakukohdeViite);
    }

    @Override
    public boolean kuuluuSijoitteluun(String oid) {
        QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
        QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;
        return from(jono)
                .leftJoin(jono.valinnanVaihe, vv)
                .leftJoin(vv.hakukohdeViite, hakukohdeViite)
                .where(
                        hakukohdeViite.oid.eq(oid),
                        vv.aktiivinen.eq(true),
                        jono.aktiivinen.eq(true),
                        jono.siirretaanSijoitteluun.eq(true)
                ).exists();
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public List<HakukohdeViite> search(String hakuOid, List<String> tila, String searchString) {
        QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
        JPAQuery a = from(hakukohdeViite).leftJoin(hakukohdeViite.valintaryhma).fetch();

        if (StringUtils.isNotBlank(hakuOid)) {
            a.where(hakukohdeViite.hakuoid.eq(hakuOid));
        }
        if (tila != null && tila.size() > 0) {
            a.where(hakukohdeViite.tila.in(tila));
        }
        if (StringUtils.isNotBlank(searchString)) {
            a.where(hakukohdeViite.nimi.containsIgnoreCase(searchString));
        }

        return a.list(hakukohdeViite);

    }

    @Override
    public List<HakukohdeViite> readByHakukohdekoodiUri(String koodiUri) {
        QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
        QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;

        return from(hk)
                .innerJoin(hk.hakukohdekoodi, koodi)
                .where(koodi.uri.eq(koodiUri))
                .distinct().list(hk);
    }
}
