package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiLisataException;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 18.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintakoeServiceTest {

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Test
    public void testLisaaValintakoeValinnanVaiheelle() {
        final String valinnanVaiheOid = "83";
        final int before = 3;

        ValinnanVaihe vaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
        assertEquals(ValinnanVaiheTyyppi.VALINTAKOE, vaihe.getValinnanVaiheTyyppi());

        assertEquals(before, valintakoeService.findValintakoeByValinnanVaihe(valinnanVaiheOid).size());

        final Long laskentakaavaId = 101L;
        ValintakoeDTO valintakoe = new ValintakoeDTO();
        valintakoe.setNimi("nimi");
        valintakoe.setTunniste("tunniste");
        valintakoe.setLaskentakaavaId(laskentakaavaId);

        Valintakoe lisatty = valintakoeService.lisaaValintakoeValinnanVaiheelle(valinnanVaiheOid, valintakoe);
        assertTrue(StringUtils.isNotBlank(lisatty.getOid()));

        final int after = before + 1;
        assertEquals(after, valintakoeService.findValintakoeByValinnanVaihe(valinnanVaiheOid).size());
    }

    @Test(expected = ValintakoettaEiVoiLisataException.class)
    public void testLisaaValintakoeTavalliselleValinnanVaiheelle() {
        final String valinnanVaiheOid = "84";

        ValinnanVaihe vaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
        assertEquals(ValinnanVaiheTyyppi.TAVALLINEN, vaihe.getValinnanVaiheTyyppi());

        final Long laskentakaavaId = 101L;
        ValintakoeDTO valintakoe = new ValintakoeDTO();
        valintakoe.setTunniste("tunniste");
        valintakoe.setLaskentakaavaId(laskentakaavaId);

        valintakoeService.lisaaValintakoeValinnanVaiheelle(valinnanVaiheOid, valintakoe);
    }
}
