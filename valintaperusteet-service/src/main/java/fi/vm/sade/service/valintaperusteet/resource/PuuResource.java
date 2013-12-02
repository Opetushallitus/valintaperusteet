package fi.vm.sade.service.valintaperusteet.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.PuuService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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
@Api(value = "/puu", description = "Resurssi valintaperustepuun hakemiseen")
public class PuuResource {

    @Autowired
    private PuuService puuService;

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Hakee valintaperustepuun annettujen parametrien perusteella", response = ValintaperustePuuDTO.class)
    public List<ValintaperustePuuDTO> search(
            @ApiParam(value = "Hakulauseke") @QueryParam("q") String searchString,
            @ApiParam(value = "Haun OID") @QueryParam("hakuOid") String hakuOid,
            @ApiParam(value = "Tila") @QueryParam("tila") List<String> tila,
            @ApiParam(value = "Hakukohteet") @QueryParam("hakukohteet") @DefaultValue("true") boolean hakukohteet) {
        return puuService.search(hakuOid, tila, searchString, hakukohteet);
    }
}
