package fi.vm.sade.service.valintaperusteet.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;

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
    Response poistaHakijaryhma(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, HakijaryhmaValintatapajonoDTO jono);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/jarjesta")
    List<HakijaryhmaValintatapajonoDTO> jarjesta(@PathParam("oid") String hakijaryhmaValintatapajonoOid, List<String> oids);
}
