package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;
import com.google.common.collect.ImmutableMap;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.sharedutils.AuditLog;
import fi.vm.sade.sharedutils.ValintaResource;
import fi.vm.sade.sharedutils.ValintaperusteetOperation;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import java.util.Optional;

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
    HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    protected final static Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaResourceImpl.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakijaryhmän OID:n perusteella", response = HakijaryhmaValintatapajonoDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Hakijaryhmää ei löydy"),})
    public HakijaryhmaDTO read(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            return modelMapper.map(hakijaryhmaService.readByOid(oid), HakijaryhmaDTO.class);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakijaryhmän", response = HakijaryhmaDTO.class)
    public HakijaryhmaDTO update(
            @ApiParam(value = "Päivitettävän hakijaryhmän OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Hakijaryhmän uudet tiedot", required = true) HakijaryhmaCreateDTO hakijaryhma, @Context HttpServletRequest request) {
        Hakijaryhma old = hakijaryhmaService.readByOid(oid);
        Hakijaryhma updated = hakijaryhmaService.update(oid, hakijaryhma);
        AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.HAKIJARYHMA_PAIVITYS, ValintaResource.HAKIJARYHMA, oid, updated, old);
        return modelMapper.map(updated, HakijaryhmaDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän OID:n perusteella")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "Poisto onnistui"),
            @ApiResponse(code = 403, message = "Hakijaryhmää ei voida poistaa, esim. se on peritty")})
    public Response delete(
            @ApiParam(value = "Poistettavan hakijaryhmän OID", required = true) @PathParam("oid") String oid, @Context HttpServletRequest request) {
        try {
            HakijaryhmaDTO hakijaryhmaDTO = modelMapper.map(hakijaryhmaService.readByOid(oid), HakijaryhmaDTO.class);
            hakijaryhmaService.deleteByOid(oid, false);
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.HAKIJARYHMA_POISTO, ValintaResource.HAKIJARYHMA, oid, null, hakijaryhmaDTO);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (HakijaryhmaaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
    }

    @PUT
    @Path("/siirra")
    @PreAuthorize(CRUD)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response siirra(HakijaryhmaSiirraDTO dto, @Context HttpServletRequest request) {
        Optional<Hakijaryhma> siirretty = hakijaryhmaService.siirra(dto);
        siirretty.ifPresent(hakijaryhma -> {
            Map<String, String> additionalAuditInfo = new HashMap<>();
            additionalAuditInfo.put("Nimi", hakijaryhma.getNimi() + ", " + dto.getNimi());
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.HAKIJARYHMA_SIIRTO, ValintaResource.HAKIJARYHMA, hakijaryhma.getOid(), hakijaryhma, null, additionalAuditInfo);
        });
        return siirretty
            .map(hakijaryhma -> Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(hakijaryhma, HakijaryhmaDTO.class)).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää hakijaryhmät parametrina annetun OID-listan mukaiseen järjestykseen", response = HakijaryhmaDTO.class)
    public List<HakijaryhmaDTO> jarjesta(@ApiParam(value = "Hakijaryhmien uusi järjestys", required = true) List<String> oids, @Context HttpServletRequest request) {
        List<Hakijaryhma> hrl = hakijaryhmaService.jarjestaHakijaryhmat(oids);
        Map<String, String> uusiJarjestys = ImmutableMap.of("hakijaryhmaoids", toNullsafeString(oids));
        AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.HAKIJARYHMA_JARJESTA,
            ValintaResource.HAKIJARYHMA, null, null, null, uusiJarjestys);
        return modelMapper.mapList(hrl, HakijaryhmaDTO.class);
    }
}
