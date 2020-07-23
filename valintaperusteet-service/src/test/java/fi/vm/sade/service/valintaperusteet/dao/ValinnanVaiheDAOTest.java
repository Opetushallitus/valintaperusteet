package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
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

/** User: kwuoti Date: 18.1.2013 Time: 10.04 */
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
public class ValinnanVaiheDAOTest {

  @Autowired private ValinnanVaiheDAO valinnanVaiheDAO;

  @Test
  public void testHaeValintaryhmanViimeinenValinnanVaihe() {

    final String valintaryhmaOid = "oid6";
    ValinnanVaihe valinnanVaihe =
        valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(valintaryhmaOid);
    int i = 0;
    while (valinnanVaihe != null) {
      ++i;
      assertEquals(valintaryhmaOid, valinnanVaihe.getValintaryhma().getOid());
      valinnanVaihe = valinnanVaihe.getEdellinenValinnanVaihe();
    }

    assertEquals(5, i);
  }

  @Test
  public void testFindByValintaryhma() {
    List<ValinnanVaihe> oid1 = valinnanVaiheDAO.findByValintaryhma("oid2");
    assertEquals(1, oid1.size());
  }

  @Test
  public void testFindValinnanvaihe() {
    ValinnanVaihe valinnanVaihe = valinnanVaiheDAO.readByOid("32");
    assertEquals(valinnanVaihe.getMaster().getId().longValue(), 20L);
  }

  @Test
  public void testFindByHakukohde() {
    final String hakukohdeOid = "oid12";
    List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
    assertEquals(2, vaiheet.size());
  }
}
