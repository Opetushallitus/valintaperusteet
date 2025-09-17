package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSetLocation("classpath:test-data.xml")
public class PuuServiceTest extends WithSpringBoot {

  @Autowired private PuuService puuService;

  @Test
  public void haeValintaryhmaPuu() {
    final String oid = "oid6";
    List<ValintaperustePuuDTO> puu = puuService.search(null, List.of(), "", false, "", oid);
    assertEquals(1, puu.size());
    assertEquals(puu.get(0).getOid(), oid);
    assertTrue(puu.get(0).getAlavalintaryhmat().isEmpty());
  }

  @Test
  public void haeValintaryhmaPuuHaulla() {
    final String hakuOid = "uusiHakuOid";
    List<ValintaperustePuuDTO> puu = puuService.searchByHaku(hakuOid);
    assertEquals(1, puu.size());
    assertEquals(puu.get(0).getHakuOid(), hakuOid);
    assertTrue(puu.get(0).getAlavalintaryhmat().isEmpty());
  }

  @Test
  public void haeValintaryhmaPuuHaullaSisaltaenHakukohteet() {
    final String hakuOid = "hakuOidJollaOrpoHakukohde";
    List<ValintaperustePuuDTO> puu = puuService.searchByHaku(hakuOid);
    assertEquals(2, puu.size());
    assertEquals(puu.get(0).getHakuOid(), hakuOid);
    assertNull(puu.get(1).getHakuOid());
    assertEquals(puu.get(1).getHakukohdeViitteet().size(), 1);
  }
}
