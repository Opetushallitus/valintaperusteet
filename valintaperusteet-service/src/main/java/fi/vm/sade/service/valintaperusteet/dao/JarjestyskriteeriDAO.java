package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import java.util.List;

public interface JarjestyskriteeriDAO extends JpaDAO<Jarjestyskriteeri, Long> {
  /**
   * Palauttaa valintatapajonon jarjestyskriteerit prioriteettijarjestyksessa
   *
   * @param oid Valintatapajono
   * @return Lista prioriteettijarjestyksessa
   */
  List<Jarjestyskriteeri> findByJono(String oid);

  List<Jarjestyskriteeri> findByHakukohde(String oid);

  Jarjestyskriteeri readByOid(String oid);

  Jarjestyskriteeri haeValintatapajononViimeinenJarjestyskriteeri(String valintatapajonoOid);

  List<Jarjestyskriteeri> findByLaskentakaava(long id);

  List<Jarjestyskriteeri> jarjestaUudelleen(Valintatapajono jono, List<String> uusiJarjestys);

  List<Jarjestyskriteeri> jarjestaUudelleenMasterJarjestyksenMukaan(
      Valintatapajono jono, List<Jarjestyskriteeri> uusiMasterJarjestys);

  void delete(Jarjestyskriteeri jarjestyskriteeri);
}
