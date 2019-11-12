package fi.vm.sade.service.valintaperusteet;

import fi.vm.sade.jetty.OpintopolkuJetty;

public class ValintaperusteetJetty extends OpintopolkuJetty {
    public  static final String VALINTAPERUSTEET_SERVICE_CONTEXT = "/valintaperusteet-service";

    public static void main(String... args) {
        new ValintaperusteetJetty().start(VALINTAPERUSTEET_SERVICE_CONTEXT);
    }
}
