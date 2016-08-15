package fi.vm.sade.service.valintaperusteet.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.40
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaperusteServiceTest {

    @Autowired
    private ValintaperusteService valintaperusteService;

    @Test
    public void testHaeValintatapajonotSijoittelulle() {
        final String hakukohdeoid = "oid6";

        assertEquals(9, valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeoid).size());
    }

    @Test
    public void testHaeValintaperusteet() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid6", null));

        Set<String> tunnisteet = new HashSet<>(Arrays.asList(new String[]{"tunniste1", "tunniste2", "tunniste3", "tunniste4"}));

        List<ValintaperusteetDTO> ValintaperusteetDTOs = valintaperusteService.haeValintaperusteet(params);

        assertEquals(3, ValintaperusteetDTOs.size());
        for (ValintaperusteetDTO vp : ValintaperusteetDTOs) {
            assertEquals(tunnisteet.size(), vp.getHakukohteenValintaperuste().size());
            for (HakukohteenValintaperusteDTO hkvpt : vp.getHakukohteenValintaperuste()) {
                assertTrue(tunnisteet.contains(hkvpt.getTunniste()));
                assertNotNull(hkvpt.getArvo());
            }
        }

        assertEquals(1, ValintaperusteetDTOs.get(1).getValinnanVaihe().getValinnanVaiheJarjestysluku());
        assertEquals(3, (ValintaperusteetDTOs.get(0).getValinnanVaihe()).getValintatapajono().get(0).getJarjestyskriteerit().size());
        assertEquals(1, ( ValintaperusteetDTOs.get(0).getValinnanVaihe()).getValintatapajono().get(1).getPrioriteetti());
        assertEquals(1, ( ValintaperusteetDTOs.get(0).getValinnanVaihe()).getValintatapajono().get(0).getJarjestyskriteerit().get(1).getPrioriteetti());
    }

    @Test
    public void testHaeValintaperusteetJarjestysluvulla() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid6", 2));
        List<ValintaperusteetDTO> ValintaperusteetDTOs = valintaperusteService.haeValintaperusteet(params);

        assertEquals(1, ValintaperusteetDTOs.size());
    }

    @Test(expected = RuntimeException.class)
    public void testHaeValintaperusteetEpakelvollaJarjestysluvulla() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid6", -1));
        valintaperusteService.haeValintaperusteet(params);
    }

    @Test(expected = RuntimeException.class)
    public void testHaeValintaperusteetLiianIsollaJarjestysluvulla() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid6", 77));
        valintaperusteService.haeValintaperusteet(params);
    }

    @Test
    public void testHaeValintaperusteetJarjestysluvulla2() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid6", 2));
        params.add(getHakuparametritDTO("oid2", 2));
        List<ValintaperusteetDTO> ValintaperusteetDTOs = valintaperusteService.haeValintaperusteet(params);

        assertEquals(2, ValintaperusteetDTOs.size());
    }

    @Test(expected = RuntimeException.class)
    public void testHaeValintaperusteetNullilla() {
        valintaperusteService.haeValintaperusteet(null);
    }

    @Test
    public void testHaeValintaperusteet2() {
        List<HakuparametritDTO> params = new ArrayList<>();

        params.add(getHakuparametritDTO("oid1", null));
        params.add(getHakuparametritDTO("oid2", null));
        params.add(getHakuparametritDTO("oid3", null));
        params.add(getHakuparametritDTO("oid4", null));
        params.add(getHakuparametritDTO("oid5", null));
        params.add(getHakuparametritDTO("oid6", null));

        List<ValintaperusteetDTO> ValintaperusteetDTOs = valintaperusteService.haeValintaperusteet(params);

        assertEquals(6, ValintaperusteetDTOs.size());
    }

    private HakuparametritDTO getHakuparametritDTO(String oid, Integer jl) {
        HakuparametritDTO HakuparametritDTO = new HakuparametritDTO();
        HakuparametritDTO.setHakukohdeOid(oid);
        HakuparametritDTO.setValinnanVaiheJarjestysluku(jl);
        return HakuparametritDTO;
    }

    @Test
    public void testHaeValintakoeValintaperusteet() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid10", null));

        List<ValintaperusteetDTO> ValintaperusteetDTOs = valintaperusteService.haeValintaperusteet(params);
        assertEquals(1, ValintaperusteetDTOs.size());

        ValintaperusteetValinnanVaiheDTO vv = ValintaperusteetDTOs.get(0).getValinnanVaihe();

        assertNotNull(vv.getValinnanVaiheJarjestysluku());
        assertNotNull(vv.getValinnanVaiheOid());
        assertEquals(1, vv.getValintakoe().size());

        ValintakoeDTO vk = vv.getValintakoe().get(0);
        assertNotNull(vk.getFunktiokutsu());
        assertNotNull(vk.getKuvaus());
        assertNotNull(vk.getNimi());
        assertNotNull(vk.getOid());
        assertNotNull(vk.getTunniste());
    }

    @Test(expected = RuntimeException.class)
    public void testHaeValintaperusteetMukanaEpaaktiivisiaVaiheita() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid21", 0));
        List<ValintaperusteetDTO> vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(1, vps.size());
        assertEquals("108", vps.get(0).getValinnanVaihe().getValinnanVaiheOid());

        params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid21", 2));
        vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(1, vps.size());

        params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid21", 1));
        valintaperusteService.haeValintaperusteet(params);
    }

    @Test
    public void testHaeKaikkiValintaperusteetMukanaEpaaktiivisiaVaiheita() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid21", null));
        List<ValintaperusteetDTO> vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(2, vps.size());
        assertEquals("108", vps.get(0).getValinnanVaihe().getValinnanVaiheOid());
        assertEquals("110", vps.get(1).getValinnanVaihe().getValinnanVaiheOid());
    }

    @Test
    public void testHakukohdettaEiOleOlemassa() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("ei-ole-olemassa", 1));

        assertEquals(0, valintaperusteService.haeValintaperusteet(params).size());
    }

    @Test
    public void testHaeHakukohteenValintaperusteet() {
        List<HakuparametritDTO> params = new ArrayList<>();
        params.add(getHakuparametritDTO("oid21", null));
        List<ValintaperusteetDTO> vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(2, vps.size());
        assertEquals("108", vps.get(0).getValinnanVaihe().getValinnanVaiheOid());
        assertEquals("110", vps.get(1).getValinnanVaihe().getValinnanVaiheOid());
        assertEquals(4, vps.get(0).getHakukohteenValintaperuste().size());
        assertEquals(4, vps.get(1).getHakukohteenValintaperuste().size());

    }

    @Test
    public void testhaeValintatapajonotSijoittelulle() {
        final Map<String, List<ValintatapajonoDTO>> valintatapajonotSijoittelulle =
                valintaperusteService.haeValintatapajonotSijoittelulle(Arrays.asList("oid6"));
        assertNotNull(valintatapajonotSijoittelulle);
        final List<ValintatapajonoDTO> valintatapajonoDTOs = valintatapajonotSijoittelulle.get("oid6");
        assertEquals(3, valintatapajonoDTOs.size());
        assertEquals(0, valintatapajonoDTOs.get(0).getPrioriteetti());
        assertEquals("6", valintatapajonoDTOs.get(0).getOid());
        assertEquals(2, valintatapajonoDTOs.get(2).getPrioriteetti());
        assertEquals("7", valintatapajonoDTOs.get(2).getOid());

        assertEquals(1, valintatapajonoDTOs.get(1).getPrioriteetti());
        assertEquals("8", valintatapajonoDTOs.get(1).getOid());
    }
}
