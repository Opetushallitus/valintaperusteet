package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import fi.vm.sade.service.valintaperusteet.dto.*;

@Path("hakijaryhma")
public interface HakijaryhmaResource {

    /**
     * Operation is idempotent
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/haku")
    List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(List<String> hakukohdeOids);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/haku/{hakuOid}")
    List<HakijaryhmaValintatapajonoDTO> readByHakuOid(@PathParam("hakuOid") String hakuOid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    HakijaryhmaDTO read(@PathParam("oid") String oid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{hakijaryhmaOid}/valintatapajono")
    List<HakijaryhmaValintatapajonoDTO> valintatapajonot(@PathParam("hakijaryhmaOid") String hakijaryhmaOid);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    HakijaryhmaDTO update(@PathParam("oid") String oid, HakijaryhmaCreateDTO hakijaryhma);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<HakijaryhmaDTO> jarjesta(List<String> oids);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid);

    @PUT
    @Path("/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response siirra(HakijaryhmaSiirraDTO dto);
}
