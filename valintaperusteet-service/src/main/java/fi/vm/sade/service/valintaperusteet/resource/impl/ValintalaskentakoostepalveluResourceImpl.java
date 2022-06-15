package fi.vm.sade.service.valintaperusteet.resource.impl;

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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
@Path("valintalaskentakoostepalvelu")
@PreAuthorize("isAuthenticated()")
public class ValintalaskentakoostepalveluResourceImpl {
  private static final Logger LOG =
      LoggerFactory.getLogger(ValintalaskentakoostepalveluResourceImpl.class);
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
  public ValintalaskentakoostepalveluResourceImpl(
      ValintaperusteService valintaperusteService,
      HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService,
      ValinnanVaiheService valinnanVaiheService,
      ValintaryhmaService valintaryhmaService,
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

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/valintatapajono/kopiot")
  public Map<String, List<String>> findKopiot(@QueryParam("oid") List<String> oid) {
    try {
      return valintatapajonoService.findKopiot(oid);
    } catch (Exception e) {
      LOG.error("Virhe valintatapajonojen kopioiden hakemisessa!", e);
      throw e;
    }
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("/valintatapajono")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
      List<String> hakukohdeOids) {
    long t0 = System.currentTimeMillis();
    try {
      return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOids);
    } catch (Exception e) {
      LOG.error("Hakukohteiden valintatapajonojen hakeminen epäonnistui.", e);
      throw e;
    } finally {
      LOG.info("Valintatapajonojen haku kesti {}ms", (System.currentTimeMillis() - t0));
    }
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/haku")
  public List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(List<String> hakukohdeOids) {
    if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
      LOG.error("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla");
      throw new WebApplicationException(
          new RuntimeException("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla"),
          Response.Status.NOT_FOUND);
    }
    long started = System.currentTimeMillis();
    LOG.info("Haetaan hakukohdeOid joukolla {}", Arrays.toString(hakukohdeOids.toArray()));
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
      throw new WebApplicationException(e, Response.Status.NOT_FOUND);
    } catch (Exception e) {
      LOG.error("Hakijaryhmää ei saatu haettua!", e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    } finally {
      LOG.info(
          "Haku kesti {} ms. Hakukohteet: {}",
          (System.currentTimeMillis() - started),
          Arrays.toString(hakukohdeOids.toArray()));
    }
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/haku/valintatapajono/hakijaryhmat")
  public List<HakijaryhmaValintatapajonoDTO> readByValintatapajonoOids(
      List<String> valintatapajonoOids) {
    if (valintatapajonoOids == null || valintatapajonoOids.isEmpty()) {
      LOG.error("Yritettiin hakea hakijaryhmia tyhjalla valintatapajono OID joukolla");
      throw new WebApplicationException(
          new RuntimeException(
              "Yritettiin hakea hakijaryhmia valintatapajono hakukohde OID joukolla"),
          Response.Status.NOT_FOUND);
    }
    long started = System.currentTimeMillis();
    LOG.info(
        "Haetaan valintatapajonoOid joukolla {}", Arrays.toString(valintatapajonoOids.toArray()));
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
      throw new WebApplicationException(e, Response.Status.NOT_FOUND);
    } catch (Exception e) {
      LOG.error("Hakijaryhmää ei saatu haettua!", e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
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
  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("/{oid}/valintakoe")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
      value = "Hakee hakukohteen valintakokeet OID:n perusteella",
      response = ValintakoeDTO.class)
  public List<ValintakoeDTO> valintakoesForHakukohde(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
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
  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("tunniste/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee valintakokeen OID:n perusteella", response = ValintakoeDTO.class)
  public List<ValintakoeDTO> readByTunnisteet(
      @ApiParam(value = "tunnisteet", required = true) List<String> tunnisteet) {
    return modelMapper.mapList(valintakoeService.readByTunnisteet(tunnisteet), ValintakoeDTO.class);
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("hakukohde/haku/{hakuOid}")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee haun hakukohteet", response = HakukohdeViiteDTO.class)
  public List<HakukohdeViiteDTO> haunHakukohteet(
      @ApiParam(value = "hakuOid", required = true) @PathParam("hakuOid") String hakuOid) {
    return modelMapper.mapList(hakukohdeService.haunHakukohteet(hakuOid), HakukohdeViiteDTO.class);
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("hakukohde/{oid}/ilmanlaskentaa")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
      value = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa",
      response = ValintatapajonoDTO.class)
  public List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(@PathParam("oid") String oid) {
    List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs =
        modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid), ValinnanVaiheJonoillaDTO.class);
    JononPrioriteettiAsettaja.filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(
        valinnanVaiheJonoillaDTOs);
    return valinnanVaiheJonoillaDTOs;
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("hakukohde/avaimet/{oid}")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
  public List<ValintaperusteDTO> findAvaimet(
      @ApiParam(value = "Hakukohde OID", required = true) @PathParam("oid") String oid) {
    return laskentakaavaService.findAvaimetForHakukohde(oid);
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("hakukohde/avaimet")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
  public List<HakukohdeJaValintaperusteDTO> findAvaimet(
      @ApiParam(value = "Hakukohde OIDs", required = true) List<String> oids) {
    return oids.stream()
        .map(
            oid ->
                new HakukohdeJaValintaperusteDTO(
                    oid, laskentakaavaService.findAvaimetForHakukohde(oid)))
        .collect(Collectors.toList());
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("hakukohde/valintakoe")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
      value = "Hakee hakukohteen valintakokeet OID:n perusteella",
      response = ValintakoeDTO.class)
  public List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(List<String> oids) {
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

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("valintaperusteet/{hakukohdeOid}")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee valintaperusteet")
  public List<ValintaperusteetDTO> haeValintaperusteet(
      @ApiParam(value = "Hakukohde OID") @PathParam("hakukohdeOid") String hakukohdeOid,
      @ApiParam(value = "Valinnanvaiheen järjestysluku") @QueryParam("vaihe")
          Integer valinnanVaiheJarjestysluku) {
    HakuparametritDTO hakuparametrit = new HakuparametritDTO();
    hakuparametrit.setHakukohdeOid(hakukohdeOid);
    if (valinnanVaiheJarjestysluku != null) {
      hakuparametrit.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
    }
    List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);
    return valintaperusteService.haeValintaperusteet(list);
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("valintaperusteet")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee valintaperusteet")
  public List<ValintaperusteetDTO> haeValintaperusteet(
      @ApiParam(value = "Hakukohde OIDs") List<String> hakukohdeOids) {
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

  @POST
  @PreAuthorize(UPDATE_CRUD)
  @Path("valintaperusteet/tuoHakukohde")
  @Consumes(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "importoi hakukohde")
  public Response tuoHakukohde(
      @ApiParam(value = "Importoitava hakukohde") HakukohdeImportDTO hakukohde) {
    if (hakukohde == null) {
      LOG.error("Valintaperusteet sai null hakukohteen importoitavaksi!");
      throw new RuntimeException("Valintaperusteet sai null hakukohteen importoitavaksi!");
    }
    try {
      valintaperusteService.tuoHakukohde(hakukohde);
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error(
          "Hakukohteen importointi valintaperusteisiin epaonnistui! {}",
          hakukohde.getHakukohdeOid(),
          e);
      throw e;
    }
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("valintaperusteet/hakijaryhma/{hakukohdeOid}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(
      @PathParam("hakukohdeOid") String hakukohdeOid) {
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

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("hakukohde/{oid}/valinnanvaihe")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Palauttaa valintatapajonot", response = ValintatapajonoDTO.class)
  public List<ValinnanVaiheJonoillaDTO> vaiheetJaJonot(@PathParam("oid") String oid) {
    return modelMapper.mapList(
        hakukohdeService.vaiheetJaJonot(oid), ValinnanVaiheJonoillaDTO.class);
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("valinnanvaihe/{oid}/hakukohteet")
  @ApiOperation(
      value = "Hakee oidit hakukohteille, jotka liittyvät valinnanvaiheeseen valintaryhmän kautta",
      response = ValinnanVaiheDTO.class)
  public Set<String> hakukohteet(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
    Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
    return valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("valinnanvaihe/{oid}/valintaperusteet")
  @ApiOperation(
      value = "Hakee valintaperusteet, jotka liittyvät valinnanvaiheeseen valintaryhmän kautta",
      response = ValinnanVaiheDTO.class)
  public List<ValintaperusteetDTO> valintaperusteet(
      @ApiParam(value = "Valinnanvaihe OID", required = true) @PathParam("oid") String oid) {
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

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("valintaryhma/{oid}/vastuuorganisaatio")
  @ApiOperation(value = "Hakee valintaryhmän vastuuorganisaation oidin", response = String.class)
  public String valintaryhmaVastuuorganisaatio(
      @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
    return valintaryhmaService.readByOid(oid).getVastuuorganisaatio().getOid();
  }
}
