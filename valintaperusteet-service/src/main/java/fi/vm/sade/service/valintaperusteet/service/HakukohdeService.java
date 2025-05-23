package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeKoosteTietoDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import java.util.List;

public interface HakukohdeService {
  List<HakukohdeViite> findAll();

  List<HakukohdeViite> haunHakukohteet(String hakuOid, Boolean vainValintakokeelliset);

  HakukohdeViite readByOid(String oid);

  List<HakukohdeViite> readByOids(List<String> oids);

  List<HakukohdeViite> findRoot();

  List<HakukohdeViite> findByValintaryhmaOid(String oid);

  HakukohdeViite update(String oid, HakukohdeViiteCreateDTO incoming) throws Exception;

  List<HakukohdeKoosteTietoDTO> haunHakukohdeTiedot(String hakuOid);

  boolean kuuluuSijoitteluun(String oid);

  List<ValinnanVaihe> ilmanLaskentaa(String oid);

  List<ValinnanVaihe> vaiheetJaJonot(String oid);

  HakukohdeViite siirraHakukohdeValintaryhmaan(
      String hakukohdeOid, String valintaryhmaOid, boolean siirretaanManuaalisesti);

  HakukohdeViite insert(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid);
}
