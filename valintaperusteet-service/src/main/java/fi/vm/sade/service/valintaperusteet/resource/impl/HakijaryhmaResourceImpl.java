package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Component
@Path("hakijaryhma")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma", description = "Resurssi hakijaryhmien käsittelyyn")
public class HakijaryhmaResourceImpl implements HakijaryhmaResource {

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

    protected final static Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaResourceImpl.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @Secured({ READ, UPDATE, CRUD })
    @ApiOperation(value = "Hakee hakijaryhmän OID:n perusteella", response = HakijaryhmaDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Hakijaryhmää ei löydy"), })
    public HakijaryhmaDTO read(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
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
    @Secured({ READ, UPDATE, CRUD })
    @ApiOperation(value = "Hakee hakijaryhmän ja siihen liittyvät valintatapajonot OID:n perusteella", response = HakijaryhmaValintatapajonoDTO.class)
    public List<HakijaryhmaValintatapajonoDTO> valintatapajonot(
            @ApiParam(value = "OID", required = true) @PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        try {
            return modelMapper.mapList(hakijaryhmaValintatapajonoService.findByHakijaryhma(hakijaryhmaOid),
                    HakijaryhmaValintatapajonoDTO.class);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @Secured({ UPDATE, CRUD })
    @ApiOperation(value = "Päivittää hakijaryhmän", response = HakijaryhmaDTO.class)
    public HakijaryhmaDTO update(
            @ApiParam(value = "Päivitettävän hakijaryhmän OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Hakijaryhmän uudet tiedot", required = true) HakijaryhmaCreateDTO hakijaryhma) {
        return modelMapper.map(hakijaryhmaService.update(oid, hakijaryhma), HakijaryhmaDTO.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @Secured({ UPDATE, CRUD })
    @ApiOperation(value = "Järjestää hakijaryhmät parametrina annetun listan mukaan", response = HakijaryhmaDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "OID-lista on tyhjä"), })
    public List<HakijaryhmaDTO> jarjesta(
            @ApiParam(value = "Hakijaryhmien uusi järjestys", required = true) List<String> oids) {
        try {
            return modelMapper.mapList(hakijaryhmaService.jarjestaHakijaryhmat(oids), HakijaryhmaDTO.class);
        } catch (HakijaryhmaOidListaOnTyhjaException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/{oid}")
    @Secured({ CRUD })
    @ApiOperation(value = "Poistaa hakijaryhmän OID:n perusteella")
    @ApiResponses(value = { @ApiResponse(code = 202, message = "Poisto onnistui"),
            @ApiResponse(code = 403, message = "Hakijaryhmää ei voida poistaa, esim. se on peritty") })
    public Response delete(
            @ApiParam(value = "Poistettavan hakijaryhmän OID", required = true) @PathParam("oid") String oid) {
        try {
            hakijaryhmaService.deleteByOid(oid, false);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (HakijaryhmaaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
    }

}
