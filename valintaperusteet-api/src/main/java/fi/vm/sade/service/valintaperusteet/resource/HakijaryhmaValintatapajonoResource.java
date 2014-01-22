package fi.vm.sade.service.valintaperusteet.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoUpdateDTO;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Path("hakijaryhma_valintatapajono")
public interface HakijaryhmaValintatapajonoResource {

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    Response poistaHakijaryhma(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, HakijaryhmaValintatapajonoUpdateDTO jono);

}
