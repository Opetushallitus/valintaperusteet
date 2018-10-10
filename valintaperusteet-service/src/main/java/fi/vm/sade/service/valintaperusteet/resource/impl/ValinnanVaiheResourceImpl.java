package fi.vm.sade.service.valintaperusteet.resource.impl;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.resource.ValinnanVaiheResource;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaihettaEiVoiPoistaaException;
import fi.vm.sade.sharedutils.AuditLog;
import fi.vm.sade.sharedutils.ValintaResource;
import fi.vm.sade.sharedutils.ValintaperusteetOperation;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

@Component
@Path("valinnanvaihe")
@PreAuthorize("isAuthenticated()")
@Api(value = "/valinnanvaihe", description = "Resurssi valinnan vaiheiden käsittelyyn")
public class ValinnanVaiheResourceImpl implements ValinnanVaiheResource {
    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    ValinnanVaiheService valinnanVaiheService;

    @Autowired
    ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValinnanVaiheResourceImpl.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valinnan vaiheen OID:n perusteella", response = ValinnanVaiheDTO.class)
    public ValinnanVaiheDTO read(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.map(valinnanVaiheService.readByOid(oid), ValinnanVaiheDTO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/valintatapajono")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valinnan vaiheen valintatapajonot OID:n perusteella", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> listJonos(@ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(jonoService.findJonoByValinnanvaihe(oid), ValintatapajonoDTO.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/valintatapajonot")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee useiden valinnan vaiheiden valintatapajonot OIDien perusteella", response = ValintatapajonoDTO.class)
    public List<ValinnanVaiheJaValintatapajonoDTO> valintatapajonot(@ApiParam(value = "Valinnan vaiheiden OIDit", required = true) List<String> valinnanvaiheOidit) {
        return valinnanvaiheOidit.stream().map(oid -> {
            List<ValintatapajonoDTO> valintatapajonot = modelMapper.mapList(jonoService.findJonoByValinnanvaihe(oid), ValintatapajonoDTO.class);
            Boolean kuuluuSijoitteluun = valinnanVaiheService.kuuluuSijoitteluun(oid);
            return new ValinnanVaiheJaValintatapajonoDTO(oid, kuuluuSijoitteluun, valintatapajonot);
        }).filter(dto -> !dto.getValintatapajonot().isEmpty()).collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/valintakoe")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintakokeet valinnan vaiheen OID:n perusteella", response = ValintakoeDTO.class)
    public List<ValintakoeDTO> listValintakokeet(@ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(valintakoeService.findValintakoeByValinnanVaihe(oid), ValintakoeDTO.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentOid}/valintatapajono")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Lisää valintatapajonon valinnan vaiheelle")
    public Response addJonoToValinnanVaihe(
            @ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("parentOid") String parentOid,
            @ApiParam(value = "Lisättävä valintatapajono", required = true) ValintatapajonoCreateDTO jono,
            @Context HttpServletRequest request) {
        try {
            ValintatapajonoDTO inserted = modelMapper.map(jonoService.lisaaValintatapajonoValinnanVaiheelle(parentOid, jono, null), ValintatapajonoDTO.class);
            AuditLog.log(AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.VALINNANVAIHE_LISAYS_VALINTATAPAJONO, ValintaResource.VALINNANVAIHE, parentOid, Changes.addedDto(inserted));
            return Response.status(Response.Status.CREATED).entity(inserted).build();
        } catch (Exception e) {
            LOGGER.error("error in addJonoToValinnanVaihe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentOid}/valintakoe")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Lisää valintakokeen valinnan vaiheelle")
    public Response addValintakoeToValinnanVaihe(
            @ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("parentOid") String parentOid,
            @ApiParam(value = "Lisättävä valintakoe", required = true) ValintakoeCreateDTO koe,
            @Context HttpServletRequest request) {
        try {
            ValintakoeDTO valintakoe = modelMapper.map(valintakoeService.lisaaValintakoeValinnanVaiheelle(parentOid, koe), ValintakoeDTO.class);
            AuditLog.log(AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.VALINNANVAIHE_LISAYS_VALINTAKOE, ValintaResource.VALINNANVAIHE, parentOid, Changes.addedDto(valintakoe));
            return Response.status(Response.Status.CREATED).entity(valintakoe).build();
        } catch (Exception e) {
            LOGGER.error("error in addValintakoeToValinnanVaihe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää valinnan vaihetta", response = ValinnanVaiheDTO.class)
    public ValinnanVaiheDTO update(
            @ApiParam(value = "Päivitettävän valinnan vaiheen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Päivitettävän valinnan vaiheen uudet tiedot", required = true) ValinnanVaiheCreateDTO valinnanVaihe,
            @Context HttpServletRequest request) {
        ValinnanVaiheDTO vanhaVV = modelMapper.map(valinnanVaiheService.readByOid(oid), ValinnanVaiheDTO.class);
        ValinnanVaiheDTO uusiVV = modelMapper.map(valinnanVaiheService.update(oid, valinnanVaihe), ValinnanVaiheDTO.class);
        AuditLog.log(AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.VALINNANVAIHE_PAIVITYS, ValintaResource.VALINNANVAIHE, oid, Changes.updatedDto(uusiVV, vanhaVV));
        return uusiVV;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valinnan vaiheet parametrina annetun OID-listan mukaiseen järjestykseen", response = ValinnanVaiheDTO.class)
    public List<ValinnanVaiheDTO> jarjesta(@ApiParam(value = "Valinnan vaiheiden uusi järjestys", required = true) List<String> oids, @Context HttpServletRequest request) {
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.jarjestaValinnanVaiheet(oids);
        Map<String, String> additionalInfo = ImmutableMap.of("valinnanvaiheoids", toNullsafeString(oids));
        AuditLog.log(AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.VALINNANVAIHE_JARJESTA, ValintaResource.VALINNANVAIHE, null, Changes.EMPTY, additionalInfo);
        return modelMapper.mapList(valinnanVaiheet, ValinnanVaiheDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa valinnan vaiheen OID:n perusteetlla")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Valinnan vaihetta ei ole olemassa"),
            @ApiResponse(code = 400, message = "Valinnan vaihetta ei voida poistaa, esim. se on peritty")})
    public Response delete(
            @ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("oid") String oid, @Context HttpServletRequest request) {
        try {
            ValinnanVaiheDTO old = modelMapper.map(valinnanVaiheService.readByOid(oid), ValinnanVaiheDTO.class);
            valinnanVaiheService.deleteByOid(oid);
            AuditLog.log(AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.VALINNANVAIHE_POISTO, ValintaResource.VALINNANVAIHE, oid,  Changes.deleteDto(old));
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (ValinnanVaiheEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (ValinnanVaihettaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa tiedon siitä, kuuluuko valinnan vaihe sijoitteluun", response = Boolean.class)
    public Map<String, Boolean> kuuluuSijoitteluun(@ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("oid") String oid) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("sijoitteluun", valinnanVaiheService.kuuluuSijoitteluun(oid));
        return map;
    }

    @POST
    @Path("/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa tiedon siitä, kuuluvatko valinnan vaiheet sijoitteluun", response = Boolean.class)
    public Map<String, Boolean> kuuluuSijoitteluun(@ApiParam(value = "Valinnan vaiheiden OID", required = true) List<String> oids) {
        Map<String, Boolean> map = new HashMap<>();
        oids.forEach((oid) -> map.put(oid, valinnanVaiheService.kuuluuSijoitteluun(oid)));
        return map;
    }
}
