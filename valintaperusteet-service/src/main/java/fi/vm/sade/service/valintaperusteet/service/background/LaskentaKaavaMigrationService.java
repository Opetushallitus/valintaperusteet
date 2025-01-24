package fi.vm.sade.service.valintaperusteet.service.background;

import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Profile("default")
public class LaskentaKaavaMigrationService implements InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(LaskentaKaavaMigrationService.class);

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private TransactionTemplate transactionTemplate;

  private static final int RINNAKKAISUUS = 16;

  private ExecutorService executor = Executors.newWorkStealingPool(RINNAKKAISUUS);

  @Override
  public void afterPropertiesSet() {
    IntStream.range(0, RINNAKKAISUUS)
        .mapToObj(
            i ->
                executor.submit(
                    () -> {
                      while (true) {
                        try {
                          Instant start = Instant.now();
                          Optional<Long> id = laskentakaavaDAO.migrateLaskentakaavat();
                          if (id.isEmpty()) {
                            LOG.warn("No kaavas to migrate, exiting");
                            return;
                          }
                          LOG.warn(
                              "Saved laskentakaava for id: "
                                  + id.get()
                                  + ", duration: "
                                  + Duration.between(start, Instant.now()).toMillis());
                        } catch (Exception e) {
                          LOG.error("Error during laskentakaava migration", e);
                        }
                      }
                    }))
        .toList();
  }
}
