package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: kwuoti Date: 18.1.2013 Time: 10.04 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class FunktiokutsuDAOTest extends WithSpringBoot {

  @Autowired private FunktiokutsuDAO funktiokutsuDAO;

  @Autowired private GenericDAO dao;

  @Test
  public void testFindFunktiokutsuByHakukohdeOids() throws Exception {
    List<Funktiokutsu> kaavat = funktiokutsuDAO.findFunktiokutsuByHakukohdeOid("oid17");
    Thread.sleep(5000);
    assertEquals(2, kaavat.size());
  }
}
