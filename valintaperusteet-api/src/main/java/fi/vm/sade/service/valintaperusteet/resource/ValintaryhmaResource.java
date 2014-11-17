package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaListDTO;

/**
 * Created with IntelliJ IDEA. User: kkammone Date: 10.1.2013 Time: 12:01 To
 * change this template use File | Settings | File Templates.
 */
@Path("valintaryhma")
public interface ValintaryhmaResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaryhmaDTO> search(@QueryParam("paataso") Boolean paataso, @QueryParam("parentsOf") String parentsOf);

    @DELETE
    @Path("/{oid}")
    public Response delete(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    ValintaryhmaDTO queryFull(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/hakijaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    List<HakijaryhmaDTO> hakijaryhmat(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/parents")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaryhmaListDTO> parentHierarchy(@PathParam("oid") String parentsOf);

    @GET
    @Path("/{oid}/lapsi")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaryhmaDTO> queryChildren(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON)
    List<HakukohdeViiteDTO> childHakukohdes(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValinnanVaiheDTO> valinnanVaiheet(@PathParam("oid") String oid);

    @PUT
    @Path("/{parentOid}/lapsi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertChild(@PathParam("parentOid") String parentOid, ValintaryhmaCreateDTO valintaryhma);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insert(ValintaryhmaCreateDTO valintaryhma);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, ValintaryhmaCreateDTO valintaryhma);

    @PUT
    @Path("/{oid}/kopioiLapseksi")
    @Produces(MediaType.APPLICATION_JSON)
    Response copyAsChild(@PathParam("oid") String oid, @QueryParam("lahdeOid") String lahdeOid, @QueryParam("nimi") String nimi);

    @PUT
    @Path("/{valintaryhmaOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertValinnanvaihe(@PathParam("valintaryhmaOid") String valintaryhmaOid,
            @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid,
            ValinnanVaiheCreateDTO valinnanVaihe);

    @PUT
    @Path("/{valintaryhmaOid}/hakijaryhma")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertHakijaryhma(@PathParam("valintaryhmaOid") String valintaryhamOid, HakijaryhmaCreateDTO hakijaryhma);

    @POST
    @Path("/{valintaryhmaOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateHakukohdekoodi(@PathParam("valintaryhmaOid") String valintaryhmaOid, Set<KoodiDTO> hakukohdekoodit);

    @PUT
    @Path("/{valintaryhmaOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertHakukohdekoodi(@PathParam("valintaryhmaOid") String valintaryhamOid, KoodiDTO hakukohdekoodi);

    @POST
    @Path("/{valintaryhmaOid}/valintakoekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateValintakoekoodi(@PathParam("valintaryhmaOid") String valintaryhmaOid, List<KoodiDTO> valintakoekoodit);

    @PUT
    @Path("/{valintaryhmaOid}/valintakoekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertValintakoekoodi(@PathParam("valintaryhmaOid") String valintaryhamOid, KoodiDTO valintakoekoodi);

}
