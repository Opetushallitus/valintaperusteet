package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeViiteDAOTest extends WithSpringBoot {

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Test
  public void testFindAll() {
    List<HakukohdeViite> hakukohdeViites = hakukohdeViiteDAO.findAll();
    assertEquals(33, hakukohdeViites.size());
  }
}
