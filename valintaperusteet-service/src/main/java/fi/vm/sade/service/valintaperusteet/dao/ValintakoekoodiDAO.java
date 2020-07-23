package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import java.util.List;

public interface ValintakoekoodiDAO
    extends JpaDAO<Valintakoekoodi, Long>, KoodiDAO<Valintakoekoodi> {
  List<Valintakoekoodi> findByValintaryhma(String valintaryhmaOid);

  Valintakoekoodi readByUri(String uri);

  List<Valintakoekoodi> findByUris(String[] koodiUris);

  Valintakoekoodi insertOrUpdate(Valintakoekoodi koodi);
}
