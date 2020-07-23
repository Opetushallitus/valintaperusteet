package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Koodi;
import java.util.List;

public interface KoodiDAO<T extends Koodi> extends JpaDAO<T, Long> {
  T readByUri(String koodiUri);

  List<T> findByUris(String... koodiUris);

  T insertOrUpdate(T koodi);
}
