package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.service.impl.util.ValintaperusteServiceUtil;
import org.junit.jupiter.api.Test;

/** User: wuoti Date: 13.5.2013 Time: 9.24 */
public class ValintaperusteServiceUtilTest {

  @Test
  public void test() {
    assertTrue(
        Laskentakaavavalidaattori.onkoLaskettavaKaavaValidi(
                ValintaperusteServiceUtil.getAinaPakollinenFunktiokutsu())
            .isEmpty());
  }
}
