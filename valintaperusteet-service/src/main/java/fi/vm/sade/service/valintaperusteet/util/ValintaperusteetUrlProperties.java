package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.properties.OphProperties;
import org.springframework.stereotype.Component;

@Component
public class ValintaperusteetUrlProperties extends OphProperties {
    public ValintaperusteetUrlProperties() {

        addOptionalFiles("oph-configuration/common.properties", "oph-configuration/valintaperusteet-service-oph.properties");
    }
}
