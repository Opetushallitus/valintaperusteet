package fi.vm.sade.service.valintaperusteet;

import fi.vm.sade.jetty.OpintopolkuJetty;

public class ValintaperusteetJetty extends OpintopolkuJetty {
    public static void main(String... args) {
        new ValintaperusteetJetty().start("/valintaperusteet-service");
    }
}
