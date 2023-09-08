package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** User: wuoti Date: 16.5.2013 Time: 12.11 */
@DataSetLocation("classpath:test-data.xml")
public class ValintakoekoodiServiceTest extends WithSpringBoot {

  @Autowired private ValintakoekoodiService valintakoekoodiService;

  @Autowired private ValintakoekoodiDAO valintakoekoodiDAO;

  @Autowired private ValintaryhmaService valintaryhmaService;

  private KoodiDTO luoValintakoekoodi(String uri, String arvo, String nimi) {
    KoodiDTO koodi = new KoodiDTO();
    koodi.setUri(uri);
    koodi.setArvo(arvo);
    koodi.setNimiFi(nimi);
    koodi.setNimiSv(nimi);
    koodi.setNimiEn(nimi);

    return koodi;
  }

  @Test
  public void testLisaaValintakoekoodiValintaryhmalle() {
    final String valintaryhmaOid = "oid43";
    final String opetuskielikoodiUri = "eiolevielaolemassa";

    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    assertEquals(0, valintaryhma.getValintakoekoodit().size());

    KoodiDTO koodi =
        luoValintakoekoodi(opetuskielikoodiUri, opetuskielikoodiUri, opetuskielikoodiUri);

    assertNull(valintakoekoodiDAO.readByUri(opetuskielikoodiUri));
    valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, koodi);

    valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    assertEquals(1, valintaryhma.getValintakoekoodit().size());
    assertEquals(
        opetuskielikoodiUri, valintaryhma.getValintakoekoodit().iterator().next().getUri());
  }

  @Test
  public void testLisaaSamaValintakoekoodiValintaryhmalle() {
    final String valintaryhmaOid = "oid43";
    final String opetuskielikoodiUri = "eiolevielaolemassa";

    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    assertEquals(0, valintaryhma.getValintakoekoodit().size());

    KoodiDTO koodi =
        luoValintakoekoodi(opetuskielikoodiUri, opetuskielikoodiUri, opetuskielikoodiUri);

    assertNull(valintakoekoodiDAO.readByUri(opetuskielikoodiUri));
    valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, koodi);
    valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, koodi);
  }

  private boolean tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(
      Set<Valintakoekoodi> valintakoekoodit, Map<String, Integer> urit) {

    Map<String, Integer> valintaryhmanKoodit = new HashMap<String, Integer>();

    for (Valintakoekoodi k : valintakoekoodit) {
      if (!valintaryhmanKoodit.containsKey(k.getUri())) {
        valintaryhmanKoodit.put(k.getUri(), 1);
      } else {
        Integer lkm = valintaryhmanKoodit.get(k.getUri()) + 1;
        valintaryhmanKoodit.put(k.getUri(), lkm);
      }
    }

    return urit.equals(valintaryhmanKoodit);
  }

  private static class UriJaEsiintymisLkm {
    public UriJaEsiintymisLkm(String uri, Integer esiintymisLkm) {
      this.uri = uri;
      this.esiintymisLkm = esiintymisLkm;
    }

    private String uri;
    private Integer esiintymisLkm;

    private String getUri() {
      return uri;
    }

    private void setUri(String uri) {
      this.uri = uri;
    }

    private Integer getEsiintymisLkm() {
      return esiintymisLkm;
    }

    private void setEsiintymisLkm(Integer esiintymisLkm) {
      this.esiintymisLkm = esiintymisLkm;
    }
  }

  private UriJaEsiintymisLkm luoUri(String uri, int esiintymisLkm) {
    return new UriJaEsiintymisLkm(uri, esiintymisLkm);
  }

  private Map<String, Integer> luoUriMap(UriJaEsiintymisLkm... urit) {
    Map<String, Integer> map = new HashMap<String, Integer>();

    for (UriJaEsiintymisLkm u : urit) {
      map.put(u.getUri(), u.getEsiintymisLkm());
    }

    return map;
  }

  @Test
  public void testPaivitaValintaryhmanOpetuskielikoodit() {
    final String valintaryhmaOid = "oid51";

    Map<String, Integer> valintakoekooditAluksi =
        luoUriMap(luoUri("valintakoeuri1", 1), luoUri("valintakoeuri2", 1));
    Map<String, Integer> valintakoekooditLopuksi =
        luoUriMap(luoUri("valintakoeuri1", 1), luoUri("valintakoeuri3", 1), luoUri("aivanuusi", 1));

    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    Set<Valintakoekoodi> valintaryhmanValintakoekoodit =
        valintakoekoodiDAO.findByValintaryhma(valintaryhmaOid);
    assertTrue(
        tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(
            valintaryhmanValintakoekoodit, valintakoekooditAluksi));

    List<KoodiDTO> koodit = new ArrayList<KoodiDTO>();
    for (Map.Entry<String, Integer> u : valintakoekooditLopuksi.entrySet()) {
      String uri = u.getKey();
      Integer lkm = u.getValue();
      for (int i = 0; i < lkm; ++i) {
        koodit.add(luoValintakoekoodi(uri, uri, uri));
      }
    }

    valintakoekoodiService.updateValintaryhmanValintakoekoodit(valintaryhmaOid, koodit);
    valintaryhmanValintakoekoodit = valintakoekoodiDAO.findByValintaryhma(valintaryhmaOid);
    assertTrue(
        tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(
            valintaryhmanValintakoekoodit, valintakoekooditLopuksi));
  }
}
