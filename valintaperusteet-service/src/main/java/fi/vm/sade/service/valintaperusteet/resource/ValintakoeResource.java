package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.04
 */
@Component
@Path("valintakoe")
@PreAuthorize("isAuthenticated()")
public class ValintakoeResource {

    @Autowired
    private ValintakoeService valintakoeService;

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public Valintakoe readByOid(@PathParam("oid") String oid) {
        return valintakoeService.readByOid(oid);
    }


    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response update(@PathParam("oid") String oid, ValintakoeDTO valintakoe) {
        Valintakoe update = valintakoeService.update(oid, valintakoe);

        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @DELETE
    @Path("{oid}")
    @Secured({CRUD})
    public Response delete(@PathParam("oid") String oid) {
        valintakoeService.deleteByOid(oid);
        return Response.status(Response.Status.ACCEPTED).build();
    }


}
