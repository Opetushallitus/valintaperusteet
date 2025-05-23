package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.dto.HakuViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaPlainDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.ActorService;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/resources/laskentakaava")
@PreAuthorize("isAuthenticated()")
@Tag(
    name = "/resources/laskentakaava",
    description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class LaskentakaavaResource {
  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private ActorService actorService;

  private static final Logger LOGGER = LoggerFactory.getLogger(LaskentakaavaResource.class);

  @GetMapping(value = "/funktiokuvaus", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Palauttaa funktiokuvaukset")
  public String funktiokuvaukset() {
    return Funktiokuvaaja.annaFunktiokuvauksetAsJson();
  }

  @GetMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Tyhjentää laskentakaavat välimuistista")
  public String tyhjennaCache() {
    laskentakaavaService.tyhjennaCache();
    return "cache tyhjennetty";
  }

  @GetMapping(value = "/funktiokuvaus/{nimi}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Palauttaa parametrina annetun funktion kuvauksen")
  @PreAuthorize(READ_UPDATE_CRUD)
  public String funktiokuvaus(@PathVariable("nimi") final String nimi) {
    return Funktiokuvaaja.annaFunktiokuvausAsJson(nimi);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee laskentakaavan ID:n perusteella")
  public LaskentakaavaDTO kaava(
      @PathVariable("id") final Long id,
      @RequestParam(value = "funktiopuu", defaultValue = "true") final Boolean funktiopuu) {
    if (funktiopuu) {
      LaskentakaavaDTO mapped =
          modelMapper.map(laskentakaavaService.haeMallinnettuKaava(id), LaskentakaavaDTO.class);
      return mapped;
    } else {
      Optional<Laskentakaava> kaava = laskentakaavaService.pelkkaKaava(id);
      return kaava
          .map(
              k -> {
                k.setFunktiokutsu(null);
                return modelMapper.map(k, LaskentakaavaDTO.class);
              })
          .orElse(new LaskentakaavaDTO());
    }
  }

  @GetMapping(value = "/hakuoid", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee laskentakaavan hakuoidin ID:n perusteella")
  public HakuViiteDTO kaavanHakuoid(
      @RequestParam(value = "valintaryhma", required = false) final String valintaryhmaOid,
      @RequestParam(value = "hakukohde", required = false) final String hakukohdeOid) {
    HakuViiteDTO haku = new HakuViiteDTO();
    haku.setHakuoid(laskentakaavaService.haeHakuoid(hakukohdeOid, valintaryhmaOid));
    return haku;
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee laskentakaavat annettujen hakuparametrien perusteella")
  public List<LaskentakaavaListDTO> kaavat(
      @RequestParam(value = "myosLuonnos", defaultValue = "false", required = false)
          final Boolean all,
      @RequestParam(value = "valintaryhma", required = false) final String valintaryhmaOid,
      @RequestParam(value = "hakukohde", required = false) final String hakukohdeOid,
      @RequestParam(value = "tyyppi", required = false)
          final fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi) {
    return modelMapper.mapList(
        laskentakaavaService.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi),
        LaskentakaavaListDTO.class);
  }

  @PostMapping(
      value = "/validoi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Validoi parametrina annetun laskentakaavan")
  public LaskentakaavaDTO validoi(@RequestBody final LaskentakaavaDTO laskentakaava) {
    return modelMapper.map(laskentakaavaService.validoi(laskentakaava), LaskentakaavaDTO.class);
  }

  @PostMapping(
      value = "/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää laskentakaavan")
  public ResponseEntity<LaskentakaavaDTO> update(
      @PathVariable("id") final Long id,
      @RequestBody final LaskentakaavaCreateDTO laskentakaava,
      final HttpServletRequest request) {
    LaskentakaavaDTO afterUpdate = null;
    try {
      LaskentakaavaDTO beforeUpdate =
          modelMapper.map(laskentakaavaService.haeMallinnettuKaava(id), LaskentakaavaDTO.class);
      afterUpdate =
          modelMapper.map(laskentakaavaService.update(id, laskentakaava), LaskentakaavaDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.LASKENTAKAAVA_PAIVITYS,
          ValintaResource.LASKENTAKAAVA,
          afterUpdate.getId().toString(),
          Changes.updatedDto(afterUpdate, beforeUpdate));
      return ResponseEntity.ok(afterUpdate);
    } catch (LaskentakaavaEiValidiException e) {
      LOGGER.error("Laskentakaava ei ole validi!", e);
      return ResponseEntity.badRequest().body(afterUpdate);
    } catch (Exception e) {
      LOGGER.error("Virhe päivitettäessä laskentakaavaa.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää uuden laskentakaavan")
  public ResponseEntity<LaskentakaavaDTO> insert(
      @RequestBody final LaskentakaavaInsertDTO laskentakaava, final HttpServletRequest request) {
    LaskentakaavaDTO inserted = null;
    try {
      inserted =
          Optional.ofNullable(
                  modelMapper.map(
                      laskentakaavaService.insert(
                          laskentakaava.getLaskentakaava(),
                          laskentakaava.getHakukohdeOid(),
                          laskentakaava.getValintaryhmaOid()),
                      LaskentakaavaDTO.class))
              .orElse(new LaskentakaavaDTO());
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.LASKENTAKAAVA_LISAYS,
          ValintaResource.LASKENTAKAAVA,
          laskentakaava.getHakukohdeOid(),
          Changes.addedDto(inserted));
      return ResponseEntity.status(HttpStatus.CREATED).body(inserted);
    } catch (LaskentakaavaEiValidiException e) {
      LOGGER.error("Laskentakaava ei ole validi.", e);
      return ResponseEntity.badRequest().body(inserted);
    } catch (Exception e) {
      LOGGER.error("Virhe tallennettaessa laskentakaavaa.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping(
      value = "/siirra",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  public ResponseEntity<LaskentakaavaDTO> siirra(
      @RequestBody final LaskentakaavaSiirraDTO dto, final HttpServletRequest request) {
    Optional<Laskentakaava> siirretty = laskentakaavaService.siirra(dto);
    return siirretty
        .map(
            kaava -> {
              AuditLog.log(
                  ValintaperusteetAudit.AUDIT,
                  AuditLog.getUser(request),
                  ValintaperusteetOperation.LASKENTAKAAVA_SIIRTO,
                  ValintaResource.LASKENTAKAAVA,
                  Long.toString(kaava.getId()),
                  Changes.addedDto(modelMapper.map(kaava, LaskentakaavaDTO.class)));
              return ResponseEntity.accepted().body(modelMapper.map(kaava, LaskentakaavaDTO.class));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/{id}/valintaryhma", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  public ResponseEntity<ValintaryhmaPlainDTO> valintaryhma(@PathVariable("id") final Long id) {
    Optional<Valintaryhma> ryhma = laskentakaavaService.valintaryhma(id);
    return ryhma
        .map(r -> ResponseEntity.accepted().body(modelMapper.map(r, ValintaryhmaPlainDTO.class)))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  public ResponseEntity<Object> poista(
      @PathVariable("id") final Long id, final HttpServletRequest request) {
    boolean poistettu = laskentakaavaService.poista(id);
    AuditLog.log(
        ValintaperusteetAudit.AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.LASKENTAKAAVA_POISTO,
        ValintaResource.LASKENTAKAAVA,
        id.toString(),
        Changes.EMPTY);
    if (poistettu) {
      return ResponseEntity.accepted().build();
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}
