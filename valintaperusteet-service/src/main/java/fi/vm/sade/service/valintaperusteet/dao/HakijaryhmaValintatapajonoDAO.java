package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import java.util.Collection;
import java.util.List;

public interface HakijaryhmaValintatapajonoDAO extends JpaDAO<HakijaryhmaValintatapajono, Long> {
  HakijaryhmaValintatapajono readByOid(String oid);

  List<HakijaryhmaValintatapajono> findByValintatapajono(String oid);

  List<HakijaryhmaValintatapajono> findByValintatapajonos(List<String> oids);

  List<HakijaryhmaValintatapajono> findByHakukohde(String oid);

  List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> oids);

  HakijaryhmaValintatapajono haeHakukohteenViimeinenHakijaryhma(String hakukohdeOid);

  HakijaryhmaValintatapajono haeValintatapajononViimeinenHakijaryhma(String valintatapajonoOid);

  List<HakijaryhmaValintatapajono> jarjestaUudelleen(
      HakukohdeViite hakukohdeViite, List<String> uusiJarjestys);
}
