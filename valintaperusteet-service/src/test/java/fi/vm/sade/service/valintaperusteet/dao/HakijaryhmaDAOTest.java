package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class HakijaryhmaDAOTest extends WithSpringBoot {

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
