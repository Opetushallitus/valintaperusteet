package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.OPH_CRUD;

import fi.vm.sade.service.valintaperusteet.resource.LuoValintaperusteetResource;
import fi.vm.sade.service.valintaperusteet.service.LuoValintaperusteetService;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@Path("luovalintaperusteet")
@PreAuthorize("isAuthenticated()")
public class LuoValintaperusteetResourceImpl implements LuoValintaperusteetResource {
  @Autowired private LuoValintaperusteetService luoValintaperusteetService;

  @GET
  @Path("luo")
  @PreAuthorize(OPH_CRUD)
  public Response luo() {
    try {
      luoValintaperusteetService.luo();
      return Response.status(Response.Status.ACCEPTED).build();
    } catch (IOException e) {
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
  }
}
