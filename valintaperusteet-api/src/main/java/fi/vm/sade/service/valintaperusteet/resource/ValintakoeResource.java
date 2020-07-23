package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
  Response update(
      @PathParam("oid") String oid, ValintakoeDTO valintakoe, @Context HttpServletRequest request);

  @DELETE
  @Path("/{oid}")
  Response delete(@PathParam("oid") String oid, @Context HttpServletRequest request);
}
