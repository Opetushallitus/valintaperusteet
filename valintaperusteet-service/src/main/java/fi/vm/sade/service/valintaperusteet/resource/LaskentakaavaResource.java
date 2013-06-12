package fi.vm.sade.service.valintaperusteet.resource;


import fi.vm.sade.kaava.Funktiokuvaaja;
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

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE;


/**
 * User: kwuoti
 * Date: 17.1.2013
 * Time: 13.54
 */
@Component
@Path("laskentakaava")
@PreAuthorize("isAuthenticated()")
public class LaskentakaavaResource {

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    private final static Logger LOGGER = LoggerFactory.getLogger(LaskentakaavaResource.class);

    @GET
    @Path("funktiokuvaus")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    public String funktiokuvaukset() {
        return Funktiokuvaaja.annaFunktiokuvauksetAsJson();
    }

    @GET
    @Path("funktiokuvaus/{nimi}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    public String funktiokuvaus(@PathParam("nimi") String nimi) {
        return Funktiokuvaaja.annaFunktiokuvausAsJson(nimi);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured({READ, UPDATE, CRUD})
    public Laskentakaava kaava(@PathParam("id") Long id) {
        return laskentakaavaService.haeMallinnettuKaava(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Laskentakaava.class)
    @Secured({READ, UPDATE, CRUD})
    public List<Laskentakaava> kaavat(
            @DefaultValue("false") @QueryParam("myosLuonnos") Boolean all,
            @QueryParam("valintaryhma") String valintaryhmaOid,
            @QueryParam("hakukohde") String hakukohdeOid,
            @QueryParam("tyyppi") Funktiotyyppi tyyppi) {
        return laskentakaavaService.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi);
    }

    @GET
    @Path("{id}/laske")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({READ, UPDATE, CRUD})
    public String laske(@PathParam("id") Long id) {
        return "";// laskentakaavaService.laske(id);
    }

    @POST
    @Path("validoi")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured({READ, UPDATE, CRUD})
    public Laskentakaava validoi(Laskentakaava laskentakaava) {
        return laskentakaavaService.validoi(laskentakaava);
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response update(@PathParam("id") Long id, @QueryParam("metadata") @DefaultValue("false") Boolean metadata, Laskentakaava laskentakaava) {
        try {
            Laskentakaava updated = null;
            if(metadata) {
                updated = laskentakaavaService.updateMetadata(laskentakaava);
                updated.setFunktiokutsu(null);
            } else {
                updated = laskentakaavaService.update("" + id, laskentakaava);
            }

            return Response.status(Response.Status.OK).entity(updated).build();
        } catch (LaskentakaavaEiValidiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getValidoituLaskentakaava()).build();
        } catch (Exception e) {
            LOGGER.error("Virhe päivitettäessä laskentakaavaa.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Laskentakaava.class)
    @Secured({CRUD})
    public Response insert(Laskentakaava laskentakaava) {

        try {
            Laskentakaava inserted = laskentakaavaService.insert(laskentakaava);
            return Response.status(Response.Status.CREATED).entity(inserted).build();
        } catch (LaskentakaavaEiValidiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getValidoituLaskentakaava()).build();
        } catch (Exception e) {
            LOGGER.error("Virhe tallennettaessa laskentakaavaa.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
