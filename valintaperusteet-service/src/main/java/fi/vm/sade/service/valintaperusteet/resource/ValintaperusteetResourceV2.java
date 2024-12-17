package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/resources/V2valintaperusteet")
@PreAuthorize("isAuthenticated()")
@Tag(
    name = "/resources/V2valintaperusteet",
    description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn V2")
public class ValintaperusteetResourceV2 extends AbstractValintaperusteetResource {
  @PreAuthorize(CRUD)
  @PostMapping(
      value = "/tuoHakukohde",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "importoi hakukohde")
  @Override
  public ResponseEntity<Object> tuoHakukohde(@RequestBody final HakukohdeImportDTO hakukohde) {
    return super.tuoHakukohde(hakukohde);
  }

  @PreAuthorize(UPDATE_CRUD)
  @PostMapping(
      value = "/{oid}/automaattinenSiirto",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Lisää/poistaa valintatapajonon sijoittelusta")
  public ValintatapajonoDTO updateAutomaattinenSijoitteluunSiirto(
      @Parameter(description = "Valintatapajonon OID") @PathVariable("oid")
          final String valintatapajonoOid,
      @Parameter(description = "Sijoittelustatus", required = true)
          @RequestParam(value = "status", required = false)
          final boolean status,
      final HttpServletRequest request) {
    ValintatapajonoDTO beforeUpdate =
        modelMapper.map(
            valintatapajonoService.readByOid(valintatapajonoOid), ValintatapajonoDTO.class);
    ValintatapajonoDTO afterUpdate =
        modelMapper.map(
            valintatapajonoService.updateAutomaattinenSijoitteluunSiirto(
                valintatapajonoOid, status),
            ValintatapajonoDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.AUTOMAATTISEN_SIJOITTELUN_SIIRRON_PAIVITYS,
        ValintaResource.VALINTAPERUSTEET,
        valintatapajonoOid,
        Changes.updatedDto(afterUpdate, beforeUpdate));
    return modelMapper.map(afterUpdate, ValintatapajonoDTO.class);
  }
}
