package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdekoodiOnLiitettyToiseenValintaryhmaanException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.junit.Assert.*;

/**
 * User: wuoti
 * Date: 16.5.2013
 * Time: 12.11
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdekoodiServiceTest {

    @Autowired
    private HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    private Hakukohdekoodi luoHakukohdekoodi(String uri, String arvo, String nimi) {
        Hakukohdekoodi koodi = new Hakukohdekoodi();
        koodi.setUri(uri);
        koodi.setArvo(arvo);
        koodi.setNimiFi(nimi);
        koodi.setNimiSv(nimi);
        koodi.setNimiEn(nimi);

        return koodi;
    }

    @Test
    public void testLisaaHakukohdekoodiValintaryhmalle() {
        final String valintaryhmaOid = "oid43";
        final String hakukohdekoodiUri = "eiolevielaolemassa";

        Hakukohdekoodi koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

        assertNull(hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri));
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(valintaryhmaOid, koodi);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
        assertEquals(valintaryhmaOid, haettu.getValintaryhma().getOid());
    }

    @Test(expected = HakukohdekoodiOnLiitettyToiseenValintaryhmaanException.class)
    public void testLisaaHakukohdekoodiJokaOnjoLiitettyValintaryhmaan() {
        final String valintaryhmaOid = "oid43";
        final String hakukohdekoodiUri = "hakukohdekoodiuri7";

        Hakukohdekoodi koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(valintaryhmaOid, koodi);
    }

    @Test
    public void testLisaaUusiHakukohdekoodiHakukohteelle() {
        final String hakukohdeOid = "oid15";
        final String hakukohdekoodiUri = "eiolevielaolemassa";

        Hakukohdekoodi koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

        assertNull(hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri));
        assertNull(hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid));
        hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, koodi);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
        assertEquals(1, haettu.getHakukohteet().size());
        assertEquals(hakukohdeOid, haettu.getHakukohteet().iterator().next().getOid());
    }

    @Test
    public void testLisaaOlemassaOlevaHakukohdekoodiHakukohteelle() {
        final String hakukohdeOid = "oid15";
        final String hakukohdekoodiUri = "hakukohdekoodiuri8";

        Hakukohdekoodi koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
        assertNotNull(haettu);
        assertEquals(0, haettu.getHakukohteet().size());

        hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, koodi);

        haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
        assertEquals(1, haettu.getHakukohteet().size());
        assertEquals(hakukohdeOid, haettu.getHakukohteet().iterator().next().getOid());
    }

    @Test
    public void testVaihdaHakukohteenHakukohdekoodia() {
        final String hakukohdeOid = "oid16";
        final String hakukohdekoodiUri = "hakukohdekoodiuri10";

        Hakukohdekoodi koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

        assertNotNull(hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri));
        Hakukohdekoodi vanhaKoodi = hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid);
        assertNotNull(vanhaKoodi);
        assertFalse(hakukohdekoodiUri.equals(vanhaKoodi.getUri()));

        hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, koodi);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
        assertEquals(1, haettu.getHakukohteet().size());
        assertEquals(hakukohdeOid, haettu.getHakukohteet().iterator().next().getOid());

        assertEquals(0, hakukohdekoodiDAO.findByKoodiUri(vanhaKoodi.getUri()).getHakukohteet().size());
    }

    @Test
    public void testUpdateHakukohdeUusiHakukohdekoodi() {
        final String hakukohdeOid = "oid15";
        final String hakukohdekoodiUri = "eiolevielaolemassa";

        Hakukohdekoodi koodi = luoHakukohdekoodi(hakukohdekoodiUri, hakukohdekoodiUri, hakukohdekoodiUri);

        assertNull(hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri));
        assertNull(hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid));
        hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, koodi);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
        assertEquals(1, haettu.getHakukohteet().size());
        assertEquals(hakukohdeOid, haettu.getHakukohteet().iterator().next().getOid());
    }

}
