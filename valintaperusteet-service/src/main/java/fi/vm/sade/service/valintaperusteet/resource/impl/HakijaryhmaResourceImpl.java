package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
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

import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

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
            @ApiParam(value = "Hakijaryhmän uudet tiedot", required = true) HakijaryhmaCreateDTO hakijaryhma) {
        Hakijaryhma h = hakijaryhmaService.update(oid, hakijaryhma);
        AUDIT.log(builder()
                .id(username())
                .hakijaryhmaOid(oid)
                .valintaryhmaOid(Optional.ofNullable(h.getValintaryhma()).map(v -> v.getOid()).orElse(null))
                .add("hakijaryhmanvalintatapajonot", Arrays.toString(Optional.ofNullable(h.getJonot()).orElse(Collections.<HakijaryhmaValintatapajono>emptySet())
                        .stream().map(v -> v.getOid()).toArray()))
                .add("valintatapajonoids", Arrays.toString(Optional.ofNullable(h.getValintatapajonoIds()).orElse(Collections.<String>emptyList())
                        .stream().toArray()))
                .add("kiintio", h.getKiintio())
                .add("kuvaus", h.getKuvaus())
                .add("laskentakaavaid", h.getLaskentakaavaId())
                .add("nimi", h.getNimi())
                .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_PAIVITYS)
                .build());
        return modelMapper.map(h, HakijaryhmaDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän OID:n perusteella")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "Poisto onnistui"),
            @ApiResponse(code = 403, message = "Hakijaryhmää ei voida poistaa, esim. se on peritty")})
    public Response delete(
            @ApiParam(value = "Poistettavan hakijaryhmän OID", required = true) @PathParam("oid") String oid) {
        try {
            hakijaryhmaService.deleteByOid(oid, false);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(oid)
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_POISTO)
                    .build());
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
    public Response siirra(HakijaryhmaSiirraDTO dto) {
        Optional<Hakijaryhma> siirretty = hakijaryhmaService.siirra(dto);
        return siirretty.map(kaava ->
        {
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(kaava.getOid())
                    .valintaryhmaOid(Optional.ofNullable(kaava.getValintaryhma()).map(v -> v.getOid()).orElse(null))
                    .add("hakijaryhmanvalintatapajonot", Arrays.toString(Optional.ofNullable(kaava.getJonot()).orElse(Collections.<HakijaryhmaValintatapajono>emptySet())
                            .stream().map(v -> v.getOid()).toArray()))
                    .add("valintatapajonoids", Arrays.toString(Optional.ofNullable(kaava.getValintatapajonoIds()).orElse(Collections.<String>emptyList())
                            .stream().toArray()))
                    .add("kiintio", kaava.getKiintio())
                    .add("kuvaus", kaava.getKuvaus())
                    .add("laskentakaavaid", kaava.getLaskentakaavaId())
                    .add("nimi", kaava.getNimi(), dto.getNimi())
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_SIIRTO)
                    .build());
                return Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(kaava, HakijaryhmaDTO.class)).build();
        }).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

}
