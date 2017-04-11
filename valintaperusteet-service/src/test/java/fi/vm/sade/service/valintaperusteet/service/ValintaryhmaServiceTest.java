package fi.vm.sade.service.valintaperusteet.service;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.SyoteparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private ValintakoeDAO valintakoeDAO;

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
     * Ensure there's no links between old and new hierarchies when Valintaryhmä is copied between roots.
     *
     * @see <a href="https://jira.oph.ware.fi/jira/browse/BUG-1359">BUG-1359: valintakokeen päivittäminen ei periydy</a>
     */
    @Test
    public void kopioiValintaryhmaHierarkianTaydellisesti() throws Exception {
        // create original root VR
        Valintaryhma originalRoot = valintaryhmaService.insert(createValintaryhma("parent"));
        assertNotNull("Parent ValintaRyhmä should be persisted at this point", originalRoot);
        // add valinnanvaihe to original root
        addValinnanvaihe(originalRoot, "tavallinen", true, ValinnanVaiheTyyppi.TAVALLINEN);
        ValinnanVaihe examVV = addValinnanvaihe(originalRoot, "valintakoe", true, ValinnanVaiheTyyppi.VALINTAKOE);
        // create Laskentakaavas to be paired with various entities
        Laskentakaava originalValintakoeKaava = laskentakaavaService.insert(createLaskentakaava("valintakoekaava"), null, originalRoot.getOid());
        Laskentakaava originalHakijaryhmaKaava = laskentakaavaService.insert(createLaskentakaava("hakijaryhmäkaava"), null, originalRoot.getOid());
        // add hakijaryhma to original root
        Hakijaryhma originalHakijaryhma = hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle(originalRoot.getOid(), createHakijaryhma(originalHakijaryhmaKaava, "hakijaryhmä 1", Optional.empty()));
        // add valintakoe (=qualification exam) to matching VV
        ValintakoeCreateDTO valintakoe = new ValintakoeCreateDTO();
        valintakoe.setNimi("qualification exam");
        valintakoe.setAktiivinen(true);
        valintakoe.setKutsunKohde(Koekutsu.HAKIJAN_VALINTA);
        valintakoe.setKutsutaankoKaikki(true);
        valintakoe.setLahetetaankoKoekutsut(true);
        valintakoe.setTunniste("ID");
        valintakoe.setLaskentakaavaId(originalValintakoeKaava.getId());
        valintakoeService.lisaaValintakoeValinnanVaiheelle(examVV.getOid(), valintakoe);

        // create original child VR
        Valintaryhma originalChild = valintaryhmaService.insert(createValintaryhma("child"), originalRoot.getOid());
        assertNotNull("Child ValintaRyhmä should be persisted at this point", originalChild);

        // copy the original hierarchy
        Valintaryhma copiedRoot = valintaryhmaService.copyAsChild(originalRoot.getOid(), null, "copy of " + originalRoot.getNimi());

        // reload originalRoot so that all references are updated for assertions below
        originalRoot = valintaryhmaService.readByOid(originalRoot.getOid());

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

        assertValintakoeLaskentakaavaIds(originalRoot, Iterables.concat(originalVKs, originalChildVKs));
        assertValintakoeLaskentakaavaIds(copiedRoot, Iterables.concat(copiedVKs, copiedChildVKs));

        assertHakijaryhmaLinking(copiedRoot);
    }

    /**
     * Make sure child's Hakijaryhma is not cloned as toplevel Hakijaryhma when the assignment is done through
     * Valintatapajono.
     * 
     * Specifically this bug required the HR to be added to root VR, then linked to root VR's VTJ and a child with exact
     * same structure which reuses the root HR without modifications.
     *
     * @see <a href="https://jira.oph.ware.fi/jira/browse/BUG-1376">BUG-1376: Alivalintaryhmän luominen muodostaa satoja hakijaryhmiä</a>
     */
    @Test
    public void addingNewChildValintaryhmaDoesNotExcessivelyCloneHakijaryhmas() throws Exception {
        // create original root VR with single hakijaryhma
        Valintaryhma rootVR = valintaryhmaService.insert(createValintaryhma("parent"));
        Laskentakaava rootLK = laskentakaavaService.insert(createLaskentakaava("hakijaryhmäkaava"), null, rootVR.getOid());
        Hakijaryhma rootHR = hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle(rootVR.getOid(), createHakijaryhma(rootLK, "hakijaryhmä 1", Optional.of(createHakijaryhmatyyppikoodi())));
        // add valinnanvaihe with valintatapajono
        ValinnanVaihe rootVV = addValinnanvaihe(rootVR, "valinnanvaihe 1", true, ValinnanVaiheTyyppi.TAVALLINEN);
        Valintatapajono rootVTJ = valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(rootVV.getOid(), createValintatapajono("valintatapajono 1"), null);
        // link root's HR with the VTJ
        hakijaryhmaValintatapajonoService.liitaHakijaryhmaValintatapajonolle(rootVTJ.getOid(), rootHR.getOid());

        // add new child VR to root
        Valintaryhma childVR = valintaryhmaService.insert(createValintaryhma("child A"), rootVR.getOid());
        ValinnanVaihe childVV = addValinnanvaihe(childVR, "valinnanvaihe 2", true, ValinnanVaiheTyyppi.TAVALLINEN);
        // add VTJ to child VR which reuses the parent's HR
        Valintatapajono childVTJ = valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(childVV.getOid(), createValintatapajono("valintatapajono 2"), null);
        hakijaryhmaValintatapajonoService.liitaHakijaryhmaValintatapajonolle(childVTJ.getOid(), rootHR.getOid());

        // add new VR to root as one normally would
        Valintaryhma addedVR = valintaryhmaService.insert(createValintaryhma("child B"), rootVR.getOid());

        // list available HRs for the child B
        List<Hakijaryhma> hrs = hakijaryhmaService.findByValintaryhma(addedVR.getOid());
        // make sure there's only one HR as one would expect
        assertEquals("There should be exactly one Hakijaryhma for child B as it is added as child to parent, not child B", 1, hrs.size());
    }

    private void assertHakijaryhmaLinking(Valintaryhma copiedRoot) {
        List<Hakijaryhma> copiedHakijaryhmas = hakijaryhmaService.findByValintaryhma(copiedRoot.getOid());
        assertEquals("Copied Valintaryhma should have a single copied Hakijaryhma", 1, copiedHakijaryhmas.size());
        Hakijaryhma copiedHakijaryhma = copiedHakijaryhmas.get(0);
        assertEquals("Copied Hakijaryhma should be owned by copied root", copiedRoot.getOid(), copiedHakijaryhma.getValintaryhma().getOid());
        assertEquals("Copied Hakijaryhma's Laskentakaava should be owned by copied root", copiedRoot.getOid(), copiedHakijaryhma.getLaskentakaava().getValintaryhma().getOid());
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

    /**
     * Require all kokeet entries to reference Laskentakaava ids in given valintaryhma
     */
    private void assertValintakoeLaskentakaavaIds(Valintaryhma valintaryhma, Iterable<Valintakoe> kokeet) {
        // this reloads the kaavas as in valintaryhma they're a lazy set and we don't have active Hibernate Session here
        Set<Long> vrIds = laskentakaavaService.findKaavas(true, valintaryhma.getOid(), null, null).stream()
                .map(Laskentakaava::getId)
                .collect(Collectors.toSet());
        kokeet.forEach((koe) -> {
            assertNotNull("Expected reference to laskentakaava in Valintakoe " + koe.getId() + "::" + koe.getNimi() + " is missing", koe.getLaskentakaavaId());
            assertTrue("Valintaryhma " + valintaryhma.getId() + "::" + valintaryhma.getNimi() + " does not contain expected laskentakaava id " + koe.getLaskentakaavaId(),
                        vrIds.contains(koe.getLaskentakaavaId()));

        });
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

    private HakijaryhmaCreateDTO createHakijaryhma(Laskentakaava laskentakaava, String nimi, Optional<KoodiDTO> hakijaryhmatyyppikoodi) {
        HakijaryhmaCreateDTO hakijaryhma = new HakijaryhmaCreateDTO();
        hakijaryhma.setNimi(nimi);
        hakijaryhma.setLaskentakaavaId(laskentakaava.getId());
        hakijaryhmatyyppikoodi.ifPresent(hakijaryhma::setHakijaryhmatyyppikoodi);
        return hakijaryhma;
    }

    private LaskentakaavaCreateDTO createLaskentakaava(String nimi) {
        LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
        laskentakaava.setNimi(nimi);
        laskentakaava.setFunktiokutsu(createFunktiokutsu());
        laskentakaava.setOnLuonnos(true);
        return laskentakaava;
    }

    private FunktiokutsuDTO createFunktiokutsu() {
        FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
        funktiokutsu.setFunktionimi(Funktionimi.LUKUARVO);
        funktiokutsu.setSyoteparametrit(Sets.newHashSet(createSyoteparametri()));
        return funktiokutsu;
    }

    private SyoteparametriDTO createSyoteparametri() {
        SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
        syoteparametri.setArvo("5.0");
        syoteparametri.setAvain("luku");
        return syoteparametri;
    }

    private KoodiDTO createHakijaryhmatyyppikoodi() {
        KoodiDTO koodi = new KoodiDTO();
        koodi.setUri("www.example.fi");
        return koodi;
    }

    private ValintatapajonoCreateDTO createValintatapajono(String nimi) {
        ValintatapajonoCreateDTO vtj = new ValintatapajonoCreateDTO();
        vtj.setNimi(nimi);
        vtj.setAktiivinen(true);
        vtj.setAloituspaikat(10);
        vtj.setTasapistesaanto(Tasapistesaanto.ARVONTA);
        return vtj;
    }
}
