package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;

import java.util.List;

public interface ValintakoekoodiService {
    void lisaaValintakoekoodiValintaryhmalle(String valintaryhmaOid, KoodiDTO valintakoekoodi);

    void updateValintaryhmanValintakoekoodit(String valintaryhmaOid, List<KoodiDTO> valintakoekoodit);
}
