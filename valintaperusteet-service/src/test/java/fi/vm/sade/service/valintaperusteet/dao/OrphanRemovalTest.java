package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: kwuoti Date: 18.1.2013 Time: 10.04 */
@DataSetLocation("classpath:orphan-test-data.xml")
@Transactional
public class OrphanRemovalTest extends WithSpringBoot {

  @Autowired private FunktiokutsuDAO funktiokutsuDAO;

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private GenericDAO dao;

  @Test
  public void testDeleteOrphans() {
    assertNotNull(funktiokutsuDAO.getFunktiokutsu(9L));
    assertEquals(1, funktiokutsuDAO.deleteOrphans());
    assertNull(funktiokutsuDAO.getFunktiokutsu(9L));
    assertNotNull(laskentakaavaDAO.getLaskentakaava(1L));
    assertNotNull(laskentakaavaDAO.getLaskentakaava(2L));
    assertNotNull(funktiokutsuDAO.getFunktiokutsu(2L));
  }
}
