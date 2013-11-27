package fi.vm.sade.service.valintaperusteet.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaOidListaOnTyhjaException;
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
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("hakijaryhma")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma", description = "Resurssi hakijaryhmien käsittelyyn")
public class HakijaryhmaResource {

    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    HakijaryhmaService hakijaryhmaService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    protected final static Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("/{oid}")
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Hakee hakijaryhmän OID:n perusteella", response = HakijaryhmaDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Hakijaryhmää ei löydy"),
    })
    public HakijaryhmaDTO read(@PathParam("oid") String oid) {
        try {
            return modelMapper.map(hakijaryhmaService.readByOid(oid), HakijaryhmaDTO.class);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{hakijaryhmaOid}/valintatapajono")
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Hakee hakijaryhmän ja siihen liittyvät valintatapajonot OID:n perusteella", response = HakijaryhmaValintatapajonoDTO.class)
    public List<HakijaryhmaValintatapajonoDTO> valintatapajonot(@PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        try {
            return modelMapper.mapList(hakijaryhmaValintatapajonoService.findByHakijaryhma(hakijaryhmaOid), HakijaryhmaValintatapajonoDTO.class);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("/{oid}")
    @Secured({UPDATE, CRUD})
    @ApiOperation(value = "Päivittää hakijaryhmän", response = HakijaryhmaDTO.class)
    public HakijaryhmaDTO update(@PathParam("oid") String oid, HakijaryhmaDTO hakijaryhma) {
        return modelMapper.map(hakijaryhmaService.update(oid, hakijaryhma), HakijaryhmaDTO.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("/jarjesta")
    @Secured({UPDATE, CRUD})
    @ApiOperation(value = "Järjestää hakijaryhmät parametrina annetun listan mukaan", response = HakijaryhmaDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "OID-lista on tyhjä"),
    })
    public List<HakijaryhmaDTO> jarjesta(List<String> oids) {
        try {
            return modelMapper.mapList(hakijaryhmaService.jarjestaHakijaryhmat(oids), HakijaryhmaDTO.class);
        } catch (HakijaryhmaOidListaOnTyhjaException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/{oid}")
    @Secured({CRUD})
    @ApiOperation(value = "Poistaa hakijaryhmän OID:n perusteella")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Poisto onnistui"),
            @ApiResponse(code = 403, message = "Hakijaryhmää ei voida poistaa, esim. se on peritty")
    })
    public Response delete(@PathParam("oid") String oid) {
        try {
            hakijaryhmaService.deleteByOid(oid, false);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (HakijaryhmaaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
    }


}
