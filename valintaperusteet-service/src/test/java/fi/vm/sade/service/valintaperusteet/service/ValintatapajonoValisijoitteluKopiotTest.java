package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 15.14 To change this template use
 * File | Settings | File Templates.
 */
@DataSetLocation("classpath:test-data-valisijoittelu.xml")
public class ValintatapajonoValisijoitteluKopiotTest extends WithSpringBoot {
  @Autowired private ValintatapajonoService valintatapajonoService;

  @Autowired private ValinnanVaiheDAO valinnanVaiheDAO;

  @Autowired private ValintatapajonoDAO valintatapajonoDAO;

  @Test
  public void testValintaryhmanKopiot() throws Exception {
    Map<String, List<String>> kopiot = valintatapajonoService.findKopiot(Arrays.asList("3"));

    assertEquals(1, kopiot.get("oid2").size());
    assertEquals("3", kopiot.get("oid2").get(0));
  }

  @Test
  public void testHakukohteenKopiot() throws Exception {
    Map<String, List<String>> kopiot = valintatapajonoService.findKopiot(Arrays.asList("1"));

    assertEquals(1, kopiot.get("oid1").size());
    assertEquals("1", kopiot.get("oid1").get(0));
  }
}
