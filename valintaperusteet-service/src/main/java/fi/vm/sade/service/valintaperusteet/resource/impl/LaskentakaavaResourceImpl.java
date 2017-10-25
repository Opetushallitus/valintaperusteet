package fi.vm.sade.service.valintaperusteet.resource.impl;

import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.resource.LaskentakaavaResource;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.ActorService;
import fi.vm.sade.generic.AuditLog;
import fi.vm.sade.service.valintaperusteet.util.ValintaResource;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import java.util.List;
import java.util.Optional;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

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
            @ApiParam(value = "Päivitettävän laskentakaavan uudet tiedot", required = true) LaskentakaavaCreateDTO laskentakaava, @Context HttpServletRequest request) {
        LaskentakaavaDTO before_update = null;
        LaskentakaavaDTO updated = null;
        try {
            before_update = modelMapper.map(laskentakaavaService.haeMallinnettuKaava(id), LaskentakaavaDTO.class);
            updated = modelMapper.map(laskentakaavaService.update(id, laskentakaava), LaskentakaavaDTO.class);
            AuditLog.log(ValintaperusteetOperation.LASKENTAKAAVA_PAIVITYS, ValintaResource.LASKENTAKAAVA, id.toString(), updated, before_update, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .add("laskentakaavaid", updated.getId())
                    .add("kuvaus", updated.getKuvaus())
                    .add("nimi", updated.getNimi())
                    .add("luonnos", updated.getOnLuonnos())
                    .setOperaatio(ValintaperusteetOperation.LASKENTAKAAVA_PAIVITYS)
                    .build());
            */
            // Kaava päivitetty, poistetaan orvot
            actorService.runOnce();
            return Response.status(Response.Status.OK).entity(updated).build();
        } catch (LaskentakaavaEiValidiException e) {
            LOGGER.error("Laskentakaava ei ole validi!", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(updated).build();
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
            AuditLog.log(ValintaperusteetOperation.LASKENTAKAAVA_LISAYS, ValintaResource.LASKENTAKAAVA, laskentakaava.getHakukohdeOid(), inserted, null, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .add("laskentakaavaid", inserted.getId())
                    .add("kuvaus", inserted.getKuvaus())
                    .add("nimi", inserted.getNimi())
                    .add("luonnos", inserted.getOnLuonnos())
                    .setOperaatio(ValintaperusteetOperation.LASKENTAKAAVA_LISAYS)
                    .build());
            */
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
            AuditLog.log(ValintaperusteetOperation.LASKENTAKAAVA_SIIRTO, ValintaResource.LASKENTAKAAVA, dto.getHakukohdeOid(), siirretty.get(), null, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .add("laskentakaavaid", kaava.getId())
                    .setOperaatio(ValintaperusteetOperation.LASKENTAKAAVA_SIIRTO)
                    .build());
            */
                return Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(kaava, LaskentakaavaDTO.class)).build();})
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
        AuditLog.log(ValintaperusteetOperation.LASKENTAKAAVA_POISTO, ValintaResource.LASKENTAKAAVA, id.toString(), null, null, request);
        /*
        AUDIT.log(builder()
                .id(username())
                .add("laskentakaavaid", id)
                .setOperaatio(ValintaperusteetOperation.LASKENTAKAAVA_POISTO)
                .build());
        */
        if (poistettu) {
            // Kaava poistettu, poistetaan orvot
            actorService.runOnce();
            return Response.status(Response.Status.ACCEPTED).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
