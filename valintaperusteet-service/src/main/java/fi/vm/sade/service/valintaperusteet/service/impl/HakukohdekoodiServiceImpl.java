package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 13.5.2013
 * Time: 13.03
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class HakukohdekoodiServiceImpl implements HakukohdekoodiService {

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;


    @Override
    public void updateValintaryhmaHakukohdekoodit(String valintaryhmaOid, Set<Hakukohdekoodi> hakukohdekoodit) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        Map<String, Hakukohdekoodi> uris = new HashMap<String, Hakukohdekoodi>();

        if (hakukohdekoodit == null) {
            hakukohdekoodit = new HashSet<Hakukohdekoodi>();
        }

        for (Hakukohdekoodi hakukohdekoodi : hakukohdekoodit) {
            uris.put(hakukohdekoodi.getUri(), hakukohdekoodi);
        }

        Set<String> uriSet = uris.keySet();
        Set<Hakukohdekoodi> poistettavatKoodit = new HashSet<Hakukohdekoodi>();

        // Haetaan koodit, jotka eivät ole enää liitetty tähän valintaryhmään
        for (Hakukohdekoodi koodi : valintaryhma.getHakukohdekoodit()) {
            if (!uriSet.contains(koodi.getUri())) {
                poistettavatKoodit.add(koodi);
            }
        }

        // Poistetaan liitokset tähän valintaryhmään
        for (Hakukohdekoodi koodi : poistettavatKoodit) {
            valintaryhma.getHakukohdekoodit().remove(koodi);
            koodi.getValintaryhmat().remove(valintaryhma);
        }

        List<Hakukohdekoodi> managedKoodis =
                hakukohdekoodiDAO.findByUris(uriSet.toArray(new String[uris.size()]));

        // Lisätään uudet liitokset hakukohdekoodien ja valintaryhmän välille
        for (Hakukohdekoodi managedKoodi : managedKoodis) {
            uris.remove(managedKoodi.getUri());
            valintaryhma.getHakukohdekoodit().add(managedKoodi);
            managedKoodi.getValintaryhmat().add(valintaryhma);
        }

        // Lisätään vielä mahdolliset uudet hakukohdekoodit kantaan
        for (Hakukohdekoodi uusiKoodi : uris.values()) {
            Hakukohdekoodi lisatty = hakukohdekoodiDAO.insert(uusiKoodi);
            valintaryhma.getHakukohdekoodit().add(lisatty);
            lisatty.getValintaryhmat().add(valintaryhma);
        }
    }

    @Override
    public void lisaaHakukohdekoodiValintaryhmalle(String valintaryhmaOid, Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodi.getUri());
        if (haettu != null) {
            haettu.setArvo(hakukohdekoodi.getArvo());
            haettu.setNimiEn(hakukohdekoodi.getNimiEn());
            haettu.setNimiFi(hakukohdekoodi.getNimiFi());
            haettu.setNimiSv(hakukohdekoodi.getNimiSv());
            valintaryhma.getHakukohdekoodit().add(haettu);
            haettu.getValintaryhmat().add(valintaryhma);
        } else {
            haettu = hakukohdekoodiDAO.insert(hakukohdekoodi);
            valintaryhma.getHakukohdekoodit().add(haettu);
            haettu.getValintaryhmat().add(valintaryhma);
        }
    }

    @Override
    public Hakukohdekoodi lisaaHakukohdekoodiHakukohde(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi) {
        HakukohdeViite hakukohdeViite = hakukohdeService.readByOid(hakukohdeOid);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodi.getUri());

        if (haettu == null) {
            haettu = hakukohdekoodiDAO.insert(hakukohdekoodi);
        }

        haettu.setArvo(hakukohdekoodi.getArvo());
        haettu.setNimiEn(hakukohdekoodi.getNimiEn());
        haettu.setNimiFi(hakukohdekoodi.getNimiFi());
        haettu.setNimiSv(hakukohdekoodi.getNimiSv());

        if (!haettu.getHakukohteet().contains(hakukohdeViite)) {
            haettu.addHakukohde(hakukohdeViite);
        }

        return haettu;
    }

    @Override
    public Hakukohdekoodi updateHakukohdeHakukohdekoodi(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi) {
        return lisaaHakukohdekoodiHakukohde(hakukohdeOid, hakukohdekoodi);
    }
}
