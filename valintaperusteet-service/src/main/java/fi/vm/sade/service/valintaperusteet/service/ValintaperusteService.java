package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeImportDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ValintaperusteService {
  List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(String hakukohdeOid);

  Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
      Collection<String> hakukohdeOids);

  List<ValintaperusteetDTO> haeValintaperusteet(List<HakuparametritDTO> hakuparametrit);

  void tuoHakukohde(HakukohdeImportDTO hakukohde);
}
