package fi.vm.sade.service.valintaperusteet.service.impl.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectGraphUtil {

  private static Logger LOG = LoggerFactory.getLogger(ObjectGraphUtil.class);

  private static final ObjectMapper OBJECT_MAPPER;
  private static final ThreadLocal<Context> CONTEXT;

  static class Context<T> {
    public Collection<T> objects;
    public Set<Object> visited;
    public Class<T> clazz;
    public boolean hasCycles;

    public Context(Class<T> clazz) {
      this.objects = new ArrayList<>();
      this.visited = new HashSet<>();
      this.clazz = clazz;
      this.hasCycles = false;
    }
  }

  static {
    CONTEXT = new ThreadLocal<>();
    OBJECT_MAPPER = new ObjectMapper();
    SimpleModule module =
        new SimpleModule("ExtractingSerializer", new Version(1, 0, 0, null, null, null));
    module.setSerializerModifier(
        new BeanSerializerModifier() {
          @Override
          public JsonSerializer<?> modifySerializer(
              SerializationConfig config, BeanDescription beanDesc, JsonSerializer serializer) {
            return new StdSerializer(Funktiokutsu.class) {
              @Override
              public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                  throws IOException {
                Context context = CONTEXT.get();
                if (context.visited.contains(value)) {
                  context.hasCycles = true;
                  return;
                }
                try {
                  if (!value.getClass().getPackageName().startsWith("java.lang")) {
                    // jos ei primitiivinen arvo niin voi aiheuttaa syklejä
                    context.visited.add(value);
                  }
                  if (context.clazz.isAssignableFrom(value.getClass())) {
                    context.objects.add(value);
                  }
                  serializer.serialize(value, gen, provider);
                } finally {
                  context.visited.remove(value);
                }
              }
            };
          }
        });
    OBJECT_MAPPER.registerModule(module);
    OBJECT_MAPPER.configure(MapperFeature.USE_ANNOTATIONS, false);
  }

  /**
   * Hakee rekursiivisesti objektipuussa olevat halutun tyyppiset objektit.
   *
   * <p>Totetutuksesta: Jacksonin väärinkäyttö vaikutti olevan vähiten monimutkainen tapa tehdä
   * asia. Nopealla katsauksella Javaan ei ole olemassa kirjastoa joka hoitaisi tämän.
   *
   * @param objectGraph objekti jonka puusta haetaan rekursiiviesti kaikki halutun tyyppiset
   *     objektit
   * @param clazz haluttu objektityyppi
   * @return objektipuun kaikki määritellyn tyyppiset objektit, mukaanlukien juuri
   */
  public static <T> Collection<T> extractObjectsOfType(Object objectGraph, Class<T> clazz) {
    CONTEXT.set(new Context<>(clazz));
    try {
      OBJECT_MAPPER.writeValueAsString(objectGraph);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    Collection<T> result = CONTEXT.get().objects;
    CONTEXT.remove();
    return result;
  }
}
