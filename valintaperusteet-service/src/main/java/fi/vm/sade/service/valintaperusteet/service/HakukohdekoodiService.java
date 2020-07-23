package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import java.util.Set;

public interface HakukohdekoodiService {
  void updateValintaryhmaHakukohdekoodit(String valintaryhmaOid, Set<KoodiDTO> hakukohdekoodit);

  void lisaaHakukohdekoodiValintaryhmalle(String valintaryhmaOid, KoodiDTO hakukohdekoodi);

  Hakukohdekoodi lisaaHakukohdekoodiHakukohde(String hakukohdeOid, KoodiDTO hakukohdekoodi);

  Hakukohdekoodi updateHakukohdeHakukohdekoodi(String hakukohdeOid, KoodiDTO hakukohdekoodi);
}
