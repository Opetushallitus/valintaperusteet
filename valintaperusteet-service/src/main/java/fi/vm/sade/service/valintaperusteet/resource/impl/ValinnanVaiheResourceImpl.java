package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.resource.ValinnanVaiheResource;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaihettaEiVoiPoistaaException;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

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
        Map<String,List<ValintatapajonoDTO>> valintatapajonot = new HashMap<>();
        valinnanvaiheOidit.forEach((oid) -> valintatapajonot.put(oid, modelMapper.mapList(jonoService.findJonoByValinnanvaihe(oid), ValintatapajonoDTO.class)));
        return valintatapajonot.keySet().stream().map((oid) ->
            new ValinnanVaiheJaValintatapajonoDTO(oid, valintatapajonot.get(oid))
        ).filter((vaihe) -> !vaihe.getValintatapajonot().isEmpty()).collect(Collectors.toList());
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
            @ApiParam(value = "Lisättävä valintatapajono", required = true) ValintatapajonoCreateDTO jono) {
        try {
            ValintatapajonoDTO inserted = modelMapper.map(jonoService.lisaaValintatapajonoValinnanVaiheelle(parentOid, jono, null), ValintatapajonoDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .valinnanvaiheOid(parentOid)
                    .valintatapajonoOid(inserted.getOid())
                    .setOperaatio(ValintaperusteetOperation.VALINNANVAIHE_LISAYS_VALINTATAPAJONO)
                    .build());
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
            @ApiParam(value = "Lisättävä valintakoe", required = true) ValintakoeCreateDTO koe) {
        try {
            ValintakoeDTO vk = modelMapper.map(valintakoeService.lisaaValintakoeValinnanVaiheelle(parentOid, koe), ValintakoeDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .valinnanvaiheOid(parentOid)
                    .valintakoeOid(vk.getOid())
                    .setOperaatio(ValintaperusteetOperation.VALINNANVAIHE_LISAYS_VALINTAKOE)
                    .build());
            return Response.status(Response.Status.CREATED).entity(vk).build();
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
            @ApiParam(value = "Päivitettävän valinnan vaiheen uudet tiedot", required = true) ValinnanVaiheCreateDTO valinnanVaihe) {
        ValinnanVaihe vv = valinnanVaiheService.update(oid, valinnanVaihe);
        AUDIT.log(builder()
                .id(username())
                .valinnanvaiheOid(vv.getOid())
                .add("aktiivinen", vv.getAktiivinen())
                .add("periytyy", vv.getInheritance())
                .add("nimi", vv.getNimi())
                .add("kuvaus", vv.getKuvaus())
                .setOperaatio(ValintaperusteetOperation.VALINNANVAIHE_PAIVITYS)
                .build());
        return modelMapper.map(vv, ValinnanVaiheDTO.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valinnan vaiheet parametrina annetun OID-listan mukaiseen järjestykseen", response = ValinnanVaiheDTO.class)
    public List<ValinnanVaiheDTO> jarjesta(@ApiParam(value = "Valinnan vaiheiden uusi järjestys", required = true) List<String> oids) {
        List<ValinnanVaihe> vvl = valinnanVaiheService.jarjestaValinnanVaiheet(oids);
        AUDIT.log(builder()
                .id(username())
                .add("valinnanvaiheoids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null))
                .setOperaatio(ValintaperusteetOperation.VALINNANVAIHE_JARJESTA)
                .build());
        return modelMapper.mapList(vvl, ValinnanVaiheDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa valinnan vaiheen OID:n perusteetlla")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Valinnan vaihetta ei ole olemassa"),
            @ApiResponse(code = 400, message = "Valinnan vaihetta ei voida poistaa, esim. se on peritty")})
    public Response delete(
            @ApiParam(value = "Valinnan vaiheen OID", required = true) @PathParam("oid") String oid) {
        try {
            valinnanVaiheService.deleteByOid(oid);
            AUDIT.log(builder()
                    .id(username())
                    .valinnanvaiheOid(oid)
                    .setOperaatio(ValintaperusteetOperation.VALINNANVAIHE_POISTO)
                    .build());
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
