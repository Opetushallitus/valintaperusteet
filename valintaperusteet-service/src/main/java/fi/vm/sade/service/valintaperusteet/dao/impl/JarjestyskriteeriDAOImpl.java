package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QJarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.QLaskentakaava;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.QValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class JarjestyskriteeriDAOImpl extends AbstractJpaDAOImpl<Jarjestyskriteeri, Long>
    implements JarjestyskriteeriDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public List<Jarjestyskriteeri> findByJono(String oid) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QValintatapajono j = QValintatapajono.valintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(jk)
            .join(jk.valintatapajono, j)
            .fetchJoin()
            .leftJoin(jk.edellinen)
            .fetchJoin()
            .leftJoin(jk.master)
            .fetchJoin()
            .leftJoin(jk.laskentakaava)
            .fetchJoin()
            .where(j.oid.eq(oid))
            .fetch());
  }

  @Override
  public List<Jarjestyskriteeri> findByHakukohde(String oid) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QValintatapajono vtj = QValintatapajono.valintatapajono;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintaryhma vr = QValintaryhma.valintaryhma;
    QHakukohdeViite hkv = QHakukohdeViite.hakukohdeViite;
    return queryFactory()
        .selectFrom(jk)
        .join(jk.valintatapajono, vtj)
        .join(vtj.valinnanVaihe, vv)
        .join(vv.hakukohdeViite, hkv)
        .leftJoin(vv.valintaryhma, vr)
        .where(hkv.oid.eq(oid))
        .distinct()
        .fetch();
  }

  @Override
  public Jarjestyskriteeri readByOid(String oid) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    return queryFactory().selectFrom(jk).where(jk.oid.eq(oid)).fetchFirst();
  }

  @Override
  public Jarjestyskriteeri haeValintatapajononViimeinenJarjestyskriteeri(
      String valintatapajonoOid) {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    return queryFactory()
        .select(jk)
        .from(jono)
        .leftJoin(jono.jarjestyskriteerit, jk)
        .where(
            jk.id
                .notIn(
                    JPAExpressions.select(jk.edellinen.id).from(jk).where(jk.edellinen.isNotNull()))
                .and(jono.oid.eq(valintatapajonoOid)))
        .fetchFirst();
  }

  @Override
  public List<Jarjestyskriteeri> findByLaskentakaava(long id) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QLaskentakaava lk = QLaskentakaava.laskentakaava;
    return queryFactory().selectFrom(jk).join(jk.laskentakaava, lk).where(lk.id.eq(id)).fetch();
  }

  private List<Jarjestyskriteeri> findByJono(Valintatapajono jono) {
    QJarjestyskriteeri jarjestyskriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    return queryFactory()
        .selectFrom(jarjestyskriteeri)
        .leftJoin(jarjestyskriteeri.edellinen)
        .fetchJoin()
        .leftJoin(jarjestyskriteeri.master)
        .fetchJoin()
        .leftJoin(jarjestyskriteeri.laskentakaava)
        .fetchJoin()
        .where(jarjestyskriteeri.valintatapajono.id.eq(jono.getId()))
        .distinct()
        .fetch();
  }

  @Override
  public List<Jarjestyskriteeri> jarjestaUudelleen(
      Valintatapajono jono, List<String> uusiJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(), findByJono(jono), uusiJarjestys);
  }

  @Override
  public List<Jarjestyskriteeri> jarjestaUudelleenMasterJarjestyksenMukaan(
      Valintatapajono jono, List<Jarjestyskriteeri> uusiMasterJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleenMasterJarjestyksenMukaan(
        getEntityManager(), findByJono(jono), uusiMasterJarjestys);
  }

  @Override
  public void delete(Jarjestyskriteeri jarjestyskriteeri) {
    for (Jarjestyskriteeri kopio : jarjestyskriteeri.getKopiot()) {
      delete(kopio);
    }
    EntityManager entityManager = getEntityManager();

    QJarjestyskriteeri seuraava = QJarjestyskriteeri.jarjestyskriteeri;
    Jarjestyskriteeri seuraavaJarjestyskriteeri =
        queryFactory()
            .selectFrom(seuraava)
            .where(seuraava.edellinen.id.eq(jarjestyskriteeri.getId()))
            .fetchFirst();

    if (seuraavaJarjestyskriteeri != null) {
      Jarjestyskriteeri edellinen = jarjestyskriteeri.getEdellinen();

      if (jarjestyskriteeri.getEdellinen() == null) {
        jarjestyskriteeri.setEdellinen(jarjestyskriteeri);
        entityManager.flush();
      }

      seuraavaJarjestyskriteeri.setEdellinen(edellinen);
    }

    entityManager.remove(jarjestyskriteeri);
  }

  @Override
  public Jarjestyskriteeri insert(Jarjestyskriteeri uusi) {
    QJarjestyskriteeri jarjestyskriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    Jarjestyskriteeri seuraava =
        queryFactory()
            .selectFrom(jarjestyskriteeri)
            .where(
                jarjestyskriteeri
                    .valintatapajono
                    .id
                    .eq(uusi.getValintatapajono().getId())
                    .and(
                        uusi.getEdellinen() == null
                            ? jarjestyskriteeri.edellinen.isNull()
                            : jarjestyskriteeri.edellinen.id.eq(uusi.getEdellinen().getId())))
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
