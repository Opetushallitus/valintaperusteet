package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/resources/valintakoe")
@PreAuthorize("isAuthenticated()")
@Tag(name = "/resources/valintakoe", description = "Resurssi valintakokeiden käsittelyyn")
public class ValintakoeResource {

  @Autowired private ValintakoeService valintakoeService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  /**
   * @Transactional Heittaa lazy initin. Ehka modelmapper servicen puolelle?
   */
  @Transactional
  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintakokeen OID:n perusteella")
  public ValintakoeDTO readByOid(@PathVariable("oid") final String oid) {
    return modelMapper.map(valintakoeService.readByOid(oid), ValintakoeDTO.class);
  }

  @Transactional
  @PostMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintakokeen OID:n perusteella")
  public List<ValintakoeDTO> readByOids(@RequestBody final List<String> oids) {
    return modelMapper.mapList(valintakoeService.readByOids(oids), ValintakoeDTO.class);
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää valintakoetta")
  public ResponseEntity<ValintakoeDTO> update(
      @PathVariable("oid") final String oid,
      @RequestBody final ValintakoeDTO valintakoe,
      final HttpServletRequest request) {
    ValintakoeDTO beforeUpdate =
        modelMapper.map(valintakoeService.readByOid(oid), ValintakoeDTO.class);
    ValintakoeDTO afterUpdate =
        modelMapper.map(valintakoeService.update(oid, valintakoe), ValintakoeDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTAKOE_PAIVITYS,
        ValintaResource.VALINTAKOE,
        oid,
        Changes.updatedDto(afterUpdate, beforeUpdate));
    return ResponseEntity.accepted().body(afterUpdate);
  }

  @DeleteMapping(value = "/{oid}")
  @PreAuthorize(CRUD)
  @Operation(summary = "Poistaa valintakokeen OID:n perusteella")
  public ResponseEntity<Object> delete(
      @PathVariable("oid") final String oid, final HttpServletRequest request) {
    ValintakoeDTO dto = valintakoeService.delete(oid);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTAKOE_POISTO,
        ValintaResource.VALINTAKOE,
        oid,
        Changes.deleteDto(dto));
    return ResponseEntity.accepted().build();
  }
}
