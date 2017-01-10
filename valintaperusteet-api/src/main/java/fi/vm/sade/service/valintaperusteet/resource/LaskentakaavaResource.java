package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;

@Path("laskentakaava")
public interface LaskentakaavaResource {

    @GET
    @Path("/funktiokuvaus")
    @Produces(MediaType.APPLICATION_JSON)
    String funktiokuvaukset();

    @GET
    @Path("/cache")
    @Produces(MediaType.TEXT_PLAIN)
    String tyhjennaCache();

    @GET
    @Path("/funktiokuvaus/{nimi}")
    @Produces(MediaType.APPLICATION_JSON)
    String funktiokuvaus(@PathParam("nimi") String nimi);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    LaskentakaavaDTO kaava(@PathParam("id") Long id, @DefaultValue("true") @QueryParam("funktiopuu") Boolean funktiopuu);

    @GET
    @Path("/hakuoid")
    @Produces(MediaType.TEXT_PLAIN)
    HakuViiteDTO kaavanHakuoid(@QueryParam("valintaryhma") String valintaryhmaOid, @QueryParam("hakukohde") String hakukohdeOid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<LaskentakaavaListDTO> kaavat(@DefaultValue("false") @QueryParam("myosLuonnos") Boolean all,
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
    Response update(@PathParam("id") Long id, LaskentakaavaCreateDTO laskentakaava);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insert(LaskentakaavaInsertDTO laskentakaava);

    @PUT
    @Path("/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response siirra(LaskentakaavaSiirraDTO dto);

    @GET
    @Path("/{id}/valintaryhma")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response valintaryhma(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response poista(@PathParam("id") Long id);

}
