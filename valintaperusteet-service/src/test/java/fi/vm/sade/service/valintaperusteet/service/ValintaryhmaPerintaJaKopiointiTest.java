package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@DataSetLocation("classpath:test-data-perinta.xml")
@ActiveProfiles({"dev", "vtsConfig"})
public class ValintaryhmaPerintaJaKopiointiTest extends WithSpringBoot {

  @Autowired private ValintatapajonoService valintatapajonoService;

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private ValintaperusteService valintaperusteService;

  @Test
  public void testMasterTayttojononMuutos() {
    final Valintatapajono paivitettava = valintatapajonoService.readByOid("2");
    final Valintatapajono tayttojono = valintatapajonoService.readByOid("4");

    paivitettava.setVarasijanTayttojono(tayttojono);
    final ValintatapajonoCreateDTO mapped =
        modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
    valintatapajonoService.update("2", mapped);

    Valintatapajono paivitetty = valintatapajonoService.readByOid("2");
    assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "4");

    final Valintatapajono tayttojono2 = valintatapajonoService.readByOid("6");
    paivitettava.setVarasijanTayttojono(tayttojono2);
    final ValintatapajonoCreateDTO mapped2 =
        modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
    valintatapajonoService.update("2", mapped2);

    paivitetty = valintatapajonoService.readByOid("2");
    assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "6");

    paivitettava.setVarasijanTayttojono(null);
    final ValintatapajonoCreateDTO mapped3 =
        modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
    valintatapajonoService.update("2", mapped3);

    paivitetty = valintatapajonoService.readByOid("2");
    assertNull(paivitetty.getVarasijanTayttojono());
  }

  @Test
  public void testPeritynTayttojononMuutos() {
    // Testataan, että modelMapping ei hajoa vaikka data on kuraa
    final Valintatapajono peritty = valintatapajonoService.readByOid("3");
    final Valintatapajono tayttojono = valintatapajonoService.readByOid("4");
    peritty.setVarasijanTayttojono(tayttojono);
    final ValintatapajonoCreateDTO mapped4 =
        modelMapper.map(peritty, ValintatapajonoCreateDTO.class);
    valintatapajonoService.update("3", mapped4);

    Map<String, List<ValintatapajonoDTO>> oid2 =
        valintaperusteService.haeValintatapajonotSijoittelulle(Arrays.asList("oid2"));

    ValintatapajonoDTO jono3 =
        oid2.get("oid2").stream().filter(j -> j.getOid().equals("3")).findFirst().get();
    assertNull(jono3.getTayttojono());

    // Testataan mappays oikealle datalla
    peritty.setVarasijanTayttojono(valintatapajonoService.readByOid("7"));
    final ValintatapajonoCreateDTO mapped5 =
        modelMapper.map(peritty, ValintatapajonoCreateDTO.class);
    valintatapajonoService.update("3", mapped5);

    oid2 = valintaperusteService.haeValintatapajonotSijoittelulle(Arrays.asList("oid2"));

    jono3 = oid2.get("oid2").stream().filter(j -> j.getOid().equals("3")).findFirst().get();
    assertEquals(jono3.getTayttojono(), "7");
  }

  @Test
  public void testKokoValintaryhmaPuunKopiointiToisenPuunAlle() {
    Valintaryhma valintaryhma = valintaryhmaService.copyAsChild("oid2", "oid1", "oid2 kopio");
    assertNotEquals("oid2", valintaryhma.getOid());
    assertEquals("oid2 kopio", valintaryhma.getNimi());
    assertEquals("oid1", valintaryhma.getYlavalintaryhma().getOid());

    Set<Laskentakaava> laskentakaavat = valintaryhma.getLaskentakaava();
    assertEquals(1L, laskentakaavat.size());
    Laskentakaava laskentakaava = laskentakaavat.iterator().next();
    assertEquals("Ammatillinen koulutus, lisäpiste", laskentakaava.getNimi());
    assertNotEquals(2L, laskentakaava.getId().longValue());
    valintaryhma.getValinnanvaiheet().stream()
        .forEach(
            v -> {
              assertEquals(3, v.getJonot().size());
              assertEquals(
                  Sets.newHashSet("Kolmas jono", "Täyttöjono", "Jono 2"),
                  v.getJonot().stream().map(j -> j.getNimi()).collect(Collectors.toSet()));
              v.getJonot()
                  .forEach(
                      j -> {
                        assertNull(j.getVarasijanTayttojono());
                        if (j.getNimi().equals("Jono 2")) {
                          assertEquals(1, j.getJarjestyskriteerit().size());
                          Laskentakaava kaava =
                              j.getJarjestyskriteerit().iterator().next().getLaskentakaava();
                          assertEquals("Ammatillinen koulutus, lisäpiste", kaava.getNimi());
                          assertNotEquals(2L, kaava.getId().longValue());
                          assertEquals(laskentakaava.getId(), kaava.getId());
                        }
                      });
            });

    assertEquals(1, valintaryhma.getAlavalintaryhmat().size());
    Valintaryhma kopioituAlivalintaryhma = valintaryhma.getAlavalintaryhmat().iterator().next();
    assertEquals("Valintaryhma 3", kopioituAlivalintaryhma.getNimi());
    assertNotEquals("oid3", kopioituAlivalintaryhma.getOid());

    Set<Laskentakaava> aliLaskentakaavat = kopioituAlivalintaryhma.getLaskentakaava();
    assertEquals(1L, aliLaskentakaavat.size());
    assertEquals(
        "Ammatillinen koulutus, lisäpiste2", aliLaskentakaavat.iterator().next().getNimi());
    assertNotEquals(3L, aliLaskentakaavat.iterator().next().getId().longValue());
  }

  @Test
  public void testKokoValintaryhmaPuunKopiointiJuurenAlle() {
    Valintaryhma valintaryhma = valintaryhmaService.copyAsChild("oid1", null, "oid1 kopio");
    assertNotEquals("oid1", valintaryhma.getOid());
    assertEquals("oid1 kopio", valintaryhma.getNimi());
    assertEquals(null, valintaryhma.getYlavalintaryhma());

    assertEquals(1, valintaryhma.getValinnanvaiheet().size());
    ValinnanVaihe vaihe = valintaryhma.getValinnanvaiheet().iterator().next();
    assertEquals("valinnanvaihe2", vaihe.getNimi());
    assertNotEquals("valinnanvaihe2oid", vaihe.getOid());

    assertEquals(1, valintaryhma.getHakijaryhmat().size());
    Hakijaryhma kopioituHakijaryhma = valintaryhma.getHakijaryhmat().iterator().next();
    assertEquals("Hakijaryhma 1", kopioituHakijaryhma.getNimi());
    assertNotEquals("oid1", kopioituHakijaryhma.getOid());

    Set<Laskentakaava> laskentakaavat = valintaryhma.getLaskentakaava();
    assertEquals(1L, laskentakaavat.size());
    Laskentakaava laskentakaava = laskentakaavat.iterator().next();
    assertEquals("Ammatillinen koulutus, lisäpiste", laskentakaava.getNimi());
    assertNotEquals(2L, laskentakaava.getId().longValue());
    valintaryhma.getValinnanvaiheet().stream()
        .forEach(
            v -> {
              assertEquals(3, v.getJonot().size());
              assertEquals(
                  Sets.newHashSet("Kolmas jono", "Täyttöjono", "Jono 2"),
                  v.getJonot().stream().map(j -> j.getNimi()).collect(Collectors.toSet()));
              v.getJonot()
                  .forEach(
                      j -> {
                        assertNull(j.getVarasijanTayttojono());
                        if (j.getNimi().equals("Jono 2")) {
                          assertEquals(1, j.getJarjestyskriteerit().size());
                          Laskentakaava kaava =
                              j.getJarjestyskriteerit().iterator().next().getLaskentakaava();
                          assertEquals("Ammatillinen koulutus, lisäpiste", kaava.getNimi());
                          assertNotEquals(2L, kaava.getId().longValue());
                          assertEquals(laskentakaava.getId(), kaava.getId());
                          assertEquals(1, j.getHakijaryhmat().size());
                          HakijaryhmaValintatapajono jononRyhma =
                              j.getHakijaryhmat().iterator().next();
                          assertEquals("Hakijaryhma 1", jononRyhma.getHakijaryhma().getNimi());
                          assertEquals(
                              kopioituHakijaryhma.getId(), jononRyhma.getHakijaryhma().getId());
                        }
                      });
            });

    assertEquals(1, valintaryhma.getAlavalintaryhmat().size());
    Valintaryhma kopioituAlivalintaryhma = valintaryhma.getAlavalintaryhmat().iterator().next();
    assertEquals("Valintaryhma 4", kopioituAlivalintaryhma.getNimi());
    assertNotEquals("oid4", kopioituAlivalintaryhma.getOid());

    Set<Laskentakaava> aliLaskentakaavat = kopioituAlivalintaryhma.getLaskentakaava();
    assertEquals(1L, aliLaskentakaavat.size());
    assertEquals(
        "Ammatillinen koulutus, lisäpiste3", aliLaskentakaavat.iterator().next().getNimi());
    assertNotEquals(4L, aliLaskentakaavat.iterator().next().getId().longValue());
  }

  @Test
  public void testAliValintaryhmanKopiointiToisenPuunAlle() {
    Valintaryhma valintaryhma = valintaryhmaService.copyAsChild("oid3", "oid1", "oid3 kopio");
    assertNotEquals("oid3", valintaryhma.getOid());
    assertEquals("oid3 kopio", valintaryhma.getNimi());
    assertEquals("oid1", valintaryhma.getYlavalintaryhma().getOid());

    Set<Laskentakaava> laskentakaavat = valintaryhma.getLaskentakaava();
    assertEquals(2L, laskentakaavat.size());
    valintaryhma.getValinnanvaiheet().stream()
        .forEach(
            v -> {
              assertEquals(3, v.getJonot().size());
              assertEquals(
                  Sets.newHashSet("Kolmas jono", "Täyttöjono", "Jono 2"),
                  v.getJonot().stream().map(j -> j.getNimi()).collect(Collectors.toSet()));
              v.getJonot()
                  .forEach(
                      j -> {
                        assertNull(j.getVarasijanTayttojono());
                        if (j.getNimi().equals("Jono 2")) {
                          assertEquals(1, j.getJarjestyskriteerit().size());
                          Laskentakaava kaava =
                              j.getJarjestyskriteerit().iterator().next().getLaskentakaava();
                          assertEquals("Ammatillinen koulutus, lisäpiste", kaava.getNimi());
                          assertNotEquals(2L, kaava.getId().longValue());
                        }
                      });
            });
  }
}
