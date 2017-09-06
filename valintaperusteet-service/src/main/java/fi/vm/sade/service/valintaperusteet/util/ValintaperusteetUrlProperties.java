package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.properties.OphProperties;
import org.springframework.stereotype.Component;

@Component
public class ValintaperusteetUrlProperties extends OphProperties {
    public ValintaperusteetUrlProperties() {

        addOptionalFiles(System.getProperty("user.home")+"/oph-configuration/common.properties");
        addOptionalFiles("valintaperusteet-service-oph.properties");

    }
}
