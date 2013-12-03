package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 4.2.2013
 * Time: 15.20
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class JarjestyskriteeriResourceTest {

    private JarjestyskriteeriResource resource = new JarjestyskriteeriResource();
    private TestUtil testUtil = new TestUtil(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JarjestyskriteeriDAO jarjestyskriteeriDAO;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(resource);
    }

    @Test
    public void testUpdate() throws Exception {
        Jarjestyskriteeri jk = jarjestyskriteeriDAO.readByOid("1");

//        assertEquals(1, (int)jk.getPrioriteetti());
//        jk.setPrioriteetti(100);

        JarjestyskriteeriCreateDTO update = new JarjestyskriteeriCreateDTO();
        update.setMetatiedot("metatiedot");
        update.setLaskentakaavaId(jk.getLaskentakaavaId());

        resource.update("1", update);

        jk = jarjestyskriteeriDAO.readByOid("1");
//        assertEquals(100, (int)jk.getPrioriteetti());

        testUtil.lazyCheck(JsonViews.Basic.class, jk);
    }

    @Test
    public void testRemove() throws Exception {
        Jarjestyskriteeri jk = jarjestyskriteeriDAO.readByOid("1");
//        assertEquals(1, (int)jk.getPrioriteetti());

        resource.delete("1");
        jk = jarjestyskriteeriDAO.readByOid("1");
        assertEquals(null, jk);
    }

    @Test
    public void testJarjesta() throws Exception {
        String[] uusiJarjestys = {"3203", "3202", "3201"};
        List<JarjestyskriteeriDTO> jarjestetty = resource.jarjesta(Arrays.asList(uusiJarjestys));
        testUtil.lazyCheck(JsonViews.Basic.class, jarjestetty);
    }

}
