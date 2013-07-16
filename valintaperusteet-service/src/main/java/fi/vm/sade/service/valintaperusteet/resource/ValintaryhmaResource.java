package fi.vm.sade.service.valintaperusteet.resource;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * Created with IntelliJ IDEA. User: kkammone Date: 10.1.2013 Time: 12:01 To
 * change this template use File | Settings | File Templates.
 */
@Component
@Path("valintaryhma")
@PreAuthorize("isAuthenticated()")
public class ValintaryhmaResource {

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    private OpetuskielikoodiService opetuskielikoodiService;

    @Autowired
    private ValintakoekoodiService valintakoekoodiService;

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public Valintaryhma queryFull(@PathParam("oid") String oid) {
        return valintaryhmaService.readByOid(oid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured({READ, UPDATE, CRUD})
    public List<Valintaryhma> search(@QueryParam("paataso") Boolean paataso, @QueryParam("parentsOf") String parentsOf) {
        List<Valintaryhma> valintaryhmas = new ArrayList<Valintaryhma>();
        if (Boolean.TRUE.equals(paataso)) {
            valintaryhmas.addAll(valintaryhmaService.findValintaryhmasByParentOid(null));
        }
        if (parentsOf != null) {
            valintaryhmas.addAll(valintaryhmaService.findParentHierarchyFromOid(parentsOf));
        }
        return valintaryhmas;
    }

    @GET
    @Path("{oid}/parents")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.ParentHierarchy.class)
    @Secured({READ, UPDATE, CRUD})
    public List<Valintaryhma> parentHierarchy(@PathParam("oid") String parentsOf) {
        List<Valintaryhma> valintaryhmas = new ArrayList<Valintaryhma>();
        if (parentsOf != null) {
            valintaryhmas.addAll(valintaryhmaService.findParentHierarchyFromOid(parentsOf));
        }
        return valintaryhmas;
    }

    @GET
    @Path("{oid}/lapsi")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<Valintaryhma> queryChildren(@PathParam("oid") String oid) {
        return valintaryhmaService.findValintaryhmasByParentOid(oid);
    }

    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<HakukohdeViite> childHakukohdes(@PathParam("oid") String oid) {
        return hakukohdeService.findByValintaryhmaOid(oid);
    }

    @GET
    @Path("{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured({READ, UPDATE, CRUD})
    public List<ValinnanVaihe> valinnanVaiheet(@PathParam("oid") String oid) {
        return valinnanVaiheService.findByValintaryhma(oid);
    }

    @PUT
    @Path("{parentOid}/lapsi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertChild(@PathParam("parentOid") String parentOid, Valintaryhma valintaryhma) {
        try {
            valintaryhma = valintaryhmaService.insert(valintaryhma, parentOid);
            return Response.status(Response.Status.CREATED).entity(valintaryhma).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insert(Valintaryhma valintaryhma) {
        try {
            valintaryhmaService.insert(valintaryhma);
            return Response.status(Response.Status.CREATED).entity(valintaryhma).build();
        } catch (Exception e) {
            LOGGER.error("Error creating valintaryhmä.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response update(@PathParam("oid") String oid, Valintaryhma valintaryhma) {
        valintaryhmaService.update(oid, valintaryhma);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @PUT
    @Path("{valintaryhmaOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertValinnanvaihe(@PathParam("valintaryhmaOid") String valintaryhamOid,
                                        @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid, ValinnanVaihe valinnanVaihe) {
        try {
            valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhamOid, valinnanVaihe,
                    edellinenValinnanVaiheOid);
            return Response.status(Response.Status.CREATED).entity(valinnanVaihe).build();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error creating valinnanvaihe.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("{valintaryhmaOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response updateHakukohdekoodi(@PathParam("valintaryhmaOid") String valintaryhmaOid,
                                         Set<Hakukohdekoodi> hakukohdekoodit) {
        try {
            hakukohdekoodiService.updateValintaryhmaHakukohdekoodit(valintaryhmaOid, hakukohdekoodit);
            return Response.status(Response.Status.ACCEPTED).entity(hakukohdekoodit).build();
        } catch (Exception e) {
            LOGGER.error("Error updating hakukohdekoodit.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("{valintaryhmaOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertHakukohdekoodi(@PathParam("valintaryhmaOid") String valintaryhamOid,
                                         Hakukohdekoodi hakukohdekoodi) {
        try {
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(valintaryhamOid, hakukohdekoodi);
            return Response.status(Response.Status.CREATED).entity(hakukohdekoodi).build();
        } catch (Exception e) {
            LOGGER.error("Error inserting hakukohdekoodi.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("{valintaryhmaOid}/opetuskielikoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured(CRUD)
    public Response updateOpetuskielikoodi(@PathParam("valintaryhmaOid") String valintaryhmaOid,
                                           Set<Opetuskielikoodi> opetuskielikoodit) {
        try {
            opetuskielikoodiService.updateValintaryhmaOpetuskielikoodit(valintaryhmaOid, opetuskielikoodit);
            return Response.status(Response.Status.ACCEPTED).entity(opetuskielikoodit).build();
        } catch (Exception e) {
            LOGGER.error("Error updating opetuskielikoodit.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("{valintaryhmaOid}/opetuskielikoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertOpetuskielikoodi(@PathParam("valintaryhmaOid") String valintaryhamOid,
                                           Opetuskielikoodi opetuskielikoodi) {
        try {
            opetuskielikoodiService.lisaaOpetuskielikoodiValintaryhmalle(valintaryhamOid, opetuskielikoodi);
            return Response.status(Response.Status.CREATED).entity(opetuskielikoodi).build();
        } catch (Exception e) {
            LOGGER.error("Error inserting opetuskielikoodi.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("{valintaryhmaOid}/valintakoekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Secured(CRUD)
    public Response updateValintakoekoodi(@PathParam("valintaryhmaOid") String valintaryhmaOid,
                                          List<Valintakoekoodi> valintakoekoodit) {
        try {
            valintakoekoodiService.updateValintaryhmanValintakoekoodit(valintaryhmaOid, valintakoekoodit);
            return Response.status(Response.Status.ACCEPTED).entity(valintakoekoodit).build();
        } catch (Exception e) {
            LOGGER.error("Error updating valintakoekoodit.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("{valintaryhmaOid}/valintakoekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({CRUD})
    public Response insertValintakoekoodi(@PathParam("valintaryhmaOid") String valintaryhamOid,
                                          Valintakoekoodi valintakoekoodi) {
        try {
            valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhamOid, valintakoekoodi);
            return Response.status(Response.Status.CREATED).entity(valintakoekoodi).build();
        } catch (Exception e) {
            LOGGER.error("Error inserting valintakoekoodi.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
