package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dto.ErrorDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeJaLinkitettyHakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeJaValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeJaValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeJaValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeJaValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.LinkitettyHakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJaPrioriteettiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdekoodiService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.util.JononPrioriteettiAsettaja;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "/resources/hakukohde", description = "Resurssi hakukohteiden käsittelyyn")
@RequestMapping(value = "/resources/hakukohde")
public class HakukohdeResource {
  private static final String HAKUKOHDE_VIITE_PREFIX = "{{hakukohde.";
  protected static final Logger LOG = LoggerFactory.getLogger(HakukohdeResource.class);

  @Autowired HakukohdeService hakukohdeService;

  @Autowired HakukohdekoodiService hakukohdekoodiService;

  @Autowired ValinnanVaiheService valinnanVaiheService;

  @Autowired ValintakoeService valintakoeService;

  @Autowired JarjestyskriteeriService jarjestyskriteeriService;

  @Autowired LaskentakaavaService laskentakaavaService;

  @Autowired ValintatapajonoService valintatapajonoService;

  @Autowired HakukohdeViiteDAO hakukohdeViiteDAO;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired private OidService oidService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  public HakukohdeResource() {}

  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteita. Joko kaikki tai päätason hakukohteet.")
  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<HakukohdeViiteDTO> query(
      @RequestParam(value = "paataso", defaultValue = "false") final boolean paataso) {
    List<HakukohdeViite> hakukohteet = null;
    if (paataso) {
      hakukohteet = hakukohdeService.findRoot();
    } else {
      hakukohteet = hakukohdeService.findAll();
    }
    return modelMapper.mapList(hakukohteet, HakukohdeViiteDTO.class);
  }

