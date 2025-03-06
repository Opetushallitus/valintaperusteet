package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.ErrorDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoekoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/resources/valintaryhma")
@PreAuthorize("isAuthenticated()")
@Tag(name = "/resources/valintaryhma", description = "Resurssi valintaryhmien käsittelyyn")
public class ValintaryhmaResource {

  @Lazy @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private HakukohdekoodiService hakukohdekoodiService;

  @Autowired private ValintakoekoodiService valintakoekoodiService;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private OidService oidService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  protected static final Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmiä annettujen hakukriteerien perusteella")
  public List<ValintaryhmaDTO> search(
      @Parameter(description = "Haetaanko pääatason valintaryhmät")
          @RequestParam(value = "paataso", required = false)
          final Boolean paataso,
      @Parameter(description = "Parent-valintaryhmän OID, jonka lapsia haetaan")
          @RequestParam(value = "parentsOf", required = false)
          final String parentsOf) {
    List<ValintaryhmaDTO> valintaryhmas = new ArrayList<>();
    if (Boolean.TRUE.equals(paataso)) {
      valintaryhmas.addAll(
          modelMapper.mapList(
              valintaryhmaService.findValintaryhmasByParentOid(null), ValintaryhmaDTO.class));
    }
    if (parentsOf != null) {
      valintaryhmas.addAll(
          modelMapper.mapList(
              valintaryhmaService.findParentHierarchyFromOid(parentsOf), ValintaryhmaDTO.class));
    }
    return valintaryhmas;
  }

  @DeleteMapping(value = "/{oid}")
  @PreAuthorize(CRUD)
  @Operation(summary = "Poistaa valintaryhmän OID:n perusteella")
  @ApiResponses({@ApiResponse(responseCode = "404", description = "Valintaryhmää ei ole olemassa")})
  public ResponseEntity<Object> delete(
      @Parameter(description = "Valintaryhmän OID") @PathVariable("oid") final String oid,
      final HttpServletRequest request) {
    try {
      ValintaryhmaDTO beforeDelete =
          modelMapper.map(valintaryhmaService.readByOid(oid), ValintaryhmaDTO.class);
      valintaryhmaService.delete(oid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_POISTO,
          ValintaResource.VALINTARYHMA,
          oid,
          Changes.deleteDto(beforeDelete));
      return ResponseEntity.accepted().build();
    } catch (ValintaryhmaEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    }
  }

  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmän OID:n perusteella")
  public ValintaryhmaDTO queryFull(@PathVariable("oid") final String oid) {
    return modelMapper.map(valintaryhmaService.readByOid(oid), ValintaryhmaDTO.class);
  }

