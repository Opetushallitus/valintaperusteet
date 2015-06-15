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

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;

@Path("valintakoe")
public interface ValintakoeResource {

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintakoeDTO> readByOids(List<String> oids);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    ValintakoeDTO readByOid(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, ValintakoeDTO valintakoe);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid);
}
