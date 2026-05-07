package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.dto.model.SiirtotiedostoConstants.SIIRTOTIEDOSTO_DATETIME_FORMAT;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;

import fi.vm.sade.service.valintaperusteet.dto.SiirtotiedostoResult;
import fi.vm.sade.service.valintaperusteet.service.SiirtotiedostoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/resources/siirtotiedosto")
@PreAuthorize("isAuthenticated()")
@Tag(
    name = "/resources/siirtotiedosto",
    description = "Resurssi valintaperusteiden kirjoittamiseen raportoinnin siirtotiedostoihin")
public class SiirtotiedostoResource {

  @Autowired private SiirtotiedostoService siirtotiedostoService;

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/byTimeRange", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary =
          "Luo siirtotiedostot annetulla aikavälillä luoduista / muutetuista valintaperusteista hakukohteittain")
  public ResponseEntity<SiirtotiedostoResult> createByTimeRange(
      @Parameter(description = "Alkuaika", example = "2024-08-01T00:00:00")
          @RequestParam(required = false, defaultValue = "1970-01-01T00:00:00")
          @DateTimeFormat(pattern = SIIRTOTIEDOSTO_DATETIME_FORMAT)
          LocalDateTime startDatetime,
      @Parameter(description = "Loppuaika", example = "2024-11-13T00:00:00")
          @RequestParam(required = false)
          @DateTimeFormat(pattern = SIIRTOTIEDOSTO_DATETIME_FORMAT)
          LocalDateTime endDatetime) {
    LocalDateTime end = Objects.requireNonNullElseGet(endDatetime, LocalDateTime::now);
    SiirtotiedostoResult response = siirtotiedostoService.createSiirtotiedostot(startDatetime, end);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/avaimet", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary =
          "Luo siirtotiedostot annetulla aikavälillä luoduista / muutetuista valintaperusteiden avaimista hakukohteittain")
  public ResponseEntity<SiirtotiedostoResult> createAvaimetByTimeRange(
      @Parameter(description = "Alkuaika", example = "2024-08-01T00:00:00")
          @RequestParam(required = false, defaultValue = "1970-01-01T00:00:00")
          @DateTimeFormat(pattern = SIIRTOTIEDOSTO_DATETIME_FORMAT)
          LocalDateTime startDatetime,
      @Parameter(description = "Loppuaika", example = "2024-11-13T00:00:00")
          @RequestParam(required = false)
          @DateTimeFormat(pattern = SIIRTOTIEDOSTO_DATETIME_FORMAT)
          LocalDateTime endDatetime) {
    LocalDateTime end = Objects.requireNonNullElseGet(endDatetime, LocalDateTime::now);
    SiirtotiedostoResult response =
        siirtotiedostoService.createSiirtotiedostotForAvaimet(startDatetime, end);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