  @GetMapping(value = "/{oid}/hakijaryhma", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakijaryhmät valintaryhmän OID:n perusteella")
  public List<HakijaryhmaDTO> hakijaryhmat(
      @Parameter(description = "Valintaryhmän OID") @PathVariable("oid") final String oid) {
    return modelMapper.mapList(hakijaryhmaService.findByValintaryhma(oid), HakijaryhmaDTO.class);
  }

  @GetMapping(value = "/{oid}/parents", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmän parent-valintaryhmät OID:n perusteella")
  public List<ValintaryhmaListDTO> parentHierarchy(@PathVariable("oid") final String parentsOf) {
    List<ValintaryhmaListDTO> valintaryhmas = new ArrayList<>();
    if (parentsOf != null) {
      valintaryhmas.addAll(
          modelMapper.mapList(
              valintaryhmaService.findParentHierarchyFromOid(parentsOf),
              ValintaryhmaListDTO.class));
    }
    return valintaryhmas;
  }

  @GetMapping(value = "/{oid}/lapsi", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmän lapsivalintaryhmät OID:n perusteella")
  public List<ValintaryhmaDTO> queryChildren(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        valintaryhmaService.findValintaryhmasByParentOid(oid), ValintaryhmaDTO.class);
  }

  @GetMapping(value = "/{oid}/hakukohde", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmän lapsihakukohteet OID:n perusteella")
  public List<HakukohdeViiteDTO> childHakukohdes(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        hakukohdeService.findByValintaryhmaOid(oid), HakukohdeViiteDTO.class);
  }

  @GetMapping(value = "/{oid}/valinnanvaihe", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valinnan vaiheet valintaryhmän OID:n perusteella")
  public List<ValinnanVaiheDTO> valinnanVaiheet(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        valinnanVaiheService.findByValintaryhma(oid), ValinnanVaiheDTO.class);
  }

  @GetMapping(
      value = "/onko-haulla-valintaryhmia/{hakuOid}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Palauttaa tiedon onko haulla valintaryhmiä")
  public Boolean onkoHaullaValintaryhmia(@PathVariable("hakuOid") final String hakuOid) {
    return valintaryhmaService.onkoHaullaValintaryhmia(hakuOid);
  }

  @PutMapping(
      value = "/{parentOid}/lapsi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää lapsivalintaryhmän parametrina annetulle parent-valintaryhmälle")
  public ResponseEntity<ValintaryhmaDTO> insertChild(
      @Parameter(description = "Parent-valintaryhmän OID") @PathVariable("parentOid")
          final String parentOid,
      @RequestBody final ValintaryhmaCreateDTO valintaryhma,
      final HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(
              valintaryhmaService.insert(valintaryhma, parentOid), ValintaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.LAPSIVALINTARYHMA_LISAYS_PARENT,
          ValintaResource.VALINTARYHMA,
          parentOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOGGER.error("Lapsivalintaryhmän lisäys valintaryhmälle {} ei onnistunut.", parentOid, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää valintaryhmän")
  public ResponseEntity<Object> insert(
      @RequestBody final ValintaryhmaCreateDTO valintaryhma, final HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(valintaryhmaService.insert(valintaryhma), ValintaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS,
          ValintaResource.VALINTARYHMA,
          lisatty.getOid(),
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOGGER.error("Error creating valintaryhmä.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorDTO(e.getMessage()));
    }
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää valintaryhmän")
  public ResponseEntity<Object> update(
      @Parameter(description = "Päivitettävän valintaryhmän OID") @PathVariable("oid")
          final String oid,
      @RequestBody final ValintaryhmaCreateDTO valintaryhma,
      final HttpServletRequest request) {
    ValintaryhmaDTO beforeUpdate =
        modelMapper.map(valintaryhmaService.readByOid(oid), ValintaryhmaDTO.class);
    ValintaryhmaDTO afterUpdate =
        modelMapper.map(valintaryhmaService.update(oid, valintaryhma), ValintaryhmaDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTARYHMA_PAIVITYS,
        ValintaResource.VALINTARYHMA,
        oid,
        Changes.updatedDto(afterUpdate, beforeUpdate));
    return ResponseEntity.accepted().build();
  }

  @PutMapping(value = "/{oid}/kopioiLapseksi", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää lapsivalintaryhmän kopioimalla lähdevalintaryhmän")
  public ResponseEntity<Object> copyAsChild(
      @PathVariable("oid") final String oid,
      @RequestParam(value = "lahdeOid", required = false) final String lahdeOid,
      @RequestParam(value = "nimi", required = false) final String nimi,
      final HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(
              valintaryhmaService.copyAsChild(lahdeOid, oid, nimi), ValintaryhmaDTO.class);
      Map<String, String> additionalAuditInfo = new HashMap<>();
      additionalAuditInfo.put("lahdeOid", lahdeOid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.LAPSIVALINTARYHMA_LISAYS,
          ValintaResource.VALINTARYHMA,
          oid,
          Changes.addedDto(lisatty),
          additionalAuditInfo);
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOGGER.error("Error copying valintaryhmä.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorDTO(e.getMessage()));
    }
  }

  @PutMapping(value = "/kopioiJuureen", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää valintaryhmän juureen kopioimalla lähdevalintaryhmän")
  public ResponseEntity<Object> copyToRoot(
      @RequestParam(value = "lahdeOid", required = false) final String lahdeOid,
      @RequestParam(value = "nimi", required = false) final String nimi,
      final HttpServletRequest request) {
    try {
      ValintaryhmaDTO lisatty =
          modelMapper.map(
              valintaryhmaService.copyAsChild(lahdeOid, null, nimi), ValintaryhmaDTO.class);
      Map<String, String> additionalAuditInfo = new HashMap<>();
      additionalAuditInfo.put("lahdeOid", lahdeOid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS,
          ValintaResource.VALINTARYHMA,
          lisatty.getOid(),
          Changes.addedDto(lisatty),
          additionalAuditInfo);
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOGGER.error("Error copying valintaryhmä.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorDTO(e.getMessage()));
    }
  }

  @PostMapping(
      value = "/{valintaryhmaOid}/valinnanvaihe",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää valinnan vaiheen valintaryhmälle")
  public ResponseEntity<ValinnanVaiheDTO> insertValinnanvaihe(
      @Parameter(description = "Valintaryhmän OID, jolla valinnan vaihe lisätään")
          @PathVariable("valintaryhmaOid")
          final String valintaryhmaOid,
      @Parameter(description = "Valinnan vaiheen OID, jonka jälkeen uusi valinnan vaihe lisätään")
          @RequestParam(value = "edellinenValinnanVaiheOid", required = false)
          final String edellinenValinnanVaiheOid,
      @RequestBody final ValinnanVaiheCreateDTO valinnanVaihe,
      final HttpServletRequest request) {
    try {
      ValinnanVaiheDTO lisatty =
          modelMapper.map(
              valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(
                  valintaryhmaOid, valinnanVaihe, edellinenValinnanVaiheOid),
              ValinnanVaiheDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_VALINNANVAIHE,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOGGER.error("Error creating valinnanvaihe.", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping(
      value = "/{valintaryhmaOid}/hakijaryhma",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää hakijaryhmän valintaryhmälle")
  public ResponseEntity<Object> insertHakijaryhma(
      @Parameter(description = "Valintaryhmän OID, jolle hakijaryhmä lisätään")
          @PathVariable("valintaryhmaOid")
          final String valintaryhmaOid,
      @RequestBody final HakijaryhmaCreateDTO hakijaryhma,
      final HttpServletRequest request) {
    try {
      HakijaryhmaDTO lisatty =
          modelMapper.map(
              hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle(valintaryhmaOid, hakijaryhma),
              HakijaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_HAKIJARYHMA,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (LaskentakaavaOidTyhjaException e) {
      LOGGER.warn("Error creating hakijaryhma for valintaryhmä: " + e.toString());
      Map<String, String> map = new HashMap<>();
      map.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(map);
    } catch (Exception e) {
      LOGGER.error("Error creating hakijaryhma for valintaryhmä.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(
      value = "/{valintaryhmaOid}/hakukohdekoodi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää valintaryhmän hakukohdekoodeja")
  public ResponseEntity<Object> updateHakukohdekoodi(
      @Parameter(description = "Valintaryhmän OID, jonka hakukohdekoodeja päivitetään")
          @PathVariable("valintaryhmaOid")
          final String valintaryhmaOid,
      @RequestBody final Set<KoodiDTO> hakukohdekoodit,
      final HttpServletRequest request) {
    try {
      hakukohdekoodiService.updateValintaryhmaHakukohdekoodit(valintaryhmaOid, hakukohdekoodit);
      Map<String, String> uudetHakukohdekoodit =
          ImmutableMap.of("Uudet hakukohdekoodit", toNullsafeString(hakukohdekoodit));
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_PAIVITYS_HAKUKOHDEKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.EMPTY,
          uudetHakukohdekoodit);
      return ResponseEntity.accepted().body(hakukohdekoodit);
    } catch (Exception e) {
      LOGGER.error("Error updating hakukohdekoodit.", e);
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PutMapping(
      value = "/{valintaryhmaOid}/hakukohdekoodi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää hakukohdekoodin valintaryhmälle")
  public ResponseEntity<Object> insertHakukohdekoodi(
      @Parameter(description = "Valintaryhmän OID, jolle hakukohdekoodi lisätään")
          @PathVariable("valintaryhmaOid")
          final String valintaryhmaOid,
      @RequestBody final KoodiDTO hakukohdekoodi,
      final HttpServletRequest request) {
    try {
      hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(valintaryhmaOid, hakukohdekoodi);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_HAKUKOHDEKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(hakukohdekoodi));
      return ResponseEntity.status(HttpStatus.CREATED).body(hakukohdekoodi);
    } catch (Exception e) {
      LOGGER.error("Error inserting hakukohdekoodi.", e);
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PostMapping(
      value = "/{valintaryhmaOid}/valintakoekoodi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Päivittää valintaryhmän valintakoekoodeja")
  public ResponseEntity<Object> updateValintakoekoodi(
      @Parameter(description = "Valintaryhmän OID, jonka valintakoekoodeja päivitetään")
          @PathVariable("valintaryhmaOid")
          final String valintaryhmaOid,
      @RequestBody final List<KoodiDTO> valintakoekoodit,
      final HttpServletRequest request) {
    try {
      valintakoekoodiService.updateValintaryhmanValintakoekoodit(valintaryhmaOid, valintakoekoodit);
      Map<String, String> muutetutValintakoekoodit =
          ImmutableMap.of("Päivitetyt valintakoekoodit", toNullsafeString(valintakoekoodit));
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_PAIVITYS_VALINTAKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.EMPTY,
          muutetutValintakoekoodit);
      return ResponseEntity.accepted().body(valintakoekoodit);
    } catch (Exception e) {
      LOGGER.error("Error updating valintakoekoodit.", e);
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PutMapping(
      value = "/{valintaryhmaOid}/valintakoekoodi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää valintakoekoodin valintaryhmälle")
  public ResponseEntity<Object> insertValintakoekoodi(
      @Parameter(description = "Valintaryhmän OID, jolle valintakoekoodi lisätään")
          @PathVariable("valintaryhmaOid")
          final String valintaryhmaOid,
      @RequestBody final KoodiDTO valintakoekoodi,
      final HttpServletRequest request) {
    try {
      valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(valintaryhmaOid, valintakoekoodi);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTARYHMA_LISAYS_VALINTAKOEKOODI,
          ValintaResource.VALINTARYHMA,
          valintaryhmaOid,
          Changes.addedDto(valintakoekoodi));
      return ResponseEntity.status(HttpStatus.CREATED).body(valintakoekoodi);
    } catch (Exception e) {
      LOGGER.error("Error inserting valintakoekoodi.", e);
      Map<String, String> error = new HashMap<>();
      error.put("message", e.getMessage());
      return ResponseEntity.internalServerError().body(error);
    }
  }
}
