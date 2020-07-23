package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hakijaryhma")
public interface HakijaryhmaResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{oid}")
  HakijaryhmaDTO read(@PathParam("oid") String oid);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{oid}")
  HakijaryhmaDTO update(
      @PathParam("oid") String oid,
      HakijaryhmaCreateDTO hakijaryhma,
      @Context HttpServletRequest request);

  @DELETE
  @Path("/{oid}")
  Response delete(@PathParam("oid") String oid, @Context HttpServletRequest request);

  @PUT
  @Path("/siirra")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Response siirra(HakijaryhmaSiirraDTO dto, @Context HttpServletRequest request);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/jarjesta")
  List<HakijaryhmaDTO> jarjesta(List<String> oids, @Context HttpServletRequest request);
}
