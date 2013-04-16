package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.04
 */
@Component
@Path("/valintakoe")
public class ValintakoeResource {

    @Autowired
    private ValintakoeService valintakoeService;

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Valintakoe readByOid(@PathParam("oid") String oid) {
        return valintakoeService.readByOid(oid);
    }
}
