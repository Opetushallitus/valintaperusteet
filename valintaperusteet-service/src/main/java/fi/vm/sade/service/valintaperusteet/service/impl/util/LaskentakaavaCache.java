package fi.vm.sade.service.valintaperusteet.service.impl.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class LaskentakaavaCache {
  private Cache<Long, Laskentakaava> laskentakaavat =
      CacheBuilder.newBuilder()
          .expireAfterWrite(30, TimeUnit.MINUTES)
          .maximumSize(5000)
          .softValues()
          .build();

  public Long addLaskentakaava(Laskentakaava download, long id) {
    laskentakaavat.put(id, download);
    return id;
  }

  public Laskentakaava get(Long id) {
    Laskentakaava laskentakaava = laskentakaavat.getIfPresent(id);
    return laskentakaava;
  }

  public void clear() {
    laskentakaavat.invalidateAll();
  }
}
