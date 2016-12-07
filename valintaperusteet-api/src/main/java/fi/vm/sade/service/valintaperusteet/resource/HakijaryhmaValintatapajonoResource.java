package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hakijaryhma_valintatapajono")
public interface HakijaryhmaValintatapajonoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    HakijaryhmaValintatapajonoDTO read(@PathParam("oid") String oid);

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    Response poistaHakijaryhma(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, HakijaryhmaValintatapajonoDTO jono);
}
