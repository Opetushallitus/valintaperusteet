package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: bleed Date: 1/17/13 Time: 1:30 PM */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaDAOTest extends WithSpringBoot {

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Test
  public void testFindValintaryhmaByNullParentOid() throws Exception {
    List<Valintaryhma> valintaryhmas = valintaryhmaDAO.findChildrenByParentOid(null);
    assertEquals(46, valintaryhmas.size());
  }

  @Test
  public void testFindValintaryhmaByParentOid() throws Exception {
    Valintaryhma toplevelValintaryhma = valintaryhmaDAO.findChildrenByParentOid(null).get(0);
    List<Valintaryhma> valintaryhmas =
        valintaryhmaDAO.findChildrenByParentOid(toplevelValintaryhma.getOid());
    assertEquals(4, valintaryhmas.size());
  }

  @Test
  public void testReadHierarchy() throws Exception {
    // Pitäisi tulla järjestyksessä: oid31, oid30, oid29, oid28
    List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readHierarchy("oid31");
    assertEquals(4, valintaryhmas.size());
    assertEquals("oid31", valintaryhmas.get(0).getOid());
    assertEquals("oid30", valintaryhmas.get(1).getOid());
    assertEquals("oid29", valintaryhmas.get(2).getOid());
    assertEquals("oid28", valintaryhmas.get(3).getOid());
  }

  @Test
  public void testHaeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan() {
    List<Valintaryhma> valintaryhmat =
        valintaryhmaDAO.haeHakukohdekoodinJaValintakoekoodienMukaan(
            "hakuoid50", "hakukohdekoodiuri11", Collections.singleton("koekoodi2"));

    assertEquals(1, valintaryhmat.size());
    assertEquals("oid44", valintaryhmat.get(0).getOid());
  }

  @Test
  public void testHaeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan2() {
    List<Valintaryhma> valintaryhmat =
        valintaryhmaDAO.haeHakukohdekoodinJaValintakoekoodienMukaan(
            "hakuoid50", "hakukohdekoodiuri15", new HashSet<>());

    assertEquals(1, valintaryhmat.size());
    assertEquals("oid50", valintaryhmat.get(0).getOid());
  }

  @Test
  public void testHaeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan3() {
    HashSet<String> valintakoekoodit = new HashSet<>();
    valintakoekoodit.add("koekoodi1");
    valintakoekoodit.add("koekoodi2");
    List<Valintaryhma> valintaryhmat =
        valintaryhmaDAO.haeHakukohdekoodinJaValintakoekoodienMukaan(
            "hakuoid50", "hakukohdekoodiuri11", valintakoekoodit);

    assertEquals(1, valintaryhmat.size());
    assertEquals("oid45", valintaryhmat.get(0).getOid());
  }

  @Test
  public void testHaeOidinMukaanHakukohdekooditJaValintakoekooditMukana() {
    final String valintaryhmaOid = "oid58";

    Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(valintaryhmaOid);
    assertEquals(1, valintaryhma.getValintakoekoodit().size());
    assertEquals(2, valintaryhma.getHakukohdekoodit().size());
  }
}
