package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("valintaperusteet")
public interface ValintaperusteetResource {

    @GET
    @Path("valintatapajono/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(@PathParam("hakukohdeOid") String hakukohdeOid);

    @GET
    @Path("{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaperusteetDTO> haeValintaperusteet(@PathParam("hakukohdeOid") String hakukohdeOid, @QueryParam("vaihe") Integer valinnanVaiheJarjestysluku);

    @GET
    @Path("hakijaryhma/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(@PathParam("hakukohdeOid") String hakukohdeOid);

    @POST
    @Path("tuoHakukohde")
    @Consumes(MediaType.APPLICATION_JSON)
    Response tuoHakukohde(HakukohdeImportDTO hakukohde);
}
