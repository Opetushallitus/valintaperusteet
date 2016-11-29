package fi.vm.sade.service.valintaperusteet.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data-perinta.xml")
public class ValintaryhmaPerintaJaKopiointiTest {

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private ValintaperusteService valintaperusteService;

    @Test
    public void testMasterTayttojononMuutos() {
        final Valintatapajono paivitettava = valintatapajonoService.readByOid("2");
        final Valintatapajono tayttojono = valintatapajonoService.readByOid("4");

        paivitettava.setVarasijanTayttojono(tayttojono);
        final ValintatapajonoCreateDTO mapped = modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("2", mapped);

        Valintatapajono paivitetty = valintatapajonoService.readByOid("2");
        assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "4");

        final Valintatapajono tayttojono2 = valintatapajonoService.readByOid("6");
        paivitettava.setVarasijanTayttojono(tayttojono2);
        final ValintatapajonoCreateDTO mapped2 = modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("2", mapped2);

        paivitetty = valintatapajonoService.readByOid("2");
        assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "6");

        paivitettava.setVarasijanTayttojono(null);
        final ValintatapajonoCreateDTO mapped3 = modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
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
        final ValintatapajonoCreateDTO mapped4 = modelMapper.map(peritty, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("3", mapped4);

        Map<String,List<ValintatapajonoDTO>> oid2 = valintaperusteService.haeValintatapajonotSijoittelulle(Arrays.asList("oid2"));

        ValintatapajonoDTO jono3 = oid2.get("oid2").stream().filter(j -> j.getOid().equals("3")).findFirst().get();
        assertNull(jono3.getTayttojono());

        // Testataan mappays oikealle datalla
        peritty.setVarasijanTayttojono(valintatapajonoService.readByOid("7"));
        final ValintatapajonoCreateDTO mapped5 = modelMapper.map(peritty, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("3", mapped5);

        oid2 = valintaperusteService.haeValintatapajonotSijoittelulle(Arrays.asList("oid2"));

        jono3 = oid2.get("oid2").stream().filter(j -> j.getOid().equals("3")).findFirst().get();
        assertEquals(jono3.getTayttojono(), "7");
    }

    @Test
    public void testValintaryhmanKopiointi() {
        final Valintatapajono paivitettava = valintatapajonoService.readByOid("2");
        final Valintatapajono tayttojono = valintatapajonoService.readByOid("4");
        paivitettava.setVarasijanTayttojono(tayttojono);
        final ValintatapajonoCreateDTO mapped6 = modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("2", mapped6);

        Valintatapajono paivitetty = valintatapajonoService.readByOid("2");
        assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "4");

        Valintaryhma valintaryhma = valintaryhmaService.copyAsChild("oid2", "oid1", "oid2 kopio");
        assertNotEquals("oid2", valintaryhma.getOid());
        assertEquals("oid2 kopio", valintaryhma.getNimi());
        assertEquals("oid1", valintaryhma.getYlavalintaryhma().getOid());
        Set<Laskentakaava> laskentakaavat = valintaryhma.getLaskentakaava();
        assertEquals(1L, laskentakaavat.size());

        Laskentakaava laskentakaava = laskentakaavat.iterator().next();
        assertEquals("Ammatillinen koulutus, lisäpiste", laskentakaava.getNimi());
        assertNotEquals(2L, laskentakaava.getId().longValue());
        valintaryhma.getValinnanvaiheet().stream().forEach(v -> {
            assertEquals(3, v.getJonot().size());
            assertEquals(Sets.newHashSet("Kolmas jono", "Täyttöjono", "Jono 2"), v.getJonot().stream().map(j -> j.getNimi()).collect(Collectors.toSet()));
            v.getJonot().forEach(j -> {
                assertNull(j.getVarasijanTayttojono());
                if(j.getNimi().equals("Jono 2")) {
                    assertEquals(1, j.getJarjestyskriteerit().size());
                    Laskentakaava kaava = j.getJarjestyskriteerit().iterator().next().getLaskentakaava();
                    assertEquals("Ammatillinen koulutus, lisäpiste", kaava.getNimi());
                    assertEquals(2L, kaava.getId().longValue());
                }
            });
        });
    }

}
