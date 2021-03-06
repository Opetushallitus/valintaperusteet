package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.ErrorDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.resource.ValintaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@Path("valintaryhma")
@PreAuthorize("isAuthenticated()")
@Api(value = "/valintaryhma", description = "Resurssi valintaryhmien käsittelyyn")
public class ValintaryhmaResourceImpl implements ValintaryhmaResource {

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private HakukohdekoodiService hakukohdekoodiService;

  @Autowired private ValintakoekoodiService valintakoekoodiService;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private OidService oidService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  protected static final Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResourceImpl.class);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(
      value = "Hakee valintaryhmiä annettujen hakukriteerien perusteella",
      response = ValintaryhmaDTO.class)
  public List<ValintaryhmaDTO> search(
      @ApiParam(value = "Haetaanko pääatason valintaryhmät") @QueryParam("paataso") Boolean paataso,
      @ApiParam(value = "Parent-valintaryhmän OID, jonka lapsia haetaan") @QueryParam("parentsOf")
          String parentsOf) {
    List<ValintaryhmaDTO> valintaryhmas = new ArrayList<>();
    if (Boolean.TRUE.equals(paataso)) {
      valintaryhmas.addAll(
          modelMapper.mapList(
              valintaryhmaService.findValintaryhmasByParentOid(null), ValintaryhmaDTO.class));
    }
    if (parentsOf != null) {
      valintaryhmas.addAll(
          modelMapper.mapList(
              valintaryhmaService.findParentHierarchyFromOid(parentsOf), ValintaryhmaDTO.class));
    }
    return valintaryhmas;
  }

  @DELETE
  @Path("/{oid}")
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Poistaa valintaryhmän OID:n perusteella")
  @ApiResponses({@ApiResponse(code = 404, message = "Valintaryhmää ei ole olemassa")})
  public Response delete(
      @ApiParam(value = "Valintaryhmän OID", required = true) @PathParam("oid") String oid,
      @Context HttpServletRequest request) {
    try {
      ValintaryhmaDTO beforeDelete =
          modelMapper.map(valintaryhmaService.readByOid(oid), ValintaryhmaDTO.class);
      valintaryhmaService.delete(oid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_POISTO,
          ValintaResource.VALINTARYHMA,
          oid,
          Changes.deleteDto(beforeDelete));
      return Response.status(Response.Status.ACCEPTED).build();
    } catch (ValintaryhmaEiOleOlemassaException e) {
      throw new WebApplicationException(e, Response.Status.NOT_FOUND);
    }
  }

  @GET
  @Path("/{oid}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(value = "Hakee valintaryhmän OID:n perusteella", response = ValintaryhmaDTO.class)
  public ValintaryhmaDTO queryFull(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
    return modelMapper.map(valintaryhmaService.readByOid(oid), ValintaryhmaDTO.class);
  }

  @GET
  @Path("/{oid}/hakijaryhma")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(
      value = "Hakee hakijaryhmät valintaryhmän OID:n perusteella",
      response = HakijaryhmaDTO.class)
  public List<HakijaryhmaDTO> hakijaryhmat(
      @ApiParam(value = "Valintaryhmän OID", required = true) @PathParam("oid") String oid) {
    return modelMapper.mapList(hakijaryhmaService.findByValintaryhma(oid), HakijaryhmaDTO.class);
  }

  @GET
  @Path("/{oid}/parents")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(
      value = "Hakee valintaryhmän parent-valintaryhmät OID:n perusteella",
      response = ValintaryhmaListDTO.class)
  public List<ValintaryhmaListDTO> parentHierarchy(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String parentsOf) {
    List<ValintaryhmaListDTO> valintaryhmas = new ArrayList<>();
    if (parentsOf != null) {
      valintaryhmas.addAll(
          modelMapper.mapList(
              valintaryhmaService.findParentHierarchyFromOid(parentsOf),
              ValintaryhmaListDTO.class));
    }
    return valintaryhmas;
  }

  @GET
  @Path("/{oid}/lapsi")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(
      value = "Hakee valintaryhmän lapsivalintaryhmät OID:n perusteella",
      response = ValintaryhmaDTO.class)
  public List<ValintaryhmaDTO> queryChildren(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
    return modelMapper.mapList(
        valintaryhmaService.findValintaryhmasByParentOid(oid), ValintaryhmaDTO.class);
  }

  @GET
  @Path("/{oid}/hakukohde")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(
      value = "Hakee valintaryhmän lapsihakukohteet OID:n perusteella",
      response = HakukohdeViiteDTO.class)
  public List<HakukohdeViiteDTO> childHakukohdes(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
    return modelMapper.mapList(
        hakukohdeService.findByValintaryhmaOid(oid), HakukohdeViiteDTO.class);
  }

  @GET
  @Path("/{oid}/valinnanvaihe")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(READ_UPDATE_CRUD)
  @ApiOperation(
      value = "Hakee valinnan vaiheet valintaryhmän OID:n perusteella",
      response = ValinnanVaiheDTO.class)
  public List<ValinnanVaiheDTO> valinnanVaiheet(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
    return modelMapper.mapList(
        valinnanVaiheService.findByValintaryhma(oid), ValinnanVaiheDTO.class);
  }

  @PUT
  @Path("/{parentOid}/lapsi")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää lapsivalintaryhmän parametrina annetulle parent-valintaryhmälle")
  public Response insertChild(
      @ApiParam(value = "Parent-valintaryhmän OID", required = true) @PathParam("parentOid")
          String parentOid,
      @ApiParam(value = "Lisättävä valintaryhmä", required = true)
          ValintaryhmaCreateDTO valintaryhma,
      @Context HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(
              valintaryhmaService.insert(valintaryhma, parentOid), ValintaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.LAPSIVALINTARYHMA_LISAYS_PARENT,
          ValintaResource.VALINTARYHMA,
          parentOid,
          Changes.addedDto(lisatty));
      return Response.status(Response.Status.CREATED).entity(lisatty).build();
    } catch (Exception e) {
      LOGGER.error("Lapsivalintaryhmän lisäys valintaryhmälle {} ei onnistunut.", parentOid, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää valintaryhmän")
  public Response insert(
      @ApiParam(value = "Uusi valintaryhmä", required = true) ValintaryhmaCreateDTO valintaryhma,
      @Context HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(valintaryhmaService.insert(valintaryhma), ValintaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS,
          ValintaResource.VALINTARYHMA,
          lisatty.getOid(),
          Changes.addedDto(lisatty));
      return Response.status(Response.Status.CREATED).entity(lisatty).build();
    } catch (Exception e) {
      LOGGER.error("Error creating valintaryhmä.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorDTO(e.getMessage()))
          .build();
    }
  }

  @POST
  @Path("/{oid}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(UPDATE_CRUD)
  @ApiOperation(value = "Päivittää valintaryhmän")
  public Response update(
      @ApiParam(value = "Päivitettävän valintaryhmän OID", required = true) @PathParam("oid")
          String oid,
      @ApiParam(value = "Päivitettävän valintaryhmän uudet tiedot")
          ValintaryhmaCreateDTO valintaryhma,
      @Context HttpServletRequest request) {
    ValintaryhmaDTO beforeUpdate =
        modelMapper.map(valintaryhmaService.readByOid(oid), ValintaryhmaDTO.class);
    ValintaryhmaDTO afterUpdate =
        modelMapper.map(valintaryhmaService.update(oid, valintaryhma), ValintaryhmaDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTARYHMA_PAIVITYS,
        ValintaResource.VALINTARYHMA,
        oid,
        Changes.updatedDto(afterUpdate, beforeUpdate));
    return Response.status(Response.Status.ACCEPTED).build();
  }

  @PUT
  @Path("/{oid}/kopioiLapseksi")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää lapsivalintaryhmän kopioimalla lähdevalintaryhmän")
  public Response copyAsChild(
      @PathParam("oid") String oid,
      @QueryParam("lahdeOid") String lahdeOid,
      @QueryParam("nimi") String nimi,
      @Context HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(
              valintaryhmaService.copyAsChild(lahdeOid, oid, nimi), ValintaryhmaDTO.class);
      Map<String, String> additionalAuditInfo = new HashMap<>();
      additionalAuditInfo.put("lahdeOid", lahdeOid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.LAPSIVALINTARYHMA_LISAYS,
          ValintaResource.VALINTARYHMA,
          oid,
          Changes.addedDto(lisatty),
          additionalAuditInfo);
      return Response.status(Response.Status.CREATED).entity(lisatty).build();
    } catch (Exception e) {
      LOGGER.error("Error copying valintaryhmä.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorDTO(e.getMessage()))
          .build();
    }
  }

  @PUT
  @Path("/kopioiJuureen")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää valintaryhmän juureen kopioimalla lähdevalintaryhmän")
  public Response copyToRoot(
      @QueryParam("lahdeOid") String lahdeOid,
      @QueryParam("nimi") String nimi,
      @Context HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(
              valintaryhmaService.copyAsChild(lahdeOid, null, nimi), ValintaryhmaDTO.class);
      Map<String, String> additionalAuditInfo = new HashMap<>();
      additionalAuditInfo.put("lahdeOid", lahdeOid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS,
          ValintaResource.VALINTARYHMA,
          lisatty.getOid(),
          Changes.addedDto(lisatty),
          additionalAuditInfo);
      return Response.status(Response.Status.CREATED).entity(lisatty).build();
    } catch (Exception e) {
      LOGGER.error("Error copying valintaryhmä.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorDTO(e.getMessage()))
          .build();
    }
  }

  @POST
  @Path("/{valintaryhmaOid}/valinnanvaihe")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää valinnan vaiheen valintaryhmälle")
  public Response insertValinnanvaihe(
      @ApiParam(value = "Valintaryhmän OID, jolla valinnan vaihe lisätään", required = true)
          @PathParam("valintaryhmaOid")
          String valintaryhmaOid,
      @ApiParam(value = "Valinnan vaiheen OID, jonka jälkeen uusi valinnan vaihe lisätään")
          @QueryParam("edellinenValinnanVaiheOid")
          String edellinenValinnanVaiheOid,
      @ApiParam(value = "Uusi valinnan vaihe", required = true)
          ValinnanVaiheCreateDTO valinnanVaihe,
      @Context HttpServletRequest request) {
    try {
      ValinnanVaiheDTO lisatty =
          modelMapper.map(
              valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                  valintaryhmaOid, valinnanVaihe, edellinenValinnanVaiheOid),
              ValinnanVaiheDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_VALINNANVAIHE,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(lisatty));
      return Response.status(Response.Status.CREATED).entity(lisatty).build();
    } catch (Exception e) {
      LOGGER.error("Error creating valinnanvaihe.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PUT
  @Path("/{valintaryhmaOid}/hakijaryhma")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää hakijaryhmän valintaryhmälle")
  public Response insertHakijaryhma(
      @ApiParam(value = "Valintaryhmän OID, jolle hakijaryhmä lisätään", required = true)
          @PathParam("valintaryhmaOid")
          String valintaryhmaOid,
      @ApiParam(value = "Lisättävä hakijaryhmä", required = true) HakijaryhmaCreateDTO hakijaryhma,
      @Context HttpServletRequest request) {
    try {
      HakijaryhmaDTO lisatty =
          modelMapper.map(
              hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle(valintaryhmaOid, hakijaryhma),
              HakijaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_HAKIJARYHMA,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(lisatty));
      return Response.status(Response.Status.CREATED).entity(lisatty).build();
    } catch (LaskentakaavaOidTyhjaException e) {
      LOGGER.warn("Error creating hakijaryhma for valintaryhmä: " + e.toString());
      Map<String, String> map = new HashMap<>();
      map.put("error", e.getMessage());
      return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
    } catch (Exception e) {
      LOGGER.error("Error creating hakijaryhma for valintaryhmä.", e);
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
  }

  @POST
  @Path("/{valintaryhmaOid}/hakukohdekoodi")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(UPDATE_CRUD)
  @ApiOperation(value = "Päivittää valintaryhmän hakukohdekoodeja")
  public Response updateHakukohdekoodi(
      @ApiParam(value = "Valintaryhmän OID, jonka hakukohdekoodeja päivitetään", required = true)
          @PathParam("valintaryhmaOid")
          String valintaryhmaOid,
      @ApiParam(value = "Uudet hakukohdekoodit", required = true) Set<KoodiDTO> hakukohdekoodit,
      @Context HttpServletRequest request) {
    try {
      hakukohdekoodiService.updateValintaryhmaHakukohdekoodit(valintaryhmaOid, hakukohdekoodit);
      Map<String, String> uudetHakukohdekoodit =
          ImmutableMap.of("Uudet hakukohdekoodit", toNullsafeString(hakukohdekoodit));
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_PAIVITYS_HAKUKOHDEKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.EMPTY,
          uudetHakukohdekoodit);
      return Response.status(Response.Status.ACCEPTED).entity(hakukohdekoodit).build();
    } catch (Exception e) {
      LOGGER.error("Error updating hakukohdekoodit.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/{valintaryhmaOid}/hakukohdekoodi")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää hakukohdekoodin valintaryhmälle")
  public Response insertHakukohdekoodi(
      @ApiParam(value = "Valintaryhmän OID, jolle hakukohdekoodi lisätään", required = true)
          @PathParam("valintaryhmaOid")
          String valintaryhmaOid,
      @ApiParam(value = "Lisättävä hakukohdekoodi", required = true) KoodiDTO hakukohdekoodi,
      @Context HttpServletRequest request) {
    try {
      hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(valintaryhmaOid, hakukohdekoodi);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_HAKUKOHDEKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(hakukohdekoodi));
      return Response.status(Response.Status.CREATED).entity(hakukohdekoodi).build();
    } catch (Exception e) {
      LOGGER.error("Error inserting hakukohdekoodi.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/{valintaryhmaOid}/valintakoekoodi")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Päivittää valintaryhmän valintakoekoodeja")
  public Response updateValintakoekoodi(
      @ApiParam(value = "Valintaryhmän OID, jonka valintakoekoodeja päivitetään", required = true)
          @PathParam("valintaryhmaOid")
          String valintaryhmaOid,
      @ApiParam(value = "Päivitettävät valintakoekoodit", required = true)
          List<KoodiDTO> valintakoekoodit,
      @Context HttpServletRequest request) {
    try {
      valintakoekoodiService.updateValintaryhmanValintakoekoodit(valintaryhmaOid, valintakoekoodit);
      Map<String, String> muutetutValintakoekoodit =
          ImmutableMap.of("Päivitetyt valintakoekoodit", toNullsafeString(valintakoekoodit));
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_PAIVITYS_VALINTAKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.EMPTY,
          muutetutValintakoekoodit);
      return Response.status(Response.Status.ACCEPTED).entity(valintakoekoodit).build();
    } catch (Exception e) {
      LOGGER.error("Error updating valintakoekoodit.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/{valintaryhmaOid}/valintakoekoodi")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(CRUD)
  @ApiOperation(value = "Lisää valintakoekoodin valintaryhmälle")
  public Response insertValintakoekoodi(
      @ApiParam(value = "Valintaryhmän OID, jolle valintakoekoodi lisätään", required = true)
          @PathParam("valintaryhmaOid")
          String valintaryhmaOid,
      @ApiParam(value = "Lisättävä valintakoekoodi", required = true) KoodiDTO valintakoekoodi,
      @Context HttpServletRequest request) {
    try {
      valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, valintakoekoodi);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_VALINTAKOEKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(valintakoekoodi));
      return Response.status(Response.Status.CREATED).entity(valintakoekoodi).build();
    } catch (Exception e) {
      LOGGER.error("Error inserting valintakoekoodi.", e);
      Map<String, String> error = new HashMap<>();
      error.put("message", e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
  }
}
