package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.javautils.legacy_caching_rest_client.CachingRestClient;
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
        String callerId = "1.2.246.562.10.00000000001.valintaperusteet.valintaperusteet-service";
        restClient = new CachingRestClient(callerId);

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
