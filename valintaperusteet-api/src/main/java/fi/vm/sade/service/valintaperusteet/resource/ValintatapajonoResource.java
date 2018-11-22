package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.*;

@Path("valintatapajono")
public interface ValintatapajonoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/kopiot")
    Map<String, List<String>> findKopiot(@QueryParam("oid") List<String> oid);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    ValintatapajonoDTO readByOid(@PathParam("oid") String oid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/jarjestyskriteeri")
    List<JarjestyskriteeriDTO> findJarjestyskriteeri(@PathParam("oid") String oid);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma/{hakijaryhmaOid}")
    Response liitaHakijaryhma(@PathParam("valintatapajonoOid") String valintatapajonoOid,
                              @PathParam("hakijaryhmaOid") String hakijaryhmaOid, @Context HttpServletRequest request);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma")
    List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(@PathParam("valintatapajonoOid") String valintatapajonoOid);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma")
    Response insertHakijaryhma(@PathParam("valintatapajonoOid") String valintatapajonoOid, HakijaryhmaCreateDTO hakijaryhma, @Context HttpServletRequest request);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintatapajonoDTO> findAll();

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, ValintatapajonoCreateDTO jono, @Context HttpServletRequest request);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/jarjestyskriteeri")
    Response insertJarjestyskriteeri(@PathParam("valintatapajonoOid") String valintatapajonoOid,
                                     JarjestyskriteeriInsertDTO jk,
                                     @Context HttpServletRequest request) throws Exception;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<ValintatapajonoDTO> jarjesta(List<String> oids, @Context HttpServletRequest request);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid, @Context HttpServletRequest request);
}
