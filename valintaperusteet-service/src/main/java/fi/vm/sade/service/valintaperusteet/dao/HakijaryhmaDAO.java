package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import java.util.List;

public interface HakijaryhmaDAO extends JpaDAO<Hakijaryhma, Long> {
  Hakijaryhma readByOid(String oid);

  List<Hakijaryhma> findByValintatapajono(String oid);

  List<Hakijaryhma> findByHakukohde(String oid);

  List<Hakijaryhma> findByValintaryhma(String oid);

  Hakijaryhma haeValintaryhmanViimeinenHakijaryhma(String valintaryhmaOid);

  List<Hakijaryhma> findByLaskentakaava(long id);
}
