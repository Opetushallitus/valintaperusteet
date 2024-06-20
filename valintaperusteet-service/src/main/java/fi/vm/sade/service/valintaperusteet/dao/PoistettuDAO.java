package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Poistettu;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PoistettuDAO {
  List<Poistettu> findPoistetutHakukohdeViitteet(LocalDateTime start, LocalDateTime end);

  List<Poistettu> findPoistetutValinnanvaiheet(LocalDateTime start, LocalDateTime end);

  List<Poistettu> findPoistetutValintatapajonot(LocalDateTime start, LocalDateTime end);

  List<Poistettu> findPoistetutValintakokeet(LocalDateTime start, LocalDateTime end);

  List<Poistettu> findPoistetutValintaperusteet(LocalDateTime start, LocalDateTime end);

  List<Poistettu> findHakukohdeviitteetFromHistory(Collection<Long> ids);

  List<Poistettu> findValinnanvaiheetFromHistory(Collection<Long> ids);
}
