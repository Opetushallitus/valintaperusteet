package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.JononPrioriteettiAsettaja;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/resources/valintalaskentakoostepalvelu")
@PreAuthorize("isAuthenticated()")
public class ValintalaskentakoostepalveluResource {
  private static final Logger LOG =
      LoggerFactory.getLogger(ValintalaskentakoostepalveluResource.class);
  private static final String HAKUKOHDE_VIITE_PREFIX = "{{hakukohde.";

  private final ValintaperusteService valintaperusteService;
  private final HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;
  private final ValinnanVaiheService valinnanVaiheService;
  private final ValintaryhmaService valintaryhmaService;
  private final ValintatapajonoService valintatapajonoService;
  private final LaskentakaavaService laskentakaavaService;
  private final ValintaperusteetModelMapper modelMapper;
  private final HakukohdeService hakukohdeService;
  private final ValintakoeService valintakoeService;

  @Autowired
  public ValintalaskentakoostepalveluResource(
      ValintaperusteService valintaperusteService,
      HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService,
      ValinnanVaiheService valinnanVaiheService,
      @Lazy ValintaryhmaService valintaryhmaService,
      ValintatapajonoService valintatapajonoService,
      LaskentakaavaService laskentakaavaService,
      ValintaperusteetModelMapper modelMapper,
      HakukohdeService hakukohdeService,
      ValintakoeService valintakoeService) {
    this.valintaperusteService = valintaperusteService;
    this.hakijaryhmaValintatapajonoService = hakijaryhmaValintatapajonoService;
    this.valinnanVaiheService = valinnanVaiheService;
    this.valintaryhmaService = valintaryhmaService;
    this.valintatapajonoService = valintatapajonoService;
    this.laskentakaavaService = laskentakaavaService;
    this.modelMapper = modelMapper;
    this.hakukohdeService = hakukohdeService;
    this.valintakoeService = valintakoeService;
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/valintatapajono/kopiot", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, List<String>> findKopiot(
      @RequestParam(value = "oid", required = false) final List<String> oid) {
    try {
      return valintatapajonoService.findKopiot(oid);
    } catch (Exception e) {
      LOG.error("Virhe valintatapajonojen kopioiden hakemisessa!", e);
      throw e;
    }
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/valintatapajono",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
      @RequestBody final List<String> hakukohdeOids) {
    long t0 = System.currentTimeMillis();
    try {
      return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOids);
    } finally {
      LOG.info("Valintatapajonojen haku kesti {}ms", (System.currentTimeMillis() - t0));
    }
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/haku",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(
      @RequestBody final List<String> hakukohdeOids) {
    if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
      LOG.error("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla");
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          null,
          new RuntimeException("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla"));
    }
    long started = System.currentTimeMillis();
    LOG.info(
        "Haetaan hakukohdeOid joukolla {}",
        Arrays.toString(hakukohdeOids.stream().map(this::sanitizeOid).toArray()));
    try {
      Map<String, List<HakijaryhmaValintatapajono>> hakukohdeHakijaryhmaMap =
          hakijaryhmaValintatapajonoService.findByHakukohteet(hakukohdeOids).stream()
              .collect(Collectors.groupingBy(hr -> hr.getHakukohdeViite().getOid()));

      List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs = new ArrayList<>();
      hakukohdeHakijaryhmaMap
          .keySet()
          .forEach(
              hakukohdeOid -> {
                List<HakijaryhmaValintatapajono> jarjestetytHakijaryhmat =
                    LinkitettavaJaKopioitavaUtil.jarjesta(
                        hakukohdeHakijaryhmaMap.get(hakukohdeOid));
                List<HakijaryhmaValintatapajonoDTO> hakukohteenHakijaryhmaValintatapajonoDTOs =
                    jarjestetytHakijaryhmat.stream()
                        .map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class))
                        .collect(Collectors.toList());
                IntStream.range(0, hakukohteenHakijaryhmaValintatapajonoDTOs.size())
                    .forEach(
                        i -> {
                          hakukohteenHakijaryhmaValintatapajonoDTOs.get(i).setPrioriteetti(i);
                        });
                hakijaryhmaValintatapajonoDTOs.addAll(hakukohteenHakijaryhmaValintatapajonoDTOs);
              });
      return hakijaryhmaValintatapajonoDTOs;
    } catch (HakijaryhmaEiOleOlemassaException e) {
      LOG.error("Hakijaryhmää ei löytynyt!", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    } catch (Exception e) {
      LOG.error("Hakijaryhmää ei saatu haettua!", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, null, e);
    } finally {
      LOG.info(
          "Haku kesti {} ms. Hakukohteet: {}",
          (System.currentTimeMillis() - started),
          Arrays.toString(hakukohdeOids.stream().map(this::sanitizeOid).toArray()));
    }
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/haku/valintatapajono/hakijaryhmat",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public List<HakijaryhmaValintatapajonoDTO> readByValintatapajonoOids(
      @RequestBody final List<String> valintatapajonoOids) {
    if (valintatapajonoOids == null || valintatapajonoOids.isEmpty()) {
      LOG.error("Yritettiin hakea hakijaryhmia tyhjalla valintatapajono OID joukolla");
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          null,
          new RuntimeException(
              "Yritettiin hakea hakijaryhmia valintatapajono hakukohde OID joukolla"));
    }
    long started = System.currentTimeMillis();
    LOG.info(
        "Haetaan valintatapajonoOid joukolla {}",
        Arrays.toString(valintatapajonoOids.stream().map(this::sanitizeOid).toArray()));
    try {
      Map<String, List<HakijaryhmaValintatapajono>> valintatapajonoHakijaryhmaMap =
          hakijaryhmaValintatapajonoService.findHakijaryhmaByJonos(valintatapajonoOids).stream()
              .collect(Collectors.groupingBy(hr -> hr.getValintatapajono().getOid()));

      List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs = new ArrayList<>();
      valintatapajonoHakijaryhmaMap
          .keySet()
          .forEach(
              hakukohdeOid -> {
                List<HakijaryhmaValintatapajono> jarjestetytHakijaryhmat =
                    LinkitettavaJaKopioitavaUtil.jarjesta(
                        valintatapajonoHakijaryhmaMap.get(hakukohdeOid));
                List<HakijaryhmaValintatapajonoDTO> valintapajononHakijaryhmaValintatapajonoDTOs =
                    jarjestetytHakijaryhmat.stream()
                        .map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class))
                        .collect(Collectors.toList());
                IntStream.range(0, valintapajononHakijaryhmaValintatapajonoDTOs.size())
                    .forEach(
                        i -> {
                          valintapajononHakijaryhmaValintatapajonoDTOs.get(i).setPrioriteetti(i);
                        });
                hakijaryhmaValintatapajonoDTOs.addAll(valintapajononHakijaryhmaValintatapajonoDTOs);
              });

      return hakijaryhmaValintatapajonoDTOs;
    } catch (HakijaryhmaEiOleOlemassaException e) {
      LOG.error("Hakijaryhmää ei löytynyt!", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
    } catch (Exception e) {
      LOG.error("Hakijaryhmää ei saatu haettua!", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, null, e);
    } finally {
      LOG.info(
          "Haku kesti {} ms. Valintatapajonot: {}",
          (System.currentTimeMillis() - started),
          Arrays.toString(valintatapajonoOids.toArray()));
    }
  }

  private Function<ValintakoeDTO, ValintakoeDTO> lisaaSelvitettyTunniste(
      Map<String, String> tunnisteArvoPari, String hakukohdeOid) {
    return vk -> {
      if (Optional.ofNullable(vk.getTunniste()).orElse("").startsWith(HAKUKOHDE_VIITE_PREFIX)) {
        String tunniste = vk.getTunniste().replace(HAKUKOHDE_VIITE_PREFIX, "").replace("}}", "");
        if (!tunnisteArvoPari.containsKey(tunniste)) {
          LOG.error(
              "Tunnistetta {} ei voitu selvittää. Tämä oletettavasti johtuu puuttuvista valintaperusteista hakukohteelle {}",
              tunniste,
              hakukohdeOid);
          vk.setSelvitettyTunniste(tunniste);
        } else {
          vk.setSelvitettyTunniste(tunnisteArvoPari.get(tunniste));
        }
      } else {
        vk.setSelvitettyTunniste(vk.getTunniste());
      }
      return vk;
    };
  }

  @Transactional
  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/{oid}/valintakoe", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee hakukohteen valintakokeet OID:n perusteella")
  public List<ValintakoeDTO> valintakoesForHakukohde(@PathVariable("oid") final String oid) {
    HakukohdeViite viite = hakukohdeService.readByOid(oid);
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
        .map(lisaaSelvitettyTunniste(tunnisteArvoPari, oid))
        .collect(Collectors.toList());
  }

  @Transactional
  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/tunniste",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee valintakokeen OID:n perusteella")
  public List<ValintakoeDTO> readByTunnisteet(@RequestBody final List<String> tunnisteet) {
    return modelMapper.mapList(valintakoeService.readByTunnisteet(tunnisteet), ValintakoeDTO.class);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/hakukohde/haku/{hakuOid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee haun hakukohteet")
  public List<HakukohdeViiteDTO> haunHakukohteet(
      @PathVariable("hakuOid") final String hakuOid,
      @RequestParam(value = "vainValintakokeelliset", required = false, defaultValue = "false")
          final Boolean vainValintakokeelliset) {
    List<HakukohdeViite> hakukohteet =
        hakukohdeService.haunHakukohteet(hakuOid, vainValintakokeelliset);
    return modelMapper.mapList(hakukohteet, HakukohdeViiteDTO.class);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/hakukohde/{oid}/ilmanlaskentaa",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa")
  public List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(@PathVariable("oid") final String oid) {
    List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs =
        modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid), ValinnanVaiheJonoillaDTO.class);
    JononPrioriteettiAsettaja.filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(
        valinnanVaiheJonoillaDTOs);
    return valinnanVaiheJonoillaDTOs;
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/hakukohde/avaimet/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee hakukohteen syötettävät tiedot")
  public List<ValintaperusteDTO> findAvaimet(@PathVariable("oid") final String oid) {
    return laskentakaavaService.findAvaimetForHakukohde(oid);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/hakukohde/avaimet",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee hakukohteen syötettävät tiedot")
  public List<HakukohdeJaValintaperusteDTO> findAvaimet(@RequestBody final List<String> oids) {
    return oids.stream()
        .map(
            oid ->
                new HakukohdeJaValintaperusteDTO(
                    oid, laskentakaavaService.findAvaimetForHakukohde(oid)))
        .collect(Collectors.toList());
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/hakukohde/valintakoe",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee hakukohteen valintakokeet OID:n perusteella")
  public List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(
      @RequestBody final List<String> oids) {
    List<HakukohdeViite> viites = hakukohdeService.readByOids(oids);
    Map<String, HakukohdeViite> viitteet =
        viites.stream().collect(Collectors.toMap(v -> v.getOid(), v -> v));
    return oids.stream()
        .map(
            oid -> {
              Map<String, String> tunnisteArvoPari;
              if (viitteet.containsKey(oid)) {
                Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet =
                    Optional.ofNullable(viitteet.get(oid).getHakukohteenValintaperusteet())
                        .orElse(Collections.emptyMap());
                tunnisteArvoPari =
                    hakukohteenValintaperusteet.values().stream()
                        .collect(Collectors.toMap(t -> t.getTunniste(), t -> t.getArvo()));
              } else {
                tunnisteArvoPari = Collections.emptyMap();
              }
              List<ValintakoeDTO> valintakoeDtos =
                  modelMapper
                      .mapList(
                          valintakoeService.findValintakoesByValinnanVaihes(
                              valinnanVaiheService.findByHakukohde(oid.toString())),
                          ValintakoeDTO.class)
                      .stream()
                      .map(lisaaSelvitettyTunniste(tunnisteArvoPari, oid))
                      .collect(Collectors.toList());
              if (valintakoeDtos == null || valintakoeDtos.isEmpty()) {
                return null;
              }
              return new HakukohdeJaValintakoeDTO(oid, valintakoeDtos);
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/valintaperusteet/{hakukohdeOid}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee valintaperusteet")
  public List<ValintaperusteetDTO> haeValintaperusteet(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @Parameter(description = "Valinnanvaiheen järjestysluku")
          @RequestParam(value = "vaihe", required = false)
          final Integer valinnanVaiheJarjestysluku) {
    HakuparametritDTO hakuparametrit = new HakuparametritDTO();
    hakuparametrit.setHakukohdeOid(hakukohdeOid);
    if (valinnanVaiheJarjestysluku != null) {
      hakuparametrit.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
    }
    List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);
    return valintaperusteService.haeValintaperusteet(list);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/valintaperusteet",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee valintaperusteet")
  public List<ValintaperusteetDTO> haeValintaperusteet(
      @RequestBody final List<String> hakukohdeOids) {
    return valintaperusteService.haeValintaperusteet(
        hakukohdeOids.stream()
            .map(
                oid -> {
                  HakuparametritDTO hakuparametritDTO = new HakuparametritDTO();
                  hakuparametritDTO.setHakukohdeOid(oid);
                  return hakuparametritDTO;
                })
            .collect(Collectors.toList()));
  }

  @PreAuthorize(UPDATE_CRUD)
  @PostMapping(
      value = "/valintaperusteet/tuoHakukohde",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "importoi hakukohde")
  public ResponseEntity<Object> tuoHakukohde(@RequestBody final HakukohdeImportDTO hakukohde) {
    if (hakukohde == null) {
      LOG.error("Valintaperusteet sai null hakukohteen importoitavaksi!");
      throw new RuntimeException("Valintaperusteet sai null hakukohteen importoitavaksi!");
    }
    try {
      valintaperusteService.tuoHakukohde(hakukohde);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      LOG.error(
          "Hakukohteen importointi valintaperusteisiin epaonnistui! {}",
          hakukohde.getHakukohdeOid(),
          e);
      throw e;
    }
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/valintaperusteet/hakijaryhma/{hakukohdeOid}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(
      @PathVariable("hakukohdeOid") final String hakukohdeOid) {
    StopWatch stopWatch =
        new StopWatch("Hakukohteen  " + hakukohdeOid + " hakijaryhmien haku valintalaskennalle");
    LOG.info("Haetaan hakijaryhmät hakukohteelle {}", hakukohdeOid);
    stopWatch.start("Haetaan hakukohteen hakijaryhmät");
    List<HakijaryhmaValintatapajono> hakukohteenRyhmat =
        hakijaryhmaValintatapajonoService.findByHakukohde(hakukohdeOid);
    stopWatch.stop();
    stopWatch.start("Haetaan valinnanvaiheet");
    List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
    stopWatch.stop();
    vaiheet.stream()
        .forEachOrdered(
            vaihe -> {
              stopWatch.start("Haetaan valintatapajonot vaiheelle: " + vaihe.getOid());
              List<Valintatapajono> jonot =
                  valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
              stopWatch.stop();
              jonot.stream()
                  .forEachOrdered(
                      jono -> {
                        stopWatch.start("Haetaan hakijaryhmä valintatapajonolle: " + jono.getOid());
                        hakukohteenRyhmat.addAll(
                            hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(jono.getOid()));
                        stopWatch.stop();
                      });
            });
    List<ValintaperusteetHakijaryhmaDTO> result = new ArrayList<>();
    for (int i = 0; i < hakukohteenRyhmat.size(); i++) {
      HakijaryhmaValintatapajono original = hakukohteenRyhmat.get(i);
      stopWatch.start(
          "Haketaan hakijaryhmän " + original.getHakijaryhma().getOid() + " laskentakaava");
      Laskentakaava laskentakaava =
          laskentakaavaService.haeLaskettavaKaava(
              original.getHakijaryhma().getLaskentakaava().getId(), Laskentamoodi.VALINTALASKENTA);
      stopWatch.stop();
      ;
      ValintaperusteetHakijaryhmaDTO dto =
          modelMapper.map(original, ValintaperusteetHakijaryhmaDTO.class);
      // Asetetaan laskentakaavan nimi ensimmäisen funktiokutsun nimeksi
      laskentakaava
          .getFunktiokutsu()
          .getSyoteparametrit()
          .forEach(
              s -> {
                if (s.getAvain().equals("nimi")) {
                  s.setArvo(laskentakaava.getNimi());
                }
              });
      dto.setFunktiokutsu(
          modelMapper.map(laskentakaava.getFunktiokutsu(), ValintaperusteetFunktiokutsuDTO.class));
      dto.setNimi(original.getHakijaryhma().getNimi());
      dto.setKuvaus(original.getHakijaryhma().getKuvaus());
      dto.setPrioriteetti(i);
      dto.setKaytetaanRyhmaanKuuluvia(original.isKaytetaanRyhmaanKuuluvia());
      dto.setHakukohdeOid(hakukohdeOid);
      if (original.getValintatapajono() != null) {
        dto.setValintatapajonoOid(original.getValintatapajono().getOid());
      }
      result.add(dto);
    }
    LOG.info(stopWatch.prettyPrint());
    return result;
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/hakukohde/{oid}/valinnanvaihe", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Palauttaa valintatapajonot")
  public List<ValinnanVaiheJonoillaDTO> vaiheetJaJonot(@PathVariable("oid") final String oid) {
    return modelMapper.mapList(
        hakukohdeService.vaiheetJaJonot(oid), ValinnanVaiheJonoillaDTO.class);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/valinnanvaihe/{oid}/hakukohteet",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary =
          "Hakee oidit hakukohteille, jotka liittyvät valinnanvaiheeseen valintaryhmän kautta")
  public Set<String> hakukohteet(@PathVariable("oid") final String oid) {
    Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
    return valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/valinnanvaihe/{oid}/valintaperusteet",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Hakee valintaperusteet, jotka liittyvät valinnanvaiheeseen valintaryhmän kautta")
  public List<ValintaperusteetDTO> valintaperusteet(@PathVariable("oid") final String oid) {
    Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
    Set<String> hakukohdeOids = valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
    return hakukohdeOids.stream()
        .flatMap(
            hakukohdeOid -> {
              HakuparametritDTO hakuparametrit = new HakuparametritDTO();
              hakuparametrit.setHakukohdeOid(hakukohdeOid);
              return valintaperusteService
                  .haeValintaperusteet(Arrays.asList(hakuparametrit))
                  .stream();
            })
        .collect(Collectors.toList());
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/valintaryhma/{oid}/vastuuorganisaatio",
      produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Hakee valintaryhmän vastuuorganisaation oidin")
  public String valintaryhmaVastuuorganisaatio(@PathVariable("oid") final String oid) {
    return valintaryhmaService.readByOid(oid).getVastuuorganisaatio().getOid();
  }

  private String sanitizeOid(final String oid) {
    return oid.replaceAll("[\n\r]", "_");
  }
}
