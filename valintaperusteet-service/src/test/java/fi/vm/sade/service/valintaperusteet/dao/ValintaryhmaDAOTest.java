package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
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

import static org.junit.Assert.assertEquals;

/**
 * User: bleed
 * Date: 1/17/13
 * Time: 1:30 PM
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaDAOTest {

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Test
    public void testFindValintaryhmaByNullParentOid() throws Exception {
        List<Valintaryhma> valintaryhmas = valintaryhmaDAO.findChildrenByParentOid(null);
        assertEquals(37, valintaryhmas.size());
    }

    @Test
    public void testFindValintaryhmaByParentOid() throws Exception {
        Valintaryhma toplevelValintaryhma = valintaryhmaDAO.findChildrenByParentOid(null).get(0);
        List<Valintaryhma> valintaryhmas = valintaryhmaDAO.findChildrenByParentOid(toplevelValintaryhma.getOid());
        assertEquals(4, valintaryhmas.size());
    }

    @Test
    public void testReadHierarchy() throws Exception {
        // Pitäisi tulla järjestyksessä: oid31, oid30, oid29, oid28
        List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readHierarchy("oid31");
        assertEquals(4, valintaryhmas.size());
        assertEquals("oid31", valintaryhmas.get(0).getOid());
        assertEquals("oid30", valintaryhmas.get(1).getOid());
        assertEquals("oid29", valintaryhmas.get(2).getOid());
        assertEquals("oid28", valintaryhmas.get(3).getOid());
    }

    @Test
    public void testHaeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan() {
        final String valintaryhmaOid1 = "oid44";
        final String valintaryhmaOid2 = "oid45";

        final String[] opetuskieliUrit = new String[]{"kieli_ru", "kieli_fi"};
        final String[] valintakoekoodiUrit = new String[]{"koekoodi2"};
        final String hakukohdekoodiUri = "hakukohdekoodiuri11";

        List<Valintaryhma> valintaryhmat =
                valintaryhmaDAO.haeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan(hakukohdekoodiUri,
                        Arrays.asList(opetuskieliUrit), Arrays.asList(valintakoekoodiUrit));

        assertEquals(2, valintaryhmat.size());

        Collections.sort(valintaryhmat, new Comparator<Valintaryhma>() {
            @Override
            public int compare(Valintaryhma o1, Valintaryhma o2) {
                return o1.getOid().compareTo(o2.getOid());
            }
        });

        assertEquals(valintaryhmaOid1, valintaryhmat.get(0).getOid());
        assertEquals(valintaryhmaOid2, valintaryhmat.get(1).getOid());
    }

    @Test
    public void testHaeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan2() {
        final String hakukohdekoodiUri = "hakukohdekoodiuri15";
        final String opetuskieliUri = "kieli_fi";

        List<Valintaryhma> valintaryhmat = valintaryhmaDAO.haeHakukohdekoodinOpetuskielikoodienJaValintakoekoodienMukaan(hakukohdekoodiUri,
                Arrays.asList(opetuskieliUri), new ArrayList<String>());

        assertEquals(1, valintaryhmat.size());
        assertEquals("oid50", valintaryhmat.get(0).getOid());
    }
}
