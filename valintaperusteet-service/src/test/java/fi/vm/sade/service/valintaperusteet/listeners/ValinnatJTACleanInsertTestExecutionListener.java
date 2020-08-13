package fi.vm.sade.service.valintaperusteet.listeners;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import java.sql.Connection;
import javax.persistence.EntityManager;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.hibernate.internal.SessionImpl;
import org.springframework.core.io.Resource;
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
      SessionImpl session = (SessionImpl) entityManager.getDelegate();
      Connection jdbcConn = session.connection();
      IDatabaseConnection con = new DatabaseConnection(jdbcConn);
      if (ALL_TABLES_FILTER == null) {
        ALL_TABLES_FILTER = new DatabaseSequenceFilter(con);
      }
      new TransactionOperation(DatabaseOperation.CLEAN_INSERT)
          .execute(con, new FilteredDataSet(ALL_TABLES_FILTER, replacementDataSet));
      con.close();
    }
  }
}
