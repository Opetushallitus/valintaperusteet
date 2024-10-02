package fi.vm.sade.service.valintaperusteet.ovara.ajastus.impl;

import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.ovara.ajastus.SiirtotiedostoProsessi;
import fi.vm.sade.service.valintaperusteet.ovara.ajastus.SiirtotiedostoProsessiRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class SiirtotiedostoProsessiRepositoryImpl
    extends AbstractJpaDAOImpl<SiirtotiedostoProsessi, Long>
    implements SiirtotiedostoProsessiRepository {
  @PersistenceContext private EntityManager entityManager;

  private final JdbcTemplate jdbcTemplate;

  public SiirtotiedostoProsessiRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public SiirtotiedostoProsessi findLatestSuccessful() {
    String sT =
        """
                SELECT execution_uuid, window_start, window_end, run_start, run_end, cast(info as varchar), success, error_message from siirtotiedosto where success order by run_end desc limit 1""";
    Query query = entityManager.createNativeQuery(sT);
    Object[] result = (Object[]) query.getSingleResult();
    return new SiirtotiedostoProsessi(result);
  }

  @Override
  public void persist(SiirtotiedostoProsessi sp) {
    System.out.println("Persisting: " + sp);
    String infoStr = sp.getInfo() != null ? sp.getInfo().toString() : "{}";
    this.jdbcTemplate.update(
        "insert into siirtotiedosto (execution_uuid, window_start, window_end, run_start, run_end, info, success, error_message) "
            + "values (?::uuid, ?::timestamptz, ?::timestamptz, ?::timestamptz, ?::timestamptz, ?::jsonb, ?, ?)",
        sp.getExecutionUuid(),
        sp.getWindowStart(),
        sp.getWindowEnd(),
        sp.getRunStart(),
        sp.getRunEnd(),
        infoStr,
        sp.getSuccess(),
        sp.getErrorMessage());
  }
}
