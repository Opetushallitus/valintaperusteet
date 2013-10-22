package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("valintatapajono")
@PreAuthorize("isAuthenticated()")
public class ValintatapajonoResource {
    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoResource.class);

    @Autowired
    ValintatapajonoService valintatapajonoService;

    @Autowired
    HakijaryhmaService hakijaryhmaService;

    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;


    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public Valintatapajono readByOid(@PathParam("oid") String oid) {
        return valintatapajonoService.readByOid(oid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Path("{oid}/jarjestyskriteeri")
    @Secured({READ, UPDATE, CRUD})
    public List<Jarjestyskriteeri> findJarjestyskriteeri(@PathParam("oid") String oid) {
        return jarjestyskriteeriService.findJarjestyskriteeriByJono(oid);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Path("{valintatapajonoOid}/hakijaryhma/{hakijaryhmaOid}")
    @Secured({READ, UPDATE, CRUD})
    public Response liitaHakijaryhma(@PathParam("valintatapajonoOid") String valintatapajonoOid,
                                      @PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhmaOid);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            LOGGER.error("Error linking hakijaryhma.", e);
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{valintatapajonoOid}/hakijaryhma")
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<Hakijaryhma> hakijaryhmat(@PathParam("valintatapajonoOid") String valintatapajonoOid) {
        return hakijaryhmaService.findHakijaryhmaByJono(valintatapajonoOid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<Valintatapajono> findAll() {
        return valintatapajonoService.findAll();
    }

    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({UPDATE, CRUD})
    public Response update(@PathParam("oid") String oid, Valintatapajono jono) {
        Valintatapajono update = valintatapajonoService.update(oid, jono);
        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Path("{valintatapajonoOid}/jarjestyskriteeri")
    @Secured({CRUD})
    public Response insertJarjestyskriteeri(@PathParam("valintatapajonoOid") String valintatapajonoOid,
                                            JSONObject jk) throws IOException, JSONException {
        Jarjestyskriteeri jarjestyskriteeri = new Jarjestyskriteeri();
        jarjestyskriteeri.setMetatiedot(jk.optString("metatiedot"));
        jarjestyskriteeri.setAktiivinen(jk.getBoolean("aktiivinen"));
        Long laskentakaavaId = jk.optLong("laskentakaava_id");

        Jarjestyskriteeri insert = jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid,
                jarjestyskriteeri,
                null,
                laskentakaavaId);
        return Response.status(Response.Status.ACCEPTED).entity(insert).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("jarjesta")
    @Secured({UPDATE, CRUD})
    public List<Valintatapajono> jarjesta(List<String> oids) {

        return valintatapajonoService.jarjestaValintatapajonot(oids);
    }

    @DELETE
    @Path("{oid}")
    @Secured({CRUD})
    public Response delete(@PathParam("oid") String oid) {
        valintatapajonoService.deleteByOid(oid);
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
