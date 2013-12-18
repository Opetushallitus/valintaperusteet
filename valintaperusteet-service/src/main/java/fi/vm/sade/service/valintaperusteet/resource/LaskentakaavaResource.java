package fi.vm.sade.service.valintaperusteet.resource;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;


/**
 * User: kwuoti
 * Date: 17.1.2013
 * Time: 13.54
 */
@Component
@Path("laskentakaava")
@PreAuthorize("isAuthenticated()")
@Api(value = "/laskentakaava", description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class LaskentakaavaResource {

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(LaskentakaavaResource.class);

    @GET
    @Path("/funktiokuvaus")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Palauttaa funktiokuvaukset")
    public String funktiokuvaukset() {
        return Funktiokuvaaja.annaFunktiokuvauksetAsJson();
    }

    @GET
    @Path("/funktiokuvaus/{nimi}")
    @ApiOperation(value = "Palauttaa parametrina annetun funktion kuvauksen")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    public String funktiokuvaus(@ApiParam(value = "Funktion nimi", required = true) @PathParam("nimi") String nimi) {
        return Funktiokuvaaja.annaFunktiokuvausAsJson(nimi);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Hakee laskentakaavan ID:n perusteella", response = LaskentakaavaDTO.class)
    public LaskentakaavaDTO kaava(@ApiParam(value = "Laskentakaavan ID", required = true) @PathParam("id") Long id) {
        long beginTime = System.currentTimeMillis();
        Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - beginTime) / 1000L;
        LOGGER.info("Laskentakaavan hakemiseen kului: {} sekuntia", timeTaken);

        beginTime = System.currentTimeMillis();
        LaskentakaavaDTO kaava = modelMapper.map(laskentakaava, LaskentakaavaDTO.class);
        endTime = System.currentTimeMillis();
        timeTaken = (endTime - beginTime) / 1000L;
        LOGGER.info("Laskentakaava-LaskentakaavaDTO muunnokseen kului: {} sekuntia", timeTaken);
        return kaava;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Hakee laskentakaavat annettujen hakuparametrien perusteella", response = LaskentakaavaListDTO.class)
    public List<LaskentakaavaListDTO> kaavat(
            @ApiParam(value = "Haetaanko myös luonnos-tilassa olevat kaavat") @DefaultValue("false") @QueryParam("myosLuonnos") Boolean all,
            @ApiParam(value = "Valintaryhmä OID, jonka kaavoja haetaan") @QueryParam("valintaryhma") String valintaryhmaOid,
            @ApiParam(value = "Hakukohde OID, jonka kaavoja haetaan") @QueryParam("hakukohde") String hakukohdeOid,
            @ApiParam(value = "Haettavien laskentakaavojen tyyppi") @QueryParam("tyyppi") Funktiotyyppi tyyppi) {
        return modelMapper.mapList(laskentakaavaService.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi), LaskentakaavaListDTO.class);
    }

    @POST
    @Path("/validoi")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Validoi parametrina annetun laskentakaavan", response = LaskentakaavaDTO.class)
    public LaskentakaavaDTO validoi(@ApiParam(value = "Validoitava laskentakaava", required = true) LaskentakaavaDTO laskentakaava) {
        return modelMapper.map(laskentakaavaService.validoi(laskentakaava), LaskentakaavaDTO.class);
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({UPDATE, CRUD})
    @ApiOperation(value = "Päivittää laskentakaavan")
    public Response update(@ApiParam(value = "Päivitettävän laskentakaavan ID", required = true) @PathParam("id") Long id,
                           @ApiParam(value = "Päivitetäänkö vain metadataa") @QueryParam("metadata") @DefaultValue("false") Boolean metadata,
                           @ApiParam(value = "Päivitettävän laskentakaavan uudet tiedot", required = true) LaskentakaavaCreateDTO laskentakaava) {
        LaskentakaavaDTO updated = null;
        try {
            if (metadata) {
                updated = modelMapper.map(laskentakaavaService.updateMetadata(id, laskentakaava), LaskentakaavaDTO.class);
                updated.setFunktiokutsu(null);
            } else {
                updated = modelMapper.map(laskentakaavaService.update(id, laskentakaava), LaskentakaavaDTO.class);
            }

            return Response.status(Response.Status.OK).entity(updated).build();
        } catch (LaskentakaavaEiValidiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(updated).build();
        } catch (Exception e) {
            LOGGER.error("Virhe päivitettäessä laskentakaavaa.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({CRUD})
    @ApiOperation(value = "Lisää uuden laskentakaavan")
    public Response insert(@ApiParam(value = "Lisättävä laskentakaava", required = true) LaskentakaavaInsertDTO laskentakaava) {
        LaskentakaavaDTO inserted = null;

        try {
            inserted = modelMapper.map(laskentakaavaService.insert(laskentakaava.getLaskentakaava(),
                    laskentakaava.getHakukohdeOid(), laskentakaava.getValintaryhmaOid()), LaskentakaavaDTO.class);
            return Response.status(Response.Status.CREATED).entity(inserted).build();
        } catch (LaskentakaavaEiValidiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(inserted).build();
        } catch (Exception e) {
            LOGGER.error("Virhe tallennettaessa laskentakaavaa.", e);

            System.out.print(e);

            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
