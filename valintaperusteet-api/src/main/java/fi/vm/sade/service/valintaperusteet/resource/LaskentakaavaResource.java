package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;

/**
 * User: kwuoti Date: 17.1.2013 Time: 13.54
 */
@Path("laskentakaava")
public interface LaskentakaavaResource {

    @GET
    @Path("/funktiokuvaus")
    @Produces(MediaType.APPLICATION_JSON)
    String funktiokuvaukset();

    @GET
    @Path("/funktiokuvaus/{nimi}")
    @Produces(MediaType.APPLICATION_JSON)
    String funktiokuvaus(@PathParam("nimi") String nimi);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    LaskentakaavaDTO kaava(@PathParam("id") Long id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<LaskentakaavaListDTO> kaavat(@QueryParam("myosLuonnos") Boolean all,
            @QueryParam("valintaryhma") String valintaryhmaOid, @QueryParam("hakukohde") String hakukohdeOid,
            @QueryParam("tyyppi") Funktiotyyppi tyyppi);

    @POST
    @Path("/validoi")
    @Produces(MediaType.APPLICATION_JSON)
    LaskentakaavaDTO validoi(LaskentakaavaDTO laskentakaava);

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("id") Long id, @QueryParam("metadata") @DefaultValue("false") Boolean metadata,
            LaskentakaavaCreateDTO laskentakaava);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insert(LaskentakaavaInsertDTO laskentakaava);

}
