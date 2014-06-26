package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: wuoti
 * Date: 16.5.2013
 * Time: 12.11
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintakoekoodiServiceTest {

    @Autowired
    private ValintakoekoodiService valintakoekoodiService;

    @Autowired
    private ValintakoekoodiDAO valintakoekoodiDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    private KoodiDTO luoValintakoekoodi(String uri, String arvo, String nimi) {
        KoodiDTO koodi = new KoodiDTO();
        koodi.setUri(uri);
        koodi.setArvo(arvo);
        koodi.setNimiFi(nimi);
        koodi.setNimiSv(nimi);
        koodi.setNimiEn(nimi);

        return koodi;
    }

    @Test
    public void testLisaaValintakoekoodiValintaryhmalle() {
        final String valintaryhmaOid = "oid43";
        final String opetuskielikoodiUri = "eiolevielaolemassa";

        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertEquals(0, valintaryhma.getValintakoekoodit().size());

        KoodiDTO koodi = luoValintakoekoodi(opetuskielikoodiUri, opetuskielikoodiUri, opetuskielikoodiUri);

        assertNull(valintakoekoodiDAO.readByUri(opetuskielikoodiUri));
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, koodi);

        valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertEquals(1, valintaryhma.getValintakoekoodit().size());
        assertEquals(opetuskielikoodiUri, valintaryhma.getValintakoekoodit().iterator().next().getUri());
    }

    @Test
    public void testLisaaSamaValintakoekoodiValintaryhmalle() {
        final String valintaryhmaOid = "oid43";
        final String opetuskielikoodiUri = "eiolevielaolemassa";

        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertEquals(0, valintaryhma.getValintakoekoodit().size());

        KoodiDTO koodi = luoValintakoekoodi(opetuskielikoodiUri, opetuskielikoodiUri, opetuskielikoodiUri);

        assertNull(valintakoekoodiDAO.readByUri(opetuskielikoodiUri));
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, koodi);
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, koodi);

        valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        assertEquals(2, valintaryhma.getValintakoekoodit().size());

        for (Valintakoekoodi k : valintaryhma.getValintakoekoodit()) {
            assertEquals(opetuskielikoodiUri, k.getUri());
        }
    }

    private boolean tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(List<Valintakoekoodi> valintakoekoodit,
                                                                         Map<String, Integer> urit) {

        Map<String, Integer> valintaryhmanKoodit = new HashMap<String, Integer>();

        for (Valintakoekoodi k : valintakoekoodit) {
            if (!valintaryhmanKoodit.containsKey(k.getUri())) {
                valintaryhmanKoodit.put(k.getUri(), 1);
            } else {
                Integer lkm = valintaryhmanKoodit.get(k.getUri()) + 1;
                valintaryhmanKoodit.put(k.getUri(), lkm);
            }
        }

        return urit.equals(valintaryhmanKoodit);
    }

    private static class UriJaEsiintymisLkm {
        public UriJaEsiintymisLkm(String uri, Integer esiintymisLkm) {
            this.uri = uri;
            this.esiintymisLkm = esiintymisLkm;
        }

        private String uri;
        private Integer esiintymisLkm;

        private String getUri() {
            return uri;
        }

        private void setUri(String uri) {
            this.uri = uri;
        }

        private Integer getEsiintymisLkm() {
            return esiintymisLkm;
        }

        private void setEsiintymisLkm(Integer esiintymisLkm) {
            this.esiintymisLkm = esiintymisLkm;
        }
    }

    private UriJaEsiintymisLkm luoUri(String uri, int esiintymisLkm) {
        return new UriJaEsiintymisLkm(uri, esiintymisLkm);
    }

    private Map<String, Integer> luoUriMap(UriJaEsiintymisLkm... urit) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (UriJaEsiintymisLkm u : urit) {
            map.put(u.getUri(), u.getEsiintymisLkm());
        }

        return map;
    }

    @Test
    public void testPaivitaValintaryhmanOpetuskielikoodit() {
        final String valintaryhmaOid = "oid51";


        Map<String, Integer> valintakoekooditAluksi = luoUriMap(luoUri("valintakoeuri1", 1),
                luoUri("valintakoeuri2", 2));
        Map<String, Integer> valintakoekooditLopuksi = luoUriMap(luoUri("valintakoeuri1", 1),
                luoUri("valintakoeuri3", 2), luoUri("aivanuusi", 5));

        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        List<Valintakoekoodi> valintaryhmanValintakoekoodit = valintakoekoodiDAO.findByValintaryhma(valintaryhmaOid);
        assertTrue(tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(valintaryhmanValintakoekoodit, valintakoekooditAluksi));

        List<KoodiDTO> koodit = new ArrayList<KoodiDTO>();
        for (Map.Entry<String, Integer> u : valintakoekooditLopuksi.entrySet()) {
            String uri = u.getKey();
            Integer lkm = u.getValue();
            for (int i = 0; i < lkm; ++i) {
                koodit.add(luoValintakoekoodi(uri, uri, uri));
            }
        }

        valintakoekoodiService.updateValintaryhmanValintakoekoodit(valintaryhmaOid, koodit);
        valintaryhmanValintakoekoodit = valintakoekoodiDAO.findByValintaryhma(valintaryhmaOid);
        assertTrue(tarkastaEttaValintaryhmallaOnKaikkiOpetuskielikoodit(valintaryhmanValintakoekoodit, valintakoekooditLopuksi));

    }
}
