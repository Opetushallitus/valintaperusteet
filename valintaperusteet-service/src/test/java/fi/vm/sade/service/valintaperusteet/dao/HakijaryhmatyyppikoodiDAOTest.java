package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** Created by tuukka.palomaki on 14/09/16. */
@Transactional
@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class HakijaryhmatyyppikoodiDAOTest extends WithSpringBoot {

  @Autowired private HakijaryhmatyyppikoodiDAO hakijaryhmatyyppikoodiDAO;

  @Test
  public void readByUri() throws Exception {
    final String hakijaryhmatyyppiUri = "hakijaryhmantyypit_ensikertalaiset";
    final String tyypinNimi = "hakijaryhmantyypit_ensikertalaiset nimi";

    Hakijaryhmatyyppikoodi haettu = hakijaryhmatyyppikoodiDAO.readByUri(hakijaryhmatyyppiUri);
    assertEquals(tyypinNimi, haettu.getNimiFi());
  }

  @Test
  public void findByUris() throws Exception {
    final String[] hakijaryhmatyyppiUrit = new String[3];
    hakijaryhmatyyppiUrit[0] = "hakijaryhmantyypit_ensikertalaiset";
    hakijaryhmatyyppiUrit[1] = "hakijaryhmantyypit_muu";
    hakijaryhmatyyppiUrit[2] = "hakijaryhmantyypit_notfound";

    List<Hakijaryhmatyyppikoodi> haettu =
        hakijaryhmatyyppikoodiDAO.findByUris(hakijaryhmatyyppiUrit);
    assertEquals(2, haettu.size());
  }

  @Test
  public void insertOrUpdate() throws Exception {
    final String uri = "hakijaryhmantyypit_uusi";
    final String nimiFi = "hakijaryhmantyypit_uusi nimi";
    final String uusi_nimiFi = "hakijaryhmantyypit_uusi uusi nimi";

    Hakijaryhmatyyppikoodi uusi = new Hakijaryhmatyyppikoodi();
    uusi.setUri(uri);
    uusi.setNimiFi(nimiFi);
    uusi.setArvo("hakijaryhmantyypit_uusi arvo");

    // insert
    hakijaryhmatyyppikoodiDAO.insertOrUpdate(uusi);

    Hakijaryhmatyyppikoodi haettu = hakijaryhmatyyppikoodiDAO.readByUri(uri);
    assertEquals(haettu.getUri(), uri);
    assertEquals(haettu.getNimiFi(), nimiFi);

    // update
    haettu.setNimiFi(uusi_nimiFi);
    hakijaryhmatyyppikoodiDAO.insertOrUpdate(haettu);

    Hakijaryhmatyyppikoodi haettu_uusi = hakijaryhmatyyppikoodiDAO.readByUri(uri);
    assertEquals(haettu_uusi.getUri(), uri);
    assertEquals(haettu_uusi.getNimiFi(), uusi_nimiFi);
  }
}
