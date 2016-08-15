package fi.vm.sade.service.valintaperusteet.resource.impl;

import fi.vm.sade.generic.rest.CorsFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ValintaperusteetServiceJerseyResourceConfiguration extends ResourceConfig {
    public ValintaperusteetServiceJerseyResourceConfiguration() {
        register(JacksonFeature.class);
        register(HakijaryhmaResourceImpl.class);
        register(HakijaryhmaValintatapajonoResourceImpl.class);
        register(HakukohdeResourceImpl.class);
        register(JarjestyskriteeriResourceImpl.class);
        register(LaskentakaavaResourceImpl.class);
        register(LuoValintaperusteetResourceImpl.class);
        register(PuuResourceImpl.class);
        register(ValinnanVaiheResourceImpl.class);
        register(ValintakoeResourceImpl.class);
        register(ValintalaskentakoostepalveluResourceImpl.class);
        register(ValintaryhmaResourceImpl.class);
        register(ValintatapajonoResourceImpl.class);
        registerInstances(
            new com.wordnik.swagger.jaxrs.listing.ResourceListingProvider(),
            new com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider());
        register(com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON.class);
        register(CorsFilter.class);
    }
}
