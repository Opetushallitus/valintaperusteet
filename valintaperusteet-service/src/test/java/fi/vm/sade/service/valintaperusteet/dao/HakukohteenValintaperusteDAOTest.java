package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: wuoti Date: 12.9.2013 Time: 13.53 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class HakukohteenValintaperusteDAOTest extends WithSpringBoot {

  @Autowired private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

  @Test
  public void test() {
    final String hakukohdeOid = "oid22";
    Set<String> tunnisteet =
        new HashSet<String>(
            Arrays.asList(new String[] {"tunniste1", "tunniste2", "tunniste3", "tunniste4"}));

    List<HakukohteenValintaperuste> valintaperusteet =
        hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohdeOid);
    assertEquals(tunnisteet.size(), valintaperusteet.size());
    for (HakukohteenValintaperuste vp : valintaperusteet) {
      tunnisteet.contains(vp.getTunniste());
    }
  }
}
