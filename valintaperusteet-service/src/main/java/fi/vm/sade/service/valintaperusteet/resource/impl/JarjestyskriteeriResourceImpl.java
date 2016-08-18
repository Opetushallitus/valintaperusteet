package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.resource.JarjestyskriteeriResource;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

@Component
@Path("jarjestyskriteeri")
@PreAuthorize("isAuthenticated()")
@Api(value = "/jarjestyskriteeri", description = "Resurssi järjestyskriteerien käsittelyyn")
public class JarjestyskriteeriResourceImpl implements JarjestyskriteeriResource {
    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee järjestyskriteerin OID:n perusteella", response = JarjestyskriteeriDTO.class)
    @ApiResponses(@ApiResponse(code = 404, message = "Järjestyskriteeriä ei löydy"))
    public JarjestyskriteeriDTO readByOid(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            return modelMapper.map(jarjestyskriteeriService.readByOid(oid), JarjestyskriteeriDTO.class);
        } catch (JarjestyskriteeriEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää järjestyskriteeriä OID:n perusteella")
    @ApiResponses(@ApiResponse(code = 400, message = "Laskentakaavaa ei ole määritetty"))
    public Response update(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Järjestyskriteerin uudet tiedot ja laskentakaava", required = true) JarjestyskriteeriInsertDTO jk) {
        try {
            JarjestyskriteeriDTO update = modelMapper.map(jarjestyskriteeriService.update(oid, jk.getJarjestyskriteeri(), jk.getLaskentakaavaId()), JarjestyskriteeriDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .jarjestyskriteeriOid(oid)
                    .valintatapajonoOid(update.getValintatapajonoOid())
                    .add("periytyy", update.getInheritance())
                    .add("laskentakaavaid", update.getLaskentakaavaId())
                    .add("prioriteetti", update.getPrioriteetti())
                    .add("aktiivinen", update.getAktiivinen())
                    .add("metatiedot", update.getMetatiedot())
                    .setOperaatio(ValintaperusteetOperation.JARJESTYSKRITEERI_PAIVITYS)
                    .build());
            return Response.status(Response.Status.ACCEPTED).entity(update).build();
        } catch (LaskentakaavaOidTyhjaException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa järjestyskriteerin OID:n perusteella")
    @ApiResponses(@ApiResponse(code = 403, message = "Järjestyskriteeriä ei voida poistaa, esim. se on peritty"))
    public Response delete(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            jarjestyskriteeriService.deleteByOid(oid);
            AUDIT.log(builder()
                    .id(username())
                    .jarjestyskriteeriOid(oid)
                    .setOperaatio(ValintaperusteetOperation.JARJESTYSKRITEERI_POISTO)
                    .build());
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (JarjestyskriteeriaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää järjestyskriteerit annetun listan mukaiseen järjestykseen")
    public List<JarjestyskriteeriDTO> jarjesta(@ApiParam(value = "Uusi järjestys", required = true) List<String> oids) {
        List<Jarjestyskriteeri> jks = jarjestyskriteeriService.jarjestaKriteerit(oids);
        AUDIT.log(builder()
                .id(username())
                .add("jarjestyskriteerioids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null))
                .setOperaatio(ValintaperusteetOperation.JARJESTYSKRITEERIT_JARJESTA)
                .build());
        return modelMapper.mapList(jks, JarjestyskriteeriDTO.class);
    }
}
