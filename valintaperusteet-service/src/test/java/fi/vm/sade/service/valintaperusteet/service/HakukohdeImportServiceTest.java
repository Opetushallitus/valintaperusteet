package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdekoodiTyyppi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 15.06
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeImportServiceTest {

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private HakukohdeImportService hakukohdeImportService;

    private HakukohdeImportTyyppi luoHakukohdeImportTyyppi(String hakukohdeOid, String hakuOid, String koodiUri) {
        HakukohdeImportTyyppi imp = new HakukohdeImportTyyppi();
        imp.setHakukohdeOid(hakukohdeOid);
        imp.setHakuOid(hakuOid);
        imp.setNimi(hakukohdeOid);

        HakukohdekoodiTyyppi koodi = new HakukohdekoodiTyyppi();
        koodi.setKoodiUri(koodiUri);
        koodi.setArvo(koodiUri);
        koodi.setNimiFi(koodiUri);
        koodi.setNimiSv(koodiUri);
        koodi.setNimiEn(koodiUri);

        imp.setHakukohdekoodi(koodi);
        return imp;
    }

    @Test
    public void testImportNewHakukohde() {
        final String hakukohdeOid = "uusiHakukohdeOid";
        final String hakuOid = "uusiHakuOid";
        final String koodiUri = "uusihakukohdekoodiuri";

        assertNull(hakukohdekoodiDAO.findByKoodiUri(koodiUri));
        assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, koodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(koodiUri);
        assertEquals(koodiUri, koodi.getUri());

        HakukohdeViite koodiHakukohde = koodi.getHakukohde();
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
        assertEquals(koodiHakukohde, hakukohde);
        assertEquals(hakukohdeOid, hakukohde.getOid());
    }

    @Test
    public void testImportHakukohdeWithExistingValintaryhma() {
        final String hakukohdeOid = "uusiHakukohdeOid";
        final String hakuOid = "uusiHakuOid";
        final String koodiUri = "hakukohdekoodiuri2";

        final String valintaryhmaOid = "oid37";

        Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(koodiUri);
        assertEquals(valintaryhmaOid, koodi.getValintaryhma().getOid());
        assertNull(koodi.getHakukohde());
        assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

        assertEquals(0, hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid).size());

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, koodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        koodi = hakukohdekoodiDAO.findByKoodiUri(koodiUri);
        assertNotNull(koodi.getValintaryhma());
        assertEquals(valintaryhmaOid, koodi.getValintaryhma().getOid());

        HakukohdeViite koodiHakukohde = koodi.getHakukohde();
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
        assertEquals(koodiHakukohde, hakukohde);

        List<HakukohdeViite> hakukohteetByValintaryhma = hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid);
        assertEquals(1, hakukohteetByValintaryhma.size());
        assertEquals(hakukohde, hakukohteetByValintaryhma.get(0));


    }
}
