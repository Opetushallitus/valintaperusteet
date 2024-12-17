package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HakijaryhmaDAOImpl extends AbstractJpaDAOImpl<Hakijaryhma, Long>
    implements HakijaryhmaDAO {
  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public Hakijaryhma readByOid(String oid) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

    Hakijaryhma haettu =
        queryFactory()
            .selectFrom(hakijaryhma)
            .where(hakijaryhma.oid.eq(oid))
            .leftJoin(hakijaryhma.jonot, hakijaryhmaValintatapajono)
            .fetchJoin()
            .leftJoin(hakijaryhmaValintatapajono.valintatapajono)
            .fetchJoin()
            .leftJoin(hakijaryhmaValintatapajono.hakukohdeViite)
            .fetchJoin()
            .leftJoin(hakijaryhma.valintaryhma)
            .fetchJoin()
            .leftJoin(hakijaryhma.laskentakaava)
            .fetchJoin()
            .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
            .fetchJoin()
            .fetchFirst();
    return haettu;
  }

  @Override
  public List<Hakijaryhma> findByValintaryhma(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(hakijaryhma)
            .join(hakijaryhma.valintaryhma, valintaryhma)
            .fetchJoin()
            .leftJoin(hakijaryhma.laskentakaava)
            .fetchJoin()
            .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
            .fetchJoin()
            .leftJoin(hakijaryhma.edellinenHakijaryhma)
            .fetchJoin()
            .leftJoin(hakijaryhma.masterHakijaryhma)
            .fetchJoin()
            .where(valintaryhma.oid.eq(oid))
            .distinct()
            .fetch());
  }

  @Override
  public Hakijaryhma haeValintaryhmanViimeinenHakijaryhma(String valintaryhmaOid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return queryFactory()
        .select(hakijaryhma)
        .from(valintaryhma)
        .leftJoin(valintaryhma.hakijaryhmat, hakijaryhma)
        .where(valintaryhma.oid.eq(valintaryhmaOid))
        .fetchFirst();
  }

  @Override
  public List<Hakijaryhma> findByLaskentakaava(long id) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return queryFactory()
        .selectFrom(hakijaryhma)
        .leftJoin(hakijaryhma.laskentakaava)
        .where(hakijaryhma.laskentakaava.id.eq(id))
        .fetch();
  }

  @Override
  public List<Hakijaryhma> jarjestaUudelleen(
      Valintaryhma valintaryhma, List<String> uusiJarjestys) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(),
        queryFactory()
            .selectFrom(hakijaryhma)
            .leftJoin(hakijaryhma.laskentakaava)
            .fetchJoin()
            .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
            .fetchJoin()
            .leftJoin(hakijaryhma.edellinenHakijaryhma)
            .fetchJoin()
            .leftJoin(hakijaryhma.masterHakijaryhma)
            .fetchJoin()
            .where(hakijaryhma.valintaryhma.id.eq(valintaryhma.getId()))
            .distinct()
            .fetch(),
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
        queryFactory()
            .selectFrom(seuraava)
            .where(seuraava.edellinenHakijaryhma.id.eq(hakijaryhma.getId()))
            .fetchFirst();

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
            : queryFactory()
                .selectFrom(hakijaryhma)
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
