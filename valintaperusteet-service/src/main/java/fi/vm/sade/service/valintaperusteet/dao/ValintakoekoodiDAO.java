package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import java.util.List;
import java.util.Set;

public interface ValintakoekoodiDAO
    extends JpaDAO<Valintakoekoodi, Long>, KoodiDAO<Valintakoekoodi> {
  Set<Valintakoekoodi> findByValintaryhma(String valintaryhmaOid);

  Valintakoekoodi readByUri(String uri);

  List<Valintakoekoodi> findByUris(String[] koodiUris);

  Valintakoekoodi insertOrUpdate(Valintakoekoodi koodi);
}
