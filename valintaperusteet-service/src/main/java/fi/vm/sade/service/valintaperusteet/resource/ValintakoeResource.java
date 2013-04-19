package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.04
 */
@Component
@Path("/valintakoe")
public class ValintakoeResource {

    @Autowired
    private ValintakoeService valintakoeService;

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Valintakoe readByOid(@PathParam("oid") String oid) {
        return valintakoeService.readByOid(oid);
    }


    @POST
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    public Response update(@PathParam("oid") String oid, JSONObject jk) {
        Valintakoe vk = new Valintakoe();
        vk.setOid(jk.optString("oid"));
        vk.setNimi(jk.optString("nimi"));
        vk.setKuvaus(jk.optString("kuvaus"));
        vk.setTunniste(jk.optString("tunniste"));

       // Laskentakaava laskentakaava = new Laskentakaava();
     //   laskentakaava.setId(jk.optLong("laskentakaava_id"));

       // jarjestyskriteeri.setLaskentakaava(laskentakaava);

        Valintakoe update = valintakoeService.update(oid, vk);

        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @DELETE
    @Path("{oid}")
    public Response delete(@PathParam("oid") String oid) {
        valintakoeService.deleteByOid(oid);
        return Response.status(Response.Status.ACCEPTED).build();
    }


}
