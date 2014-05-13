package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.HashMap;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.resource.HakukohdeResource;
import fi.vm.sade.service.valintaperusteet.resource.ValintaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;

/**
 * Created with IntelliJ IDEA. User: kkammone Date: 10.1.2013 Time: 12:01 To
 * change this template use File | Settings | File Templates.
 */
@Component
@Path("hakukohde")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakukohde", description = "Resurssi hakukohteiden käsittelyyn")
public class HakukohdeResourceImpl implements HakukohdeResource {

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @Autowired
    HakukohdeService hakukohdeService;

    @Autowired
    HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    ValinnanVaiheService valinnanVaiheService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    LaskentakaavaService laskentakaavaService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    public HakukohdeResourceImpl() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteita. Joko kaikki tai päätason hakukohteet.", response = HakukohdeViiteDTO.class)
    public List<HakukohdeViiteDTO> query(
            @QueryParam("paataso") @DefaultValue("false") @ApiParam(name = "paataso", value = "Haetaanko päätason hakukohteet vai kaikki") boolean paataso) {
        List<HakukohdeViite> hakukohteet = null;

        if (paataso) {
            hakukohteet = hakukohdeService.findRoot();
        } else {
            hakukohteet = hakukohdeService.findAll();
        }

        return modelMapper.mapList(hakukohteet, HakukohdeViiteDTO.class);
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen OID:n perusteella", response = HakukohdeViiteDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Hakukohdetta ei löydy") })
    public HakukohdeViiteDTO queryFull(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            return modelMapper.map(hakukohdeService.readByOid(oid), HakukohdeViiteDTO.class);
        } catch (HakukohdeViiteEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää hakukohteen valintaryhmään (tai juureen, jos valintaryhmän OID:a ei ole annettu)")
    public Response insert(
            @ApiParam(value = "Lisättävä hakukohde ja valintaryhmä", required = true) HakukohdeInsertDTO hakukohde) {
        try {
            HakukohdeViiteDTO hkv = modelMapper.map(
                    hakukohdeService.insert(hakukohde.getHakukohde(), hakukohde.getValintaryhmaOid()),
                    HakukohdeViiteDTO.class);
            return Response.status(Response.Status.CREATED).entity(hkv).build();
        } catch (Exception e) {
            LOGGER.warn("Hakukohdetta ei saatu lisättyä. ", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorDTO(e.getMessage())).build();
        }
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakukohdetta OID:n perusteella")
    public Response update(
            @ApiParam(value = "Päivitettävän hakukohteen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "hakukohteen uudet tiedot", required = true) HakukohdeViiteCreateDTO hakukohdeViite) {
        try {
            HakukohdeViiteDTO hkv = modelMapper.map(hakukohdeService.update(oid, hakukohdeViite),
                    HakukohdeViiteDTO.class);
            return Response.status(Response.Status.ACCEPTED).entity(hkv).build();
        } catch (Exception e) {
            LOGGER.warn("Hakukohdetta ei saatu päivitettyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen valinnan vaiheet OID:n perusteella", response = ValinnanVaiheDTO.class)
    public List<ValinnanVaiheDTO> valinnanVaihesForHakukohde(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(valinnanVaiheService.findByHakukohde(oid), ValinnanVaiheDTO.class);
    }

    @GET
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen valintakokeet OID:n perusteella", response = ValintakoeDTO.class)
    public List<ValintakoeDTO> valintakoesForHakukohde(String oid) {
        return modelMapper.mapList(valintakoeService.findValintakoesByValinnanVaihes(valinnanVaiheService.findByHakukohde(oid)), ValintakoeDTO.class);
    }


    @GET
    @Path("/{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa tiedon, kuuluuko hakukohde sijoitteluun", response = Boolean.class)
    public Map<String, Boolean> kuuluuSijoitteluun(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("sijoitteluun", hakukohdeService.kuuluuSijoitteluun(oid));
        return map;
    }

    @GET
    @Path("/{oid}/ilmanlaskentaa")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> ilmanLaskentaa(String oid) {
        return modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid), ValintatapajonoDTO.class);
    }

    @GET
    @Path("/{oid}/hakijaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen hakijaryhmät", response = HakijaryhmaDTO.class)
    public List<HakijaryhmaDTO> hakijaryhmat(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(hakijaryhmaService.findByHakukohde(oid), HakijaryhmaDTO.class);
    }

    @GET
    @Path("/{oid}/laskentakaava")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen järjestyskriteerit", response = JarjestyskriteeriDTO.class)
    public List<JarjestyskriteeriDTO> findLaskentaKaavat(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(jarjestyskriteeriService.findByHakukohde(oid), JarjestyskriteeriDTO.class);
    }

    @GET
    @Path("/avaimet/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
    public List<ValintaperusteDTO> findAvaimet(@ApiParam(value = "Hakukohde OID", required = true) @PathParam("oid") String oid) {
        return laskentakaavaService.findAvaimetForHakukohde(oid);
    }

    @GET
    @Path("{hakukohdeOid}/avaimet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    public HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(@PathParam("hakukohdeOid") String hakukohdeOid) {
        return laskentakaavaService.findHakukohteenAvaimet(hakukohdeOid);
    }

    @PUT
    @Path("/{hakukohdeOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää valinnan vaiheen hakukohteelle")
    @ApiResponses(@ApiResponse(code = 400, message = "Valinnan vaiheen lisääminen epäonnistui"))
    public Response insertValinnanvaihe(
            @ApiParam(value = "Hakukohteen OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Edellisen valinnan vaiheen OID (jos valinnan vaihe halutaa lisätä tietyn vaiheen jälkeen, muussa tapauksessa uusi vaihe lisätään viimeiseksi)") @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid,
            @ApiParam(value = "Uusi valinnan vaihe", required = true) ValinnanVaiheCreateDTO valinnanVaihe) {
        try {
            ValinnanVaiheDTO lisatty = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheHakukohteelle(
                    hakukohdeOid, valinnanVaihe, edellinenValinnanVaiheOid), ValinnanVaiheDTO.class);
            return Response.status(Response.Status.CREATED).entity(lisatty).build();
        } catch (Exception e) {
            LOGGER.error("Error creating valinnanvaihe.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{hakukohdeOid}/hakijaryhma")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää hakijaryhmän hakukohteelle")
    @ApiResponses(@ApiResponse(code = 400, message = "Hakijaryhmän lisääminen epäonnistui"))
    public Response insertHakijaryhma(
            @ApiParam(value = "Hakukohteen OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakijaryhmä", required = true) HakijaryhmaCreateDTO hakijaryhma) {
        try {
            HakijaryhmaDTO lisatty = modelMapper.map(
                    hakijaryhmaService.lisaaHakijaryhmaHakukohteelle(hakukohdeOid, hakijaryhma), HakijaryhmaDTO.class);
            return Response.status(Response.Status.CREATED).entity(lisatty).build();
        } catch (Exception e) {
            LOGGER.error("Error creating hakijaryhma.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakukohteen hakukohdekoodia")
    @ApiResponses(@ApiResponse(code = 400, message = "Päivittäminen epäonnistui"))
    public Response updateHakukohdekoodi(
            @ApiParam(value = "Hakukohde OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakukohdekoodi", required = true) KoodiDTO hakukohdekoodi) {
        try {
            KoodiDTO lisatty = modelMapper.map(
                    hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, hakukohdekoodi), KoodiDTO.class);
            return Response.status(Response.Status.ACCEPTED).entity(lisatty).build();
        } catch (Exception e) {
            LOGGER.error("Error updating hakukohdekoodit.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää hakukohdekoodin hakukohteelle")
    @ApiResponses(@ApiResponse(code = 400, message = "Lisääminen epäonnistui"))
    public Response insertHakukohdekoodi(
            @ApiParam(value = "Hakukohde OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakukohdekoodi", required = true) KoodiDTO hakukohdekoodi) {
        try {
            KoodiDTO lisatty = modelMapper.map(
                    hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, hakukohdekoodi), KoodiDTO.class);
            return Response.status(Response.Status.CREATED).entity(lisatty).build();
        } catch (Exception e) {
            LOGGER.error("Error inserting hakukohdekoodi.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{hakukohdeOid}/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Siirtää hakukohteen uuteen valintaryhmään (tai juureen, jos valintaryhmää ei anneta)")
    @ApiResponses(@ApiResponse(code = 400, message = "Siirtäminen epäonnistui"))
    public Response siirraHakukohdeValintaryhmaan(
            @ApiParam(value = "Hakukohde OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Uuden valintaryhmän OID") String valintaryhmaOid) {
        try {
            HakukohdeViiteDTO hakukohde = modelMapper.map(
                    hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, true),
                    HakukohdeViiteDTO.class);
            return Response.status(Response.Status.ACCEPTED).entity(hakukohde).build();
        } catch (Exception e) {
            LOGGER.error("Error moving hakukohde to new valintaryhma.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
