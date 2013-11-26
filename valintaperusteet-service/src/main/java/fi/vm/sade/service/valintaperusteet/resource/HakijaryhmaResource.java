package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
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
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("hakijaryhma")
@PreAuthorize("isAuthenticated()")
public class HakijaryhmaResource {

    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    HakijaryhmaService hakijaryhmaService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    protected final static Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("{oid}")
    @Secured({READ, UPDATE, CRUD})
    public Hakijaryhma read(@PathParam("oid") String oid) {
        return hakijaryhmaService.readByOid(oid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hakijaryhmaOid}/valintatapajono")
    @JsonView({JsonViews.Basic.class})
    @Secured({READ, UPDATE, CRUD})
    public List<HakijaryhmaValintatapajono> valintatapajonot(@PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        return hakijaryhmaValintatapajonoService.findByHakijaryhma(hakijaryhmaOid);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("{oid}")
    @Secured({UPDATE, CRUD})
    public Hakijaryhma update(@PathParam("oid") String oid, Hakijaryhma hakijaryhma) {
        return hakijaryhmaService.update(oid, hakijaryhma);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Basic.class)
    @Path("jarjesta")
    @Secured({UPDATE, CRUD})
    public List<Hakijaryhma> jarjesta(List<String> oids) {
        return hakijaryhmaService.jarjestaHakijaryhmat(oids);
    }

    @DELETE
    @Path("{oid}")
    @Secured({CRUD})
    public Response delete(@PathParam("oid") String oid) {
        hakijaryhmaService.deleteByOid(oid, false);
        return Response.status(Response.Status.ACCEPTED).build();
    }


}
