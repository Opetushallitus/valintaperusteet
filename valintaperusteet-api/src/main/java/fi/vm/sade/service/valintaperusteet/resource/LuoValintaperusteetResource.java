package fi.vm.sade.service.valintaperusteet.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("luovalintaperusteet")
public interface LuoValintaperusteetResource {

    @GET
    @Path("luo")
    Response luo();
}
