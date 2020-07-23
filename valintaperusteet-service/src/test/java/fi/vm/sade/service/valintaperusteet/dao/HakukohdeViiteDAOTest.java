package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(
    listeners = {
      ValinnatJTACleanInsertTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class
    })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeViiteDAOTest {

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Test
  public void testFindAll() {
    List<HakukohdeViite> hakukohdeViites = hakukohdeViiteDAO.findAll();
    assertEquals(32, hakukohdeViites.size());
  }
}
