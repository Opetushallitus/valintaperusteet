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

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Path("hakijaryhma")
public interface HakijaryhmaResource {

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

}
