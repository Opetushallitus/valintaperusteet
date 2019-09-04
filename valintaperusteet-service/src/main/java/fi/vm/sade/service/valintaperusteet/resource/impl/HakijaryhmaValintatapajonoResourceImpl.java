package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;
import com.google.common.collect.ImmutableMap;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("hakijaryhma_valintatapajono")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma_valintatapajono", description = "Resurssi hakijaryhmien ja valintatapajonojen välisten liitosten käsittelyyn")
public class HakijaryhmaValintatapajonoResourceImpl implements HakijaryhmaValintatapajonoResource {
    protected final static Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaValintatapajonoResource.class);

    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public HakijaryhmaValintatapajonoDTO read(String oid) {
        try {
            return modelMapper.map(hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoDTO.class);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän ja valintatapajonon välisen liitoksen")
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Liitosta ei voida poistaa, esim. se on peritty"),})
    public Response poistaHakijaryhma(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid, @Context HttpServletRequest request) {
        try {
            HakijaryhmaValintatapajonoDTO hakijaryhmaValintatapajonoDTO = modelMapper.map(hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoDTO.class);
            hakijaryhmaValintatapajonoService.deleteByOid(oid, false);
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_POISTO, ValintaResource.HAKIJARYHMA_VALINTATAPAJONO, oid, Changes.deleteDto(hakijaryhmaValintatapajonoDTO));
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

    @Override
    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakijaryhmän ja valintatapajonon välistä liitosta")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Liitosta ei ole olemassa"),})
    public Response update(
            @ApiParam(value = "Päivitettävän liitoksen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Liitoksen uudet tiedot", required = true) HakijaryhmaValintatapajonoDTO jono, @Context HttpServletRequest request) {
        try {
            HakijaryhmaValintatapajonoDTO beforeUpdate = modelMapper.map(hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoDTO.class);
            HakijaryhmaValintatapajonoDTO afterUpdate = modelMapper.map(hakijaryhmaValintatapajonoService.update(oid, jono), HakijaryhmaValintatapajonoDTO.class);
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_PAIVITYS, ValintaResource.HAKIJARYHMA_VALINTATAPAJONO, oid, Changes.updatedDto(afterUpdate, beforeUpdate));
            return Response.status(Response.Status.ACCEPTED).entity(afterUpdate).build();
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonon hakijaryhmät annetun OID-listan mukaan", response = ValintatapajonoDTO.class)
    public List<HakijaryhmaValintatapajonoDTO> jarjesta(@ApiParam(value = "OID-lista jonka mukaiseen järjestykseen valintatapajonon hakijaryhmät järjestetään", required = true) List<String> oids, @Context HttpServletRequest request) {
        List<HakijaryhmaValintatapajono> jarjestetytHakijaryhmat = hakijaryhmaValintatapajonoService.jarjestaHakijaryhmat(oids);

        //For auditlog
        String targetOid = null;
        if(!jarjestetytHakijaryhmat.isEmpty()) {
            targetOid = jarjestetytHakijaryhmat.get(0).getHakukohdeViite().getOid(); }
        Map<String, String> sortedOids = ImmutableMap.of("Oid-järjestys", toNullsafeString(oids));
        AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.VALINTATAPAJONO_HAKIJARYHMAT_JARJESTA,
            ValintaResource.HAKIJARYHMA_VALINTATAPAJONO, targetOid, Changes.EMPTY, sortedOids);
        return modelMapper.mapList(jarjestetytHakijaryhmat, HakijaryhmaValintatapajonoDTO.class);
    }
}
