package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 31.1.2013
 * Time: 10.51
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("jarjestyskriteeri")
public class JarjestyskriteeriResource {
    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Jarjestyskriteeri readByOid(@PathParam("oid") String oid) {
        return jarjestyskriteeriService.readByOid(oid);
    }

    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Response update(@PathParam("oid") String oid, JSONObject jk) {
        Jarjestyskriteeri jarjestyskriteeri = new Jarjestyskriteeri();
        jarjestyskriteeri.setOid(jk.optString("oid"));
        jarjestyskriteeri.setMetatiedot(jk.optString("metatiedot"));
        jarjestyskriteeri.setAktiivinen(jk.optBoolean("aktiivinen"));

        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setId(jk.optLong("laskentakaava_id"));

        jarjestyskriteeri.setLaskentakaava(laskentakaava);

        Jarjestyskriteeri update = jarjestyskriteeriService.update(oid, jarjestyskriteeri);

        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @DELETE
    @Path("{oid}")
    public Response delete(@PathParam("oid") String oid) {
        jarjestyskriteeriService.deleteByOid(oid);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("/jarjesta")
    public List<Jarjestyskriteeri> jarjesta(List<String> oids) {

        return jarjestyskriteeriService.jarjestaKriteerit(oids);
    }
}
