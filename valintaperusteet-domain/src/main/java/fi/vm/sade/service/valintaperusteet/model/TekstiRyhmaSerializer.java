package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TekstiRyhmaSerializer extends JsonSerializer<TekstiRyhma> {

  @Override
  public void serialize(TekstiRyhma value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    Map<String, Object> map = new HashMap<>();
    map.put("id", value.getId());
    map.put("tekstit", value.getTekstit());
    map.put("version", value.getVersion());
    gen.writeObject(map);
  }
}
