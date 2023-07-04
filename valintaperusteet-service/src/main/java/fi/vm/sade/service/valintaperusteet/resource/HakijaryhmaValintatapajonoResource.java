package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = "/resources/hakijaryhma_valintatapajono")
@Tag(
    name = "/resources/hakijaryhma_valintatapajono",
    description = "Resurssi hakijaryhmien ja valintatapajonojen välisten liitosten käsittelyyn")
public class HakijaryhmaValintatapajonoResource {
  protected static final Logger LOGGER =
      LoggerFactory.getLogger(HakijaryhmaValintatapajonoResource.class);

  @Autowired ValintatapajonoService jonoService;

  @Autowired ValintakoeService valintakoeService;

  @Autowired HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  public HakijaryhmaValintatapajonoDTO read(@PathVariable("oid") final String oid) {
    try {
      return modelMapper.map(
          hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoDTO.class);
    } catch (HakijaryhmaEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, null, e);
    }
  }

  @DeleteMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Poistaa hakijaryhmän ja valintatapajonon välisen liitoksen")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "403",
            description = "Liitosta ei voida poistaa, esim. se on peritty"),
      })
  public ResponseEntity<Object> poistaHakijaryhma(
      @PathVariable("oid") final String oid, final HttpServletRequest request) {
    try {
      HakijaryhmaValintatapajonoDTO dto = hakijaryhmaValintatapajonoService.delete(oid);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_POISTO,
          ValintaResource.HAKIJARYHMA_VALINTATAPAJONO,
          oid,
          Changes.deleteDto(dto));
      return ResponseEntity.ok().build();
    } catch (HakijaryhmaaEiVoiPoistaaException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, null, e);
    } catch (Exception e) {
      LOGGER.error("Error removing hakijaryhma.", e);
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää hakijaryhmän ja valintatapajonon välistä liitosta")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "404", description = "Liitosta ei ole olemassa"),
      })
  public ResponseEntity<HakijaryhmaValintatapajonoDTO> update(
      @PathVariable("oid") final String oid,
      @RequestBody final HakijaryhmaValintatapajonoDTO jono,
      final HttpServletRequest request) {
    try {
      HakijaryhmaValintatapajonoDTO beforeUpdate =
          modelMapper.map(
              hakijaryhmaValintatapajonoService.readByOid(oid),
              HakijaryhmaValintatapajonoDTO.class);
      HakijaryhmaValintatapajonoDTO afterUpdate =
          modelMapper.map(
              hakijaryhmaValintatapajonoService.update(oid, jono),
              HakijaryhmaValintatapajonoDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_PAIVITYS,
          ValintaResource.HAKIJARYHMA_VALINTATAPAJONO,
          oid,
          Changes.updatedDto(afterUpdate, beforeUpdate));
      return ResponseEntity.accepted().body(afterUpdate);
    } catch (HakijaryhmaEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    }
  }

  @PostMapping(
      value = "/jarjesta",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Järjestää valintatapajonon hakijaryhmät annetun OID-listan mukaan")
  public List<HakijaryhmaValintatapajonoDTO> jarjesta(
      @RequestBody final List<String> oids, final HttpServletRequest request) {
    List<HakijaryhmaValintatapajono> jarjestetytHakijaryhmat =
        hakijaryhmaValintatapajonoService.jarjestaHakijaryhmat(oids);

    // For auditlog
    String targetOid = null;
    if (!jarjestetytHakijaryhmat.isEmpty()) {
      targetOid = jarjestetytHakijaryhmat.get(0).getHakukohdeViite().getOid();
    }
    Map<String, String> sortedOids = ImmutableMap.of("Oid-järjestys", toNullsafeString(oids));
    AuditLog.log(
        ValintaperusteetAudit.AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTATAPAJONO_HAKIJARYHMAT_JARJESTA,
        ValintaResource.HAKIJARYHMA_VALINTATAPAJONO,
        targetOid,
        Changes.EMPTY,
        sortedOids);
    return modelMapper.mapList(jarjestetytHakijaryhmat, HakijaryhmaValintatapajonoDTO.class);
  }
}
