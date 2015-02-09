package fi.vm.sade.service.valintaperusteet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentSet;

import java.io.IOException;

/**
 * User: bleed
 * Date: 3/7/13
 * Time: 4:17 PM
 */
public class CollectionSerializer extends JsonSerializer<PersistentSet> {
    @Override
    public void serialize(PersistentSet value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if(value == null || !Hibernate.isInitialized(value)) {
            jgen.writeNull();
            return;
        }

        jgen.writeStartArray();
        for(Object obj : value) {
            JsonSerializer<Object> valueSerializer = provider.findValueSerializer(obj.getClass(), null);
            valueSerializer.serialize(obj, jgen, provider);
        }
        jgen.writeEndArray();
    }
}
