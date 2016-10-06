package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;

import java.util.List;

public interface HakijaryhmatyyppikoodiService {
    void updateHakijaryhmanTyyppikoodi(String hakijaryhmaOid, KoodiDTO hakijaryhmatyyppikoodi);

    void updateHakijaryhmaValintatapajononTyyppikoodi(String hakijaryhmaJonoOid, KoodiDTO hakijaryhmatyyppikoodi);
}
