package fi.vm.sade.service.valintaperusteet;

import fi.vm.sade.jetty.OpintopolkuJetty;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.time.Duration;

import static fi.vm.sade.service.valintaperusteet.ValintaperusteetJetty.VALINTAPERUSTEET_SERVICE_CONTEXT;

/**
 * Huom: Jos haluat ajaa tätä luokkaa IDEstä, aseta sen työhakemistoksi projektin juurihakemisto.
 *
 * Normaali käyttö: https://github.com/Opetushallitus/local-environment
 */
public class ValintaperusteetJettyForOpintopolkuLocal extends OpintopolkuJetty {
    public final static int port = Integer.parseInt(System.getProperty("valintaperusteet-service.port", "8081"));

    public static void main(String... args) throws IOException {
        new ValintaperusteetJettyForOpintopolkuLocal().start();
    }

    private void start() throws IOException {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setBaseResource(Resource.newResource("./valintaperusteet-service/target/classes/webapp"));
        webAppContext.setDescriptor("./valintaperusteet-service/target/classes/webapp/WEB-INF/web.xml");
        start(webAppContext, createServer(Duration.ofMinutes(1)), VALINTAPERUSTEET_SERVICE_CONTEXT);
    }

    private Server createServer(Duration idleThreadTimeout) {
        int idleThreadTimeoutMs = (int) idleThreadTimeout.toMillis();
        ThreadPool threadPool = createThreadpool(1, 10, idleThreadTimeoutMs);
        Server server = new Server(threadPool);
        ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(ValintaperusteetJettyForOpintopolkuLocal.port);
        server.setConnectors(new Connector[]{serverConnector});
        return server;
    }
}
