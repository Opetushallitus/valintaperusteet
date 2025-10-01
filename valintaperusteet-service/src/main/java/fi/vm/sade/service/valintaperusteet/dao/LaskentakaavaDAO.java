package fi.vm.sade.service.valintaperusteet.dao;

import com.querydsl.core.Tuple;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import java.util.List;
import java.util.Map;

public interface LaskentakaavaDAO extends JpaDAO<Laskentakaava, Long> {
  Laskentakaava getLaskentakaava(Long id);

  Laskentakaava getLaskentakaavaValintaryhma(Long id);

  /**
   * Hakee kannasta hakukohteille funktiokutsut
   *
   * @param oids Hakukohde oidit
   * @return tuple, missä on hakukohde_viite_oid, valinnanvaihe_oid ja funktiokutsu
   */
  List<Tuple> findLaskentakaavatByHakukohde(List<String> oids);

  List<Laskentakaava> findKaavas(
      boolean all,
      String valintaryhmaOid,
      String hakukohdeOid,
      fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi);

  List<Funktiokutsu> findFunktiokutsuByHakukohdeOid(String hakukohdeOid);

  Map<String, List<Funktiokutsu>> findFunktiokutsuByHakukohdeOids(List<String> hakukohdeOidit);

  boolean isReferencedByOtherLaskentakaavas(Long laskentakaavaId);

  void flush();
}
