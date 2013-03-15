package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 10.1.2013
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/valintaryhma")
public class ValintaryhmaResource {

    @Autowired
    private ValintaryhmaService valintaryhmaService;


    @Autowired
    private ValinnanVaiheService valinnanVaiheService;


    @Autowired
    private HakukohdeService hakukohdeService;

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Valintaryhma queryFull(@PathParam("oid") String oid) {
        return valintaryhmaService.readByOid(oid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
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
    public List<Valintaryhma> queryChildren(@PathParam("oid") String oid) {
        return valintaryhmaService.findValintaryhmasByParentOid(oid);
    }

    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public List<HakukohdeViite> childHakukohdes(@PathParam("oid") String oid) {
        return hakukohdeService.findByValintaryhmaOid(oid);
    }

    @GET
    @Path("{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    public List<ValinnanVaihe> valinnanVaiheet(@PathParam("oid") String oid) {
        return valinnanVaiheService.findByValintaryhma(oid);
    }


    @PUT
    @Path("{parentOid}/lapsi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
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
    public Response update(@PathParam("oid") String oid, Valintaryhma valintaryhma) {
        valintaryhmaService.update(oid, valintaryhma);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @PUT
    @Path("{valintaryhmaOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Response insertValinnanvaihe(@PathParam("valintaryhmaOid") String valintaryhamOid,
                                        @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid,
                                        ValinnanVaihe valinnanVaihe) {
        try {
            valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhamOid,
                    valinnanVaihe,
                    edellinenValinnanVaiheOid);
            return Response.status(Response.Status.CREATED).entity(valinnanVaihe).build();
        } catch (Exception e) {
            LOGGER.error("Error creating valinnanvaihe.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
