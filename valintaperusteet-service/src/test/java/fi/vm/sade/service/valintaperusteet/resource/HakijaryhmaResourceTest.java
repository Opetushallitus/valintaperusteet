package fi.vm.sade.service.valintaperusteet.resource;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto;
import fi.vm.sade.service.valintaperusteet.resource.impl.HakijaryhmaResourceImpl;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;

public class HakijaryhmaResourceTest {

    private HakijaryhmaResourceImpl hakijaryhmaResource;
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoServiceMock;
    private ValintaperusteetModelMapper modelMapper = new ValintaperusteetModelMapper();

    @Before
    public void setUp() {
        hakijaryhmaResource = new HakijaryhmaResourceImpl();
        hakijaryhmaValintatapajonoServiceMock = mock(HakijaryhmaValintatapajonoService.class);

        ReflectionTestUtils.setField(hakijaryhmaResource, "hakijaryhmaValintatapajonoService",
                hakijaryhmaValintatapajonoServiceMock);
        ReflectionTestUtils.setField(hakijaryhmaResource, "modelMapper", modelMapper);
    }

    @Test
    public void test() {
        final HakijaryhmaValintatapajono hrjono = new HakijaryhmaValintatapajono();

        Hakijaryhma hr = new Hakijaryhma();
        hr.setKiintio(5);
        hr.setKuvaus("hr kuvaus");
        hr.setNimi("hr nimi");
        hr.setOid("hr oid");
        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setId(100L);
        hr.setLaskentakaava(laskentakaava);

        Valintatapajono jono = new Valintatapajono();
        jono.setAktiivinen(true);
        jono.setAutomaattinenLaskentaanSiirto(true);
        jono.setValisijoittelu(false);
        jono.setAloituspaikat(10);
        jono.setEiVarasijatayttoa(true);
        jono.setKuvaus("jono kuvaus");
        jono.setOid("jono oid");
        jono.setNimi("jono nimi");
        jono.setSiirretaanSijoitteluun(true);
        jono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        jono.setVarasijat(10);

        jono.setPoissaOlevaTaytto(true);

        hrjono.setAktiivinen(true);
        hrjono.setHakijaryhma(hr);
        hrjono.setKiintio(hr.getKiintio());
        hrjono.setValintatapajono(jono);
        hrjono.setOid("hrjono oid");

        Hakijaryhmatyyppikoodi koodi = new Hakijaryhmatyyppikoodi();
        koodi.setUri("koodi_uri");
        hrjono.setHakijaryhmatyyppikoodi(koodi);

        when(hakijaryhmaValintatapajonoServiceMock.findByHakijaryhma(anyString())).thenReturn(Arrays.asList(hrjono));

        List<HakijaryhmaValintatapajonoDTO> response = hakijaryhmaResource.valintatapajonot("oid");
        Assert.assertEquals(1, response.size());
        Assert.assertEquals("koodi_uri", response.get(0).getHakijaryhmatyyppikoodi().getUri());
    }
}