  @Operation(summary = "Hakee haun hakukohteet")
  @GetMapping(value = "/haku/{hakuOid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<HakukohdeViiteDTO> haunHakukohteet(
      @PathVariable(value = "hakuOid") final String hakuOid) {
    return modelMapper.mapList(hakukohdeService.haunHakukohteet(hakuOid), HakukohdeViiteDTO.class);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen OID:n perusteella")
  @GetMapping(value = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Hakukohdetta ei löydy")})
  public HakukohdeViiteDTO queryFull(@PathVariable("oid") String oid) {
    try {
      return modelMapper.map(hakukohdeService.readByOid(oid), HakukohdeViiteDTO.class);
    } catch (HakukohdeViiteEiOleOlemassaException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
    }
  }

  @PostMapping(value = "/hakukohteet", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteet OIDien perusteella")
  public List<HakukohdeViiteDTO> hakukohteet(@RequestBody final List<String> hakukohdeOidit) {
    return modelMapper.mapList(
        hakukohdeOidit.stream()
            .map(
                (oid) -> {
                  Optional<HakukohdeViite> hakukohdeViite = Optional.empty();
                  try {
                    hakukohdeViite = Optional.of(hakukohdeService.readByOid(oid));
                  } catch (HakukohdeViiteEiOleOlemassaException hveooe) {
                  }
                  return hakukohdeViite;
                })
            .filter((viite) -> viite.isPresent())
            .map(Optional::get)
            .collect(Collectors.toList()),
        HakukohdeViiteDTO.class);
  }

  @Operation(summary = "Hakee valintaryhman OID:n perusteella")
  @GetMapping(value = "/{oid}/valintaryhma", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(
      value = {@ApiResponse(responseCode = "404", description = "Valintaryhmaa ei löydy")})
  public ValintaryhmaDTO queryValintaryhma(@PathVariable("oid") final String oid) {
    final Optional<Valintaryhma> valintaryhmaByHakukohdeOid =
        hakukohdeViiteDAO.findValintaryhmaByHakukohdeOid(oid);
    if (valintaryhmaByHakukohdeOid.isPresent()) {
      final ValintaryhmaDTO valintaryhma = new ValintaryhmaDTO();
      valintaryhma.setNimi(valintaryhmaByHakukohdeOid.get().getNimi());
      valintaryhma.setOid(valintaryhmaByHakukohdeOid.get().getOid());
      return valintaryhma;
    } else {
      return new ValintaryhmaDTO();
    }
  }

  @PostMapping(
      value = "/valintaryhmat",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee valintaryhmät hakukohteiden OIDien perusteella")
  public List<HakukohdeJaValintaryhmaDTO> queryValintaryhmat(
      @RequestBody final List<String> hakukohdeOidit) {
    return hakukohdeOidit.stream()
        .map(
            oid ->
                hakukohdeViiteDAO
                    .findValintaryhmaByHakukohdeOid(oid)
                    .flatMap(
                        valintaryhma -> {
                          final ValintaryhmaDTO ryhma = new ValintaryhmaDTO();
                          ryhma.setNimi(valintaryhma.getNimi());
                          ryhma.setOid(valintaryhma.getOid());
                          return Optional.of(new HakukohdeJaValintaryhmaDTO(oid, ryhma));
                        }))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @PutMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(
      summary =
          "Lisää hakukohteen valintaryhmään (tai juureen, jos valintaryhmän OID:a ei ole annettu)")
  public ResponseEntity<Object> insert(
      @RequestBody final HakukohdeInsertDTO hakukohde, final HttpServletRequest request) {
    try {
      HakukohdeViiteDTO hkv =
          modelMapper.map(
              hakukohdeService.insert(hakukohde.getHakukohde(), hakukohde.getValintaryhmaOid()),
              HakukohdeViiteDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_LISAYS_VALINTARYHMA,
          ValintaResource.HAKUKOHDE,
          hakukohde.getHakukohde().getOid(),
          Changes.addedDto(hkv));
      return ResponseEntity.status(HttpStatus.CREATED).body(hkv);
    } catch (Exception e) {
      LOG.warn("Hakukohdetta ei saatu lisättyä", e);
      return ResponseEntity.internalServerError().body(new ErrorDTO(e.getMessage()));
    }
  }

  @PostMapping(
      value = "/{oid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää hakukohdetta OID:n perusteella")
  public ResponseEntity<HakukohdeViiteDTO> update(
      @PathVariable("oid") final String oid,
      @RequestBody final HakukohdeViiteCreateDTO hakukohdeViite,
      final HttpServletRequest request) {
    try {
      HakukohdeViiteDTO beforeUpdate =
          modelMapper.map(hakukohdeService.readByOid(oid), HakukohdeViiteDTO.class);
      HakukohdeViiteDTO afterUpdate =
          modelMapper.map(hakukohdeService.update(oid, hakukohdeViite), HakukohdeViiteDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_PAIVITYS,
          ValintaResource.HAKUKOHDE,
          oid,
          Changes.updatedDto(afterUpdate, beforeUpdate));
      return ResponseEntity.accepted().body(afterUpdate);
    } catch (Exception e) {
      LOG.warn("Hakukohdetta ei saatu päivitettyä. ", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping(value = "/{oid}/valinnanvaihe", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen valinnan vaiheet OID:n perusteella")
  public List<ValinnanVaiheDTO> valinnanVaihesForHakukohde(
      @PathVariable("oid") final String oid,
      @RequestParam(value = "withValisijoitteluTieto", defaultValue = "false")
          final String withValisijoitteluTieto) {
    return valinnanVaiheService.findByHakukohde(oid).stream()
        .map(
            valinnanVaihe -> {
              ValinnanVaiheDTO valinnanVaiheDTO =
                  modelMapper.map(valinnanVaihe, ValinnanVaiheDTO.class);
              valinnanVaiheDTO.setJonot(
                  new HashSet<>(
                      modelMapper.mapList(
                          LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaihe.getJonot()),
                          ValintatapajonoDTO.class)));
              if (withValisijoitteluTieto.equalsIgnoreCase("true")) {
                valinnanVaiheDTO.setHasValisijoittelu(
                    valinnanVaihe.getJonot().stream().anyMatch(Valintatapajono::getValisijoittelu));
              }
              return valinnanVaiheDTO;
            })
        .collect(Collectors.toList());
  }

  @PostMapping(
      value = "/valinnanvaiheet",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteiden valinnan vaiheet OIDien perusteella")
  public List<HakukohdeJaValinnanVaiheDTO> valinnanVaiheetForHakukohteet(
      @RequestBody final List<String> hakukohdeOidit) {
    return hakukohdeOidit.stream()
        .map(
            (oid) -> {
              List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(oid);
              List<ValinnanVaiheJaPrioriteettiDTO> valinnanVaiheDtot = new ArrayList<>();
              for (int i = 0; i < valinnanVaiheet.size(); i++) {
                ValinnanVaiheJaPrioriteettiDTO dto =
                    modelMapper.map(valinnanVaiheet.get(i), ValinnanVaiheJaPrioriteettiDTO.class);
                dto.setPrioriteetti(i + 1);
                valinnanVaiheDtot.add(dto);
              }
              return valinnanVaiheDtot.isEmpty()
                  ? null
                  : new HakukohdeJaValinnanVaiheDTO(oid, valinnanVaiheDtot);
            })
        .filter((dto) -> null != dto)
        .collect(Collectors.toList());
  }

  @Transactional
  @GetMapping(value = "/{oid}/valintakoe", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen valintakokeet OID:n perusteella")
  public List<ValintakoeDTO> valintakoesForHakukohde(@PathVariable("oid") final String oid) {
    HakukohdeViite viite;
    try {
      viite = hakukohdeService.readByOid(oid);
    } catch (HakukohdeViiteEiOleOlemassaException e) {
      String message = "HakukohdeViite (" + oid + ") ei ole olemassa.";
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, message, e);
    }
    Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet =
        viite.getHakukohteenValintaperusteet();
    Map<String, String> tunnisteArvoPari =
        hakukohteenValintaperusteet.values().stream()
            .collect(Collectors.toMap(t -> t.getTunniste(), t -> t.getArvo()));
    return modelMapper
        .mapList(
            valintakoeService.findValintakoesByValinnanVaihes(
                valinnanVaiheService.findByHakukohde(oid)),
            ValintakoeDTO.class)
        .stream()
        .map(
            vk -> {
              if (Optional.ofNullable(vk.getTunniste())
                  .orElse("")
                  .startsWith(HAKUKOHDE_VIITE_PREFIX)) {
                String tunniste =
                    vk.getTunniste().replace(HAKUKOHDE_VIITE_PREFIX, "").replace("}}", "");
                vk.setSelvitettyTunniste(tunnisteArvoPari.get(tunniste));
              } else {
                vk.setSelvitettyTunniste(vk.getTunniste());
              }
              return vk;
            })
        .collect(Collectors.toList());
  }

  @PostMapping(value = "/valintakoe", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen valintakokeet OID:n perusteella")
  public List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(
      @RequestBody final List<String> oids) {
    return oids.stream()
        .map(
            oid -> {
              List<ValintakoeDTO> valintakoeDtos =
                  modelMapper.mapList(
                      valintakoeService.findValintakoesByValinnanVaihes(
                          valinnanVaiheService.findByHakukohde(oid.toString())),
                      ValintakoeDTO.class);
              if (valintakoeDtos == null || valintakoeDtos.isEmpty()) {
                return null;
              }
              return new HakukohdeJaValintakoeDTO(oid, valintakoeDtos);
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/{oid}/kuuluuSijoitteluun", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Palauttaa tiedon, kuuluuko hakukohde sijoitteluun")
  public Map<String, Boolean> kuuluuSijoitteluun(@PathVariable("oid") final String oid) {
    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("sijoitteluun", hakukohdeService.kuuluuSijoitteluun(oid));
    return map;
  }

  @GetMapping(value = "/{oid}/ilmanlaskentaa", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa")
  public List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(@PathVariable("oid") final String oid) {
    List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs =
        modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid), ValinnanVaiheJonoillaDTO.class);
    JononPrioriteettiAsettaja.filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(
        valinnanVaiheJonoillaDTOs);
    return valinnanVaiheJonoillaDTOs;
  }

  @GetMapping(value = "/{oid}/hakijaryhma", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen hakijaryhmät")
  public List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        hakijaryhmaValintatapajonoService.findByHakukohde(oid),
        HakijaryhmaValintatapajonoDTO.class);
  }

  private List<LinkitettyHakijaryhmaValintatapajonoDTO> getValintatapajonokohtaisetHakijaryhmat(
      String hakukohdeOid) {
    List<String> valintatapajonoOids =
        valinnanVaiheService.findByHakukohde(hakukohdeOid).stream()
            .map(ValinnanVaihe::getOid)
            .map(
                valinnanvaihe ->
                    valintatapajonoService.findJonoByValinnanvaihe(valinnanvaihe).stream()
                        .map(Valintatapajono::getOid))
            .flatMap(oid -> oid)
            .collect(Collectors.toList());

    return valintatapajonoOids.isEmpty()
        ? new ArrayList<>()
        : hakijaryhmaValintatapajonoService.findHakijaryhmaByJonos(valintatapajonoOids).stream()
            .map(
                hakijaryhma -> {
                  LinkitettyHakijaryhmaValintatapajonoDTO dto =
                      modelMapper.map(hakijaryhma, LinkitettyHakijaryhmaValintatapajonoDTO.class);
                  dto.setValintatapajonoOid(hakijaryhma.getValintatapajono().getOid());
                  return dto;
                })
            .collect(Collectors.toList());
  }

  private List<LinkitettyHakijaryhmaValintatapajonoDTO> getHakukohdekohtaisetHakijaryhmat(
      List<String> hakukohdeOidit) {
    return hakijaryhmaValintatapajonoService.findByHakukohteet(hakukohdeOidit).stream()
        .map(
            hakijaryhma -> {
              LinkitettyHakijaryhmaValintatapajonoDTO dto =
                  modelMapper.map(hakijaryhma, LinkitettyHakijaryhmaValintatapajonoDTO.class);
              dto.setHakukohdeOid(hakijaryhma.getHakukohdeViite().getOid());
              return dto;
            })
        .collect(Collectors.toList());
  }

  @PostMapping(
      value = "/hakijaryhmat",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteiden hakijaryhmät")
  public List<HakukohdeJaLinkitettyHakijaryhmaValintatapajonoDTO> hakijaryhmat(
      @RequestBody final List<String> hakukohdeOidit) {
    List<LinkitettyHakijaryhmaValintatapajonoDTO> hakijaryhmat =
        getHakukohdekohtaisetHakijaryhmat(hakukohdeOidit);
    return hakukohdeOidit.stream()
        .map(
            hakukohdeOid -> {
              List<LinkitettyHakijaryhmaValintatapajonoDTO> hakukohteenHakijaryhmat =
                  new ArrayList<>();
              hakukohteenHakijaryhmat.addAll(getValintatapajonokohtaisetHakijaryhmat(hakukohdeOid));
              hakukohteenHakijaryhmat.addAll(
                  hakijaryhmat.stream()
                      .filter(hakijaryhma -> hakukohdeOid.equals(hakijaryhma.getHakukohdeOid()))
                      .collect(Collectors.toList()));
              return new HakukohdeJaLinkitettyHakijaryhmaValintatapajonoDTO(
                  hakukohdeOid, hakukohteenHakijaryhmat);
            })
        .filter(hakukohde -> !hakukohde.getHakijaryhmat().isEmpty())
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/{oid}/laskentakaava", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen järjestyskriteerit")
  public List<JarjestyskriteeriDTO> findLaskentaKaavat(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        jarjestyskriteeriService.findByHakukohde(oid), JarjestyskriteeriDTO.class);
  }

  @GetMapping(value = "/avaimet/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteen syötettävät tiedot")
  public List<ValintaperusteDTO> findAvaimet(@PathVariable("oid") final String oid) {
    return laskentakaavaService.findAvaimetForHakukohde(oid);
  }

  @PostMapping(
      value = "/avaimet",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Hakee hakukohteiden syötettävät tiedot")
  public List<HakukohdeJaValintaperusteDTO> findHakukohteidenAvaimet(
      @RequestBody final List<String> hakukohdeOidit) {
    return laskentakaavaService.findAvaimetForHakukohteet(hakukohdeOidit).entrySet().stream()
        .map(entry -> new HakukohdeJaValintaperusteDTO(entry.getKey(), entry.getValue()))
        .filter(r -> !r.getValintaperusteDTO().isEmpty())
        .collect(Collectors.toList());
  }

  @GetMapping(
      value = "/{hakukohdeOid}/avaimet",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  public HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(
      @PathVariable("hakukohdeOid") final String hakukohdeOid) {
    return laskentakaavaService.findHakukohteenAvaimet(hakukohdeOid);
  }

  @PutMapping(
      value = "/{hakukohdeOid}/valinnanvaihe",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää valinnan vaiheen hakukohteelle")
  @ApiResponses(
      @ApiResponse(responseCode = "400", description = "Valinnan vaiheen lisääminen epäonnistui"))
  public ResponseEntity<ValinnanVaiheDTO> insertValinnanvaihe(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @Parameter(
              description =
                  "Edellisen valinnan vaiheen OID (jos valinnan vaihe halutaa lisätä tietyn vaiheen jälkeen, muussa tapauksessa uusi vaihe lisätään viimeiseksi)")
          @RequestParam(name = "edellinenValinnanVaiheOid", required = false)
          final String edellinenValinnanVaiheOid,
      @RequestBody final ValinnanVaiheCreateDTO valinnanVaihe,
      final HttpServletRequest request) {
    try {
      ValinnanVaiheDTO lisatty =
          modelMapper.map(
              valinnanVaiheService.lisaaValinnanVaiheHakukohteelle(
                  hakukohdeOid, valinnanVaihe, edellinenValinnanVaiheOid),
              ValinnanVaiheDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_LISAYS_VALINNANVAIHE,
          ValintaResource.HAKUKOHDE,
          hakukohdeOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOG.error("Error creating valinnanvaihe.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping(
      value = "/{hakukohdeOid}/hakijaryhma",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää hakijaryhmän hakukohteelle")
  @ApiResponses(
      @ApiResponse(responseCode = "400", description = "Hakijaryhmän lisääminen epäonnistui"))
  public ResponseEntity<Object> insertHakijaryhma(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @RequestBody final HakijaryhmaCreateDTO hakijaryhma,
      final HttpServletRequest request) {
    try {
      HakijaryhmaDTO lisatty =
          modelMapper.map(
              hakijaryhmaValintatapajonoService.lisaaHakijaryhmaHakukohteelle(
                  hakukohdeOid, hakijaryhma),
              HakijaryhmaDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_LISAYS_HAKIJARYHMA,
          ValintaResource.HAKUKOHDE,
          hakukohdeOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (LaskentakaavaOidTyhjaException e) {
      LOG.warn("Error creating hakijaryhma for hakukohde: " + e.toString());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOG.error("Error creating hakijaryhma for hakukohde.", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(
      value = "/{hakukohdeOid}/hakijaryhma/{hakijaryhmaOid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(READ_UPDATE_CRUD)
  @Operation(summary = "Liittää hakijaryhmän hakukohteelle")
  public ResponseEntity<Object> liitaHakijaryhma(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @PathVariable("hakijaryhmaOid") final String hakijaryhmaOid,
      final HttpServletRequest request) {
    try {
      hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(hakukohdeOid, hakijaryhmaOid);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_LIITOS_HAKIJARYHMA,
          ValintaResource.HAKUKOHDE,
          hakukohdeOid,
          Changes.EMPTY,
          ImmutableMap.of("liitettavaHakijaryhmaOid", hakijaryhmaOid));
      return ResponseEntity.accepted().build();
    } catch (Exception e) {
      LOG.error("Error linking hakijaryhma.", e);
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping(
      value = "/{hakukohdeOid}/hakukohdekoodi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(UPDATE_CRUD)
  @Operation(summary = "Päivittää hakukohteen hakukohdekoodia")
  @ApiResponses(@ApiResponse(responseCode = "400", description = "Päivittäminen epäonnistui"))
  public ResponseEntity<Object> updateHakukohdekoodi(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @RequestBody final KoodiDTO hakukohdekoodi,
      final HttpServletRequest request) {
    try {
      KoodiDTO lisatty =
          modelMapper.map(
              hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, hakukohdekoodi),
              KoodiDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_HAKUKOHDEKOODI_PAIVITYS,
          ValintaResource.HAKUKOHDE,
          hakukohdeOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.accepted().body(lisatty);
    } catch (Exception e) {
      LOG.error("Error updating hakukohdekoodit.", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PutMapping(
      value = "/{hakukohdeOid}/hakukohdekoodi",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(summary = "Lisää hakukohdekoodin hakukohteelle")
  @ApiResponses(@ApiResponse(responseCode = "400", description = "Lisääminen epäonnistui"))
  public ResponseEntity<Object> insertHakukohdekoodi(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @RequestBody final KoodiDTO hakukohdekoodi,
      final HttpServletRequest request) {
    try {
      KoodiDTO lisatty =
          modelMapper.map(
              hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, hakukohdekoodi),
              KoodiDTO.class);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_LISAYS_HAKUKOHDEKOODI,
          ValintaResource.HAKUKOHDE,
          hakukohdeOid,
          Changes.addedDto(lisatty));
      return ResponseEntity.status(HttpStatus.CREATED).body(lisatty);
    } catch (Exception e) {
      LOG.error("Error inserting hakukohdekoodi.", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping(
      value = "/{hakukohdeOid}/siirra",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(CRUD)
  @Operation(
      summary =
          "Siirtää hakukohteen uuteen valintaryhmään (tai juureen, jos valintaryhmää ei anneta)")
  @ApiResponses(@ApiResponse(responseCode = "400", description = "Siirtäminen epäonnistui"))
  public ResponseEntity<Object> siirraHakukohdeValintaryhmaan(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @RequestBody final String valintaryhmaOid,
      final HttpServletRequest request) {
    try {
      HakukohdeViiteDTO hakukohde =
          modelMapper.map(
              hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, true),
              HakukohdeViiteDTO.class);
      Map<String, String> additionalInfo =
          ImmutableMap.of("Uuden valintaryhman oid", valintaryhmaOid);
      AuditLog.log(
          ValintaperusteetAudit.AUDIT,
          AuditLog.getUser(request),
          ValintaperusteetOperation.HAKUKOHDE_SIIRTO_VALINTARYHMAAN,
          ValintaResource.HAKUKOHDE,
          hakukohdeOid,
          Changes.addedDto(hakukohde),
          additionalInfo);
      return ResponseEntity.accepted().body(hakukohde);
    } catch (Exception e) {
      LOG.error("Error moving hakukohde to new valintaryhma.", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
