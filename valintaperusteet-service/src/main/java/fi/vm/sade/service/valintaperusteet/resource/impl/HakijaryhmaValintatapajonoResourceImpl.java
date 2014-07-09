package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoUpdateDTO;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.resource.ValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Component
@Path("hakijaryhma_valintatapajono")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma_valintatapajono", description = "Resurssi hakijaryhmien ja valintatapajonojen välisten liitosten käsittelyyn")
public class HakijaryhmaValintatapajonoResourceImpl implements HakijaryhmaValintatapajonoResource {
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
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän ja valintatapajonon välisen liitoksen")
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Liitosta ei voida poistaa, esim. se on peritty"),})
    public Response poistaHakijaryhma(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
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
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakijaryhmän ja valintatapajonon välistä liitosta")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Liitosta ei ole olemassa"),})
    public Response update(
            @ApiParam(value = "Päivitettävän liitoksen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Liitoksen uudet tiedot", required = true) HakijaryhmaValintatapajonoUpdateDTO jono) {
        try {
            HakijaryhmaValintatapajonoDTO update = modelMapper.map(hakijaryhmaValintatapajonoService.update(oid, jono),
                    HakijaryhmaValintatapajonoDTO.class);
            return Response.status(Response.Status.ACCEPTED).entity(update).build();
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonon hakijaryhmät argumentin mukaiseen järjestykseen")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "OID-lista on tyhjä"),})
    public List<HakijaryhmaValintatapajonoUpdateDTO> jarjesta(
            @ApiParam(value = "Päivitettävän liitoksen oid", required = true) String hakijaryhmaValintatapajonoOid,
            @ApiParam(value = "Hakijaryhmien uusi järjestys", required = true) List<String> oids) {
        try {
            return modelMapper.mapList(hakijaryhmaValintatapajonoService.jarjestaHakijaryhmat(hakijaryhmaValintatapajonoOid, oids), HakijaryhmaValintatapajonoDTO.class);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

}
