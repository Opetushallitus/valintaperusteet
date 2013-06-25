package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;
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

import java.util.HashSet;
import java.util.Set;

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
public class OpetuskielikoodiServiceTest {

    @Autowired
    private OpetuskielikoodiService opetuskielikoodiService;

    @Autowired
    private OpetuskielikoodiDAO opetuskielikoodiDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    private Opetuskielikoodi luoOpetuskielikoodi(String uri, String arvo, String nimi) {
        Opetuskielikoodi koodi = new Opetuskielikoodi();
        koodi.setUri(uri);
        koodi.setArvo(arvo);
        koodi.setNimiFi(nimi);
        koodi.setNimiSv(nimi);
        koodi.setNimiEn(nimi);

        return koodi;
    }

    @Test
    public void testLisaaOpetuskielikoodiValintaryhmalle() {
        final String valintaryhmaOid = "oid43";
        final String opetuskielikoodiUri = "eiolevielaolemassa";

        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertEquals(0, valintaryhma.getOpetuskielikoodit().size());

        Opetuskielikoodi koodi = luoOpetuskielikoodi(opetuskielikoodiUri, opetuskielikoodiUri, opetuskielikoodiUri);

        assertNull(opetuskielikoodiDAO.readByUri(opetuskielikoodiUri));
        opetuskielikoodiService.lisaaOpetuskielikoodiValintaryhmalle(valintaryhmaOid, koodi);

        valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertEquals(1, valintaryhma.getOpetuskielikoodit().size());
        assertEquals(opetuskielikoodiUri, valintaryhma.getOpetuskielikoodit().iterator().next().getUri());
    }

    private boolean tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(Valintaryhma valintaryhma,
                                                                         String... opetuskielikoodiUrit) {
        outer:
        for (String uri : opetuskielikoodiUrit) {
            for (Opetuskielikoodi k : valintaryhma.getOpetuskielikoodit()) {
                if (uri.equals(k.getUri())) {
                    continue outer;
                }
            }
            return false;
        }

        return true;
    }

    @Test
    public void testPaivitaValintaryhmanOpetuskielikoodit() {
        final String valintaryhmaOid = "oid51";
        final String[] opetuskielikoodiAluksi = new String[]{"kieli_fi", "kieli_sv"};
        final String[] opetuskielikoodiLopuksi = new String[]{"kieli_fi", "kieli_ru", "taysinuusi"};

        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertTrue(tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(valintaryhma, opetuskielikoodiAluksi));

        Set<Opetuskielikoodi> koodit = new HashSet<Opetuskielikoodi>();
        for (String uri : opetuskielikoodiLopuksi) {
            koodit.add(luoOpetuskielikoodi(uri, uri, uri));
        }

        opetuskielikoodiService.updateValintaryhmaOpetuskielikoodit(valintaryhmaOid, koodit);
        valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertTrue(tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(valintaryhma, opetuskielikoodiLopuksi));

    }
}
