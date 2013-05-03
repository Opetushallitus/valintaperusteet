package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/valintatapajono")
public class ValintatapajonoResource {

    @Autowired
    ValintatapajonoService valintatapajonoService;

    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;


    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Valintatapajono readByOid(@PathParam("oid") String oid) {
        return valintatapajonoService.readByOid(oid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Path("{oid}/jarjestyskriteeri")
    public List<Jarjestyskriteeri> findJarjestyskriteeri(@PathParam("oid") String oid) {
        return jarjestyskriteeriService.findJarjestyskriteeriByJono(oid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public List<Valintatapajono> findAll() {
        return valintatapajonoService.findAll();
    }

    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Response update(@PathParam("oid") String oid, Valintatapajono jono) {
        Valintatapajono update = valintatapajonoService.update(oid, jono);
        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Path("{valintatapajonoOid}/jarjestyskriteeri")
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
    @Path("/jarjesta")
    public List<Valintatapajono> jarjesta(List<String> oids) {

        return valintatapajonoService.jarjestaValintatapajonot(oids);
    }

    @DELETE
    @Path("{oid}")
    public Response delete(@PathParam("oid") String oid) {
        valintatapajonoService.deleteByOid(oid);
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
