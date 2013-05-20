package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdekoodiOnLiitettyToiseenValintaryhmaanException;
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
    public Valintaryhma updateValintaryhmaHakukohdekoodit(String valintaryhmaOid, Set<Hakukohdekoodi> hakukohdekoodit) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        Map<String, Hakukohdekoodi> uris = new HashMap<String, Hakukohdekoodi>();

        if(hakukohdekoodit == null) {
            hakukohdekoodit = new HashSet<Hakukohdekoodi>();
        }

        for (Hakukohdekoodi hakukohdekoodi : hakukohdekoodit) {
            uris.put(hakukohdekoodi.getUri(), hakukohdekoodi);
        }

        Set<String> uriSet = uris.keySet();

        for (Hakukohdekoodi koodi : valintaryhma.getHakukohdekoodit()) {
            if(!uriSet.contains(koodi.getUri())) {
                koodi.setValintaryhma(null);
            }
        }


        List<Hakukohdekoodi> managedKoodis =
                hakukohdekoodiDAO.findByUris(uriSet.toArray(new String[uris.size()]));

        for (Hakukohdekoodi managedKoodi : managedKoodis) {
            uris.remove(managedKoodi.getUri());

            if (managedKoodi.getValintaryhma() != null && !valintaryhma.equals(managedKoodi.getValintaryhma())) {
                throw new HakukohdekoodiOnLiitettyToiseenValintaryhmaanException("Hakukohdekoodi URI "
                        + managedKoodi.getUri() + " on jo liitetty valintaryhmään OID "
                        + managedKoodi.getValintaryhma().getOid());
            }
            managedKoodi.setValintaryhma(valintaryhma);
        }

        for(Hakukohdekoodi uusiKoodi : uris.values()) {
            Hakukohdekoodi lisatty = hakukohdekoodiDAO.insert(uusiKoodi);
            lisatty.setValintaryhma(valintaryhma);
        }

        return valintaryhma;
    }

    @Override
    public void lisaaHakukohdekoodiValintaryhmalle(String valintaryhmaOid, Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodi.getUri());
        if (haettu != null) {
            if (haettu.getValintaryhma() != null && !valintaryhma.equals(haettu.getValintaryhma())) {
                throw new HakukohdekoodiOnLiitettyToiseenValintaryhmaanException("Hakukohdekoodi URI "
                        + haettu.getUri() + " on jo liitetty valintaryhmään OID " + haettu.getValintaryhma().getOid());
            }

            haettu.setValintaryhma(valintaryhma);
        } else {
            haettu = hakukohdekoodiDAO.insert(hakukohdekoodi);
            haettu.setValintaryhma(valintaryhma);
        }
    }

    @Override
    public Hakukohdekoodi lisaaHakukohdekoodiHakukohde(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi) {
        HakukohdeViite hakukohdeViite = hakukohdeService.readByOid(hakukohdeOid);

        Hakukohdekoodi haettu = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodi.getUri());

        if (haettu == null) {
            haettu = hakukohdekoodiDAO.insert(hakukohdekoodi);
        }

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
