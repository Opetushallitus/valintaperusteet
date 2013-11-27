package fi.vm.sade.service.valintaperusteet.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import fi.vm.sade.service.valintaperusteet.dto.ErrorDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 10.1.2013
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("hakukohde")
@PreAuthorize("isAuthenticated()")
public class HakukohdeResource {

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @Autowired
    HakukohdeService hakukohdeService;

    @Autowired
    HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    ValinnanVaiheService valinnanVaiheService;

    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    LaskentakaavaService laskentakaavaService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private OidService oidService;

    public HakukohdeResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Find pet by ID", notes = "More notes about this method", response = HakukohdeViite.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Pet not found")
    })
    public List<HakukohdeViite> query(@QueryParam("paataso") @DefaultValue("false") boolean paataso) {
        if (paataso) {
            return hakukohdeService.findRoot();
        } else {
            return hakukohdeService.findAll();
        }
    }


    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public HakukohdeViite queryFull(@PathParam("oid") String oid) {
        return hakukohdeService.readByOid(oid);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insert(HakukohdeViiteDTO hakukohdeViiteDTO) {
        try {
            HakukohdeViite hkv = hakukohdeService.insert(hakukohdeViiteDTO);
            return Response.status(Response.Status.CREATED).entity(hkv).build();
        } catch (Exception e) {
            LOGGER.warn("Hakukohdetta ei saatu lisättyä. ", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorDTO(e.getMessage())).build();
        }
    }

    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response update(@PathParam("oid") String oid, HakukohdeViite hakukohdeViite) {
        try {
            HakukohdeViite hkv = hakukohdeService.update(oid, hakukohdeViite);
            return Response.status(Response.Status.ACCEPTED).entity(hkv).build();
        } catch (Exception e) {
            LOGGER.warn("Hakukohdetta ei saatu päivitettyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    @ApiOperation(value = "Find pet by ID", notes = "More notes about this method", response = ValinnanVaihe.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Pet not found")
    })
    public List<ValinnanVaihe> valinnanVaihesForHakukohde(@PathParam("oid") String oid) {
        return valinnanVaiheService.findByHakukohde(oid);
    }

    @GET
    @Path("{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public Map<String, Boolean> kuuluuSijoitteluun(@PathParam("oid") String oid) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("sijoitteluun", hakukohdeService.kuuluuSijoitteluun(oid));
        return map;
    }

    @GET
    @Path("{oid}/hakijaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<Hakijaryhma> hakijaryhmat(@PathParam("oid") String oid) {
        return hakijaryhmaService.findByHakukohde(oid);
    }

    @GET
    @Path("{oid}/laskentakaava")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<Jarjestyskriteeri> findLaskentaKaavat(@PathParam("oid") String oid) {
        return jarjestyskriteeriService.findByHakukohde(oid);
    }

    @POST
    @Path("avaimet")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<ValintaperusteDTO> findAvaimet(List<String> oids) {
        return laskentakaavaService.findAvaimetForHakukohdes(oids);
    }

    @PUT
    @Path("{hakukohdeOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertValinnanvaihe(@PathParam("hakukohdeOid") String hakukohdeOid,
                                        @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid,
                                        ValinnanVaihe valinnanVaihe) {
        try {
            valinnanVaihe.setOid(oidService.haeValinnanVaiheOid());
            valinnanVaiheService.lisaaValinnanVaiheHakukohteelle(hakukohdeOid,
                    valinnanVaihe,
                    edellinenValinnanVaiheOid);
            return Response.status(Response.Status.CREATED).entity(valinnanVaihe).build();
        } catch (Exception e) {
            LOGGER.error("Error creating valinnanvaihe.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("{hakukohdeOid}/hakijaryhma")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertHakijaryhma(@PathParam("hakukohdeOid") String hakukohdeOid,
                                      Hakijaryhma hakijaryhma) {
        try {
            hakijaryhma.setOid(oidService.haeHakijaryhmaOid());

            hakijaryhma = hakijaryhmaService.lisaaHakijaryhmaHakukohteelle(hakukohdeOid,
                    hakijaryhma);
            return Response.status(Response.Status.CREATED).entity(hakijaryhma).build();
        } catch (Exception e) {
            LOGGER.error("Error creating hakijaryhma.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response updateHakukohdekoodi(@PathParam("hakukohdeOid") String hakukohdeOid,
                                         Hakukohdekoodi hakukohdekoodi) {
        try {
            hakukohdekoodi = hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, hakukohdekoodi);
            return Response.status(Response.Status.ACCEPTED).entity(hakukohdekoodi).build();
        } catch (Exception e) {
            LOGGER.error("Error updating hakukohdekoodit.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertHakukohdekoodi(@PathParam("hakukohdeOid") String hakukohdeOid,
                                         Hakukohdekoodi hakukohdekoodi) {
        try {
            hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, hakukohdekoodi);
            return Response.status(Response.Status.CREATED).entity(hakukohdekoodi).build();
        } catch (Exception e) {
            LOGGER.error("Error creating valinnanvaihe.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("{hakukohdeOid}/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response siirraHakukohdeValintaryhmaan(@PathParam("hakukohdeOid") String hakukohdeOid,
                                                  String valintaryhmaOid) {
        try {
            HakukohdeViite hakukohde = hakukohdeService.
                    siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, true);
            return Response.status(Response.Status.ACCEPTED).entity(hakukohde).build();
        } catch (Exception e) {
            LOGGER.error("Error moving hakukohde to new valintaryhma.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
