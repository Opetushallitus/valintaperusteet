package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
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
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@Tag(name = "/resources/hakijaryhma", description = "Resurssi hakijaryhmien käsittelyyn")
@RequestMapping(value = "/resources/hakijaryhma")
public class HakijaryhmaResource {
  @Autowired ValintatapajonoService jonoService;

  @Autowired ValintakoeService valintakoeService;

  @Autowired HakijaryhmaService hakijaryhmaService;

  @Autowired HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

  @Autowired HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  protected static final Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaResource.class);

  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakijaryhmän OID:n perusteella")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "404", description = "Hakijaryhmää ei löydy"),
      })
  public HakijaryhmaDTO read(@PathVariable("oid") final String oid) {
    try {
      return modelMapper.map(hakijaryhmaService.readByOid(oid), HakijaryhmaDTO.class);
    } catch (HakijaryhmaEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, null, e);
    }
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää hakijaryhmän")
  public HakijaryhmaDTO update(
      @PathVariable("oid") final String oid,
      @RequestBody final HakijaryhmaCreateDTO hakijaryhma,
      final HttpServletRequest request) {
    Hakijaryhma old = hakijaryhmaService.readByOid(oid);
    Hakijaryhma updated = hakijaryhmaService.update(oid, hakijaryhma);
    AuditLog.log(
        ValintaperusteetAudit.AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.HAKIJARYHMA_PAIVITYS,
        ValintaResource.HAKIJARYHMA,
        oid,
        Changes.updatedDto(updated, old));
    return modelMapper.map(updated, HakijaryhmaDTO.class);
  }

  @DeleteMapping(value = "/{oid}")
  @PreAuthorize(CRUD)
  @Operation(summary = "Poistaa hakijaryhmän OID:n perusteella")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "202", description = "Poisto onnistui"),
        @ApiResponse(
            responseCode = "403",
            description = "Hakijaryhmää ei voida poistaa, esim. se on peritty")
      })
  public ResponseEntity<Object> delete(
      @PathVariable("oid") final String oid, final HttpServletRequest request) {
    try {
      HakijaryhmaDTO dto = hakijaryhmaService.delete(oid);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKIJARYHMA_POISTO,
          ValintaResource.HAKIJARYHMA,
          oid,
          Changes.deleteDto(dto));
      return ResponseEntity.accepted().build();
    } catch (HakijaryhmaaEiVoiPoistaaException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, null, e);
    }
  }

  @PutMapping(
      value = "/siirra",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  public ResponseEntity<HakijaryhmaDTO> siirra(
      @RequestBody final HakijaryhmaSiirraDTO dto, final HttpServletRequest request) {
    Optional<Hakijaryhma> siirretty = hakijaryhmaService.siirra(dto);
    siirretty.ifPresent(
        hakijaryhma -> {
          Map<String, String> additionalAuditInfo = new HashMap<>();
          additionalAuditInfo.put("Nimi", hakijaryhma.getNimi() + ", " + dto.getNimi());
          AuditLog.log(
              ValintaperusteetAudit.AUDIT,
              AuditLog.getUser(request),
              ValintaperusteetOperation.HAKIJARYHMA_SIIRTO,
              ValintaResource.HAKIJARYHMA,
              hakijaryhma.getOid(),
              Changes.addedDto(hakijaryhma),
              additionalAuditInfo);
        });
    return siirretty
        .map(
            hakijaryhma ->
                ResponseEntity.accepted().body(modelMapper.map(hakijaryhma, HakijaryhmaDTO.class)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(
      value = "/jarjesta",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(
      summary = "Järjestää hakijaryhmät parametrina annetun OID-listan mukaiseen järjestykseen")
  public List<HakijaryhmaDTO> jarjesta(
      @RequestBody final List<String> oids, final HttpServletRequest request) {
    List<Hakijaryhma> hrl = hakijaryhmaService.jarjestaHakijaryhmat(oids);
    Map<String, String> uusiJarjestys = ImmutableMap.of("hakijaryhmaoids", toNullsafeString(oids));
    AuditLog.log(
        ValintaperusteetAudit.AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.HAKIJARYHMA_JARJESTA,
        ValintaResource.HAKIJARYHMA,
        null,
        Changes.EMPTY,
        uusiJarjestys);
    return modelMapper.mapList(hrl, HakijaryhmaDTO.class);
  }
}
