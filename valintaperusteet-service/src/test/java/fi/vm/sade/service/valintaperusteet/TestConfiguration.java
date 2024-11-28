package fi.vm.sade.service.valintaperusteet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.vm.sade.service.valintaperusteet.laskenta.api.LaskentaService;
import fi.vm.sade.service.valintaperusteet.laskenta.api.LaskentaServiceImpl;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"fi.vm.sade.service.valintaperusteet"})
@EnableTransactionManagement
public class TestConfiguration {
  @Bean
  public LaskentaService laskentaService() {
    return new LaskentaServiceImpl();
  }

  @Bean
  public HikariConfig hikariConfig(
      @Value(
              "jdbc:postgresql://localhost:#{systemProperties['testikannanPortti'] ?: 4320}/valintaperusteet_tests")
          final String url) {
    final HikariConfig config = new HikariConfig();
    config.setPoolName("springHikariCP");
    config.setConnectionTestQuery("SELECT 1");
    config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
    config.setMaximumPoolSize(10);
    config.setConnectionTimeout(3000);
    config.setMaxLifetime(3000);
    config.setLeakDetectionThreshold(60000);
    config.setRegisterMbeans(false);
    final Properties dsProperties = new Properties();
    dsProperties.setProperty("url", url);
    dsProperties.setProperty("user", "valintaperusteet_tests");
    dsProperties.setProperty("password", "testikannan_salasana");
    config.setDataSourceProperties(dsProperties);
    return config;
  }

  @Bean
  public DataSource dataSource(final HikariConfig hikariConfig) {
    return new HikariDataSource(hikariConfig);
  }

  @Bean("flyway")
  public Flyway flyway(final DataSource dataSource) {
    final Flyway flyway = new Flyway();
    final LazyConnectionDataSourceProxy dataSourceProxy = new LazyConnectionDataSourceProxy();
    dataSourceProxy.setTargetDataSource(dataSource);
    flyway.setDataSource(dataSourceProxy);
    flyway.migrate();
    return flyway;
  }

  @Bean
  @DependsOn("flyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
    final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    // emf.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
    emf.setDataSource(dataSource);
    emf.setJpaPropertyMap(
        Map.of(
            "hibernate.show_sql", "false",
            "hibernate.jdbc.lob.non_contextual_creation", "true"));
    emf.setPersistenceXmlLocation("classpath:META-INF/test-persistence.xml");
    return emf;
  }

  @Bean
  public JpaTransactionManager transactionManager(final EntityManagerFactory emf) {
    final JpaTransactionManager manager = new JpaTransactionManager();
    manager.setEntityManagerFactory(emf);
    return manager;
  }
}
