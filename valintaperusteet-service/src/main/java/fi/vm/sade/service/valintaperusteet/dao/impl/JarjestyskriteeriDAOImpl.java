package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
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

  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  protected JPASubQuery subQuery() {
    return new JPASubQuery();
  }

  @Override
  public List<Jarjestyskriteeri> findByJono(String oid) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QValintatapajono j = QValintatapajono.valintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(jk)
            .join(jk.valintatapajono, j)
            .fetch()
            .leftJoin(jk.edellinen)
            .fetch()
            .leftJoin(jk.master)
            .fetch()
            .leftJoin(jk.laskentakaava)
            .fetch()
            .where(j.oid.eq(oid))
            .list(jk));
  }

  @Override
  public List<Jarjestyskriteeri> findByHakukohde(String oid) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QValintatapajono vtj = QValintatapajono.valintatapajono;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintaryhma vr = QValintaryhma.valintaryhma;
    QHakukohdeViite hkv = QHakukohdeViite.hakukohdeViite;
    return from(jk)
        .join(jk.valintatapajono, vtj)
        .join(vtj.valinnanVaihe, vv)
        .join(vv.hakukohdeViite, hkv)
        .leftJoin(vv.valintaryhma, vr)
        .where(hkv.oid.eq(oid))
        .distinct()
        .list(jk);
  }

  @Override
  public Jarjestyskriteeri readByOid(String oid) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    return from(jk).where(jk.oid.eq(oid)).singleResult(jk);
  }

  @Override
  public Jarjestyskriteeri haeValintatapajononViimeinenJarjestyskriteeri(
      String valintatapajonoOid) {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    return from(jono)
        .leftJoin(jono.jarjestyskriteerit, jk)
        .where(
            jk.id
                .notIn(subQuery().from(jk).where(jk.edellinen.isNotNull()).list(jk.edellinen.id))
                .and(jono.oid.eq(valintatapajonoOid)))
        .singleResult(jk);
  }

  @Override
  public List<Jarjestyskriteeri> findByLaskentakaava(long id) {
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QLaskentakaava lk = QLaskentakaava.laskentakaava;
    return from(jk).join(jk.laskentakaava, lk).where(lk.id.eq(id)).list(jk);
  }

  private List<Jarjestyskriteeri> findByJono(Valintatapajono jono) {
    QJarjestyskriteeri jarjestyskriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    return from(jarjestyskriteeri)
        .leftJoin(jarjestyskriteeri.edellinen)
        .fetch()
        .leftJoin(jarjestyskriteeri.master)
        .fetch()
        .leftJoin(jarjestyskriteeri.laskentakaava)
        .fetch()
        .where(jarjestyskriteeri.valintatapajono.id.eq(jono.getId()))
        .distinct()
        .list(jarjestyskriteeri);
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
        from(seuraava)
            .where(seuraava.edellinen.id.eq(jarjestyskriteeri.getId()))
            .singleResult(seuraava);

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
        from(jarjestyskriteeri)
            .where(
                jarjestyskriteeri
                    .valintatapajono
                    .id
                    .eq(uusi.getValintatapajono().getId())
                    .and(
                        uusi.getEdellinen() == null
                            ? jarjestyskriteeri.edellinen.isNull()
                            : jarjestyskriteeri.edellinen.id.eq(uusi.getEdellinen().getId())))
            .singleResult(jarjestyskriteeri);
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
