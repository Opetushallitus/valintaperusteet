package fi.vm.sade.service.valintaperusteet.service.impl.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FunktiokutsuCache {
    private final Cache<Long, Funktiokutsu> funktiokutsuCache =
            CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    public void add(Long id, Funktiokutsu funktiokutsu) {
        funktiokutsuCache.put(id, funktiokutsu);
    }

    public Funktiokutsu get(Long id) {
        return funktiokutsuCache.getIfPresent(id);
    }
}
