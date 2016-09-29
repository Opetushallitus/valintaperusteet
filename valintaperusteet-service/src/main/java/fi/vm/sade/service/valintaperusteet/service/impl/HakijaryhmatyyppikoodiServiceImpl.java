package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmatyyppikoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.KoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Koodi;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class HakijaryhmatyyppikoodiServiceImpl implements HakijaryhmatyyppikoodiService {

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private HakijaryhmatyyppikoodiDAO hakijaryhmatyyppikoodiDAO;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public void lisaaHakijaryhmatyyppikoodiHakijaryhmalle(String hakijaryhmaOid, KoodiDTO hakijaryhmatyyppikoodi) {
        Hakijaryhma hakijaryhma = hakijaryhmaService.readByOid(hakijaryhmaOid);
        Hakijaryhmatyyppikoodi haettu = hakijaryhmatyyppikoodiDAO.readByUri(hakijaryhmatyyppikoodi.getUri());
        if (haettu == null) {
            haettu = hakijaryhmatyyppikoodiDAO.insert(modelMapper.map(hakijaryhmatyyppikoodi, Hakijaryhmatyyppikoodi.class));
        }
        haettu.setArvo(hakijaryhmatyyppikoodi.getArvo());
        haettu.setNimiEn(hakijaryhmatyyppikoodi.getNimiEn());
        haettu.setNimiFi(hakijaryhmatyyppikoodi.getNimiFi());
        haettu.setNimiSv(hakijaryhmatyyppikoodi.getNimiSv());

        addTyyppikoodiToHakijaryhma(hakijaryhma, haettu);
    }

    @Override
    public void updateHakijaryhmanTyyppikoodi(String hakijaryhmaOid, List<KoodiDTO> hakijaryhmatyyppikoodi) {
        Collection<Hakijaryhmatyyppikoodi> koodit = modelMapper.mapList(hakijaryhmatyyppikoodi, Hakijaryhmatyyppikoodi.class);

        Hakijaryhma hakijaryhma = hakijaryhmaService.readByOid(hakijaryhmaOid);
        hakijaryhma.getHakijaryhmatyyppikoodit().clear();

        Map<String, Koodi> urit = new HashMap<String, Koodi>();
        Map<String, Integer> esiintymat = new HashMap<String, Integer>();
        if (koodit == null) {
            koodit = new ArrayList<Hakijaryhmatyyppikoodi>();
        }
        for (Koodi k : koodit) {
            if (!urit.containsKey(k.getUri())) {
                urit.put(k.getUri(), k);
                esiintymat.put(k.getUri(), 1);
            } else {
                Integer lkm = esiintymat.get(k.getUri()) + 1;
                esiintymat.put(k.getUri(), lkm);
            }
        }
        List<Hakijaryhmatyyppikoodi> managedKoodis = hakijaryhmatyyppikoodiDAO.findByUris(urit.keySet().toArray(new String[urit.keySet().size()]));
        for (Hakijaryhmatyyppikoodi managed : managedKoodis) {
            if (urit.containsKey(managed.getUri())) {
                for (int i = 0; i < esiintymat.get(managed.getUri()); ++i) {
                    KoodiDTO kodi = new KoodiDTO();
                    kodi.setUri(managed.getUri());
                    kodi.setArvo(managed.getArvo());
                    kodi.setNimiEn(managed.getNimiEn());
                    kodi.setNimiFi(managed.getNimiFi());
                    kodi.setNimiSv(managed.getNimiSv());
                    lisaaHakijaryhmatyyppikoodiHakijaryhmalle(hakijaryhmaOid, kodi);
                }
                urit.remove(managed.getUri());
                esiintymat.remove(managed.getUri());
            }
        }
        for (Koodi uusiKoodi : urit.values()) {
            for (int i = 0; i < esiintymat.get(uusiKoodi.getUri()); ++i) {
                KoodiDTO kodi = new KoodiDTO();
                kodi.setUri(uusiKoodi.getUri());
                kodi.setArvo(uusiKoodi.getArvo());
                kodi.setNimiEn(uusiKoodi.getNimiEn());
                kodi.setNimiFi(uusiKoodi.getNimiFi());
                kodi.setNimiSv(uusiKoodi.getNimiSv());
                lisaaHakijaryhmatyyppikoodiHakijaryhmalle(hakijaryhmaOid, kodi);
            }
        }
    }

    private void addTyyppikoodiToHakijaryhma(Hakijaryhma hakijaryhma, Hakijaryhmatyyppikoodi koodi){
        hakijaryhma.getHakijaryhmatyyppikoodit().add(koodi);
    }
}
