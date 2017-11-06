package fi.vm.sade.service.valintaperusteet.resource.impl;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
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
import java.util.*;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

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
        AuditLog.log(ValintaperusteetOperation.HAKIJARYHMA_PAIVITYS, ValintaResource.HAKIJARYHMA, oid, updated, old, request);
        /*
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
        */
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
            AuditLog.log(ValintaperusteetOperation.HAKIJARYHMA_POISTO, ValintaResource.HAKIJARYHMA, oid, null, hakijaryhmaDTO, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(oid)
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_POISTO)
                    .build());
            */
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
        return siirretty.map(kaava ->
        {
            Map<String, String> additionalAuditInfo = new HashMap<>();
            additionalAuditInfo.put("Nimi", kaava.getNimi()+", "+dto.getNimi());
            AuditLog.log(ValintaperusteetOperation.HAKIJARYHMA_SIIRTO, ValintaResource.HAKIJARYHMA, dto.getValintaryhmaOid(), kaava, null, request, additionalAuditInfo);
            /*
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
             */
                return Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(kaava, HakijaryhmaDTO.class)).build();
        }).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää hakijaryhmät parametrina annetun OID-listan mukaiseen järjestykseen", response = HakijaryhmaDTO.class)
    public List<HakijaryhmaDTO> jarjesta(@ApiParam(value = "Hakijaryhmien uusi järjestys", required = true) List<String> oids, @Context HttpServletRequest request) {
        List<Hakijaryhma> hrl = hakijaryhmaService.jarjestaHakijaryhmat(oids);
        Map<String, String> uusiJarjestys = ImmutableMap.of("hakijaryhmaoids", Optional.of(oids.toArray().toString()).orElse(null));
        AuditLog.log(ValintaperusteetOperation.HAKIJARYHMA_JARJESTA, ValintaResource.HAKIJARYHMA, null,
                null, null, request, uusiJarjestys);
        /*
        AUDIT.log(builder()
                .id(username())
                .add("hakijaryhmaoids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null))
                .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_JARJESTA)
                .build());
        */
        return modelMapper.mapList(hrl, HakijaryhmaDTO.class);
    }
}
