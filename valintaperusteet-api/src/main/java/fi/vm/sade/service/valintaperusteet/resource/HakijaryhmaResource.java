package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;

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
import java.util.List;

@Path("hakijaryhma")
public interface HakijaryhmaResource {

    /**
     * Operation is idempotent
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/haku")
    List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(List<String> hakukohdeOids);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/haku/{hakuOid}")
    List<HakijaryhmaValintatapajonoDTO> readByHakuOid(@PathParam("hakuOid") String hakuOid);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    HakijaryhmaDTO read(@PathParam("oid") String oid);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    HakijaryhmaDTO update(@PathParam("oid") String oid, HakijaryhmaCreateDTO hakijaryhma);

    @DELETE
    @Path("/{oid}")
    Response delete(@PathParam("oid") String oid);

    @PUT
    @Path("/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response siirra(HakijaryhmaSiirraDTO dto);

}
