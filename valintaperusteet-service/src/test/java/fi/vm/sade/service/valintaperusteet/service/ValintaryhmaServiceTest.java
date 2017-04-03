package fi.vm.sade.service.valintaperusteet.service;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaServiceTest {

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private ValintakoeDAO valintakoeDAO;

    @Autowired
    private PlatformTransactionManager txManager;

    private TransactionTemplate tx;

    @Before
    public void setUp() throws Exception {
        tx = new TransactionTemplate(txManager);
    }

    @Test
    public void testInsertChild() {
        final String parentOid = "oid6";
        final int valinnanVaiheetLkm = 5;

        ValintaryhmaCreateDTO uusiValintaryhma = new ValintaryhmaCreateDTO();
        uusiValintaryhma.setNimi("uusi valintaryhma");

        Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma, parentOid);
        List<ValinnanVaihe> valinnanVaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO
                .findByValintaryhma(lisatty.getOid()));
        assertEquals(valinnanVaiheetLkm, valinnanVaiheet.size());

        assertEquals(10L, valinnanVaiheet.get(0).getMasterValinnanVaihe().getId().longValue());
        assertEquals(11L, valinnanVaiheet.get(1).getMasterValinnanVaihe().getId().longValue());
        assertEquals(12L, valinnanVaiheet.get(2).getMasterValinnanVaihe().getId().longValue());
        assertEquals(13L, valinnanVaiheet.get(3).getMasterValinnanVaihe().getId().longValue());
        assertEquals(14L, valinnanVaiheet.get(4).getMasterValinnanVaihe().getId().longValue());
    }

    @Test
    public void testCopyHakijaryhmaToChild() {
        final String parentOid = "oid6";
        final int valinnanVaiheetLkm = 5;

        Valintaryhma parent = valintaryhmaService.readByOid(parentOid);
        List<Hakijaryhma> parentHakijaryhmat = hakijaryhmaService.findByValintaryhma(parentOid);

        assertEquals(1, parentHakijaryhmat.size());

        ValintaryhmaCreateDTO uusiValintaryhma = new ValintaryhmaCreateDTO();
        uusiValintaryhma.setNimi("uusi valintaryhma");

        Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma, parentOid);
        List<Hakijaryhma> childHakijaryhmat = hakijaryhmaService.findByValintaryhma(lisatty.getOid());

        assertEquals(1, childHakijaryhmat.size());

        assertNotSame(parentHakijaryhmat.get(0).getOid(), childHakijaryhmat.get(0).getOid());

    }

    @Test
    public void testInsert() {
        final String parentOid = "oid33";
        {
            assertNotNull(valintaryhmaService.readByOid(parentOid));
            List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO
                    .findByValintaryhma(parentOid));

            assertEquals(2, vr33Lvaiheet.size());
            ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
            ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

            List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO
                    .findByValinnanVaihe(vaihe80L.getOid()));
            List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO
                    .findByValinnanVaihe(vaihe81L.getOid()));

            assertEquals(2, vaihe80Ljonot.size());
            assertEquals(1, vaihe81Ljonot.size());
        }

        ValintaryhmaCreateDTO uusiValintaryhma = new ValintaryhmaCreateDTO();

        uusiValintaryhma.setNimi("uusi nimi");

        Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma, parentOid);
        assertTrue(StringUtils.isNotBlank(lisatty.getOid()));

        {
            assertNotNull(valintaryhmaService.readByOid(parentOid));
            List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO
                    .findByValintaryhma(parentOid));

            assertEquals(2, vr33Lvaiheet.size());
            ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
            ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

            List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO
                    .findByValinnanVaihe(vaihe80L.getOid()));
            List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO
                    .findByValinnanVaihe(vaihe81L.getOid()));

            assertEquals(2, vaihe80Ljonot.size());
            assertEquals(1, vaihe81Ljonot.size());
        }
        {
            assertNotNull(valintaryhmaService.readByOid(lisatty.getOid()));
            List<ValinnanVaihe> uusiVaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO
                    .findByValintaryhma(lisatty.getOid()));

            assertEquals(2, uusiVaiheet.size());
            ValinnanVaihe uusiVaihe1 = uusiVaiheet.get(0);
            ValinnanVaihe uusiVaihe2 = uusiVaiheet.get(1);

            List<Valintatapajono> uusiVaihe1jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO
                    .findByValinnanVaihe(uusiVaihe1.getOid()));
            List<Valintatapajono> uusiVaihe2jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO
                    .findByValinnanVaihe(uusiVaihe2.getOid()));

            assertEquals(2, uusiVaihe1jonot.size());
            assertEquals(1, uusiVaihe2jonot.size());
        }
    }

    @Test
    public void testKopioiValintakokeetUudenAlavalintaryhmanValinnanVaiheelle() {
        final String parentValintaryhmaOid = "oid56";
        final String valinnanVaiheOid = "107";
        final String valintakoeOid = "oid14";

        Valintaryhma valintaryhma = valintaryhmaService.readByOid(parentValintaryhmaOid);
        List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByValintaryhma(parentValintaryhmaOid);
        assertEquals(1, vaiheet.size());

        ValinnanVaihe vaihe = vaiheet.get(0);
        assertEquals(ValinnanVaiheTyyppi.VALINTAKOE, vaihe.getValinnanVaiheTyyppi());
        assertEquals(valinnanVaiheOid, vaihe.getOid());

        List<Valintakoe> kokeet = valintakoeDAO.findByValinnanVaihe(vaihe.getOid());
        assertEquals(1, kokeet.size());
        assertEquals(valintakoeOid, kokeet.get(0).getOid());

        ValintaryhmaCreateDTO childCreate = new ValintaryhmaCreateDTO();

        childCreate.setNimi("uusi alavalintaryhma");
        Valintaryhma child = valintaryhmaService.insert(childCreate, parentValintaryhmaOid);

        List<ValinnanVaihe> uudetVaiheet = valinnanVaiheDAO.findByValintaryhma(child.getOid());
        assertEquals(1, uudetVaiheet.size());

        ValinnanVaihe uusiVaihe = uudetVaiheet.get(0);
        assertEquals(ValinnanVaiheTyyppi.VALINTAKOE, uusiVaihe.getValinnanVaiheTyyppi());
        assertEquals(valinnanVaiheOid, uusiVaihe.getMasterValinnanVaihe().getOid());

        List<Valintakoe> uudetKokeet = valintakoeDAO.findByValinnanVaihe(uusiVaihe.getOid());
        assertEquals(1, uudetKokeet.size());
        assertEquals(valintakoeOid, uudetKokeet.get(0).getMasterValintakoe().getOid());
    }


    /**
     * Ensure there's no links between old and new hierarchies when Valintaryhmä is copied between roots
     *
     * @see <a href="https://jira.oph.ware.fi/jira/browse/BUG-1359">BUG-1359: valintakokeen päivittäminen ei periydy</a>
     */
    @Test
    public void kopioiValintaryhmaHierarkianTaydellisesti() throws Exception {
        // create original root VR
        Valintaryhma originalRoot = valintaryhmaService.insert(createValintaryhma("parent"));
        assertNotNull("Parent ValintaRyhmä should be persisted at this point", originalRoot);
        // add some VVs to original root
        addValinnanvaihe(originalRoot, "tavallinen", true, ValinnanVaiheTyyppi.TAVALLINEN);
        ValinnanVaihe examVV = addValinnanvaihe(originalRoot, "valintakoe", true, ValinnanVaiheTyyppi.VALINTAKOE);
        // add valintakoe (=qualification exam) to matching VV
        ValintakoeCreateDTO valintakoe = new ValintakoeCreateDTO();
        valintakoe.setNimi("qualification exam");
        valintakoe.setAktiivinen(true);
        valintakoe.setKutsunKohde(Koekutsu.HAKIJAN_VALINTA);
        valintakoe.setKutsutaankoKaikki(true);
        valintakoe.setLahetetaankoKoekutsut(true);
        valintakoe.setTunniste("ID");
        valintakoeService.lisaaValintakoeValinnanVaiheelle(examVV.getOid(), valintakoe);

        // create original child VR
        Valintaryhma originalChild = valintaryhmaService.insert(createValintaryhma("child"), originalRoot.getOid());
        assertNotNull("Child ValintaRyhmä should be persisted at this point", originalChild);

        // copy the original hierarchy
        Valintaryhma copiedRoot = valintaryhmaService.copyAsChild(originalRoot.getOid(), null, "copy of " + originalRoot.getNimi());

        // get VVs for child VRs and check link integrities
        List<ValinnanVaihe> originalVVs = findVVs(originalRoot);
        List<ValinnanVaihe> copiedVVs = findVVs(copiedRoot);
        List<ValinnanVaihe> originalChildVVs = findVVs(findChildFor(originalRoot));
        List<ValinnanVaihe> copiedChildVVs = findVVs(findChildFor(copiedRoot));

        assertValinnanvaiheRootLinking(originalVVs, copiedVVs);
        assertValinnanvaiheChildLinking(originalVVs, originalChildVVs);
        assertValinnanvaiheChildLinking(copiedVVs, copiedChildVVs);

        int valintakoeIndex = 1;
        List<Valintakoe> originalVKs = valintakoeService.findValintakoeByValinnanVaihe(originalVVs.get(valintakoeIndex).getOid());
        List<Valintakoe> originalChildVKs = valintakoeService.findValintakoeByValinnanVaihe(originalChildVVs.get(valintakoeIndex).getOid());
        List<Valintakoe> copiedVKs = valintakoeService.findValintakoeByValinnanVaihe(copiedVVs.get(valintakoeIndex).getOid());
        List<Valintakoe> copiedChildVKs = valintakoeService.findValintakoeByValinnanVaihe(copiedChildVVs.get(valintakoeIndex).getOid());

        assertValintakoeRootLinking(originalVKs, copiedVKs);
        assertValintakoeChildLinking(originalVKs, originalChildVKs);
        assertValintakoeChildLinking(copiedVKs, copiedChildVKs);
    }

    private static void assertValinnanvaiheRootLinking(List<ValinnanVaihe> originalVVs, List<ValinnanVaihe> copiedVVs) {
        List<ValinnanVaihe> nonNullVVs = FluentIterable.from(Iterables.concat(originalVVs, copiedVVs))
                .transform(ValinnanVaihe::getMaster)
                .filter(Predicates.notNull())
                .toList();
        assertTrue("None of the ValinnanVaihe root entities should have any parent links!", nonNullVVs.isEmpty());
    }

    private static void assertValinnanvaiheChildLinking(List<ValinnanVaihe> rootVVs, List<ValinnanVaihe> childVVs) {
        List<Long> rootIds = Lists.transform(rootVVs, ValinnanVaihe::getId);
        List<Long> childIds = Lists.transform(childVVs, (vk) -> vk.getMaster().getId());
        assertEquals("ValinnanVaihe hierarchy's master references from child to root are incorrect!", rootIds, childIds);
    }

    private static void assertValintakoeRootLinking(List<Valintakoe> originalVKs, List<Valintakoe> copiedVKs) {
        List<Valintakoe> nonNullVKs = FluentIterable.from(Iterables.concat(originalVKs, copiedVKs))
                .transform(Valintakoe::getMaster)
                .filter(Predicates.notNull())
                .toList();
        assertTrue("None of the ValintaKoe root entities should have any parent links!", nonNullVKs.isEmpty());
    }

    private static void assertValintakoeChildLinking(List<Valintakoe> rootVKs, List<Valintakoe> childVKs) {
        List<Long> rootIds = Lists.transform(rootVKs, Valintakoe::getId);
        List<Long> childIds = Lists.transform(childVKs, (vk) -> vk.getMaster().getId());
        assertEquals("Valintakoe hierarchy's master references from child to root are incorrect!", rootIds, childIds);
    }

    private List<ValinnanVaihe> findVVs(Valintaryhma vr) {
        return valinnanVaiheService.findByValintaryhma(vr.getOid());
    }

    private Valintaryhma findChildFor(Valintaryhma vr) {
        List<Valintaryhma> children = valintaryhmaService.findValintaryhmasByParentOid(vr.getOid());
        assertEquals("Valintaryhma should have exactly one child!", 1, children.size());
        return children.get(0);
    }

    private ValinnanVaihe addValinnanvaihe(Valintaryhma parent, String nimi, boolean aktiivinen, ValinnanVaiheTyyppi tyyppi) {
        ValinnanVaiheCreateDTO valinnanvaihe = new ValinnanVaiheCreateDTO();
        valinnanvaihe.setNimi(nimi);
        valinnanvaihe.setAktiivinen(aktiivinen);
        valinnanvaihe.setValinnanVaiheTyyppi(tyyppi);
        return valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(parent.getOid(), valinnanvaihe, null);
    }

    private ValintaryhmaCreateDTO createValintaryhma(String nimi) {
        ValintaryhmaCreateDTO valintaryhma = new ValintaryhmaCreateDTO();
        valintaryhma.setNimi(nimi);
        return valintaryhma;
    }
}
