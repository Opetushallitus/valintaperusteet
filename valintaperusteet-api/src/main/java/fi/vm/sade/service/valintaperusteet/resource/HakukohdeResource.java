package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;
import java.util.Map;

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

import fi.vm.sade.service.valintaperusteet.dto.*;

@Path("hakukohde")
public interface HakukohdeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<HakukohdeViiteDTO> query(@QueryParam("paataso") @DefaultValue("false") boolean paataso);

    @GET
    @Path("haku/{hakuOid}")
    @Produces(MediaType.APPLICATION_JSON)
    List<HakukohdeViiteDTO> haunHakukohteet(@PathParam("hakuOid") String hakuOid);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    HakukohdeViiteDTO queryFull(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/valintaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    ValintaryhmaDTO queryValintaryhma(@PathParam("oid") String oid);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insert(HakukohdeInsertDTO hakukohde);

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response update(@PathParam("oid") String oid, HakukohdeViiteCreateDTO hakukohdeViite);

    @GET
    @Path("/{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValinnanVaiheDTO> valinnanVaihesForHakukohde(@PathParam("oid") String oid,
                                                      @QueryParam("withValisijoitteluTieto") String withValisijoitteluTieto);

    @GET
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintakoeDTO> valintakoesForHakukohde(@PathParam("oid") String oid);

    @POST
    @Path("/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(List<String> oids);

    @GET
    @Path("/{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Boolean> kuuluuSijoitteluun(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/ilmanlaskentaa")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/hakijaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/laskentakaava")
    @Produces(MediaType.APPLICATION_JSON)
    List<JarjestyskriteeriDTO> findLaskentaKaavat(@PathParam("oid") String oid);

    @GET
    @Path("/avaimet/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaperusteDTO> findAvaimet(@PathParam("oid") String oid);

    @GET
    @Path("{hakukohdeOid}/avaimet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(@PathParam("hakukohdeOid") String hakukohdeOid);

    @PUT
    @Path("/{hakukohdeOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertValinnanvaihe(
            @PathParam("hakukohdeOid") String hakukohdeOid,
            @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid,
            ValinnanVaiheCreateDTO valinnanVaihe);

    @PUT
    @Path("/{hakukohdeOid}/hakijaryhma")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertHakijaryhma(@PathParam("hakukohdeOid") String hakukohdeOid, HakijaryhmaCreateDTO hakijaryhma);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{hakukohdeOid}/hakijaryhma/{hakijaryhmaOid}")
    Response liitaHakijaryhma(@PathParam("hakukohdeOid") String hakukohdeOid,
                              @PathParam("hakijaryhmaOid") String hakijaryhmaOid);

    @POST
    @Path("/{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateHakukohdekoodi(@PathParam("hakukohdeOid") String hakukohdeOid, KoodiDTO hakukohdekoodi);

    @PUT
    @Path("/{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response insertHakukohdekoodi(@PathParam("hakukohdeOid") String hakukohdeOid, KoodiDTO hakukohdekoodi);

    @POST
    @Path("/{hakukohdeOid}/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response siirraHakukohdeValintaryhmaan(@PathParam("hakukohdeOid") String hakukohdeOid, String valintaryhmaOid);
}
