package fi.vm.sade.service.valintaperusteet.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;

public class JononPrioriteettiAsettajaTest {
    private int testSetSize;
    private ValinnanVaiheJonoillaDTO valinnanVaiheDto = new ValinnanVaiheJonoillaDTO();

    @Before
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
        JononPrioriteettiAsettaja.filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(Collections.singletonList(valinnanVaiheDto));
        valinnanVaiheDto.getJonot().forEach(j -> Assert.assertEquals(j.getOid(), Integer.toString(j.getPrioriteetti())));
        Assert.assertEquals(testSetSize, valinnanVaiheDto.getJonot().size());
        Assert.assertThat(testSetSize, is(greaterThan(1)));
    }
}
