package fi.vm.sade.service.valintaperusteet.resource;

import io.swagger.annotations.ApiOperation;
import fi.vm.sade.service.valintaperusteet.dto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("valintaperusteet")
public interface ValintaperusteetResource {

    @GET
    @Path("valintatapajono/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(@PathParam("hakukohdeOid") String hakukohdeOid);

    /**
     * Idempotentti operaatio eli GET body:lla
     */
    @POST
    @Path("/valintatapajono")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(List<String> hakukohdeOids);

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

    @GET
    @Path("/{oid}/automaattinenSiirto")
    @Produces(MediaType.APPLICATION_JSON)
    Boolean readAutomaattinenSijoitteluunSiirto(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}/automaattinenSiirto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ValintatapajonoDTO updateAutomaattinenSijoitteluunSiirto(@PathParam("oid") String oid, Boolean arvo);
}
