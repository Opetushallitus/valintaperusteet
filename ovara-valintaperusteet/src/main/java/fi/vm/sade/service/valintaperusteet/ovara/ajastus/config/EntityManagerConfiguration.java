package fi.vm.sade.service.valintaperusteet.ovara.ajastus.config;

import fi.vm.sade.service.valintaperusteet.model.EntityManagerUtils;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityManagerConfiguration implements InitializingBean {

  @Autowired private EntityManager entityManager;

  public void afterPropertiesSet() throws Exception {
    EntityManagerUtils.setEntityManager(this.entityManager);
  }
}
