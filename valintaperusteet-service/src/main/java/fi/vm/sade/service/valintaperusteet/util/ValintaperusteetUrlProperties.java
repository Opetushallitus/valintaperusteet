package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.properties.OphProperties;
import org.springframework.stereotype.Component;

@Component
public class ValintaperusteetUrlProperties extends OphProperties {
    public ValintaperusteetUrlProperties() {
        config.addSystemKeyForFiles("oph-properties");
        frontConfig.addSystemKeyForFiles("front-properties");
        String[] files = new String[]{"/valintaperusteet-service-oph.properties"};
        addFiles(files);
    }
}
