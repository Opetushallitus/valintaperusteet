package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: wuoti Date: 24.6.2013 Time: 12.58 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintakoekoodiDAOTest extends WithSpringBoot {

  @Autowired private ValintakoekoodiDAO valintakoekoodiDAO;

  @Test
  public void testFindByValintaryhma() {
    final String valintaryhmaOid = "oid49";
    final String valintakoekoodiUri = "valintakoeuri1";

    Set<Valintakoekoodi> koodit = valintakoekoodiDAO.findByValintaryhma(valintaryhmaOid);
    assertEquals(1, koodit.size());
    for (Valintakoekoodi koodi : koodit) {
      assertEquals(valintakoekoodiUri, koodi.getUri());
    }

    assertEquals(0, valintakoekoodiDAO.findByValintaryhma("eioleolemassa").size());
  }
}
