package fi.vm.sade.service.valintaperusteet.listeners;

import static org.dbunit.database.DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS;
import static org.dbunit.database.DatabaseConfig.PROPERTY_DATATYPE_FACTORY;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.util.Collections;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.hibernate.Session;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/** Created by kjsaila on 26/06/14. */
public class ValinnatJTACleanInsertTestExecutionListener
    extends TransactionalTestExecutionListener {
  private static DatabaseSequenceFilter ALL_TABLES_FILTER;

  public void beforeTestMethod(TestContext testContext) throws Exception {
    super.beforeTestMethod(testContext);

    // location of the data set
    String dataSetResourcePath = null;

    // first, the annotation on the test class
    DataSetLocation dsLocation =
        testContext.getTestInstance().getClass().getAnnotation(DataSetLocation.class);

    if (dsLocation != null) {
      // found the annotation
      dataSetResourcePath = dsLocation.value();
    }

    if (dataSetResourcePath != null) {
      Resource dataSetResource =
          testContext.getApplicationContext().getResource(dataSetResourcePath);
      FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
      flatXmlDataSetBuilder.setColumnSensing(true);
      IDataSet dataSet = flatXmlDataSetBuilder.build(dataSetResource.getInputStream());
      ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
      replacementDataSet.addReplacementObject("[NULL]", null);

      LocalContainerEntityManagerFactoryBean emf =
          testContext
              .getApplicationContext()
              .getBean(org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.class);

      EntityManager entityManager = emf.getObject().createEntityManager();

      Session session = entityManager.unwrap(Session.class);
      session.doWork(
          jdbcConn -> {
            try {
              IDatabaseConnection con = new DatabaseConnection(jdbcConn);
              con.getConfig()
                  .setProperty(PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
              con.getConfig().setProperty(FEATURE_ALLOW_EMPTY_FIELDS, true);
              if (ALL_TABLES_FILTER == null) {
                ALL_TABLES_FILTER = new DatabaseSequenceFilter(con);
              }

              kasvataSekvenssiaTormaystenEstamiseksi(jdbcConn);

              new TransactionOperation(DatabaseOperation.CLEAN_INSERT)
                  .execute(con, new FilteredDataSet(ALL_TABLES_FILTER, replacementDataSet));
              con.close();
            } catch (DatabaseUnitException due) {
              throw new RuntimeException(due);
            }
          });
    }
  }

  private void kasvataSekvenssiaTormaystenEstamiseksi(Connection jdbcConn) {
    final NamedParameterJdbcTemplate jdbcTemplate = luoJdbcTemplate(jdbcConn);
    jdbcTemplate.update(
        "alter sequence hibernate_sequence increment by 10000 ", Collections.emptyMap());
    jdbcTemplate.queryForObject(
        "select nextval('hibernate_sequence')", Collections.emptyMap(), Long.class);
  }

  private NamedParameterJdbcTemplate luoJdbcTemplate(Connection jdbcConn) {
    return new NamedParameterJdbcTemplate(new SingleConnectionDataSource(jdbcConn, true));
  }
}
