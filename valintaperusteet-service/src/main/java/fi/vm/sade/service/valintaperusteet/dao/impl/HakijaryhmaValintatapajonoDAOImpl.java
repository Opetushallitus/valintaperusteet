package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QValintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HakijaryhmaValintatapajonoDAOImpl
    extends AbstractJpaDAOImpl<HakijaryhmaValintatapajono, Long>
    implements HakijaryhmaValintatapajonoDAO {
  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public HakijaryhmaValintatapajono readByOid(String oid) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    QHakijaryhma h = QHakijaryhma.hakijaryhma;
    QValintatapajono v = QValintatapajono.valintatapajono;

    return queryFactory()
        .selectFrom(hv)
        .where(hv.oid.eq(oid))
        .leftJoin(hv.hakijaryhma, h)
        .fetchJoin()
        .leftJoin(h.jonot)
        .fetchJoin()
        .leftJoin(hv.valintatapajono, v)
        .fetchJoin()
        .leftJoin(v.hakijaryhmat)
        .fetchJoin()
        .leftJoin(v.valinnanVaihe)
        .fetchJoin()
        .leftJoin(hv.master)
        .fetchJoin()
        .leftJoin(hv.edellinen)
        .fetchJoin()
        .leftJoin(hv.hakijaryhmatyyppikoodi)
        .fetchJoin()
        .fetchFirst();
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByValintatapajono(String oid) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QValintatapajono v = QValintatapajono.valintatapajono;

    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(hv)
            .join(hv.valintatapajono, v)
            .fetchJoin()
            .leftJoin(hv.hakijaryhma)
            .fetchJoin()
            .leftJoin(v.hakijaryhmat)
            .fetchJoin()
            .leftJoin(v.valinnanVaihe)
            .fetchJoin()
            .leftJoin(hv.master)
            .fetchJoin()
            .leftJoin(hv.edellinen)
            .fetchJoin()
            .leftJoin(hv.hakijaryhmatyyppikoodi)
            .fetchJoin()
            .where(v.oid.eq(oid))
            .distinct()
            .fetch());
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByValintatapajonos(List<String> oids) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhma h = QHakijaryhma.hakijaryhma;
    QValintatapajono v = QValintatapajono.valintatapajono;

    return queryFactory()
        .selectFrom(hv)
        .join(hv.valintatapajono, v)
        .fetchJoin()
        .leftJoin(hv.hakijaryhma, h)
        .fetchJoin()
        .leftJoin(h.jonot)
        .fetchJoin()
        .leftJoin(v.hakijaryhmat)
        .fetchJoin()
        .leftJoin(v.valinnanVaihe)
        .fetchJoin()
        .leftJoin(hv.master)
        .fetchJoin()
        .leftJoin(hv.edellinen)
        .fetchJoin()
        .leftJoin(hv.hakijaryhmatyyppikoodi)
        .fetchJoin()
        .where(v.oid.in(oids))
        .distinct()
        .fetch();
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByHakukohde(String oid) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(hv)
            .join(hv.hakukohdeViite, v)
            .fetchJoin()
            .leftJoin(hv.hakijaryhma)
            .fetchJoin()
            .leftJoin(v.hakijaryhmat)
            .fetchJoin()
            .leftJoin(hv.master)
            .fetchJoin()
            .leftJoin(hv.edellinen)
            .fetchJoin()
            .leftJoin(hv.hakijaryhmatyyppikoodi)
            .fetchJoin()
            .where(v.oid.eq(oid))
            .distinct()
            .fetch());
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> oids) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhma h = QHakijaryhma.hakijaryhma;
    QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

    return queryFactory()
        .selectFrom(hv)
        .join(hv.hakukohdeViite, v)
        .fetchJoin()
        .leftJoin(hv.hakijaryhma, h)
        .fetchJoin()
        .leftJoin(h.jonot)
        .fetchJoin()
        .leftJoin(v.hakijaryhmat)
        .fetchJoin()
        .leftJoin(hv.master)
        .fetchJoin()
        .leftJoin(hv.edellinen)
        .fetchJoin()
        .leftJoin(hv.hakijaryhmatyyppikoodi)
        .fetchJoin()
        .where(v.oid.in(oids))
        .distinct()
        .fetch();
  }

  @Override
  public HakijaryhmaValintatapajono haeHakukohteenViimeinenHakijaryhma(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QHakijaryhmaValintatapajono hakijaryhmajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhmaValintatapajono seuraava = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    return queryFactory()
        .selectFrom(hakijaryhmajono)
        .join(hakijaryhmajono.hakukohdeViite, hakukohde)
        .where(
            hakukohde
                .oid
                .eq(hakukohdeOid)
                .and(
                    JPAExpressions.selectFrom(seuraava)
                        .where(seuraava.edellinen.id.eq(hakijaryhmajono.id))
                        .notExists()))
        .fetchFirst();
  }

  @Override
  public HakijaryhmaValintatapajono haeValintatapajononViimeinenHakijaryhma(
      String valintatapajonoOid) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    QHakijaryhmaValintatapajono hakijaryhmajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhmaValintatapajono seuraava = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    return queryFactory()
        .selectFrom(hakijaryhmajono)
        .join(hakijaryhmajono.valintatapajono, valintatapajono)
        .where(
            valintatapajono
                .oid
                .eq(valintatapajonoOid)
                .and(
                    JPAExpressions.selectFrom(seuraava)
                        .where(seuraava.edellinen.id.eq(hakijaryhmajono.id))
                        .notExists()))
        .fetchFirst();
  }

  @Override
  public List<HakijaryhmaValintatapajono> jarjestaUudelleen(
      HakukohdeViite hakukohdeViite, List<String> uusiJarjestys) {
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(),
        queryFactory()
            .selectFrom(hakijaryhmaValintatapajono)
            .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
            .fetchJoin()
            .leftJoin(hakijaryhmaValintatapajono.master)
            .fetchJoin()
            .leftJoin(hakijaryhmaValintatapajono.edellinen)
            .fetchJoin()
            .leftJoin(hakijaryhmaValintatapajono.hakijaryhmatyyppikoodi)
            .fetchJoin()
            .where(hakijaryhmaValintatapajono.hakukohdeViite.id.eq(hakukohdeViite.getId()))
            .distinct()
            .fetch(),
        uusiJarjestys);
  }

  @Override
  public void delete(HakijaryhmaValintatapajono hakijaryhmaValintatapajono) {
    for (HakijaryhmaValintatapajono kopio : hakijaryhmaValintatapajono.getKopiot()) {
      delete(kopio);
    }
    EntityManager entityManager = getEntityManager();

    QHakijaryhmaValintatapajono seuraava = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    HakijaryhmaValintatapajono seuraavaHakijaryhmaValintatapajono =
        queryFactory()
            .selectFrom(seuraava)
            .where(seuraava.edellinen.id.eq(hakijaryhmaValintatapajono.getId()))
            .fetchFirst();

    if (seuraavaHakijaryhmaValintatapajono != null) {
      HakijaryhmaValintatapajono edellinen = hakijaryhmaValintatapajono.getEdellinen();

      if (hakijaryhmaValintatapajono.getEdellinen() == null) {
        hakijaryhmaValintatapajono.setEdellinen(hakijaryhmaValintatapajono);
        entityManager.flush();
      }

      seuraavaHakijaryhmaValintatapajono.setEdellinen(edellinen);
    }

    entityManager.remove(hakijaryhmaValintatapajono);
  }

  @Override
  public HakijaryhmaValintatapajono insert(HakijaryhmaValintatapajono uusi) {
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    HakijaryhmaValintatapajono seuraava =
        queryFactory()
            .selectFrom(hakijaryhmaValintatapajono)
            .where(
                (uusi.getHakukohdeViite() == null
                        ? hakijaryhmaValintatapajono.valintatapajono.id.eq(
                            uusi.getValintatapajono().getId())
                        : hakijaryhmaValintatapajono.hakukohdeViite.id.eq(
                            uusi.getHakukohdeViite().getId()))
                    .and(
                        uusi.getEdellinen() == null
                            ? hakijaryhmaValintatapajono.edellinen.isNull()
                            : hakijaryhmaValintatapajono.edellinen.id.eq(
                                uusi.getEdellinen().getId())))
            .fetchFirst();
    if (seuraava != null && uusi.getEdellinen() == null) {
      seuraava.setEdellinen(seuraava);
      getEntityManager().flush();
    }

    getEntityManager().persist(uusi);

    if (seuraava != null) {
      seuraava.setEdellinen(uusi);
    }

    return uusi;
  }
}
