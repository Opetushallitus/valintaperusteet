package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;

public interface HakijaryhmatyyppikoodiService {
    Hakijaryhmatyyppikoodi getOrCreateHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi);
}
