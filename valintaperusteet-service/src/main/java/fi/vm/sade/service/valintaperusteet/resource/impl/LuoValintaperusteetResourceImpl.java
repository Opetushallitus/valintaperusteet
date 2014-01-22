package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD_OPH;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import fi.vm.sade.service.valintaperusteet.resource.LuoValintaperusteetResource;
import fi.vm.sade.service.valintaperusteet.service.LuoValintaperusteetService;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Component
@Path("luovalintaperusteet")
@PreAuthorize("isAuthenticated()")
public class LuoValintaperusteetResourceImpl implements LuoValintaperusteetResource {

    @Autowired
    private LuoValintaperusteetService luoValintaperusteetService;

    @GET
    @Path("luo")
    @Secured({ CRUD_OPH })
    public Response luo() {

        try {
            luoValintaperusteetService.luo();
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (IOException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
