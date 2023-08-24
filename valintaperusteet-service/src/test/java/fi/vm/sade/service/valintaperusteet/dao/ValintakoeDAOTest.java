package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: kwuoti Date: 15.4.2013 Time: 16.33 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintakoeDAOTest extends WithSpringBoot {

  @Autowired private ValintakoeDAO valintakoeDAO;

  @Test
  public void testFindValintakoeByValinnanVaihe() {
    final String valinnanVaiheOid = "83";

    List<Valintakoe> valintakokeet = valintakoeDAO.findByValinnanVaihe(valinnanVaiheOid);
    assertEquals(5, valintakokeet.size());

    Collections.sort(
        valintakokeet,
        new Comparator<Valintakoe>() {
          @Override
          public int compare(Valintakoe o1, Valintakoe o2) {
            return o1.getOid().compareTo(o2.getOid());
          }
        });

    assertEquals("oid1", valintakokeet.get(0).getOid());
    assertEquals("oid17", valintakokeet.get(1).getOid());
    assertEquals("oid2", valintakokeet.get(2).getOid());
    assertEquals("oid3", valintakokeet.get(3).getOid());
    assertEquals("oid4", valintakokeet.get(4).getOid());
  }
}
