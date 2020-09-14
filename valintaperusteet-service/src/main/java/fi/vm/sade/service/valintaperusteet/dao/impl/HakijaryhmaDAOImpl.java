package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class HakijaryhmaDAOImpl extends AbstractJpaDAOImpl<Hakijaryhma, Long>
    implements HakijaryhmaDAO {
  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  protected JPASubQuery subQuery() {
    return new JPASubQuery();
  }

  @Override
  public Hakijaryhma readByOid(String oid) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    Hakijaryhma haettu =
        from(hakijaryhma)
            .where(hakijaryhma.oid.eq(oid))
            .leftJoin(hakijaryhma.jonot, hakijaryhmaValintatapajono)
            .fetch()
            .leftJoin(hakijaryhmaValintatapajono.valintatapajono)
            .fetch()
            .leftJoin(hakijaryhmaValintatapajono.hakukohdeViite)
            .fetch()
            .leftJoin(hakijaryhma.valintaryhma)
            .fetch()
            .leftJoin(hakijaryhma.laskentakaava)
            .fetch()
            .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
            .fetch()
            .singleResult(hakijaryhma);
    return haettu;
  }

  @Override
  public List<Hakijaryhma> findByValintaryhma(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(hakijaryhma)
            .join(hakijaryhma.valintaryhma, valintaryhma)
            .fetch()
            .leftJoin(hakijaryhma.laskentakaava)
            .fetch()
            .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
            .fetch()
            .leftJoin(hakijaryhma.edellinenHakijaryhma)
            .fetch()
            .leftJoin(hakijaryhma.masterHakijaryhma)
            .fetch()
            .where(valintaryhma.oid.eq(oid))
            .distinct()
            .list(hakijaryhma));
  }

  @Override
  public Hakijaryhma haeValintaryhmanViimeinenHakijaryhma(String valintaryhmaOid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    Hakijaryhma lastValinnanVaihe =
        from(valintaryhma)
            .leftJoin(valintaryhma.hakijaryhmat, hakijaryhma)
            .where(valintaryhma.oid.eq(valintaryhmaOid))
            .singleResult(hakijaryhma);
    return lastValinnanVaihe;
  }

  @Override
  public List<Hakijaryhma> findByLaskentakaava(long id) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return from(hakijaryhma)
        .leftJoin(hakijaryhma.laskentakaava)
        .where(hakijaryhma.laskentakaava.id.eq(id))
        .list(hakijaryhma);
  }

  @Override
  public List<Hakijaryhma> jarjestaUudelleen(
      Valintaryhma valintaryhma, List<String> uusiJarjestys) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(),
        from(hakijaryhma)
            .leftJoin(hakijaryhma.laskentakaava)
            .fetch()
            .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
            .fetch()
            .leftJoin(hakijaryhma.edellinenHakijaryhma)
            .fetch()
            .leftJoin(hakijaryhma.masterHakijaryhma)
            .fetch()
            .where(hakijaryhma.valintaryhma.id.eq(valintaryhma.getId()))
            .distinct()
            .list(hakijaryhma),
        uusiJarjestys);
  }

  @Override
  public void delete(Hakijaryhma hakijaryhma) {
    for (Hakijaryhma kopio : hakijaryhma.getKopiot()) {
      delete(kopio);
    }

    EntityManager entityManager = getEntityManager();

    QHakijaryhma seuraava = QHakijaryhma.hakijaryhma;
    Hakijaryhma seuraavaHakijaryhma =
        from(seuraava)
            .where(seuraava.edellinenHakijaryhma.id.eq(hakijaryhma.getId()))
            .singleResult(seuraava);

    if (seuraavaHakijaryhma != null) {
      Hakijaryhma edellinen = hakijaryhma.getEdellinen();

      if (hakijaryhma.getEdellinen() == null) {
        hakijaryhma.setEdellinen(hakijaryhma);
        entityManager.flush();
      }

      seuraavaHakijaryhma.setEdellinen(edellinen);
    }

    entityManager.remove(hakijaryhma);
  }

  @Override
  public Hakijaryhma insert(Hakijaryhma uusi) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    Hakijaryhma seuraava =
        uusi.getValintaryhma() == null
            ? null
            : from(hakijaryhma)
                .where(
                    hakijaryhma
                        .valintaryhma
                        .id
                        .eq(uusi.getValintaryhma().getId())
                        .and(
                            uusi.getEdellinen() == null
                                ? hakijaryhma.edellinenHakijaryhma.isNull()
                                : hakijaryhma.edellinenHakijaryhma.id.eq(
                                    uusi.getEdellinen().getId())))
                .singleResult(hakijaryhma);
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
