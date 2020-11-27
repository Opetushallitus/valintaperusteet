package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
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
@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class HakijaryhmaDAOTest {

  @Autowired private HakijaryhmaDAO hakijaryhmaDAO;

  @Test
  public void testReadByOid() {
    final String HAKIJARYHMA_NIMI = "hakijaryhma 1";
    final String HAKIJARYHMA_OID = "hr1";

    Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(HAKIJARYHMA_OID);
    assertEquals(HAKIJARYHMA_NIMI, hakijaryhma.getNimi());
  }

  @Test
  public void testFindByValintaryhma() {
    final String VALINTARYHMA_OID = "vr1";
    final String HAKIJARYHMA_OID = "hr1";

    List<Hakijaryhma> byValintaryhma = hakijaryhmaDAO.findByValintaryhma(VALINTARYHMA_OID);

    assertEquals(HAKIJARYHMA_OID, byValintaryhma.get(0).getOid());
  }

  @Test
  public void testHaeValintaryhmanViimeinenHakijaryhma() {
    final String VALINTARYHMA_OID = "vr1";
    final String HAKIJARYHMA_OID = "hr1";

    Hakijaryhma byValintaryhma =
        hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(VALINTARYHMA_OID);

    assertEquals(HAKIJARYHMA_OID, byValintaryhma.getOid());
  }
}
