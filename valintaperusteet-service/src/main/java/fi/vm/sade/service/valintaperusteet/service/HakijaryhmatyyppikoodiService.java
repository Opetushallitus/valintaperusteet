package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;

import java.util.List;

public interface HakijaryhmatyyppikoodiService {
    void lisaaHakijaryhmatyyppikoodiHakijaryhmalle(String hakijaryhmaOid, KoodiDTO hakijaryhmatyyppikoodi);

    void updateHakijaryhmanTyyppikoodi(String hakijaryhmaOid, List<KoodiDTO> hakijaryhmatyyppikoodi);
}
