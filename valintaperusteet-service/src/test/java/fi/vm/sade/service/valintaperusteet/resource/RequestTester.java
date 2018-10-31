package fi.vm.sade.service.valintaperusteet.resource;

import org.jasig.cas.client.authentication.SimplePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class RequestTester {
    private static Logger LOG = LoggerFactory.getLogger(RequestTester.class);
    public static void fakeAuthentication() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(new TestingAuthenticationToken(new SimplePrincipal("1.2.246.562.24.64735725450"), new Object()));
        SecurityContextHolder.setContext(context);
    }

    public RequestTester() {
        LOG.warn("Initialising fake authentication");
        fakeAuthentication();
    }
}
