package fi.vm.sade.service.valintaperusteet.service.impl.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LaskentakaavaCache {
    private Cache<Long, Laskentakaava> laskentakaavat = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES).build();

    public Long addLaskentakaava(Laskentakaava download, long id) {
        laskentakaavat.put(id, download);
        return id;
    }

    public Laskentakaava get(Long id) {
        Laskentakaava laskentakaava = laskentakaavat.getIfPresent(id);
        return laskentakaava;
    }

    public void clear(){
        laskentakaavat.invalidateAll();
    }
}
