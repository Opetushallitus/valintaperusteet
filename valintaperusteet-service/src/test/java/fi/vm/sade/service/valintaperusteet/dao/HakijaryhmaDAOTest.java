package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
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

import java.util.List;

import static junit.framework.Assert.assertEquals;

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
        final String HAKIJARYHMA_NIMI = "hakijaryhma 1";
        final String HAKIJARYHMA_OID = "hr1";

        Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(HAKIJARYHMA_OID);
        assertEquals(HAKIJARYHMA_NIMI, hakijaryhma.getNimi() );
    }

    @Test
    public void testFindByValintaryhma() {
        final String VALINTARYHMA_OID = "vr1";
        final String HAKIJARYHMA_OID = "hr1";

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
        final String HAKIJARYHMA_OID = "hr4";

        Hakijaryhma byHakukohde = hakijaryhmaDAO.haeHakukohteenViimeinenHakijaryhma(HAKUKOHDE_OID);

        assertEquals(HAKIJARYHMA_OID, byHakukohde.getOid());
    }

    @Test
    public void testHaeValintaryhmanViimeinenHakijaryhma() {
        final String VALINTARYHMA_OID = "vr1";
        final String HAKIJARYHMA_OID = "hr1";

        Hakijaryhma byValintaryhma = hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(VALINTARYHMA_OID);

        assertEquals(HAKIJARYHMA_OID, byValintaryhma.getOid());
    }

    @Test
    public void testHaeValintatapajonolla() {
        final String VALINTATAPAJONO_OID_1 = "vtj1";
        final String VALINTATAPAJONO_OID_2 = "vtj2";
        final String VALINTATAPAJONO_OID_3 = "vtj3";
        final String VALINTATAPAJONO_OID_4 = "vtj4";
        final String VALINTATAPAJONO_OID_5 = "vtj5";

        final int LIST_SIZE_1 = 1;
        final int LIST_SIZE_2 = 2;
        final int LIST_SIZE_3 = 0;
        final int LIST_SIZE_4 = 0;
        final int LIST_SIZE_5 = 1;


        haeJono(VALINTATAPAJONO_OID_1, LIST_SIZE_1);
        haeJono(VALINTATAPAJONO_OID_2, LIST_SIZE_2);
        haeJono(VALINTATAPAJONO_OID_3, LIST_SIZE_3);
        haeJono(VALINTATAPAJONO_OID_4, LIST_SIZE_4);
        haeJono(VALINTATAPAJONO_OID_5, LIST_SIZE_5);


    }

    private void haeJono(String valintatapajonoOid, int listSize) {
        List<Hakijaryhma> byValintatapajono = hakijaryhmaDAO.findByValintatapajono(valintatapajonoOid);
        assertEquals(listSize, byValintatapajono.size());
    }


}
