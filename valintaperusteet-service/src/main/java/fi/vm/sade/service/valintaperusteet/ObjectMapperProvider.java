package fi.vm.sade.service.valintaperusteet;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
  private final ObjectMapper objectMapper;

  public ObjectMapperProvider() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    SimpleModule module = new SimpleModule("Module", new Version(1, 0, 0, null));
    module.addSerializer(PersistentSet.class, new CollectionSerializer());
    objectMapper.registerModule(module);
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return objectMapper;
  }

  @Bean(name = "valintaperusteetServiceObjectMapper")
  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
