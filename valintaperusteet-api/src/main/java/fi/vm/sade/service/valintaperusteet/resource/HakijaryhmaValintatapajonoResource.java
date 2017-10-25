package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("hakijaryhma_valintatapajono")
public interface HakijaryhmaValintatapajonoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    HakijaryhmaValintatapajonoDTO read(@PathParam("oid") String oid);

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    Response poistaHakijaryhma(@PathParam("oid") String oid, @Context HttpServletRequest request);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, HakijaryhmaValintatapajonoDTO jono, @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<HakijaryhmaValintatapajonoDTO> jarjesta(List<String> oids, @Context HttpServletRequest request);
}
