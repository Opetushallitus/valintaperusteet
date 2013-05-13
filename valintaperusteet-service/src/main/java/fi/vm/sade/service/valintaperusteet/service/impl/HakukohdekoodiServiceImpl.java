package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

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
    ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    HakukohdekoodiDAO hakukohdekoodiDAO;

    @Autowired
    HakukohdeViiteDAO hakukohdeViiteDAO;

    @Override
    public Valintaryhma updateValintaryhmaHakukohdekoodit(String valintaryhmaOid, Set<Hakukohdekoodi> hakukohdekoodit) {
        Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(valintaryhmaOid);
        Set<Long> ids = new HashSet<Long>();
        for(Hakukohdekoodi uudet : hakukohdekoodit) {
            if(uudet.getId() != null) {
                ids.add(uudet.getId());
            }
        }
        for(Hakukohdekoodi koodi : valintaryhma.getHakukohdekoodit()) {
            if(!ids.contains(koodi.getId())) {
                hakukohdekoodiDAO.remove(koodi);
            }
        }

        for(Hakukohdekoodi koodi : hakukohdekoodit) {
            if(koodi.getId() == null) {
                koodi.setValintaryhma(valintaryhma);
                hakukohdekoodiDAO.insert(koodi);
            } else {
                hakukohdekoodiDAO.update(koodi);
            }
        }

        return valintaryhma;
    }

    @Override
    public void lisaaHakukohdekoodiValintaryhmalle(String valintaryhmaOid, Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(valintaryhmaOid);
        hakukohdekoodi.setValintaryhma(valintaryhma);
        hakukohdekoodiDAO.insert(hakukohdekoodi);
    }

    @Override
    public void lisaaHakukohdekoodiHakukohde(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi) {
        HakukohdeViite hakukohdeViite = hakukohdeViiteDAO.readByOid(hakukohdeOid);
        hakukohdekoodi.setHakukohde(hakukohdeViite);
        hakukohdekoodiDAO.insert(hakukohdekoodi);
    }

    @Override
    public Hakukohdekoodi updateHakukohdeHakukohdekoodi(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi) {
        HakukohdeViite hakukohdeViite = hakukohdeViiteDAO.readByOid(hakukohdeOid);

        // ID:t ei kyllä tule restin läpi, joten tämä on vähän turhaa.
        if(hakukohdeViite.getHakukohdekoodi() != null && !hakukohdeViite.getHakukohdekoodi().getId().equals(hakukohdekoodi.getId())) {
            hakukohdekoodiDAO.remove(hakukohdeViite.getHakukohdekoodi());
        }

        if(hakukohdekoodi.getId() == null) {
            hakukohdekoodi.setHakukohde(hakukohdeViite);
            hakukohdekoodi = hakukohdekoodiDAO.insert(hakukohdekoodi);
        } else {
            hakukohdekoodiDAO.update(hakukohdekoodi);
        }

        return hakukohdekoodi;
    }
}
