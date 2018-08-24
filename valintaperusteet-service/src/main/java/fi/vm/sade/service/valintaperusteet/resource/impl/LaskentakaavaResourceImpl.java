package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.dto.HakuViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaPlainDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.resource.LaskentakaavaResource;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.ActorService;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.sharedutils.AuditLog;
import fi.vm.sade.sharedutils.ValintaResource;
import fi.vm.sade.sharedutils.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Component
@Path("laskentakaava")
@PreAuthorize("isAuthenticated()")
@Api(value = "/laskentakaava", description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class LaskentakaavaResourceImpl implements LaskentakaavaResource {
    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private ActorService actorService;

    private final static Logger LOGGER = LoggerFactory.getLogger(LaskentakaavaResourceImpl.class);

    @GET
    @Path("/funktiokuvaus")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa funktiokuvaukset")
    public String funktiokuvaukset() {
        return Funktiokuvaaja.annaFunktiokuvauksetAsJson();
    }

    @GET
    @Path("/cache")
    @Produces(MediaType.TEXT_PLAIN)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Tyhjentää laskentakaavat välimuistista")
    public String tyhjennaCache() {
        laskentakaavaService.tyhjennaCache();
        return "cache tyhjennetty";
    }

    @GET
    @Path("/funktiokuvaus/{nimi}")
    @ApiOperation(value = "Palauttaa parametrina annetun funktion kuvauksen")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    public String funktiokuvaus(@ApiParam(value = "Funktion nimi", required = true) @PathParam("nimi") String nimi) {
        return Funktiokuvaaja.annaFunktiokuvausAsJson(nimi);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee laskentakaavan ID:n perusteella", response = LaskentakaavaDTO.class)
    public LaskentakaavaDTO kaava(@ApiParam(value = "Laskentakaavan ID", required = true) @PathParam("id") Long id,
                                  @ApiParam(value = "Palautetaanko koko funktiopuu", required = false) @DefaultValue("true") @QueryParam("funktiopuu") Boolean funktiopuu) {
        if (funktiopuu) {
            LaskentakaavaDTO mapped = modelMapper.map(laskentakaavaService.haeMallinnettuKaava(id), LaskentakaavaDTO.class);
            return mapped;
        } else {
            Optional<Laskentakaava> kaava = laskentakaavaService.pelkkaKaava(id);
            return kaava.map(k -> {
                k.setFunktiokutsu(null);
                return modelMapper.map(k, LaskentakaavaDTO.class);
            }).orElse(new LaskentakaavaDTO());
        }


    }

    @Override
    @GET
    @Path("/hakuoid")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee laskentakaavan hakuoidin ID:n perusteella", response = HakuViiteDTO.class)
    public HakuViiteDTO kaavanHakuoid(
            @ApiParam(value = "Valintaryhmä OID, jonka hakua haetaan") @QueryParam("valintaryhma") String valintaryhmaOid,
            @ApiParam(value = "Hakukohde OID, jonka hakua haetaan") @QueryParam("hakukohde") String hakukohdeOid) {
        HakuViiteDTO haku = new HakuViiteDTO();
        haku.setHakuoid(laskentakaavaService.haeHakuoid(hakukohdeOid, valintaryhmaOid));
        return haku;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee laskentakaavat annettujen hakuparametrien perusteella", response = LaskentakaavaListDTO.class)
    public List<LaskentakaavaListDTO> kaavat(
            @ApiParam(value = "Haetaanko myös luonnos-tilassa olevat kaavat") @DefaultValue("false") @QueryParam("myosLuonnos") Boolean all,
            @ApiParam(value = "Valintaryhmä OID, jonka kaavoja haetaan") @QueryParam("valintaryhma") String valintaryhmaOid,
            @ApiParam(value = "Hakukohde OID, jonka kaavoja haetaan") @QueryParam("hakukohde") String hakukohdeOid,
            @ApiParam(value = "Haettavien laskentakaavojen tyyppi") @QueryParam("tyyppi") fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi) {
        return modelMapper.mapList(laskentakaavaService.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi), LaskentakaavaListDTO.class);
    }

    @POST
    @Path("/validoi")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Validoi parametrina annetun laskentakaavan", response = LaskentakaavaDTO.class)
    public LaskentakaavaDTO validoi(
            @ApiParam(value = "Validoitava laskentakaava", required = true) LaskentakaavaDTO laskentakaava) {
        return modelMapper.map(laskentakaavaService.validoi(laskentakaava), LaskentakaavaDTO.class);
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää laskentakaavan")
    public Response update(
            @ApiParam(value = "Päivitettävän laskentakaavan ID", required = true) @PathParam("id") Long id,
            @ApiParam(value = "Päivitettävän laskentakaavan uudet tiedot", required = true) LaskentakaavaCreateDTO laskentakaava,
            @Context HttpServletRequest request) {
        LaskentakaavaDTO afterUpdate = null;
        try {
            LaskentakaavaDTO beforeUpdate = modelMapper.map(laskentakaavaService.haeMallinnettuKaava(id), LaskentakaavaDTO.class);
            afterUpdate = modelMapper.map(laskentakaavaService.update(id, laskentakaava), LaskentakaavaDTO.class);
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.LASKENTAKAAVA_PAIVITYS, ValintaResource.LASKENTAKAAVA, afterUpdate.getId().toString(), Changes.updatedDto(afterUpdate, beforeUpdate));
            // Kaava päivitetty, poistetaan orvot
            actorService.runOnce();
            return Response.status(Response.Status.OK).entity(afterUpdate).build();
        } catch (LaskentakaavaEiValidiException e) {
            LOGGER.error("Laskentakaava ei ole validi!", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(afterUpdate).build();
        } catch (Exception e) {
            LOGGER.error("Virhe päivitettäessä laskentakaavaa.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää uuden laskentakaavan")
    public Response insert(
            @ApiParam(value = "Lisättävä laskentakaava", required = true) LaskentakaavaInsertDTO laskentakaava, @Context HttpServletRequest request) {
        LaskentakaavaDTO inserted = null;
        try {
            inserted = Optional.ofNullable(modelMapper.map(laskentakaavaService.insert(laskentakaava.getLaskentakaava(),
                    laskentakaava.getHakukohdeOid(), laskentakaava.getValintaryhmaOid()), LaskentakaavaDTO.class)).orElse(new LaskentakaavaDTO());
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.LASKENTAKAAVA_LISAYS, ValintaResource.LASKENTAKAAVA, laskentakaava.getHakukohdeOid(), Changes.addedDto(inserted));
            return Response.status(Response.Status.CREATED).entity(inserted).build();
        } catch (LaskentakaavaEiValidiException e) {
            LOGGER.error("Laskentakaava ei ole validi.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(inserted).build();
        } catch (Exception e) {
            LOGGER.error("Virhe tallennettaessa laskentakaavaa.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/siirra")
    @PreAuthorize(CRUD)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response siirra(LaskentakaavaSiirraDTO dto, @Context HttpServletRequest request) {
        Optional<Laskentakaava> siirretty = laskentakaavaService.siirra(dto);
        return siirretty.map(kaava -> {
            AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.LASKENTAKAAVA_SIIRTO, ValintaResource.LASKENTAKAAVA, Long.toString(kaava.getId()), Changes.addedDto(siirretty.get()));
            return Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(kaava, LaskentakaavaDTO.class)).build();
        })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/valintaryhma")
    @PreAuthorize(CRUD)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response valintaryhma(@PathParam("id") Long id) {
        Optional<Valintaryhma> ryhma = laskentakaavaService.valintaryhma(id);
        return ryhma.map(r ->
                        Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(r, ValintaryhmaPlainDTO.class)).build()
        ).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @PreAuthorize(CRUD)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response poista(@PathParam("id") Long id, @Context HttpServletRequest request) {
        boolean poistettu = laskentakaavaService.poista(id);
        AuditLog.log(ValintaperusteetAudit.AUDIT, AuditLog.getUser(request), ValintaperusteetOperation.LASKENTAKAAVA_POISTO, ValintaResource.LASKENTAKAAVA, id.toString(), Changes.EMPTY);
        if (poistettu) {
            // Kaava poistettu, poistetaan orvot
            actorService.runOnce();
            return Response.status(Response.Status.ACCEPTED).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
