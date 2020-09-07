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
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.List;
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
}
