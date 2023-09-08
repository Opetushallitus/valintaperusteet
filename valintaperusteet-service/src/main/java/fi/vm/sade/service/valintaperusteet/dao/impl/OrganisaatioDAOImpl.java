package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.OrganisaatioDAO;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;
import fi.vm.sade.service.valintaperusteet.model.QOrganisaatio;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class OrganisaatioDAOImpl extends AbstractJpaDAOImpl<Organisaatio, Long>
    implements OrganisaatioDAO {
  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public Organisaatio readByOid(String oid) {
    QOrganisaatio o = QOrganisaatio.organisaatio;
    return queryFactory().selectFrom(o).where(o.oid.eq(oid)).fetchFirst();
  }

  @Override
  public List<Organisaatio> readByOidList(Set<String> oids) {
    QOrganisaatio organisaatio = QOrganisaatio.organisaatio;
    return queryFactory().selectFrom(organisaatio).where(organisaatio.oid.in(oids)).fetch();
  }
}
