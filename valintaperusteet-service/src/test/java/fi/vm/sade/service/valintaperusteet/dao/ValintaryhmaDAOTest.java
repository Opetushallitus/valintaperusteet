package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import org.junit.Assert;
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
        Assert.assertEquals(22, valintaryhmas.size());
    }

    @Test
    public void testFindValintaryhmaByParentOid() throws Exception {
        Valintaryhma toplevelValintaryhma = valintaryhmaDAO.findChildrenByParentOid(null).get(0);
        List<Valintaryhma> valintaryhmas = valintaryhmaDAO.findChildrenByParentOid(toplevelValintaryhma.getOid());
        Assert.assertEquals(4, valintaryhmas.size());
    }

    @Test
    public void testReadHierarchy() throws Exception {
        // Pitäisi tulla järjestyksessä: oid31, oid30, oid29, oid28
        List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readHierarchy("oid31");
        Assert.assertEquals(4, valintaryhmas.size());
        Assert.assertEquals("oid31", valintaryhmas.get(0).getOid());
        Assert.assertEquals("oid30", valintaryhmas.get(1).getOid());
        Assert.assertEquals("oid29", valintaryhmas.get(2).getOid());
        Assert.assertEquals("oid28", valintaryhmas.get(3).getOid());
    }
}
