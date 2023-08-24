package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** User: wuoti Date: 16.5.2013 Time: 12.11 */
@DataSetLocation("classpath:test-data.xml")
public class HakukohdekoodiServiceTest extends WithSpringBoot {

  @Autowired private HakukohdekoodiService hakukohdekoodiService;

  @Autowired private HakukohdekoodiDAO hakukohdekoodiDAO;

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  private KoodiDTO luoHakukohdekoodi(String uri, String arvo, String nimi) {
    KoodiDTO koodi = new KoodiDTO();
    koodi.setUri(uri);
    koodi.setArvo(arvo);
    koodi.setNimiFi(nimi);
    koodi.setNimiSv(nimi);
    koodi.setNimiEn(nimi);

    return koodi;
  }

  @Test
  public void testLisaaHakukohdekoodiValintaryhmalle() {
    final String valintaryhmaOid = "oid43";
    final String hakukohdekoodiUri = "eiolevielaolemassa";

    KoodiDTO koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

    assertNull(hakukohdekoodiDAO.readByUri(hakukohdekoodiUri));
    hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(valintaryhmaOid, koodi);

    Hakukohdekoodi haettu = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
    List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
    assertEquals(1, valintaryhmas.size());
    assertEquals(valintaryhmaOid, valintaryhmas.get(0).getOid());
  }

  private boolean tarkastaEttaValintaryhmallaOnKaikkiHakukohdekoodit(
      Valintaryhma valintaryhma, String... hakukohdekoodiUrit) {
    outer:
    for (String uri : hakukohdekoodiUrit) {
      for (Hakukohdekoodi k : valintaryhma.getHakukohdekoodit()) {
        if (uri.equals(k.getUri())) {
          continue outer;
        }
      }
      return false;
    }

    return true;
  }

  @Test
  public void testPaivitaValintaryhmanHakukohdekoodit() {
    final String valintaryhmaOid = "oid51";
    final String[] hakukohdekoodiUritAluksi =
        new String[] {"hakukohdekoodiuri16", "hakukohdekoodiuri17"};
    final String[] hakukohdekoodiUritLopuksi =
        new String[] {"hakukohdekoodiuri16", "hakukohdekoodiuri18", "aivanuusi"};

    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    assertTrue(
        tarkastaEttaValintaryhmallaOnKaikkiHakukohdekoodit(valintaryhma, hakukohdekoodiUritAluksi));

    Set<KoodiDTO> paivitys = new HashSet<KoodiDTO>();

    for (String uri : hakukohdekoodiUritLopuksi) {
      paivitys.add(luoHakukohdekoodi(uri, uri, uri));
    }

    hakukohdekoodiService.updateValintaryhmaHakukohdekoodit(valintaryhmaOid, paivitys);

    valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    assertTrue(
        tarkastaEttaValintaryhmallaOnKaikkiHakukohdekoodit(
            valintaryhma, hakukohdekoodiUritLopuksi));
  }

  @Test
  public void testLisaaUusiHakukohdekoodiHakukohteelle() {
    final String hakukohdeOid = "oid15";
    final String hakukohdekoodiUri = "eiolevielaolemassa";

    KoodiDTO koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

    assertNull(hakukohdekoodiDAO.readByUri(hakukohdekoodiUri));
    assertNull(hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid));
    hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, koodi);

    Hakukohdekoodi haettu = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);

    List<HakukohdeViite> hakukohdeViites =
        hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
    assertEquals(1, hakukohdeViites.size());
    assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());
  }

  @Test
  public void testLisaaOlemassaOlevaHakukohdekoodiHakukohteelle() {
    final String hakukohdeOid = "oid15";
    final String hakukohdekoodiUri = "hakukohdekoodiuri8";

    KoodiDTO koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

    Hakukohdekoodi haettu = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
    assertNotNull(haettu);
    assertEquals(0, hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri).size());

    hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, koodi);

    List<HakukohdeViite> hakukohdeViites =
        hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
    assertEquals(1, hakukohdeViites.size());
    assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());
  }

  @Test
  public void testVaihdaHakukohteenHakukohdekoodia() {
    final String hakukohdeOid = "oid16";
    final String hakukohdekoodiUri = "hakukohdekoodiuri10";

    KoodiDTO koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

    assertNotNull(hakukohdekoodiDAO.readByUri(hakukohdekoodiUri));
    Hakukohdekoodi vanhaKoodi = hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid);
    assertNotNull(vanhaKoodi);
    assertFalse(hakukohdekoodiUri.equals(vanhaKoodi.getUri()));

    hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, koodi);

    List<HakukohdeViite> hakukohdeViites =
        hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
    assertEquals(1, hakukohdeViites.size());
    assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());

    assertEquals(0, hakukohdeViiteDAO.readByHakukohdekoodiUri(vanhaKoodi.getUri()).size());
  }

  @Test
  public void testUpdateHakukohdeUusiHakukohdekoodi() {
    final String hakukohdeOid = "oid15";
    final String hakukohdekoodiUri = "eiolevielaolemassa";

    KoodiDTO koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

    assertNull(hakukohdekoodiDAO.readByUri(hakukohdekoodiUri));
    assertNull(hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid));
    hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, koodi);

    List<HakukohdeViite> hakukohdeViites =
        hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
    assertEquals(1, hakukohdeViites.size());
    assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());
  }
}
