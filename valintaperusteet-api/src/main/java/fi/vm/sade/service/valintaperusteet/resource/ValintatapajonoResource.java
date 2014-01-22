package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Path("valintatapajono")
public interface ValintatapajonoResource {

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
            @PathParam("hakijaryhmaOid") String hakijaryhmaOid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma")
    List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(@PathParam("valintatapajonoOid") String valintatapajonoOid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintatapajonoDTO> findAll();

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, ValintatapajonoCreateDTO jono);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/jarjestyskriteeri")
    Response insertJarjestyskriteeri(@PathParam("valintatapajonoOid") String valintatapajonoOid,
            JarjestyskriteeriInsertDTO jk) throws Exception;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<ValintatapajonoDTO> jarjesta(List<String> oids);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid);

}
