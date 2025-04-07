package fi.vm.sade.service.valintaperusteet.ovara.ajastus.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Profile("ovara")
public class DatabaseConfiguration {
  @Bean
  public HikariConfig hikariConfig(
      @Value("${valintaperusteet.postgresql.maxactive}") final String maxPoolSize,
      @Value("${valintaperusteet.postgresql.maxwait}") final String maxWait,
      @Value("${valintaperusteet.postgresql.leakdetectionthresholdmillis}")
          final String leaksThreshold,
      @Value("${valintaperusteet.postgresql.url}") final String url,
      @Value("${valintaperusteet.postgresql.user}") final String user,
      @Value("${valintaperusteet.postgresql.password}") final String password) {
    final HikariConfig config = new HikariConfig();
    config.setPoolName("springHikariCP");
    config.setConnectionTestQuery("SELECT 1");
    config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
    config.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
    config.setMaxLifetime(Long.parseLong(maxWait));
    config.setLeakDetectionThreshold(Long.parseLong(leaksThreshold));
    config.setRegisterMbeans(false);
    final Properties dsProperties = new Properties();
    dsProperties.setProperty("url", url);
    dsProperties.setProperty("user", user);
    dsProperties.setProperty("password", password);
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
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      final DataSource dataSource,
      @Value("${jpa.show-sql}") final String showSql,
      @Value("${jpa.schema-update}") final String schemaUpdate) {
    final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    // emf.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
    emf.setDataSource(dataSource);
    emf.setJpaPropertyMap(
        Map.of(
            "hibernate.show_sql", showSql,
            "hibernate.hbm2ddl.auto", schemaUpdate,
            "hibernate.jdbc.lob.non_contextual_creation", "true"));
    return emf;
  }

  @Bean
  public JpaTransactionManager transactionManager(final EntityManagerFactory emf) {
    final JpaTransactionManager manager = new JpaTransactionManager();
    manager.setEntityManagerFactory(emf);
    return manager;
  }
}
