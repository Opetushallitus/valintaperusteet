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

  List<Poistettu> findParentHakukohdeviitteet(Collection<Long> ids);

  List<Poistettu> findParentHakukohdeviitteetFromHistory(Collection<Long> ids);

  List<Poistettu> findParentValinnanvaiheet(Collection<Long> ids);

  List<Poistettu> findParentValinnanvaiheetFromHistory(Collection<Long> ids);
}
