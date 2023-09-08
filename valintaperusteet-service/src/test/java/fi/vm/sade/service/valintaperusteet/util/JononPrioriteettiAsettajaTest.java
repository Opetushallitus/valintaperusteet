package fi.vm.sade.service.valintaperusteet.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JononPrioriteettiAsettajaTest {
  private int testSetSize;
  private ValinnanVaiheJonoillaDTO valinnanVaiheDto = new ValinnanVaiheJonoillaDTO();

  @BeforeEach
  public void generateTestData() {
    LinkedHashSet<ValintatapajonoDTO> jonot = new LinkedHashSet<>();
    for (int i = 0; i < 10000; i++) {
      ValintatapajonoDTO jono = new ValintatapajonoDTO();
      jono.setOid(Integer.toString(i));
      if (i % 3 == 0) {
        jono.setKaytetaanValintalaskentaa(false);
        testSetSize += 1;
      }
      jonot.add(jono);
    }
    valinnanVaiheDto.setJonot(jonot);
  }

  @Test
  public void filtteroiJaJarjestaJonotIlmanLaskentaa() {
    JononPrioriteettiAsettaja.filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(
        Collections.singletonList(valinnanVaiheDto));
    valinnanVaiheDto
        .getJonot()
        .forEach(j -> assertEquals(j.getOid(), Integer.toString(j.getPrioriteetti())));
    assertEquals(testSetSize, valinnanVaiheDto.getJonot().size());
    assertThat(testSetSize, is(greaterThan(1)));
  }
}
