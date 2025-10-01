package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.service.PuuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/resources/puu")
@PreAuthorize("isAuthenticated()")
@Tag(name = "/resources/puu", description = "Resurssi valintaperustepuun hakemiseen")
public class PuuResource {
  @Autowired private PuuService puuService;

  protected static final Logger LOGGER = LoggerFactory.getLogger(PuuResource.class);

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaperustepuun annettujen parametrien perusteella")
  public List<ValintaperustePuuDTO> search(
      @RequestParam(value = "q", required = false) final String searchString,
      @RequestParam(value = "hakuOid", required = false) final String hakuOid,
      @RequestParam(value = "tila", required = false) final List<String> tila,
      @RequestParam(value = "hakukohteet", required = false, defaultValue = "true")
          final boolean hakukohteet,
      @RequestParam(value = "kohdejoukko", required = false, defaultValue = "")
          final String kohdejoukko,
      @RequestParam(value = "valintaryhma", required = false, defaultValue = "")
          final String valintaryhma) {
    return puuService.search(hakuOid, tila, searchString, hakukohteet, kohdejoukko, valintaryhma);
  }

  @GetMapping(value = "/haku/{hakuOid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmähierarkiat haulle")
  public List<ValintaperustePuuDTO> search(
      @PathVariable(value = "hakuOid", required = false) final String hakuOid) {
    return puuService.searchByHaku(hakuOid);
  }
}
