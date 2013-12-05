package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: wuoti
 * Date: 27.11.2013
 * Time: 14.18
 * To change this template use File | Settings | File Templates.
 */
public class HakijaryhmaResourceTest {

    private HakijaryhmaResource hakijaryhmaResource;
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoServiceMock;
    private ValintaperusteetModelMapper modelMapper = new ValintaperusteetModelMapper();

    @Before
    public void setUp() {
        hakijaryhmaResource = new HakijaryhmaResource();
        hakijaryhmaValintatapajonoServiceMock = mock(HakijaryhmaValintatapajonoService.class);

        ReflectionTestUtils.setField(hakijaryhmaResource, "hakijaryhmaValintatapajonoService", hakijaryhmaValintatapajonoServiceMock);
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
        jono.setAloituspaikat(10);
        jono.setEiVarasijatayttoa(true);
        jono.setKuvaus("jono kuvaus");
        jono.setOid("jono oid");
        jono.setNimi("jono nimi");
        jono.setSiirretaanSijoitteluun(true);
        jono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        jono.setVarasijat(10);
        jono.setVarasijaTayttoPaivat(5);
        jono.setPoissaOlevaTaytto(true);

        hrjono.setAktiivinen(true);
        hrjono.setHakijaryhma(hr);
        hrjono.setValintatapajono(jono);
        hrjono.setOid("hrjono oid");


        when(hakijaryhmaValintatapajonoServiceMock.findByHakijaryhma(anyString())).thenReturn(Arrays.asList(hrjono));

        List<HakijaryhmaValintatapajonoDTO> puuppa = hakijaryhmaResource.valintatapajonot("oid");
        Hakijaryhma suo = modelMapper.map(puuppa.get(0).getHakijaryhma(), Hakijaryhma.class);

        System.out.println(puuppa.size());
    }
}
