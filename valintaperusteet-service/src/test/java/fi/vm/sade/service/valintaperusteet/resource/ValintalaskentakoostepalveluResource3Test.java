package fi.vm.sade.service.valintaperusteet.resource;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintalaskentakoostepalveluResourceImpl;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValintalaskentakoostepalveluResource3Test {
  @Test
  public void testJonojenPrioriteetitIsollaDatalla() {
    String hakukohdeOid = "hakukohdeOid";
    HakukohdeService hakukohdeService = mock(HakukohdeService.class);
    ValintalaskentakoostepalveluResourceImpl resource =
        new ValintalaskentakoostepalveluResourceImpl(
            mock(ValintaperusteService.class),
            mock(HakijaryhmaValintatapajonoService.class),
            mock(ValinnanVaiheService.class),
            mock(ValintaryhmaService.class),
            mock(ValintatapajonoService.class),
            mock(LaskentakaavaService.class),
            new ValintaperusteetModelMapper(),
            hakukohdeService,
            mock(ValintakoeService.class));

    ValinnanVaihe valinnanVaihe = new ValinnanVaihe();
    LinkedHashSet<Valintatapajono> valintatapajonot = new LinkedHashSet<>();
    for (int i = 0; i < 1000; i++) {
      Valintatapajono jono = new Valintatapajono();
      jono.setKaytetaanValintalaskentaa(false);
      jono.setOid(Integer.toString(i));
      valintatapajonot.add(jono);
    }
    valinnanVaihe.setJonot(valintatapajonot);
    when(hakukohdeService.ilmanLaskentaa(hakukohdeOid))
        .thenReturn(Collections.singletonList(valinnanVaihe));

    Set<ValintatapajonoDTO> jonoDtos = resource.ilmanLaskentaa(hakukohdeOid).get(0).getJonot();
    assertThat(jonoDtos, hasSize(greaterThan(1)));
    jonoDtos.forEach(j -> assertEquals(j.getOid(), Integer.toString(j.getPrioriteetti())));
  }
}
