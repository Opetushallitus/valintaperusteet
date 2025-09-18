package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import java.util.List;

public interface PuuService {
  List<ValintaperustePuuDTO> search(
      String hakuOid,
      List<String> tila,
      String searchString,
      boolean hakukohteet,
      String kohdejoukko,
      String valintaryhmaOid);

  List<ValintaperustePuuDTO> searchByHaku(String hakuOid);
}
