package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiPoistaaException;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
@RequestMapping(value = "/resources/valintatapajono")
@PreAuthorize("isAuthenticated()")
@Tag(name = "/resources/valintatapajono", description = "Resurssi valintatapajonojen käsittelyyn")
public class ValintatapajonoResource {
  protected static final Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoResource.class);

  @Autowired ValintatapajonoService valintatapajonoService;

  @Autowired HakijaryhmaService hakijaryhmaService;

  @Autowired HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired JarjestyskriteeriService jarjestyskriteeriService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @GetMapping(value = "/kopiot", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  public Map<String, List<String>> findKopiot(
      @RequestParam(value = "oid", required = false) final List<String> oid) {
    try {
      return valintatapajonoService.findKopiot(oid);
    } catch (Exception e) {
      LOGGER.error("Virhe valintatapajonojen kopioiden hakemisessa!", e);
      throw e;
    }
  }

  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintatapajonon OID:n perusteella")
  @ApiResponses(@ApiResponse(responseCode = "404", description = "Valintatapajonoa ei löydy"))
  public ValintatapajonoDTO readByOid(@PathVariable("oid") final String oid) {
    return modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
  }

  @PostMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintatapajonojen tiedot OID-listan perusteella")
  public List<ValintatapajonoDTO> readByOids(
      @Parameter(description = "Oidit, joita vastaavien jonojen tiedot halutaan") @RequestBody
          final List<String> oids) {
    if (oids.size() > 5000) {
      LOGGER.warn("Valintatapajonojen tiedot called with more than 5000 jonoOids.");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Valintatapajonojen tiedot called with more than 5000 jonoOids.");
    }
    return valintatapajonoService.readByOids(oids).stream()
        .map(jono -> modelMapper.map(jono, ValintatapajonoDTO.class))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/{oid}/jarjestyskriteeri", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee järjestyskriteerit valintatapajonon OID:n perusteella")
  public List<JarjestyskriteeriDTO> findJarjestyskriteeri(
      @Parameter(description = "Valintatapajonon OID") @PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        jarjestyskriteeriService.findJarjestyskriteeriByJono(oid), JarjestyskriteeriDTO.class);
  }

  @PostMapping(
      value = "/{valintatapajonoOid}/hakijaryhma/{hakijaryhmaOid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Liittää hakijaryhmän valintatapajonoon")
  public ResponseEntity<Object> liitaHakijaryhma(
      @Parameter(description = "Valintatapajonon OID, jolle hakijaryhmä liitetään")
          @PathVariable("valintatapajonoOid")
          final String valintatapajonoOid,
      @Parameter(description = "Hakijaryhmän OID, joka valintatapajonoon liitetään")
          @PathVariable("hakijaryhmaOid")
          final String hakijaryhmaOid,
      final HttpServletRequest request) {
    try {
      hakijaryhmaService.liitaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhmaOid);
      ImmutableMap<String, String> liitettavanHakijaryhmanOid =
          ImmutableMap.of("Liitettävän Hakijaryhmän Oid", hakijaryhmaOid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTATAPAJONO_LIITOS_HAKIJARYHMA,
          ValintaResource.VALINTATAPAJONO,
          valintatapajonoOid,
          Changes.EMPTY,
          liitettavanHakijaryhmanOid);
      return ResponseEntity.accepted().build();
    } catch (Exception e) {
      LOGGER.error("Error linking hakijaryhma.", e);
      Map<String, String> map = new HashMap<>();
      map.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(map);
    }
  }

  @GetMapping(
      value = "/{valintatapajonoOid}/hakijaryhma",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(
      summary = "Hakee valintatapajonoon liitetyt hakijaryhmät valintatapajonon OID:n perusteella")
  public List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(
      @Parameter(description = "Valintatapajonon OID") @PathVariable("valintatapajonoOid")
          final String valintatapajonoOid) {
    return modelMapper.mapList(
        hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(valintatapajonoOid),
        HakijaryhmaValintatapajonoDTO.class);
  }

  @PutMapping(
      value = "/{valintatapajonoOid}/hakijaryhma",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Luo valintatapajonolle uuden hakijaryhmän")
  public ResponseEntity<Object> insertHakijaryhma(
      @Parameter(description = "Valintatapajonon OID, jolle hakijaryhmä lisätään")
          @PathVariable("valintatapajonoOid")
          final String valintatapajonoOid,
      @RequestBody final HakijaryhmaCreateDTO hakijaryhma,
      final HttpServletRequest request) {
    try {
      HakijaryhmaDTO lisattava =
          modelMapper.map(
              hakijaryhmaValintatapajonoService.lisaaHakijaryhmaValintatapajonolle(
                  valintatapajonoOid, hakijaryhma),
              HakijaryhmaDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_HAKIJARYHMA,
          ValintaResource.VALINTATAPAJONO,
          valintatapajonoOid,
          Changes.addedDto(lisattava));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisattava);
    } catch (LaskentakaavaOidTyhjaException e) {
      LOGGER.warn("Error creating hakijaryhma for valintatapajono: " + e.toString());
      Map<String, String> map = new HashMap<>();
      map.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(map);
    } catch (Exception e) {
      LOGGER.error("Error creating hakijaryhma for valintatapajono.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee kaikki valintatapajonot")
  public List<ValintatapajonoDTO> findAll() {
    return modelMapper.mapList(valintatapajonoService.findAll(), ValintatapajonoDTO.class);
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää valintatapajonoa")
  public ResponseEntity<ValintatapajonoDTO> update(
      @Parameter(description = "Päivitettävän valintatapajonon OID") @PathVariable("oid")
          final String oid,
      @RequestBody final ValintatapajonoCreateDTO jono,
      final HttpServletRequest request) {
    try {
      ValintatapajonoDTO old =
          modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
      ValintatapajonoDTO update =
          modelMapper.map(valintatapajonoService.update(oid, jono), ValintatapajonoDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTATAPAJONO_PAIVITYS,
          ValintaResource.VALINTATAPAJONO,
          oid,
          Changes.updatedDto(update, old));
      return ResponseEntity.accepted().body(update);
    } catch (ValintatapajonoaEiVoiLisataException e) {
      LOGGER.error("Error creating/updating valintatapajono.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping(
      value = "/{valintatapajonoOid}/jarjestyskriteeri",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää järjestyskriteerin valintatapajonolle")
  public ResponseEntity<JarjestyskriteeriDTO> insertJarjestyskriteeri(
      @Parameter(description = "Valintatapajonon OID, jolle järjestyskriteeri lisätään")
          @PathVariable("valintatapajonoOid")
          final String valintatapajonoOid,
      @RequestBody final JarjestyskriteeriInsertDTO jk,
      final HttpServletRequest request) {
    JarjestyskriteeriDTO insert =
        modelMapper.map(
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(
                valintatapajonoOid, jk.getJarjestyskriteeri(), null, jk.getLaskentakaavaId()),
            JarjestyskriteeriDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_JARJESTYSKRITEERI,
        ValintaResource.VALINTATAPAJONO,
        valintatapajonoOid,
        Changes.addedDto(insert));
    return ResponseEntity.accepted().body(insert);
  }

  @PostMapping(
      value = "/jarjesta",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Järjestää valintatapajonot annetun OID-listan mukaan")
  public List<ValintatapajonoDTO> jarjesta(
      @Parameter(
              description = "OID-lista jonka mukaiseen järjestykseen valintatapajonot järjestetään")
          @RequestBody
          final List<String> oids,
      final HttpServletRequest request) {
    List<Valintatapajono> jarjestetytJonot = valintatapajonoService.jarjestaValintatapajonot(oids);
    ImmutableMap<String, String> jarjestetytOidit =
        ImmutableMap.of("Järjestetyt Oidit", toNullsafeString(oids));
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINTATAPAJONO_JARJESTA,
        ValintaResource.VALINTATAPAJONO,
        null,
        Changes.EMPTY,
        jarjestetytOidit);
    return modelMapper.mapList(jarjestetytJonot, ValintatapajonoDTO.class);
  }

  @DeleteMapping(value = "/{oid}")
  @PreAuthorize(CRUD)
  @Operation(summary = "Poistaa valintatapajonon OID:n perusteella")
  public ResponseEntity<Object> delete(
      @Parameter(description = "Poistettavan valintatapajonon OID") @PathVariable("oid")
          final String oid,
      final HttpServletRequest request) {
    try {
      ValintatapajonoDTO dto = valintatapajonoService.delete(oid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINTATAPAJONO_POISTO,
          ValintaResource.VALINTATAPAJONO,
          oid,
          Changes.deleteDto(dto));
      return ResponseEntity.accepted().build();
    } catch (ValintatapajonoaEiVoiPoistaaException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
