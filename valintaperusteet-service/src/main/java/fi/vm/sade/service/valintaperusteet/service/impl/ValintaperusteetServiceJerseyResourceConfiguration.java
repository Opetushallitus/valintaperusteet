package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.generic.rest.CorsFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ValintaperusteetServiceJerseyResourceConfiguration extends ResourceConfig {
    public ValintaperusteetServiceJerseyResourceConfiguration() {
        register(JacksonFeature.class);
        register(DummyOidServiceImpl.class);
        register(HakijaryhmaServiceImpl.class);
        register(HakijaryhmaValintatapajonoServiceImpl.class);
        register(HakukohdeImportServiceImpl.class);
        register(HakukohdeServiceImpl.class);
        register(JarjestyskriteeriServiceImpl.class);
        register(LaskentakaavaServiceImpl.class);
        register(LuoValintaperusteetServiceImpl.class);
        register(PuuServiceImpl.class);
        register(ValinnanVaiheServiceImpl.class);
        register(ValintakoekoodiServiceImpl.class);
        register(ValintakoeServiceImpl.class);
        register(ValintaryhmaServiceImpl.class);
        register(ValintaryhmaServiceImpl.class);
        registerInstances(
            new com.wordnik.swagger.jaxrs.listing.ResourceListingProvider(),
            new com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider());
        register(com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON.class);
        register(CorsFilter.class);
    }
}
