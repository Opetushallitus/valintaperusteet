package fi.vm.sade.service.valintaperusteet.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("hakijaryhma_valintatapajono")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma_valintatapajono", description = "Resurssi hakijaryhmien ja valintatapajonojen välisten liitosten käsittelyyn")
public class HakijaryhmaValintatapajonoResource {
    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoResource.class);

    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Path("/{oid}")
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Poistaa hakijaryhmän ja valintatapajonon välisen liitoksen")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Liitosta ei voida poistaa, esim. se on peritty"),
    })
    public Response poistaHakijaryhma(@PathParam("oid") String oid) {
        try {
            hakijaryhmaValintatapajonoService.deleteByOid(oid, false);
            return Response.status(Response.Status.OK).build();
        } catch (HakijaryhmaaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            LOGGER.error("Error removing hakijaryhma.", e);
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        }
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    @ApiOperation(value = "Päivittää hakijaryhmän ja valintatapajonon välistä liitosta")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Liitosta ei ole olemassa"),
    })
    public Response update(@PathParam("oid") String oid, HakijaryhmaValintatapajonoDTO jono) {
        try {
            HakijaryhmaValintatapajonoDTO update = modelMapper.map(hakijaryhmaValintatapajonoService.update(oid, jono), HakijaryhmaValintatapajonoDTO.class);
            return Response.status(Response.Status.ACCEPTED).entity(update).build();
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

}
