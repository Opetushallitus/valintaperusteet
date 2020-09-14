package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.List;

public interface HakijaryhmaDAO extends JpaDAO<Hakijaryhma, Long> {
  Hakijaryhma readByOid(String oid);

  List<Hakijaryhma> findByValintaryhma(String oid);

  Hakijaryhma haeValintaryhmanViimeinenHakijaryhma(String valintaryhmaOid);

  List<Hakijaryhma> findByLaskentakaava(long id);

  List<Hakijaryhma> jarjestaUudelleen(Valintaryhma valintaryhma, List<String> uusiJarjestys);

  void delete(Hakijaryhma hakijaryhma);

  Hakijaryhma insert(Hakijaryhma uusi);
}
