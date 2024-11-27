package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/resources/valintaperusteet")
@PreAuthorize("isAuthenticated()")
@Tag(
    name = "/resources/valintaperusteet",
    description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class ValintaperusteetResource extends AbstractValintaperusteetResource {
  @PreAuthorize(UPDATE_CRUD)
  @PostMapping(
      value = "/{oid}/automaattinenSiirto",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ValintatapajonoDTO updateAutomaattinenSijoitteluunSiirto(
      @PathVariable("oid") final String oid, Boolean arvo, final HttpServletRequest request) {
    ValintatapajonoDTO beforeUpdate =
        modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
    ValintatapajonoDTO afterUpdate =
        modelMapper.map(
            valintatapajonoService.updateAutomaattinenSijoitteluunSiirto(oid, arvo),
            ValintatapajonoDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.AUTOMAATTISEN_SIJOITTELUN_SIIRRON_PAIVITYS,
        ValintaResource.VALINTAPERUSTEET,
        oid,
        Changes.updatedDto(afterUpdate, beforeUpdate));
    return modelMapper.map(afterUpdate, ValintatapajonoDTO.class);
  }
}
