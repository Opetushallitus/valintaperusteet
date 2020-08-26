package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import java.util.List;

public interface JarjestyskriteeriService {
  List<Jarjestyskriteeri> findJarjestyskriteeriByJono(String oid);

  List<Jarjestyskriteeri> findByHakukohde(String oid);

  void deleteByOid(String oid);

  Jarjestyskriteeri readByOid(String oid);

  Jarjestyskriteeri lisaaJarjestyskriteeriValintatapajonolle(
      String valintatapajonoOid,
      JarjestyskriteeriInsertDTO jarjestyskriteeri);

  List<Jarjestyskriteeri> jarjestaKriteerit(List<String> oids);

  void kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(
      Valintatapajono lisatty, Valintatapajono master, JuureenKopiointiCache kopiointiCache);

  Jarjestyskriteeri update(String oid, JarjestyskriteeriInsertDTO dto);

  void delete(Jarjestyskriteeri jarjestyskriteeri);
}
