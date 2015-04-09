package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: tommiha Date: 1/17/13 Time: 12:51 PM
 */
@Repository
public class ValintatapajonoDAOImpl extends AbstractJpaDAOImpl<Valintatapajono, Long> implements ValintatapajonoDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

    public List<Valintatapajono> findByValinnanVaihe(String oid) {
        QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(valinnanVaihe).leftJoin(valinnanVaihe.jonot, jono)
                .leftJoin(valinnanVaihe.valintaryhma)
                .leftJoin(jono.edellinenValintatapajono).fetch()
                .leftJoin(jono.masterValintatapajono).fetch()
                .leftJoin(jono.varasijanTayttojono).fetch()
                .leftJoin(jono.hakijaryhmat, hv).fetch()
                .leftJoin(hv.hakijaryhma).fetch()
                .leftJoin(jono.valinnanVaihe).fetch()
                .distinct()
                .where(valinnanVaihe.oid.eq(oid)).list(jono);
    }

    @Override
    public List<Valintatapajono> findAll() {
        QValintatapajono jono = QValintatapajono.valintatapajono;
        QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
        QHakijaryhmaValintatapajono hakijaryhmaValintatapajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(jono)
                .leftJoin(jono.valinnanVaihe, valinnanVaihe).fetch()
                .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono).fetch()
                .leftJoin(jono.varasijanTayttojono).fetch()
                .leftJoin(hakijaryhmaValintatapajono.hakijaryhma).fetch()
                .leftJoin(valinnanVaihe.valintaryhma)
                .distinct().list(jono);
    }

    @Override
    public Valintatapajono readByOid(String oid) {
        QValintatapajono jono = QValintatapajono.valintatapajono;
        QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
        QHakijaryhmaValintatapajono hakijaryhmaValintatapajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(jono)
                .where(jono.oid.eq(oid))
                .leftJoin(jono.valinnanVaihe, valinnanVaihe).fetch()
                .leftJoin(jono.masterValintatapajono).fetch()
                .leftJoin(jono.edellinenValintatapajono).fetch()
                .leftJoin(jono.varasijanTayttojono).fetch()
                .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono).fetch()
                .leftJoin(hakijaryhmaValintatapajono.hakijaryhma).fetch()
                .leftJoin(valinnanVaihe.valintaryhma)
                .singleResult(jono);
    }

    @Override
    public List<Valintatapajono> haeKopiot(String oid) {
        QValintatapajono valintatapajono = QValintatapajono.valintatapajono;

        return from(valintatapajono).leftJoin(valintatapajono.valinnanVaihe).fetch()
                .leftJoin(valintatapajono.masterValintatapajono).fetch()
                .where(valintatapajono.masterValintatapajono.oid.eq(oid)).list(valintatapajono);
    }

    @Override
    public List<Valintatapajono> haeKopiotValisijoittelulle(String oid) {
        QValintatapajono valintatapajono = QValintatapajono.valintatapajono;

        return from(valintatapajono)
                .leftJoin(valintatapajono.masterValintatapajono)
                .where(valintatapajono.masterValintatapajono.oid.eq(oid))
                .where(valintatapajono.aktiivinen.eq(Boolean.TRUE))
                .where(valintatapajono.valisijoittelu.eq(Boolean.TRUE))
                .distinct()
                .list(valintatapajono);
    }

    @Override
    public List<Valintatapajono> ilmanLaskentaaOlevatHakukohteelle(String hakukohdeOid) {
        QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
        QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;

        return from(hakukohde).leftJoin(hakukohde.valinnanvaiheet, vv).leftJoin(vv.jonot, jono)
                .where(hakukohde.oid.eq(hakukohdeOid).and(jono.kaytetaanValintalaskentaa.isFalse())).distinct().list(jono);
    }

    @Override
    public List<Valintatapajono> haeValintatapajonotSijoittelulle(String hakukohdeOid) {
        QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
        QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;

        List<Valintatapajono> jonot = from(hakukohde)
                .leftJoin(hakukohde.valinnanvaiheet, vv)
                .leftJoin(vv.jonot, jono)
                .leftJoin(jono.varasijanTayttojono).fetch()
                .where(hakukohde.oid.eq(hakukohdeOid).and(jono.siirretaanSijoitteluun.isTrue()).and(vv.aktiivinen.isTrue())).distinct().list(jono);

        // BUG-255 poistetaan jonoista väärin tallentuneet täyttöjonot
        List<Long> ids = jonot.stream().map(j -> j.getId()).collect(Collectors.toList());

        jonot.forEach(j -> {
            if(j.getVarasijanTayttojono() != null && !ids.contains(j.getVarasijanTayttojono().getId())) {
                j.setVarasijanTayttojono(null);
            }
        });

        return jonot;
    }

    @Override
    public List<Valintatapajono> haeValintatapajonotHakukohteelle(String hakukohdeOid) {
        QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
        QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;

        return from(hakukohde).leftJoin(hakukohde.valinnanvaiheet, vv).leftJoin(vv.jonot, jono)
                .where(hakukohde.oid.eq(hakukohdeOid)).distinct().list(jono);
    }

    @Override
    public Valintatapajono haeValinnanVaiheenViimeinenValintatapajono(String valinnanVaiheOid) {

        QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;

        return from(valinnanVaihe)
                .leftJoin(valinnanVaihe.jonot, jono)
                .leftJoin(valinnanVaihe.valintaryhma)
                .where(jono.id.notIn(
                        subQuery().from(jono).where(jono.edellinenValintatapajono.isNotNull())
                                .list(jono.edellinenValintatapajono.id)).and(valinnanVaihe.oid.eq(valinnanVaiheOid)))
                .singleResult(jono);
    }

    @Override
    public Valintatapajono insert(Valintatapajono entity) {
        Valintatapajono insert = super.insert(entity);
        return insert;
    }
}
