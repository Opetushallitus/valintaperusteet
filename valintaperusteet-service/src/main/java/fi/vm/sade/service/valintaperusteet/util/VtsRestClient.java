package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.generic.rest.CachingRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class VtsRestClient {
    private final CachingRestClient restClient;
    @Value("${cas.service.valintaperusteet-service}")
    private String casService;

    @Value("${cas.callback.valintaperusteet-service}")
    private String casCallbackurl;

    @Value("${valintaperusteet.service.username.to.valinta.tulos.service}")
    private String vtsUsername;

    @Value("${valintaperusteet.service.password.to.valinta.tulos.service}")
    private String vtsPassword;

    private final ValintaperusteetUrlProperties valintaperusteetUrlProperties;

    @Autowired
    public VtsRestClient(ValintaperusteetUrlProperties valintaperusteetUrlProperties) {

        restClient = new CachingRestClient();

        restClient.setCallerId("valintaperusteet-service");
        restClient.setClientSubSystemCode("valintaperusteet-service");
        restClient.setCasService(casService);
        restClient.setWebCasUrl(casCallbackurl);
        restClient.setUsername(vtsUsername);
        restClient.setPassword(vtsPassword);

        this.valintaperusteetUrlProperties = valintaperusteetUrlProperties;
    }

    public boolean isJonoSijoiteltu(String jonoOid) throws IOException {
        String url = valintaperusteetUrlProperties.url("valinta-tulos-service.sijotteluexistsForJono", jonoOid);
        HashMap<String, Boolean> existsResponse = restClient.get(url, HashMap.class);
        Boolean exists = existsResponse.get("IsSijoiteltu");
        if(exists == null) return false;
        return exists;
    }
}
