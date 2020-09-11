package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QValintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class HakijaryhmaValintatapajonoDAOImpl
    extends AbstractJpaDAOImpl<HakijaryhmaValintatapajono, Long>
    implements HakijaryhmaValintatapajonoDAO {
  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  private JPASubQuery subQuery() {
    return new JPASubQuery();
  }

  @Override
  public HakijaryhmaValintatapajono readByOid(String oid) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    QHakijaryhma h = QHakijaryhma.hakijaryhma;
    QValintatapajono v = QValintatapajono.valintatapajono;

    return from(hv)
        .where(hv.oid.eq(oid))
        .leftJoin(hv.hakijaryhma, h)
        .fetch()
        .leftJoin(h.jonot)
        .fetch()
        .leftJoin(hv.valintatapajono, v)
        .fetch()
        .leftJoin(v.hakijaryhmat)
        .fetch()
        .leftJoin(v.valinnanVaihe)
        .fetch()
        .leftJoin(hv.master)
        .fetch()
        .leftJoin(hv.edellinen)
        .fetch()
        .leftJoin(hv.hakijaryhmatyyppikoodi)
        .fetch()
        .singleResult(hv);
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByValintatapajono(String oid) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QValintatapajono v = QValintatapajono.valintatapajono;

    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(hv)
            .join(hv.valintatapajono, v)
            .fetch()
            .leftJoin(hv.hakijaryhma)
            .fetch()
            .leftJoin(v.hakijaryhmat)
            .fetch()
            .leftJoin(v.valinnanVaihe)
            .fetch()
            .leftJoin(hv.master)
            .fetch()
            .leftJoin(hv.edellinen)
            .fetch()
            .leftJoin(hv.hakijaryhmatyyppikoodi)
            .fetch()
            .where(v.oid.eq(oid))
            .distinct()
            .list(hv));
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByValintatapajonos(List<String> oids) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhma h = QHakijaryhma.hakijaryhma;
    QValintatapajono v = QValintatapajono.valintatapajono;

    return from(hv)
        .join(hv.valintatapajono, v)
        .fetch()
        .leftJoin(hv.hakijaryhma, h)
        .fetch()
        .leftJoin(h.jonot)
        .fetch()
        .leftJoin(v.hakijaryhmat)
        .fetch()
        .leftJoin(v.valinnanVaihe)
        .fetch()
        .leftJoin(hv.master)
        .fetch()
        .leftJoin(hv.edellinen)
        .fetch()
        .leftJoin(hv.hakijaryhmatyyppikoodi)
        .fetch()
        .where(v.oid.in(oids))
        .distinct()
        .list(hv);
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByHakukohde(String oid) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(hv)
            .join(hv.hakukohdeViite, v)
            .fetch()
            .leftJoin(hv.hakijaryhma)
            .fetch()
            .leftJoin(v.hakijaryhmat)
            .fetch()
            .leftJoin(hv.master)
            .fetch()
            .leftJoin(hv.edellinen)
            .fetch()
            .leftJoin(hv.hakijaryhmatyyppikoodi)
            .fetch()
            .where(v.oid.eq(oid))
            .distinct()
            .list(hv));
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> oids) {
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhma h = QHakijaryhma.hakijaryhma;
    QHakukohdeViite v = QHakukohdeViite.hakukohdeViite;

    return from(hv)
        .join(hv.hakukohdeViite, v)
        .fetch()
        .leftJoin(hv.hakijaryhma, h)
        .fetch()
        .leftJoin(h.jonot)
        .fetch()
        .leftJoin(v.hakijaryhmat)
        .fetch()
        .leftJoin(hv.master)
        .fetch()
        .leftJoin(hv.edellinen)
        .fetch()
        .leftJoin(hv.hakijaryhmatyyppikoodi)
        .fetch()
        .where(v.oid.in(oids))
        .distinct()
        .list(hv);
  }

  @Override
  public HakijaryhmaValintatapajono haeHakukohteenViimeinenHakijaryhma(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QHakijaryhmaValintatapajono hakijaryhmajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhmaValintatapajono seuraava = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    return from(hakijaryhmajono)
        .join(hakijaryhmajono.hakukohdeViite, hakukohde)
        .where(
            hakukohde
                .oid
                .eq(hakukohdeOid)
                .and(
                    subQuery()
                        .from(seuraava)
                        .where(seuraava.edellinen.id.eq(hakijaryhmajono.id))
                        .notExists()))
        .singleResult(hakijaryhmajono);
  }

  @Override
  public HakijaryhmaValintatapajono haeValintatapajononViimeinenHakijaryhma(
      String valintatapajonoOid) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    QHakijaryhmaValintatapajono hakijaryhmajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    QHakijaryhmaValintatapajono seuraava = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    return from(hakijaryhmajono)
        .join(hakijaryhmajono.valintatapajono, valintatapajono)
        .where(
            valintatapajono
                .oid
                .eq(valintatapajonoOid)
                .and(
                    subQuery()
                        .from(seuraava)
                        .where(seuraava.edellinen.id.eq(hakijaryhmajono.id))
                        .notExists()))
        .singleResult(hakijaryhmajono);
  }

  @Override
  public List<HakijaryhmaValintatapajono> jarjestaUudelleen(
      HakukohdeViite hakukohdeViite, List<String> uusiJarjestys) {
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(),
        from(hakijaryhmaValintatapajono)
            .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
            .fetch()
            .leftJoin(hakijaryhmaValintatapajono.master)
            .fetch()
            .leftJoin(hakijaryhmaValintatapajono.edellinen)
            .fetch()
            .leftJoin(hakijaryhmaValintatapajono.hakijaryhmatyyppikoodi)
            .fetch()
            .where(hakijaryhmaValintatapajono.hakukohdeViite.id.eq(hakukohdeViite.getId()))
            .distinct()
            .list(hakijaryhmaValintatapajono),
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
        from(seuraava)
            .where(seuraava.edellinen.id.eq(hakijaryhmaValintatapajono.getId()))
            .singleResult(seuraava);

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
        from(hakijaryhmaValintatapajono)
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
            .singleResult(hakijaryhmaValintatapajono);
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
