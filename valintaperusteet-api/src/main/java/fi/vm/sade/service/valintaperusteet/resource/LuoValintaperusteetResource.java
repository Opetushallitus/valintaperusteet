package fi.vm.sade.service.valintaperusteet.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Path("luovalintaperusteet")
public interface LuoValintaperusteetResource {

    @GET
    @Path("luo")
    Response luo();

}
