package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: wuoti Date: 8.5.2013 Time: 14.14 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class HakukohdekoodiDAOTest extends WithSpringBoot {

  @Autowired private HakukohdekoodiDAO hakukohdekoodiDAO;

  @Test
  public void testFindByKoodiUri() {
    final String koodiUri = "hakukohdekoodiuri1";
    Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(koodiUri);

    assertEquals(koodiUri, koodi.getUri());
    assertNull(hakukohdekoodiDAO.readByUri("not-exists"));
  }

  @Test
  public void testFindByHakukohdeOid() {
    final String hakukohdeOid = "oid12";
    Hakukohdekoodi hakukohdekoodi = hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid);
    assertNotNull(hakukohdekoodi);
    assertNull(hakukohdekoodiDAO.findByHakukohdeOid("not exists"));
  }
}
