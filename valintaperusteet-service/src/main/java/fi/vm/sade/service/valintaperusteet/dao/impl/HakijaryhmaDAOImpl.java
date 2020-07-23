package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.util.List;
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
  public List<Hakijaryhma> findByValintatapajono(String oid) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;

    return from(hakijaryhma)
        .where(hakijaryhma.jonot.any().valintatapajono.oid.eq(oid))
        .leftJoin(hakijaryhma.jonot)
        .fetch()
        .leftJoin(hakijaryhma.valintaryhma)
        .fetch()
        .leftJoin(hakijaryhma.laskentakaava)
        .fetch()
        .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
        .fetch()
        .listDistinct(hakijaryhma);
  }

  @Override
  public List<Hakijaryhma> findByHakukohde(String oid) {
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;

    return from(hakijaryhma)
        .where(hakijaryhma.jonot.any().hakukohdeViite.oid.eq(oid))
        .leftJoin(hakijaryhma.jonot)
        .fetch()
        .leftJoin(hakijaryhma.valintaryhma)
        .fetch()
        .leftJoin(hakijaryhma.laskentakaava)
        .fetch()
        .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
        .fetch()
        .listDistinct(hakijaryhma);
  }

  @Override
  public List<Hakijaryhma> findByValintaryhma(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QHakijaryhma hakijaryhma = QHakijaryhma.hakijaryhma;
    return from(valintaryhma)
        .leftJoin(valintaryhma.hakijaryhmat, hakijaryhma)
        .leftJoin(hakijaryhma.valintaryhma)
        .fetch()
        .leftJoin(hakijaryhma.laskentakaava)
        .fetch()
        .leftJoin(hakijaryhma.hakijaryhmatyyppikoodi)
        .fetch()
        .leftJoin(hakijaryhma.seuraavaHakijaryhma)
        .fetch()
        .leftJoin(hakijaryhma.masterHakijaryhma)
        .fetch()
        .where(valintaryhma.oid.eq(oid))
        .list(hakijaryhma);
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
}
