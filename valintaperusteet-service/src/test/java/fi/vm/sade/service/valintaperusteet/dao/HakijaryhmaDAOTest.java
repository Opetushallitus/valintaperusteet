package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * User: kwuoti
 * Date: 18.1.2013
 * Time: 10.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class HakijaryhmaDAOTest {

    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Test
    public void testReadByOid() {
        final String HAKIJARYHMA_NIMI = "hakijaryhma1";
        final String HAKIJARYHMA_OID = "13819272933902809864870091301782";

        Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(HAKIJARYHMA_OID);
        assertEquals(HAKIJARYHMA_NIMI, hakijaryhma.getNimi() );
    }

    @Test
    public void testFindByValintaryhma() {
        final String VALINTARYHMA_OID = "1381743139706-2693498799461467830";
        final String HAKIJARYHMA_OID = "13819272933902809864870091301782";

        List<Hakijaryhma> byValintaryhma = hakijaryhmaDAO.findByValintaryhma(VALINTARYHMA_OID);

        assertEquals(HAKIJARYHMA_OID, byValintaryhma.get(0).getOid());
    }

    @Test
    public void testFindByHakukohde() {
        final String HAKUKOHDE_OID = "1";

        List<Hakijaryhma> byHakukohde = hakijaryhmaDAO.findByHakukohde(HAKUKOHDE_OID);

        assertEquals(3, byHakukohde.size());
    }

    @Test
    public void testHaeHakukohteenViimeinenHakijaryhma() {
        final String HAKUKOHDE_OID = "1";
        final String HAKIJARYHMA_OID = "13819276508298702905770965193967";

        Hakijaryhma byHakukohde = hakijaryhmaDAO.haeHakukohteenViimeinenHakijaryhma(HAKUKOHDE_OID);

        assertEquals(HAKIJARYHMA_OID, byHakukohde.getOid());
    }

    @Test
    public void testHaeValintaryhmanViimeinenHakijaryhma() {
        final String VALINTARYHMA_OID = "1381743139706-2693498799461467830";
        final String HAKIJARYHMA_OID = "1381929536619-162478418672529850";

        Hakijaryhma byValintaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(VALINTARYHMA_OID);

        assertEquals(HAKIJARYHMA_OID, byValintaryhma.getOid());
    }

    @Test
    public void testHaeValintatapajonolla() {
        final String VALINTATAPAJONO_OID_1 = "1381744037373-4242685733786926927";
        final String VALINTATAPAJONO_OID_2 = "13817440373886813568836676069139";

        final int LIST_SIZE_1 = 1;
        final int LIST_SIZE_2 = 2;

        List<Hakijaryhma> byValintatapajono = hakijaryhmaDAO.findByValintatapajono(VALINTATAPAJONO_OID_1);
        assertEquals(LIST_SIZE_1, byValintatapajono.size());

        byValintatapajono = hakijaryhmaDAO.findByValintatapajono(VALINTATAPAJONO_OID_2);
        assertEquals(LIST_SIZE_2, byValintatapajono.size());
    }

}
