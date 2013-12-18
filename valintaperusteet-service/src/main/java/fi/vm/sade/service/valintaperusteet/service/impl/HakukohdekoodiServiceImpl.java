package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.impl.util.koodi.HakukohdekoodiHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;

/**
 * User: jukais
 * Date: 13.5.2013
 * Time: 13.03
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

    @Autowired
    private ValintaperusteetModelMapper modelMapper;


    @Override
    public void updateValintaryhmaHakukohdekoodit(String valintaryhmaOid, Set<KoodiDTO> hakukohdekoodit) {
        new HakukohdekoodiHandler(valintaryhmaService, hakukohdekoodiDAO)
                .paivitaValintaryhmanKoodit(valintaryhmaOid, hakukohdekoodit == null ? null : modelMapper.mapList(new ArrayList<KoodiDTO>(hakukohdekoodit), Hakukohdekoodi.class));
    }

    @Override
    public void lisaaHakukohdekoodiValintaryhmalle(String valintaryhmaOid, KoodiDTO hakukohdekoodi) {
        new HakukohdekoodiHandler(valintaryhmaService, hakukohdekoodiDAO)
                .lisaaKoodiValintaryhmalle(valintaryhmaOid, modelMapper.map(hakukohdekoodi, Hakukohdekoodi.class));
    }

    @Override
    public Hakukohdekoodi lisaaHakukohdekoodiHakukohde(String hakukohdeOid, KoodiDTO hakukohdekoodi) {
        HakukohdeViite hakukohdeViite = hakukohdeService.readByOid(hakukohdeOid);


        Hakukohdekoodi haettu = hakukohdekoodiDAO.readByUri(hakukohdekoodi.getUri());

        if (haettu == null) {
            haettu = hakukohdekoodiDAO.insert(modelMapper.map(hakukohdekoodi, Hakukohdekoodi.class));
        }

        haettu.setArvo(hakukohdekoodi.getArvo());
        haettu.setNimiEn(hakukohdekoodi.getNimiEn());
        haettu.setNimiFi(hakukohdekoodi.getNimiFi());
        haettu.setNimiSv(hakukohdekoodi.getNimiSv());
        hakukohdeViite.setHakukohdekoodi(haettu);

        return haettu;
    }

    @Override
    public Hakukohdekoodi updateHakukohdeHakukohdekoodi(String hakukohdeOid, KoodiDTO hakukohdekoodi) {
        return lisaaHakukohdekoodiHakukohde(hakukohdeOid, hakukohdekoodi);
    }
}
