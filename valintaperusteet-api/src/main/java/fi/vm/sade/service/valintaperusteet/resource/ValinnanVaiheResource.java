package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import fi.vm.sade.service.valintaperusteet.dto.*;

@Path("valinnanvaihe")
public interface ValinnanVaiheResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    ValinnanVaiheDTO read(@PathParam("oid") String oid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/valintatapajono")
    List<ValintatapajonoDTO> listJonos(@PathParam("oid") String oid);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/valintatapajonot")
    List<ValinnanVaiheJaValintatapajonoDTO> valintatapajonot(List<String> valinnanvaiheOidit);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/valintakoe")
    List<ValintakoeDTO> listValintakokeet(@PathParam("oid") String oid);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentOid}/valintatapajono")
    Response addJonoToValinnanVaihe(@PathParam("parentOid") String parentOid, ValintatapajonoCreateDTO jono, @Context HttpServletRequest request);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentOid}/valintakoe")
    Response addValintakoeToValinnanVaihe(@PathParam("parentOid") String parentOid, ValintakoeCreateDTO koe, @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    ValinnanVaiheDTO update(@PathParam("oid") String oid, ValinnanVaiheCreateDTO valinnanVaihe, @Context HttpServletRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<ValinnanVaiheDTO> jarjesta(List<String> oids, @Context HttpServletRequest request);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid, @Context HttpServletRequest request);

    @GET
    @Path("/{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Boolean> kuuluuSijoitteluun(@PathParam("oid") String oid);

    @POST
    @Path("/kuuluuSijoitteluun")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Boolean> kuuluuSijoitteluun(List<String> valinnanvaiheOidit);
}
