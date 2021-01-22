package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import java.util.List;

public interface JarjestyskriteeriService {
  List<Jarjestyskriteeri> findJarjestyskriteeriByJono(String oid);

  List<Jarjestyskriteeri> findByHakukohde(String oid);

  JarjestyskriteeriDTO delete(String jarjestyskriteeriOid);

  Jarjestyskriteeri readByOid(String oid);

  Jarjestyskriteeri lisaaJarjestyskriteeriValintatapajonolle(
      String valintatapajonoOid,
      JarjestyskriteeriCreateDTO jarjestyskriteeri,
      String edellinenValintatapajonoOid,
      Long laskentakaavaOid);

  List<Jarjestyskriteeri> jarjestaKriteerit(List<String> oids);

  void kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(
      Valintatapajono lisatty, Valintatapajono master, JuureenKopiointiCache kopiointiCache);

  Jarjestyskriteeri update(String oid, JarjestyskriteeriCreateDTO incoming, Long laskentakaavaId);
}
