package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.toNullsafeString;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaihettaEiVoiPoistaaException;
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
import java.util.stream.Collectors;
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
@RequestMapping(value = "/resources/valinnanvaihe")
@PreAuthorize("isAuthenticated()")
@Tag(name = "/resources/valinnanvaihe", description = "Resurssi valinnan vaiheiden käsittelyyn")
public class ValinnanVaiheResource {
  @Autowired ValintatapajonoService jonoService;

  @Autowired ValintakoeService valintakoeService;

  @Autowired ValinnanVaiheService valinnanVaiheService;

  @Lazy @Autowired ValintaryhmaService valintaryhmaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  protected static final Logger LOGGER = LoggerFactory.getLogger(ValinnanVaiheResource.class);

  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valinnan vaiheen OID:n perusteella")
  public ValinnanVaiheDTO read(@PathVariable("oid") final String oid) {
    return modelMapper.map(valinnanVaiheService.readByOid(oid), ValinnanVaiheDTO.class);
  }

  @GetMapping(value = "/{oid}/valintatapajono", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valinnan vaiheen valintatapajonot OID:n perusteella")
  public List<ValintatapajonoDTO> listJonos(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(jonoService.findJonoByValinnanvaihe(oid), ValintatapajonoDTO.class);
  }

  @PostMapping(
      value = "/valintatapajonot",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee useiden valinnan vaiheiden valintatapajonot OIDien perusteella")
  public List<ValinnanVaiheJaValintatapajonoDTO> valintatapajonot(
      @RequestBody final List<String> valinnanvaiheOidit) {
    return valinnanvaiheOidit.stream()
        .map(
            oid -> {
              List<ValintatapajonoDTO> valintatapajonot =
                  modelMapper.mapList(
                      jonoService.findJonoByValinnanvaihe(oid), ValintatapajonoDTO.class);
              Boolean kuuluuSijoitteluun = valinnanVaiheService.kuuluuSijoitteluun(oid);
              return new ValinnanVaiheJaValintatapajonoDTO(
                  oid, kuuluuSijoitteluun, valintatapajonot);
            })
        .filter(dto -> !dto.getValintatapajonot().isEmpty())
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/{oid}/valintakoe", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintakokeet valinnan vaiheen OID:n perusteella")
  public List<ValintakoeDTO> listValintakokeet(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        valintakoeService.findValintakoeByValinnanVaihe(oid), ValintakoeDTO.class);
  }

  @PutMapping(
      value = "/{parentOid}/valintatapajono",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Lisää valintatapajonon valinnan vaiheelle")
  public ResponseEntity<ValintatapajonoDTO> addJonoToValinnanVaihe(
      @PathVariable("parentOid") final String parentOid,
      @RequestBody final ValintatapajonoCreateDTO jono,
      final HttpServletRequest request) {
    try {
      ValintatapajonoDTO inserted =
          modelMapper.map(
              jonoService.lisaaValintatapajonoValinnanVaiheelle(parentOid, jono, null),
              ValintatapajonoDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINNANVAIHE_LISAYS_VALINTATAPAJONO,
          ValintaResource.VALINNANVAIHE,
          parentOid,
          Changes.addedDto(inserted));
      return ResponseEntity.status(HttpStatus.CREATED).body(inserted);
    } catch (Exception e) {
      LOGGER.error("error in addJonoToValinnanVaihe", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping(
      value = "/{parentOid}/valintakoe",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Lisää valintakokeen valinnan vaiheelle")
  public ResponseEntity<ValintakoeDTO> addValintakoeToValinnanVaihe(
      @PathVariable("parentOid") final String parentOid,
      @RequestBody final ValintakoeCreateDTO koe,
      final HttpServletRequest request) {
    try {
      ValintakoeDTO valintakoe =
          modelMapper.map(
              valintakoeService.lisaaValintakoeValinnanVaiheelle(parentOid, koe),
              ValintakoeDTO.class);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINNANVAIHE_LISAYS_VALINTAKOE,
          ValintaResource.VALINNANVAIHE,
          parentOid,
          Changes.addedDto(valintakoe));
      return ResponseEntity.status(HttpStatus.CREATED).body(valintakoe);
    } catch (Exception e) {
      LOGGER.error("error in addValintakoeToValinnanVaihe", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää valinnan vaihetta")
  public ValinnanVaiheDTO update(
      @PathVariable("oid") final String oid,
      @RequestBody final ValinnanVaiheCreateDTO valinnanVaihe,
      final HttpServletRequest request) {
    ValinnanVaiheDTO vanhaVV =
        modelMapper.map(valinnanVaiheService.readByOid(oid), ValinnanVaiheDTO.class);
    ValinnanVaiheDTO uusiVV =
        modelMapper.map(valinnanVaiheService.update(oid, valinnanVaihe), ValinnanVaiheDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINNANVAIHE_PAIVITYS,
        ValintaResource.VALINNANVAIHE,
        oid,
        Changes.updatedDto(uusiVV, vanhaVV));
    return uusiVV;
  }

  @PostMapping(
      value = "/jarjesta",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(
      summary = "Järjestää valinnan vaiheet parametrina annetun OID-listan mukaiseen järjestykseen")
  public List<ValinnanVaiheDTO> jarjesta(
      @RequestBody final List<String> oids, final HttpServletRequest request) {
    List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.jarjestaValinnanVaiheet(oids);
    Map<String, String> additionalInfo =
        ImmutableMap.of("valinnanvaiheoids", toNullsafeString(oids));
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.VALINNANVAIHE_JARJESTA,
        ValintaResource.VALINNANVAIHE,
        null,
        Changes.EMPTY,
        additionalInfo);
    return modelMapper.mapList(valinnanVaiheet, ValinnanVaiheDTO.class);
  }

  @DeleteMapping(value = "/{oid}")
  @PreAuthorize(CRUD)
  @Operation(summary = "Poistaa valinnan vaiheen OID:n perusteetlla")
  @ApiResponses({
    @ApiResponse(responseCode = "404", description = "Valinnan vaihetta ei ole olemassa"),
    @ApiResponse(
        responseCode = "400",
        description = "Valinnan vaihetta ei voida poistaa, esim. se on peritty")
  })
  public ResponseEntity<Object> delete(
      @PathVariable("oid") final String oid, final HttpServletRequest request) {
    try {
      ValinnanVaiheDTO dto = valinnanVaiheService.delete(oid);
      AuditLog.log(
          AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.VALINNANVAIHE_POISTO,
          ValintaResource.VALINNANVAIHE,
          oid,
          Changes.deleteDto(dto));
      return ResponseEntity.accepted().build();
    } catch (ValinnanVaiheEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    } catch (ValinnanVaihettaEiVoiPoistaaException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
    }
  }

  @GetMapping(value = "/{oid}/kuuluuSijoitteluun", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Palauttaa tiedon siitä, kuuluuko valinnan vaihe sijoitteluun")
  public Map<String, Boolean> kuuluuSijoitteluun(@PathVariable("oid") final String oid) {
    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("sijoitteluun", valinnanVaiheService.kuuluuSijoitteluun(oid));
    return map;
  }

  @PostMapping(
      value = "/kuuluuSijoitteluun",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Palauttaa tiedon siitä, kuuluvatko valinnan vaiheet sijoitteluun")
  public Map<String, Boolean> kuuluuSijoitteluun(@RequestBody final List<String> oids) {
    Map<String, Boolean> map = new HashMap<>();
    oids.forEach((oid) -> map.put(oid, valinnanVaiheService.kuuluuSijoitteluun(oid)));
    return map;
  }
}
