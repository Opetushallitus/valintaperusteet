package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;

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
    Response update(@PathParam("oid") String oid, JarjestyskriteeriInsertDTO jk);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<JarjestyskriteeriDTO> jarjesta(List<String> oids);
}
