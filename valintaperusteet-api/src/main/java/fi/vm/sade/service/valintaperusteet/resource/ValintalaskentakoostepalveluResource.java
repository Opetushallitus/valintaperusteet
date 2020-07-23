package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/valintalaskentakoostepalvelu")
public interface ValintalaskentakoostepalveluResource {

  /** @return sijoitteluun siirrettävät valintatapajonot hakukohdeoideittain */
  @POST
  @Path("/valintatapajono")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
      List<String> hakukohdeOids);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/haku")
  List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(List<String> hakukohdeOids);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/haku/valintatapajono/hakijaryhmat")
  List<HakijaryhmaValintatapajonoDTO> readByValintatapajonoOids(List<String> valintatapajonoOids);
}
