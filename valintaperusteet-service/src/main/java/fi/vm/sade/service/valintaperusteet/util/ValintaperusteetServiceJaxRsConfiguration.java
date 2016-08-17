package fi.vm.sade.service.valintaperusteet.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValintaperusteetServiceJaxRsConfiguration {
    private final ObjectMapper objectMapper;

    @Autowired
    public ValintaperusteetServiceJaxRsConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean(name="valintaperusteetServiceJsonProvider")
    public JacksonJsonProvider getJacksonJsonProvider() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new JacksonJsonProvider(objectMapper);
    }
}
