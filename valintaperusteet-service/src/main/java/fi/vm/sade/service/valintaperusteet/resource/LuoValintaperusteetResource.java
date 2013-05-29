package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.service.LuoValintaperusteetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/luovalintaperusteet")
public class LuoValintaperusteetResource {

    @Autowired
    private LuoValintaperusteetService luoValintaperusteetService;

    @GET
    @Path("/luo")
    public Response luo() {
        try {
            luoValintaperusteetService.luo();
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (IOException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
