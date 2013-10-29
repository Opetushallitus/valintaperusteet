package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.*;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 17.10.2013
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */

@Component
@Path("puu")
@PreAuthorize("isAuthenticated()")
public class PuuResource {

    @Autowired
    private PuuService puuService;

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured({READ, UPDATE, CRUD})
    public List<ValintaperustePuuDTO> search(
            @QueryParam("q") String searchString,
            @QueryParam("hakuOid") String hakuOid,
            @QueryParam("tila") List<String> tila) {
        return puuService.search(hakuOid, tila, searchString);
    }
}
