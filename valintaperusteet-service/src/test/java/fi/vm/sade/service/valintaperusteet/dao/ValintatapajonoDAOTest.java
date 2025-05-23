package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 18.1.2013 Time: 12.11 To change this template use
 * File | Settings | File Templates.
 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintatapajonoDAOTest extends WithSpringBoot {
  @Autowired ValintatapajonoDAO valintatapajonoDAO;

  @Test
  public void testFindByValinnanvaihe() {
    List<Valintatapajono> jonot = valintatapajonoDAO.findByValinnanVaihe("1");
    System.out.println(jonot.get(0).getValinnanVaihe());
    assertEquals(5, jonot.size());
  }
}
