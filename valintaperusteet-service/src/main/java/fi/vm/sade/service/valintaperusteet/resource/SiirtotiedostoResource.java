package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.dto.model.SiirtotiedostoConstants.*;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static org.apache.commons.lang3.StringUtils.isBlank;

import fi.vm.sade.service.valintaperusteet.service.SiirtotiedostoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
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

  private LocalDateTime parseDateTime(
      String dateTimeStr, String fieldName, ZonedDateTime defaultDateTime) {
    try {
      ZonedDateTime dateTime =
          isBlank(dateTimeStr)
              ? null
              : ZonedDateTime.parse(dateTimeStr, SIIRTOTIEDOSTO_DATETIME_FORMATTER);
      if (dateTime != null) {
        return dateTime.toLocalDateTime();
      } else if (defaultDateTime != null) {
        return defaultDateTime.toLocalDateTime();
      }
      return null;
    } catch (DateTimeParseException dtpe) {
      throw new IllegalArgumentException(
          String.format(
              "Virheellinen arvo kentälle %s, vaadittu formaati: '%s'",
              fieldName, SIIRTOTIEDOSTO_DATETIME_FORMAT));
    }
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/byTimeRange", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary =
          "Luo siirtotiedostot annetulla aikavälillä luoduista / muutetuista valintaperusteista hakukohteittain")
  public ResponseEntity<String> createByTimeRange(
      @Parameter(description = "Alkuaika") @RequestParam(required = false) String startDatetime,
      @Parameter(description = "Loppuaika") @RequestParam(required = false) String endDatetime) {
    LocalDateTime start =
        parseDateTime(
            startDatetime,
            "Alkuaika",
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), SIIRTOTIEDOSTO_TIMEZONE));
    LocalDateTime end =
        parseDateTime(endDatetime, "Loppuaika", ZonedDateTime.now(SIIRTOTIEDOSTO_TIMEZONE));
    String response = siirtotiedostoService.createSiirtotiedostot(start, end);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
