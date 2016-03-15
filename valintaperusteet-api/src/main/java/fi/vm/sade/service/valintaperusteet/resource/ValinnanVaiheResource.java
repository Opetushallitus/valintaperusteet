package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;

@Path("valinnanvaihe")
public interface ValinnanVaiheResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    ValinnanVaiheDTO read(@PathParam("oid") String oid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/valintatapajono")
    List<ValintatapajonoDTO> listJonos(@PathParam("oid") String oid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/valintakoe")
    List<ValintakoeDTO> listValintakokeet(@PathParam("oid") String oid);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentOid}/valintatapajono")
    Response addJonoToValinnanVaihe(@PathParam("parentOid") String parentOid, ValintatapajonoCreateDTO jono);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{parentOid}/valintakoe")
    Response addValintakoeToValinnanVaihe(@PathParam("parentOid") String parentOid, ValintakoeCreateDTO koe);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    ValinnanVaiheDTO update(@PathParam("oid") String oid, ValinnanVaiheCreateDTO valinnanVaihe);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    List<ValinnanVaiheDTO> jarjesta(List<String> oids);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Boolean> kuuluuSijoitteluun(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/hakukohteet")
    @Produces(MediaType.APPLICATION_JSON)
    Set<String> hakukohteet(@PathParam("oid") String oid);
}
