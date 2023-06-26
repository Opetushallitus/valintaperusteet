package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohteenValintaperusteDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakukohteenValintaperuste;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HakukohteenValintaperusteDAOImpl
    extends AbstractJpaDAOImpl<HakukohteenValintaperuste, Long>
    implements HakukohteenValintaperusteDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public List<HakukohteenValintaperuste> haeHakukohteenValintaperusteet(String hakukohdeOid) {
    QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
    QHakukohteenValintaperuste vp = QHakukohteenValintaperuste.hakukohteenValintaperuste;
    HakukohdeViite viite =
        queryFactory().selectFrom(hk).where(hk.oid.eq(hakukohdeOid)).fetchFirst();
    if (viite == null) {
      return new ArrayList<>();
    }
    return queryFactory().selectFrom(vp).where(vp.hakukohde.eq(viite)).fetch();
  }

  @Override
  public List<HakukohteenValintaperuste> haeHakukohteidenValintaperusteet(
      List<String> hakukohdeOidit) {
    QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
    QHakukohteenValintaperuste vp = QHakukohteenValintaperuste.hakukohteenValintaperuste;
    List<HakukohdeViite> viite =
        queryFactory().selectFrom(hk).where(hk.oid.in(hakukohdeOidit)).fetch();
    if (viite == null || viite.isEmpty()) {
      return new ArrayList<>();
    }
    return queryFactory().selectFrom(vp).where(vp.hakukohde.in(viite)).fetch();
  }
}
