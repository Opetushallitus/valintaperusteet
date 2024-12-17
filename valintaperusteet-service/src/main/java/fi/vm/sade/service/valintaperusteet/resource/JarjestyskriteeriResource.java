package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/resources/jarjestyskriteeri")
@PreAuthorize("isAuthenticated()")
@Tag(
    name = "/resources/jarjestyskriteeri",
    description = "Resurssi järjestyskriteerien käsittelyyn")
public class JarjestyskriteeriResource {
  @Autowired JarjestyskriteeriService jarjestyskriteeriService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee järjestyskriteerin OID:n perusteella")
  @ApiResponses(@ApiResponse(responseCode = "404", description = "Järjestyskriteeriä ei löydy"))
  public JarjestyskriteeriDTO readByOid(@PathVariable("oid") final String oid) {
    try {
      return modelMapper.map(jarjestyskriteeriService.readByOid(oid), JarjestyskriteeriDTO.class);
    } catch (JarjestyskriteeriEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    }
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää järjestyskriteeriä OID:n perusteella")
  @ApiResponses(
      @ApiResponse(responseCode = "400", description = "Laskentakaavaa ei ole määritetty"))
  public ResponseEntity<JarjestyskriteeriDTO> update(
      @PathVariable("oid") final String oid,
      @RequestBody final JarjestyskriteeriInsertDTO jk,
      final HttpServletRequest request) {
    try {
      JarjestyskriteeriDTO old =
          modelMapper.map(jarjestyskriteeriService.readByOid(oid), JarjestyskriteeriDTO.class);
      JarjestyskriteeriDTO update =
          modelMapper.map(
              jarjestyskriteeriService.update(
                  oid, jk.getJarjestyskriteeri(), jk.getLaskentakaavaId()),
              JarjestyskriteeriDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.JARJESTYSKRITEERI_PAIVITYS,
          ValintaResource.JARJESTYSKRITEERIT,
          oid,
          Changes.updatedDto(update, old));
      return ResponseEntity.accepted().body(update);
    } catch (LaskentakaavaOidTyhjaException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
    }
  }

  @DeleteMapping(value = "/{oid}")
  @PreAuthorize(CRUD)
  @Operation(summary = "Poistaa järjestyskriteerin OID:n perusteella")
  @ApiResponses(
      @ApiResponse(
          responseCode = "403",
          description = "Järjestyskriteeriä ei voida poistaa, esim. se on peritty"))
  public ResponseEntity<Object> delete(
      @PathVariable("oid") final String oid, final HttpServletRequest request) {
    try {
      JarjestyskriteeriDTO dto = jarjestyskriteeriService.delete(oid);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.JARJESTYSKRITEERI_POISTO,
          ValintaResource.JARJESTYSKRITEERIT,
          oid,
          Changes.deleteDto(dto));
      return ResponseEntity.accepted().build();
    } catch (JarjestyskriteeriaEiVoiPoistaaException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, null, e);
    }
  }

  @PostMapping(
      value = "/jarjesta",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Järjestää järjestyskriteerit annetun listan mukaiseen järjestykseen")
  public List<JarjestyskriteeriDTO> jarjesta(
      @RequestBody final List<String> oids, final HttpServletRequest request) {
    List<Jarjestyskriteeri> jks = jarjestyskriteeriService.jarjestaKriteerit(oids);

    Map<String, String> additionalInfo = ImmutableMap.of("Uusi järjestys", toNullsafeString(oids));
    AuditLog.log(
        ValintaperusteetAudit.AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.JARJESTYSKRITEERIT_JARJESTA,
        ValintaResource.JARJESTYSKRITEERIT,
        null,
        Changes.EMPTY,
        additionalInfo);

    return modelMapper.mapList(jks, JarjestyskriteeriDTO.class);
  }
}
