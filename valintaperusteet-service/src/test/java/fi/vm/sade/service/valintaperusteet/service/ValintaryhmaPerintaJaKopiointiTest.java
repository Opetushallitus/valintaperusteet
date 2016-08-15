package fi.vm.sade.service.valintaperusteet.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
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

/**
 * Created with IntelliJ IDEA. User: jukais Date: 16.1.2013 Time: 14.16 To
 * change this template use File | Settings | File Templates.
 */
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
    public void testTayttojonoPerintaJaMappays() {

        // Testataan että masterin päivitys onnistuu
        Valintatapajono paivitettava = valintatapajonoService.readByOid("2");
        final Valintatapajono tayttojono = valintatapajonoService.readByOid("4");
        final Valintatapajono tayttojono2 = valintatapajonoService.readByOid("6");
        paivitettava.setVarasijanTayttojono(tayttojono);
        final ValintatapajonoCreateDTO mapped = modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("2", mapped);

        Valintatapajono paivitetty = valintatapajonoService.readByOid("2");
        assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "4");

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

        // Testataan, että modelMapping ei hajoa vaikka data on kuraa
        final Valintatapajono peritty = valintatapajonoService.readByOid("3");
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

        // Testataan kopiointi
        paivitettava.setVarasijanTayttojono(tayttojono);
        final ValintatapajonoCreateDTO mapped6 = modelMapper.map(paivitettava, ValintatapajonoCreateDTO.class);
        valintatapajonoService.update("2", mapped6);

        paivitetty = valintatapajonoService.readByOid("2");
        assertEquals(paivitetty.getVarasijanTayttojono().getOid(), "4");

        Valintaryhma valintaryhma = valintaryhmaService.copyAsChild("oid2", "oid1", "Jeppis");
        valintaryhma.getValinnanvaiheet().stream().forEach(v -> {
            assertEquals(v.getJonot().size(), 3);
            v.getJonot().forEach(j -> {
                assertNull(j.getVarasijanTayttojono());
            });
        });


    }

}
