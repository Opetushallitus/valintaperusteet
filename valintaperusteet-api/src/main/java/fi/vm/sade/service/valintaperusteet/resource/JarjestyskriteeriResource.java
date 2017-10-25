package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("jarjestyskriteeri")
public interface JarjestyskriteeriResource {

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    JarjestyskriteeriDTO readByOid(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, JarjestyskriteeriInsertDTO jk, @Context HttpServletRequest request);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid, @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<JarjestyskriteeriDTO> jarjesta(List<String> oids, @Context HttpServletRequest request);
}
