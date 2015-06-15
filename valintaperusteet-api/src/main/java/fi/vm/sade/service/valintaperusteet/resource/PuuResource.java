package fi.vm.sade.service.valintaperusteet.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;

@Path("puu")
public interface PuuResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaperustePuuDTO> search(@QueryParam("q") String searchString, @QueryParam("hakuOid") String hakuOid,
            @QueryParam("tila") List<String> tila, @QueryParam("hakukohteet") @DefaultValue("true") boolean hakukohteet,
            @QueryParam("kohdejoukko") @DefaultValue("") String kohdejoukko,
            @QueryParam("valintaryhma") @DefaultValue("") String valintaryhma);
}
