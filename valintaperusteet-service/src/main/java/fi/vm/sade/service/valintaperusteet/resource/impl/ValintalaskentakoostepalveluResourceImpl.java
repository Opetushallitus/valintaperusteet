package fi.vm.sade.service.valintaperusteet.resource.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.util.JononPrioriteettiAsettaja;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Path("valintalaskentakoostepalvelu")
public class ValintalaskentakoostepalveluResourceImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ValintalaskentakoostepalveluResourceImpl.class);
    private final static String HAKUKOHDE_VIITE_PREFIX = "{{hakukohde.";

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
    public ValintalaskentakoostepalveluResourceImpl(ValintaperusteService valintaperusteService,
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
    @Path("/valintatapajono")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
            List<String> hakukohdeOids) {
        long t0 = System.currentTimeMillis();
        try {
            return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOids);
        } finally {
            LOG.info("Valintatapajonojen haku kesti {}ms", (System.currentTimeMillis() - t0));
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/haku")
    public List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(List<String> hakukohdeOids) {
        if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
            LOG.error("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla");
            throw new WebApplicationException(new RuntimeException("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla"), Response.Status.NOT_FOUND);
        }
        long started = System.currentTimeMillis();
        LOG.info("Haetaan hakukohdeOid joukolla {}", Arrays.toString(hakukohdeOids.toArray()));
        try {
            Map<String, List<HakijaryhmaValintatapajono>> hakukohdeHakijaryhmaMap = hakijaryhmaValintatapajonoService.findByHakukohteet(hakukohdeOids).stream().collect(Collectors.groupingBy(hr -> hr.getHakukohdeViite().getOid()));

            List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs = new ArrayList<>();
            hakukohdeHakijaryhmaMap.keySet().forEach(hakukohdeOid -> {
                List<HakijaryhmaValintatapajono> jarjestetytHakijaryhmat = LinkitettavaJaKopioitavaUtil.jarjesta(hakukohdeHakijaryhmaMap.get(hakukohdeOid));
                List<HakijaryhmaValintatapajonoDTO> hakukohteenHakijaryhmaValintatapajonoDTOs = jarjestetytHakijaryhmat.stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
                IntStream.range(0, hakukohteenHakijaryhmaValintatapajonoDTOs.size()).forEach(i -> {
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
            LOG.info("Haku kesti {} ms. Hakukohteet: {}", (System.currentTimeMillis() - started), Arrays.toString(hakukohdeOids.toArray()));
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/haku/valintatapajono/hakijaryhmat")
    public List<HakijaryhmaValintatapajonoDTO> readByValintatapajonoOids(List<String> valintatapajonoOids) {
        if (valintatapajonoOids == null || valintatapajonoOids.isEmpty()) {
            LOG.error("Yritettiin hakea hakijaryhmia tyhjalla valintatapajono OID joukolla");
            throw new WebApplicationException(new RuntimeException("Yritettiin hakea hakijaryhmia valintatapajono hakukohde OID joukolla"), Response.Status.NOT_FOUND);
        }
        long started = System.currentTimeMillis();
        LOG.info("Haetaan valintatapajonoOid joukolla {}", Arrays.toString(valintatapajonoOids.toArray()));
        try {
            Map<String, List<HakijaryhmaValintatapajono>> valintatapajonoHakijaryhmaMap = hakijaryhmaValintatapajonoService.findHakijaryhmaByJonos(valintatapajonoOids).stream().collect(Collectors.groupingBy(hr -> hr.getValintatapajono().getOid() ));

            List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs = new ArrayList<>();
            valintatapajonoHakijaryhmaMap.keySet().forEach(hakukohdeOid -> {
                List<HakijaryhmaValintatapajono> jarjestetytHakijaryhmat = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoHakijaryhmaMap.get(hakukohdeOid));
                List<HakijaryhmaValintatapajonoDTO> valintapajononHakijaryhmaValintatapajonoDTOs = jarjestetytHakijaryhmat.stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
                IntStream.range(0, valintapajononHakijaryhmaValintatapajonoDTOs.size()).forEach(i -> {
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
            LOG.info("Haku kesti {} ms. Valintatapajonot: {}", (System.currentTimeMillis() - started), Arrays.toString(valintatapajonoOids.toArray()));
        }
    }

    private Function<ValintakoeDTO, ValintakoeDTO> lisaaSelvitettyTunniste(Map<String, String> tunnisteArvoPari, String hakukohdeOid) {
        return vk -> {
            if (Optional.ofNullable(vk.getTunniste()).orElse("").startsWith(HAKUKOHDE_VIITE_PREFIX)) {
                String tunniste = vk.getTunniste().replace(HAKUKOHDE_VIITE_PREFIX, "").replace("}}", "");
                if (!tunnisteArvoPari.containsKey(tunniste)) {
                    LOG.error("Tunnistetta {} ei voitu selvittää. Tämä oletettavasti johtuu puuttuvista valintaperusteista hakukohteelle {}", tunniste, hakukohdeOid);
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
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee hakukohteen valintakokeet OID:n perusteella", response = ValintakoeDTO.class)
    public List<ValintakoeDTO> valintakoesForHakukohde(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        HakukohdeViite viite = hakukohdeService.readByOid(oid);
        Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet = viite.getHakukohteenValintaperusteet();
        Map<String, String> tunnisteArvoPari = hakukohteenValintaperusteet.values().stream().collect(Collectors.toMap(t -> t.getTunniste(), t -> t.getArvo()));
        return modelMapper.mapList(valintakoeService
                .findValintakoesByValinnanVaihes(valinnanVaiheService.findByHakukohde(oid)), ValintakoeDTO.class).stream().map(
                lisaaSelvitettyTunniste(tunnisteArvoPari, oid)
        ).collect(Collectors.toList());
    }

    @Transactional
    @POST
    @Path("tunniste/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee valintakokeen OID:n perusteella", response = ValintakoeDTO.class)
    public List<ValintakoeDTO> readByTunnisteet(@ApiParam(value = "tunnisteet", required = true) List<String> tunnisteet) {
        return modelMapper.mapList(valintakoeService.readByTunnisteet(tunnisteet), ValintakoeDTO.class);
    }

    @GET
    @Path("hakukohde/haku/{hakuOid}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee haun hakukohteet", response = HakukohdeViiteDTO.class)
    public List<HakukohdeViiteDTO> haunHakukohteet(@ApiParam(value = "hakuOid", required = true) @PathParam("hakuOid") String hakuOid) {
        return modelMapper.mapList(hakukohdeService.haunHakukohteet(hakuOid), HakukohdeViiteDTO.class);
    }

    @GET
    @Path("hakukohde/{oid}/ilmanlaskentaa")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa", response = ValintatapajonoDTO.class)
    public List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(@PathParam("oid") String oid) {
        List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs = modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid), ValinnanVaiheJonoillaDTO.class);
        JononPrioriteettiAsettaja.filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(valinnanVaiheJonoillaDTOs);
        return valinnanVaiheJonoillaDTOs;
    }

    @GET
    @Path("hakukohde/avaimet/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
    public List<ValintaperusteDTO> findAvaimet(@ApiParam(value = "Hakukohde OID", required = true) @PathParam("oid") String oid) {
        return laskentakaavaService.findAvaimetForHakukohde(oid);
    }
    @POST
    @Path("hakukohde/avaimet")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
    public List<HakukohdeJaValintaperusteDTO> findAvaimet(@ApiParam(value = "Hakukohde OIDs", required = true) List<String> oids) {
        return oids.stream().map(oid -> new HakukohdeJaValintaperusteDTO(oid, laskentakaavaService.findAvaimetForHakukohde(oid))).collect(Collectors.toList());
    }
    @POST
    @Path("hakukohde/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee hakukohteen valintakokeet OID:n perusteella", response = ValintakoeDTO.class)
    public List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(List<String> oids) {
        List<HakukohdeViite> viites = hakukohdeService.readByOids(oids);
        Map<String, HakukohdeViite> viitteet = viites.stream().collect(Collectors.toMap(v -> v.getOid(), v -> v));
        return oids.stream()
                .map(oid -> {
                    Map<String, String> tunnisteArvoPari;
                    if (viitteet.containsKey(oid)) {
                        Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet = Optional.ofNullable(viitteet.get(oid).getHakukohteenValintaperusteet()).orElse(Collections.emptyMap());
                        tunnisteArvoPari = hakukohteenValintaperusteet.values().stream().collect(Collectors.toMap(t -> t.getTunniste(), t -> t.getArvo()));
                    } else {
                        tunnisteArvoPari = Collections.emptyMap();
                    }
                    List<ValintakoeDTO> valintakoeDtos = modelMapper.mapList(valintakoeService
                            .findValintakoesByValinnanVaihes(valinnanVaiheService.findByHakukohde(oid.toString())), ValintakoeDTO.class).stream()
                            .map(lisaaSelvitettyTunniste(tunnisteArvoPari, oid)).collect(Collectors.toList());
                    if (valintakoeDtos == null || valintakoeDtos.isEmpty()) {
                        return null;
                    }
                    return new HakukohdeJaValintakoeDTO(oid, valintakoeDtos);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @GET
    @Path("valintaperusteet/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee valintaperusteet")
    public List<ValintaperusteetDTO> haeValintaperusteet(
            @ApiParam(value = "Hakukohde OID") @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Valinnanvaiheen järjestysluku") @QueryParam("vaihe") Integer valinnanVaiheJarjestysluku) {
        HakuparametritDTO hakuparametrit = new HakuparametritDTO();
        hakuparametrit.setHakukohdeOid(hakukohdeOid);
        if (valinnanVaiheJarjestysluku != null) {
            hakuparametrit.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
        }
        List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);
        return valintaperusteService.haeValintaperusteet(list);
    }
    @POST
    @Path("valintaperusteet")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee valintaperusteet")
    public List<ValintaperusteetDTO> haeValintaperusteet(
            @ApiParam(value = "Hakukohde OIDs") List<String> hakukohdeOids) {
        return valintaperusteService.haeValintaperusteet(hakukohdeOids.stream().map(oid -> {
            HakuparametritDTO hakuparametritDTO = new HakuparametritDTO();
            hakuparametritDTO.setHakukohdeOid(oid);
            return hakuparametritDTO;
        }).collect(Collectors.toList()));
    }
    @POST
    @Path("valintaperusteet/tuoHakukohde")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "importoi hakukohde")
    public Response tuoHakukohde(@ApiParam(value = "Importoitava hakukohde") HakukohdeImportDTO hakukohde) {
        if (hakukohde == null) {
            LOG.error("Valintaperusteet sai null hakukohteen importoitavaksi!");
            throw new RuntimeException("Valintaperusteet sai null hakukohteen importoitavaksi!");
        }
        try {
            valintaperusteService.tuoHakukohde(hakukohde);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Hakukohteen importointi valintaperusteisiin epaonnistui! {}", hakukohde.getHakukohdeOid(), e);
            throw e;
        }
    }

    @GET
    @Path("valintaperusteet/hakijaryhma/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(@PathParam("hakukohdeOid") String hakukohdeOid) {
        StopWatch stopWatch = new StopWatch("Hakukohteen  " + hakukohdeOid + " hakijaryhmien haku valintalaskennalle");
        LOG.info("Haetaan hakijaryhmät hakukohteelle {}", hakukohdeOid);
        stopWatch.start("Haetaan hakukohteen hakijaryhmät");
        List<HakijaryhmaValintatapajono> hakukohteenRyhmat = hakijaryhmaValintatapajonoService.findByHakukohde(hakukohdeOid);
        stopWatch.stop();
        stopWatch.start("Haetaan valinnanvaiheet");
        List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        stopWatch.stop();
        vaiheet.stream().forEachOrdered(vaihe -> {
            stopWatch.start("Haetaan valintatapajonot vaiheelle: " + vaihe.getOid());
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
            stopWatch.stop();
            jonot.stream().forEachOrdered(jono -> {
                stopWatch.start("Haetaan hakijaryhmä valintatapajonolle: " + jono.getOid());
                hakukohteenRyhmat.addAll(hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(jono.getOid()));
                stopWatch.stop();
            });
        });
        List<ValintaperusteetHakijaryhmaDTO> result = new ArrayList<>();
        for (int i = 0; i < hakukohteenRyhmat.size(); i++) {
            HakijaryhmaValintatapajono original = hakukohteenRyhmat.get(i);
            stopWatch.start("Haketaan hakijaryhmän " + original.getHakijaryhma().getOid() + " laskentakaava");
            Laskentakaava laskentakaava = laskentakaavaService.haeLaskettavaKaava(original.getHakijaryhma().getLaskentakaava().getId(), Laskentamoodi.VALINTALASKENTA);
            stopWatch.stop();;
            ValintaperusteetHakijaryhmaDTO dto = modelMapper.map(original, ValintaperusteetHakijaryhmaDTO.class);
            // Asetetaan laskentakaavan nimi ensimmäisen funktiokutsun nimeksi
            laskentakaava.getFunktiokutsu().getSyoteparametrit().forEach(s -> {
                if (s.getAvain().equals("nimi")) {
                    s.setArvo(laskentakaava.getNimi());
                }
            });
            dto.setFunktiokutsu(modelMapper.map(laskentakaava.getFunktiokutsu(), ValintaperusteetFunktiokutsuDTO.class));
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
    @Path("hakukohde/{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Palauttaa valintatapajonot", response = ValintatapajonoDTO.class)
    public List<ValinnanVaiheJonoillaDTO> vaiheetJaJonot(@PathParam("oid") String oid) {
        return modelMapper.mapList(hakukohdeService.vaiheetJaJonot(oid), ValinnanVaiheJonoillaDTO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("valinnanvaihe/{oid}/hakukohteet")
    @ApiOperation(value = "Hakee oidit hakukohteille, jotka liittyvät valinnanvaiheeseen valintaryhmän kautta", response = ValinnanVaiheDTO.class)
    public Set<String> hakukohteet(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        if ("15658668992081958265865696209069".equals(oid)) {
            Set<String> res = Sets.newHashSet("1.2.246.562.20.888976339010", "1.2.246.562.20.514614370910", "1.2.246.562.20.99286336237", "1.2.246.562.20.19714446952", "1.2.246.562.20.89732770543", "1.2.246.562.20.55697662983", "1.2.246.562.20.36238791796", "1.2.246.562.20.94193179598", "1.2.246.562.20.236187613010", "1.2.246.562.20.34290785467", "1.2.246.562.20.76699432902", "1.2.246.562.20.69112348276", "1.2.246.562.20.24522544105", "1.2.246.562.20.92324077137", "1.2.246.562.20.39807545312", "1.2.246.562.20.80377398211", "1.2.246.562.20.94391979591", "1.2.246.562.20.67840538812", "1.2.246.562.20.24981093982", "1.2.246.562.20.87519172065", "1.2.246.562.20.58257442226", "1.2.246.562.20.63629312875", "1.2.246.562.20.96060484068", "1.2.246.562.20.32105952652", "1.2.246.562.20.92685283373", "1.2.246.562.20.26822688581", "1.2.246.562.20.51883023908", "1.2.246.562.20.439654371310", "1.2.246.562.20.53836090646", "1.2.246.562.20.45608950999", "1.2.246.562.20.65533683564", "1.2.246.562.20.37770438705", "1.2.246.562.20.56789659374", "1.2.246.562.20.23078586072", "1.2.246.562.20.68192990908", "1.2.246.562.20.46365054215", "1.2.246.562.20.77733645376", "1.2.246.562.20.98161033315", "1.2.246.562.20.87049420034", "1.2.246.562.20.60436647261", "1.2.246.562.20.58166360162", "1.2.246.562.20.50820092026", "1.2.246.562.20.46657321405", "1.2.246.562.20.11872076929", "1.2.246.562.20.54143375696", "1.2.246.562.20.42464979139", "1.2.246.562.20.48692521551", "1.2.246.562.20.62678246265", "1.2.246.562.20.51885690175", "1.2.246.562.20.81434339453", "1.2.246.562.20.225539789610", "1.2.246.562.20.36482951534", "1.2.246.562.20.39441326079", "1.2.246.562.20.86904350054", "1.2.246.562.20.86232469154", "1.2.246.562.20.31284681744", "1.2.246.562.20.474715414010", "1.2.246.562.20.49234356167", "1.2.246.562.20.39518364524", "1.2.246.562.20.80838756767", "1.2.246.562.20.11596387891", "1.2.246.562.20.11267757976", "1.2.246.562.20.606082127210", "1.2.246.562.20.66596519661", "1.2.246.562.20.72932910598", "1.2.246.562.20.692237378710", "1.2.246.562.20.58343521272", "1.2.246.562.20.91443536999", "1.2.246.562.20.63721517108", "1.2.246.562.20.94965447777", "1.2.246.562.20.10090272369", "1.2.246.562.20.26224815717", "1.2.246.562.20.22868361464", "1.2.246.562.20.730661360010", "1.2.246.562.20.41644536777", "1.2.246.562.20.60309063307", "1.2.246.562.20.69956950682", "1.2.246.562.20.46346985941", "1.2.246.562.20.49428200098", "1.2.246.562.20.61664078955", "1.2.246.562.20.59049631605", "1.2.246.562.20.82320889171", "1.2.246.562.20.58293230346", "1.2.246.562.20.44803996964", "1.2.246.562.20.42350873875", "1.2.246.562.20.14293354412", "1.2.246.562.20.258919983510", "1.2.246.562.20.23371226806", "1.2.246.562.20.571943016110", "1.2.246.562.20.53553442095", "1.2.246.562.20.68909819648", "1.2.246.562.20.734815931410", "1.2.246.562.20.30091081122", "1.2.246.562.20.35606374607", "1.2.246.562.20.25425233829", "1.2.246.562.20.174153733510", "1.2.246.562.20.71363959637", "1.2.246.562.20.64867044345", "1.2.246.562.20.44577407222", "1.2.246.562.20.81752507517", "1.2.246.562.20.96241665726", "1.2.246.562.20.37342718907", "1.2.246.562.20.70121085115", "1.2.246.562.20.43132096462", "1.2.246.562.20.193929019310", "1.2.246.562.20.31548620611", "1.2.246.562.20.59693461341", "1.2.246.562.20.308845034810", "1.2.246.562.20.88845072177", "1.2.246.562.20.52311020227", "1.2.246.562.20.52602064629", "1.2.246.562.20.877354727610", "1.2.246.562.20.58979335682", "1.2.246.562.20.345027021110", "1.2.246.562.20.34279244163", "1.2.246.562.20.46053174523", "1.2.246.562.20.56727333116", "1.2.246.562.20.24178811428", "1.2.246.562.20.94389220115", "1.2.246.562.20.77749747489", "1.2.246.562.20.91924248106", "1.2.246.562.20.31945739659", "1.2.246.562.20.15820093126", "1.2.246.562.20.32955061943", "1.2.246.562.20.23841887377", "1.2.246.562.20.98824757896", "1.2.246.562.20.83478188168", "1.2.246.562.20.56250138403", "1.2.246.562.20.37614335877", "1.2.246.562.20.25352003534", "1.2.246.562.20.69802962535", "1.2.246.562.20.13137411008", "1.2.246.562.20.74130038289", "1.2.246.562.20.30242836721", "1.2.246.562.20.55818204434", "1.2.246.562.20.53025089672", "1.2.246.562.20.37987218437", "1.2.246.562.20.19342972981", "1.2.246.562.20.55315622153", "1.2.246.562.20.94072528808", "1.2.246.562.20.89750116983", "1.2.246.562.20.50920715161", "1.2.246.562.20.97841212315", "1.2.246.562.20.36150386781", "1.2.246.562.20.48044949997", "1.2.246.562.20.28693679285", "1.2.246.562.20.61583720901", "1.2.246.562.20.53716525273", "1.2.246.562.20.93592624592", "1.2.246.562.20.22458138595", "1.2.246.562.20.32049658811", "1.2.246.562.20.86735986241", "1.2.246.562.20.756455050510", "1.2.246.562.20.31813242695", "1.2.246.562.20.64912493323", "1.2.246.562.20.12283714835", "1.2.246.562.20.60568549889", "1.2.246.562.20.95546499419", "1.2.246.562.20.54467228919", "1.2.246.562.20.39153197122", "1.2.246.562.20.27202616424", "1.2.246.562.20.91997018931", "1.2.246.562.20.81523807808", "1.2.246.562.20.69476943803", "1.2.246.562.20.51012227081", "1.2.246.562.20.40654986906", "1.2.246.562.20.63725248999", "1.2.246.562.20.30249072296", "1.2.246.562.20.40363460409", "1.2.246.562.20.84586571097", "1.2.246.562.20.90526477857", "1.2.246.562.20.759853487810", "1.2.246.562.20.18756409068", "1.2.246.562.20.817410315210", "1.2.246.562.20.385635424310", "1.2.246.562.20.96673705776", "1.2.246.562.20.87114628149", "1.2.246.562.20.70642327806", "1.2.246.562.20.32585183893", "1.2.246.562.20.21958266237", "1.2.246.562.20.79987073602", "1.2.246.562.20.81687208884", "1.2.246.562.20.63133464899", "1.2.246.562.20.30540846802", "1.2.246.562.20.45294207043", "1.2.246.562.20.37848810968", "1.2.246.562.20.60102561093", "1.2.246.562.20.454300521710", "1.2.246.562.20.53303336308", "1.2.246.562.20.68455237419", "1.2.246.562.20.40111526253", "1.2.246.562.20.18815562329", "1.2.246.562.20.64076112768", "1.2.246.562.20.70177400317", "1.2.246.562.20.55914732346", "1.2.246.562.20.865711869310", "1.2.246.562.20.28526597597", "1.2.246.562.20.58246521031", "1.2.246.562.20.47633156618", "1.2.246.562.20.67296709925", "1.2.246.562.20.23316441197", "1.2.246.562.20.43022774251", "1.2.246.562.20.19688530708", "1.2.246.562.20.43270851249", "1.2.246.562.20.27464522054", "1.2.246.562.20.89136767517", "1.2.246.562.20.24035879055", "1.2.246.562.20.36363013198", "1.2.246.562.20.35393145822", "1.2.246.562.20.12518404012", "1.2.246.562.20.16422697256", "1.2.246.562.20.23015834346", "1.2.246.562.20.40767887991", "1.2.246.562.20.374826718910", "1.2.246.562.20.83797094915", "1.2.246.562.20.22560572673", "1.2.246.562.20.67531255064", "1.2.246.562.20.838672039910", "1.2.246.562.20.85002671114", "1.2.246.562.20.42720846742", "1.2.246.562.20.59478640077", "1.2.246.562.20.127408570610", "1.2.246.562.20.43291571834", "1.2.246.562.20.66588527029", "1.2.246.562.20.22548654452", "1.2.246.562.20.62411258205", "1.2.246.562.20.77689559159", "1.2.246.562.20.54784179696", "1.2.246.562.20.36494490711", "1.2.246.562.20.90785797813", "1.2.246.562.20.13818134093", "1.2.246.562.20.55810114508", "1.2.246.562.20.37564809475", "1.2.246.562.20.15036906558", "1.2.246.562.20.82629429083", "1.2.246.562.20.67975638353", "1.2.246.562.20.473921603310", "1.2.246.562.20.79200428328", "1.2.246.562.20.20063090425", "1.2.246.562.20.52390892462", "1.2.246.562.20.49395493437", "1.2.246.562.20.487552872810", "1.2.246.562.20.10989471411", "1.2.246.562.20.22437106937", "1.2.246.562.20.71044192095", "1.2.246.562.20.78189461862", "1.2.246.562.20.321022742910", "1.2.246.562.20.10214431628", "1.2.246.562.20.86190100288", "1.2.246.562.20.36162915312", "1.2.246.562.20.44402657553", "1.2.246.562.20.21669331569", "1.2.246.562.20.56885938219", "1.2.246.562.20.72563088417", "1.2.246.562.20.99316539669", "1.2.246.562.20.73875663319", "1.2.246.562.20.23101884791", "1.2.246.562.20.71846864354", "1.2.246.562.20.92666213902", "1.2.246.562.20.59820997738", "1.2.246.562.20.51178253392", "1.2.246.562.20.17539543474", "1.2.246.562.20.62834777047", "1.2.246.562.20.33165410573", "1.2.246.562.20.50598999311", "1.2.246.562.20.65883366877", "1.2.246.562.20.75159680455", "1.2.246.562.20.34474711826", "1.2.246.562.20.342982355810", "1.2.246.562.20.43884347287", "1.2.246.562.20.75721458505", "1.2.246.562.20.31147465889", "1.2.246.562.20.563957318810", "1.2.246.562.20.14271192057", "1.2.246.562.20.42664451053", "1.2.246.562.20.30655027537", "1.2.246.562.20.17164123964", "1.2.246.562.20.22162976223", "1.2.246.562.20.10621224905", "1.2.246.562.20.41373863202", "1.2.246.562.20.91636646969", "1.2.246.562.20.49652384386", "1.2.246.562.20.81812046589", "1.2.246.562.20.828193742310", "1.2.246.562.20.95882603053", "1.2.246.562.20.50798309618", "1.2.246.562.20.27492713592", "1.2.246.562.20.90874229017", "1.2.246.562.20.24951790047", "1.2.246.562.20.40396804367", "1.2.246.562.20.18055950861", "1.2.246.562.20.54504484921", "1.2.246.562.20.45374940828", "1.2.246.562.20.80746742685", "1.2.246.562.20.99230970271", "1.2.246.562.20.48818034745", "1.2.246.562.20.76207792946", "1.2.246.562.20.50976531934", "1.2.246.562.20.28545243801", "1.2.246.562.20.30551653139", "1.2.246.562.20.90278836565", "1.2.246.562.20.17322154935", "1.2.246.562.20.35631559487", "1.2.246.562.20.32074857577", "1.2.246.562.20.495310850310", "1.2.246.562.20.94581285395", "1.2.246.562.20.13495347968", "1.2.246.562.20.28983202764", "1.2.246.562.20.70619381422", "1.2.246.562.20.545220877310", "1.2.246.562.20.18821084219", "1.2.246.562.20.29844222702", "1.2.246.562.20.77574455372", "1.2.246.562.20.67072544108", "1.2.246.562.20.95481098978", "1.2.246.562.20.21875334167", "1.2.246.562.20.39299861813", "1.2.246.562.20.58771947536", "1.2.246.562.20.568126439410", "1.2.246.562.20.440010459610", "1.2.246.562.20.67604399393", "1.2.246.562.20.46592859725", "1.2.246.562.20.77604561686", "1.2.246.562.20.29696445906", "1.2.246.562.20.744282430710", "1.2.246.562.20.50950817952", "1.2.246.562.20.74960405715", "1.2.246.562.20.64540461981", "1.2.246.562.20.17646107619", "1.2.246.562.20.44077352969", "1.2.246.562.20.75533831667", "1.2.246.562.20.45397022653", "1.2.246.562.20.410552123310", "1.2.246.562.20.79228020826", "1.2.246.562.20.97245389602", "1.2.246.562.20.50201351041", "1.2.246.562.20.11803649901", "1.2.246.562.20.45154854421", "1.2.246.562.20.85055422993", "1.2.246.562.20.23812871707", "1.2.246.562.20.16648052484", "1.2.246.562.20.17748072679", "1.2.246.562.20.44059652337", "1.2.246.562.20.63404773036", "1.2.246.562.20.13539732837", "1.2.246.562.20.87496016699", "1.2.246.562.20.60669960381", "1.2.246.562.20.65388543152", "1.2.246.562.20.13411578449", "1.2.246.562.20.44549024592", "1.2.246.562.20.71916806525", "1.2.246.562.20.624130170710", "1.2.246.562.20.24258796911", "1.2.246.562.20.59573689524", "1.2.246.562.20.45738194807", "1.2.246.562.20.10464858496", "1.2.246.562.20.31485186378", "1.2.246.562.20.96483458715", "1.2.246.562.20.45455534706", "1.2.246.562.20.76020919767", "1.2.246.562.20.66792447634", "1.2.246.562.20.52742311633", "1.2.246.562.20.20812577698", "1.2.246.562.20.44627296088", "1.2.246.562.20.92358980925", "1.2.246.562.20.83317213623", "1.2.246.562.20.48969086924", "1.2.246.562.20.15769979145", "1.2.246.562.20.27263111885", "1.2.246.562.20.51857651411", "1.2.246.562.20.64104372913", "1.2.246.562.20.78580427462", "1.2.246.562.20.68471907508", "1.2.246.562.20.47085437136", "1.2.246.562.20.307586348910", "1.2.246.562.20.94138687481", "1.2.246.562.20.66083574831", "1.2.246.562.20.65384213727", "1.2.246.562.20.16885318721", "1.2.246.562.20.38623340494", "1.2.246.562.20.770017927110", "1.2.246.562.20.38312632219", "1.2.246.562.20.24120290341", "1.2.246.562.20.714602855310", "1.2.246.562.20.869635329810", "1.2.246.562.20.76223251232", "1.2.246.562.20.28415373241", "1.2.246.562.20.82864868516", "1.2.246.562.20.45207046397", "1.2.246.562.20.25858037309", "1.2.246.562.20.39783304296", "1.2.246.562.20.44783478702", "1.2.246.562.20.45786831086", "1.2.246.562.20.24225951268", "1.2.246.562.20.82697362083", "1.2.246.562.20.25765855272", "1.2.246.562.20.90493655667", "1.2.246.562.20.24123632812", "1.2.246.562.20.364819642810", "1.2.246.562.20.35298184041", "1.2.246.562.20.93967863007", "1.2.246.562.20.50446575206", "1.2.246.562.20.97184653489", "1.2.246.562.20.96299260586", "1.2.246.562.20.53550063399", "1.2.246.562.20.92856692063", "1.2.246.562.20.91799784706", "1.2.246.562.20.49460215954", "1.2.246.562.20.97832539262", "1.2.246.562.20.266727607210", "1.2.246.562.20.86801677369", "1.2.246.562.20.87780961667", "1.2.246.562.20.49262275347", "1.2.246.562.20.46763264648", "1.2.246.562.20.59081900306", "1.2.246.562.20.12767497519", "1.2.246.562.20.97248570232", "1.2.246.562.20.48707846282", "1.2.246.562.20.915796823110", "1.2.246.562.20.32339637673", "1.2.246.562.20.15351832439", "1.2.246.562.20.52499664622", "1.2.246.562.20.61196785085", "1.2.246.562.20.60175052137", "1.2.246.562.20.30680471734", "1.2.246.562.20.139506902910", "1.2.246.562.20.60525505586", "1.2.246.562.20.23581770965", "1.2.246.562.20.82642159659", "1.2.246.562.20.83474401565", "1.2.246.562.20.79425176868", "1.2.246.562.20.19728701418", "1.2.246.562.20.75981958343", "1.2.246.562.20.13642718129", "1.2.246.562.20.78118175609", "1.2.246.562.20.49075456753", "1.2.246.562.20.44908986878", "1.2.246.562.20.72433074514", "1.2.246.562.20.85669522048", "1.2.246.562.20.30143638028", "1.2.246.562.20.563912317610", "1.2.246.562.20.44294729056", "1.2.246.562.20.72322960674", "1.2.246.562.20.27369185379", "1.2.246.562.20.93851709878", "1.2.246.562.20.28060193761", "1.2.246.562.20.96466321559", "1.2.246.562.20.14671757848", "1.2.246.562.20.42846232926", "1.2.246.562.20.22099868394", "1.2.246.562.20.72924879868", "1.2.246.562.20.78216826178", "1.2.246.562.20.69309139322", "1.2.246.562.20.86505193796", "1.2.246.562.20.12060097362", "1.2.246.562.20.65909563226", "1.2.246.562.20.64285079048", "1.2.246.562.20.84854582438", "1.2.246.562.20.95366823458", "1.2.246.562.20.27325268244", "1.2.246.562.20.32244856251", "1.2.246.562.20.22389285648", "1.2.246.562.20.51640229715", "1.2.246.562.20.60117726137", "1.2.246.562.20.64211737032", "1.2.246.562.20.23531871483", "1.2.246.562.20.79084367899", "1.2.246.562.20.64603204344", "1.2.246.562.20.51149445341", "1.2.246.562.20.59877471983", "1.2.246.562.20.43891318867", "1.2.246.562.20.34478015935", "1.2.246.562.20.33719449994", "1.2.246.562.20.94317175704", "1.2.246.562.20.27992290158", "1.2.246.562.20.35869272332", "1.2.246.562.20.17139158558", "1.2.246.562.20.60595181284", "1.2.246.562.20.33747278381", "1.2.246.562.20.47422385345", "1.2.246.562.20.17285471359", "1.2.246.562.20.31146088559", "1.2.246.562.20.79958906566", "1.2.246.562.20.45968402441", "1.2.246.562.20.74864838722", "1.2.246.562.20.99567723483", "1.2.246.562.20.62063549661", "1.2.246.562.20.46960981564", "1.2.246.562.20.77190124519", "1.2.246.562.20.48732886972", "1.2.246.562.20.17932130325", "1.2.246.562.20.56043263104", "1.2.246.562.20.42511516637", "1.2.246.562.20.56111446292", "1.2.246.562.20.11548363891", "1.2.246.562.20.78972726331", "1.2.246.562.20.12547713946", "1.2.246.562.20.50141157466", "1.2.246.562.20.20104382583", "1.2.246.562.20.58763283601", "1.2.246.562.20.40317552343", "1.2.246.562.20.32627445342", "1.2.246.562.20.78756923047", "1.2.246.562.20.21647322631", "1.2.246.562.20.37128407675", "1.2.246.562.20.77631369772", "1.2.246.562.20.14560110703", "1.2.246.562.20.37875054746", "1.2.246.562.20.69249514063", "1.2.246.562.20.91790652159", "1.2.246.562.20.88932662104", "1.2.246.562.20.40964388991", "1.2.246.562.20.88741880616", "1.2.246.562.20.13116684344", "1.2.246.562.20.51317293333", "1.2.246.562.20.434445349610", "1.2.246.562.20.54811262663", "1.2.246.562.20.58882080706", "1.2.246.562.20.18962166221", "1.2.246.562.20.38943600783", "1.2.246.562.20.62635272859", "1.2.246.562.20.41153148915", "1.2.246.562.20.18445879218", "1.2.246.562.20.53809707564", "1.2.246.562.20.55024551453", "1.2.246.562.20.38943109207", "1.2.246.562.20.31495434115", "1.2.246.562.20.66529355175", "1.2.246.562.20.67650322513", "1.2.246.562.20.87681906191", "1.2.246.562.20.55683086627", "1.2.246.562.20.621491364010", "1.2.246.562.20.15028216511", "1.2.246.562.20.74389126603", "1.2.246.562.20.20566064059", "1.2.246.562.20.95848090254", "1.2.246.562.20.44957145905", "1.2.246.562.20.62408195782", "1.2.246.562.20.31439319463", "1.2.246.562.20.20203533763", "1.2.246.562.20.938899885110", "1.2.246.562.20.43010576031", "1.2.246.562.20.18556222802", "1.2.246.562.20.11249084507", "1.2.246.562.20.749383964010", "1.2.246.562.20.58854963095", "1.2.246.562.20.85697859099", "1.2.246.562.20.96884323799", "1.2.246.562.20.70041177018", "1.2.246.562.20.599504746810", "1.2.246.562.20.41495624433", "1.2.246.562.20.85631468189", "1.2.246.562.20.55390447724", "1.2.246.562.20.31807553834", "1.2.246.562.20.52415670489", "1.2.246.562.20.88586147469", "1.2.246.562.20.24109685867", "1.2.246.562.20.60860714181", "1.2.246.562.20.38153110648", "1.2.246.562.20.14400840311", "1.2.246.562.20.14452683056", "1.2.246.562.20.83364480901", "1.2.246.562.20.70542750081", "1.2.246.562.20.29425203899", "1.2.246.562.20.20184551487", "1.2.246.562.20.13094960831", "1.2.246.562.20.13283668245", "1.2.246.562.20.90799104212", "1.2.246.562.20.93919371612", "1.2.246.562.20.62316231547", "1.2.246.562.20.67475968318", "1.2.246.562.20.19076950807", "1.2.246.562.20.61559614583", "1.2.246.562.20.58441467105", "1.2.246.562.20.41908899721", "1.2.246.562.20.27572071739", "1.2.246.562.20.63697893772", "1.2.246.562.20.30162471996", "1.2.246.562.20.85844645854", "1.2.246.562.20.17387181518", "1.2.246.562.20.794329424510", "1.2.246.562.20.24012398285", "1.2.246.562.20.659999133810", "1.2.246.562.20.77542890965", "1.2.246.562.20.35584330266", "1.2.246.562.20.31280295556", "1.2.246.562.20.92913635402", "1.2.246.562.20.24024622961", "1.2.246.562.20.55666108265", "1.2.246.562.20.93725702174", "1.2.246.562.20.82911332573", "1.2.246.562.20.40755601768", "1.2.246.562.20.68972223942", "1.2.246.562.20.79898986249", "1.2.246.562.20.30078691323", "1.2.246.562.20.27356406085", "1.2.246.562.20.86810204262", "1.2.246.562.20.714441172710", "1.2.246.562.20.29241751774", "1.2.246.562.20.93566041609", "1.2.246.562.20.13069823675", "1.2.246.562.20.56394806543", "1.2.246.562.20.43953637543", "1.2.246.562.20.26761913537", "1.2.246.562.20.57545417792", "1.2.246.562.20.497163467110", "1.2.246.562.20.85642141759", "1.2.246.562.20.69941761525", "1.2.246.562.20.64438057602", "1.2.246.562.20.87671498464", "1.2.246.562.20.37110452432", "1.2.246.562.20.270607452410", "1.2.246.562.20.926345960410", "1.2.246.562.20.29106305534", "1.2.246.562.20.83454931029", "1.2.246.562.20.66650047047", "1.2.246.562.20.77167324423", "1.2.246.562.20.120034279410", "1.2.246.562.20.99439488497", "1.2.246.562.20.79073467759", "1.2.246.562.20.97511234425", "1.2.246.562.20.16936255526", "1.2.246.562.20.43740441977", "1.2.246.562.20.56205643051", "1.2.246.562.20.67640020821", "1.2.246.562.20.49371093474", "1.2.246.562.20.14174609561", "1.2.246.562.20.76947802153", "1.2.246.562.20.84904472457", "1.2.246.562.20.59588714358", "1.2.246.562.20.91263797549", "1.2.246.562.20.81018334326", "1.2.246.562.20.22112601616", "1.2.246.562.20.41512225271", "1.2.246.562.20.267995898210", "1.2.246.562.20.41396016374", "1.2.246.562.20.43323820522", "1.2.246.562.20.19689775088", "1.2.246.562.20.11348976496", "1.2.246.562.20.80019514357", "1.2.246.562.20.38526042239", "1.2.246.562.20.98545001541", "1.2.246.562.20.87756396682", "1.2.246.562.20.14621421164", "1.2.246.562.20.16291737183", "1.2.246.562.20.25022790676", "1.2.246.562.20.14428065545", "1.2.246.562.20.45984795356", "1.2.246.562.20.28924015242", "1.2.246.562.20.39700753906", "1.2.246.562.20.59230214948", "1.2.246.562.20.81197658545", "1.2.246.562.20.91352432974", "1.2.246.562.20.48904118758", "1.2.246.562.20.96913361513", "1.2.246.562.20.24176296838", "1.2.246.562.20.600065259710", "1.2.246.562.20.34234457355", "1.2.246.562.20.13765770949", "1.2.246.562.20.91223331504", "1.2.246.562.20.107938245410", "1.2.246.562.20.97783930353", "1.2.246.562.20.27083799273", "1.2.246.562.20.62049846553", "1.2.246.562.20.59574295115", "1.2.246.562.20.75375500206", "1.2.246.562.20.26121753466", "1.2.246.562.20.37048333589", "1.2.246.562.20.84779000789", "1.2.246.562.20.66094749489", "1.2.246.562.20.174989158210", "1.2.246.562.20.18138123882", "1.2.246.562.20.92125956836", "1.2.246.562.20.32393676583", "1.2.246.562.20.969615087010", "1.2.246.562.20.37804037309", "1.2.246.562.20.70734200291", "1.2.246.562.20.12618908805", "1.2.246.562.20.917630687310", "1.2.246.562.20.20923052581", "1.2.246.562.20.59858781269", "1.2.246.562.20.41519927549", "1.2.246.562.20.219903655010", "1.2.246.562.20.41370939468", "1.2.246.562.20.68208862026", "1.2.246.562.20.82554304251", "1.2.246.562.20.30469617101", "1.2.246.562.20.45313795804", "1.2.246.562.20.80084568056", "1.2.246.562.20.36115740973", "1.2.246.562.20.97793211356", "1.2.246.562.20.16888214083", "1.2.246.562.20.88828060827", "1.2.246.562.20.290243717910", "1.2.246.562.20.57750573214", "1.2.246.562.20.958769864810", "1.2.246.562.20.68894849147", "1.2.246.562.20.46429186003", "1.2.246.562.20.456168598110", "1.2.246.562.20.25587413284", "1.2.246.562.20.63065644419", "1.2.246.562.20.14283209113", "1.2.246.562.20.31221020444", "1.2.246.562.20.12546277307", "1.2.246.562.20.31585965606", "1.2.246.562.20.26011913584", "1.2.246.562.20.95705960276", "1.2.246.562.20.49577637199", "1.2.246.562.20.365268160410", "1.2.246.562.20.243277686110", "1.2.246.562.20.74787527857", "1.2.246.562.20.98716067958", "1.2.246.562.20.58084870714", "1.2.246.562.20.68830188865", "1.2.246.562.20.74389484168", "1.2.246.562.20.94707253897", "1.2.246.562.20.26284860785", "1.2.246.562.20.372193657910", "1.2.246.562.20.12952808728", "1.2.246.562.20.37315593675", "1.2.246.562.20.30624172828", "1.2.246.562.20.68415417424", "1.2.246.562.20.74864213843", "1.2.246.562.20.44557334123", "1.2.246.562.20.46718136578", "1.2.246.562.20.18647668005", "1.2.246.562.20.45079063918", "1.2.246.562.20.12589599427", "1.2.246.562.20.35909213056", "1.2.246.562.20.77565776729", "1.2.246.562.20.30160414451", "1.2.246.562.20.70141119589", "1.2.246.562.20.77507181962", "1.2.246.562.20.90263422082", "1.2.246.562.20.76370464448", "1.2.246.562.20.60601819695", "1.2.246.562.20.17154339133", "1.2.246.562.20.26517382854", "1.2.246.562.20.599031000110", "1.2.246.562.20.12207649307", "1.2.246.562.20.51500811032", "1.2.246.562.20.85220915269", "1.2.246.562.20.77752936011", "1.2.246.562.20.70583489433", "1.2.246.562.20.19187057542", "1.2.246.562.20.59341964738", "1.2.246.562.20.87658852246", "1.2.246.562.20.83263781847", "1.2.246.562.20.12652863445", "1.2.246.562.20.38093883403", "1.2.246.562.20.94567000925", "1.2.246.562.20.77070247703", "1.2.246.562.20.16538933462", "1.2.246.562.20.23677239057", "1.2.246.562.20.54332879672", "1.2.246.562.20.94708890424", "1.2.246.562.20.75074131055", "1.2.246.562.20.56403781778", "1.2.246.562.20.54498010369", "1.2.246.562.20.82009514959", "1.2.246.562.20.17075735138", "1.2.246.562.20.12335885286", "1.2.246.562.20.76667706865", "1.2.246.562.20.49244261738", "1.2.246.562.20.25021477218", "1.2.246.562.20.94047640001", "1.2.246.562.20.73888779862", "1.2.246.562.20.84699699868", "1.2.246.562.20.55249730445", "1.2.246.562.20.28524264497", "1.2.246.562.20.815035172610", "1.2.246.562.20.12442752072", "1.2.246.562.20.81151387982", "1.2.246.562.20.55431632045", "1.2.246.562.20.80041469703", "1.2.246.562.20.71369382586", "1.2.246.562.20.94386537164", "1.2.246.562.20.45965117588", "1.2.246.562.20.80906052842", "1.2.246.562.20.18724751997", "1.2.246.562.20.91516335857", "1.2.246.562.20.48441976122", "1.2.246.562.20.16547244232", "1.2.246.562.20.43496865387", "1.2.246.562.20.35821694003", "1.2.246.562.20.77755061379", "1.2.246.562.20.99770632893", "1.2.246.562.20.76623611787", "1.2.246.562.20.75715489037", "1.2.246.562.20.62850827445", "1.2.246.562.20.28074782039", "1.2.246.562.20.23187243003", "1.2.246.562.20.719539463910", "1.2.246.562.20.98516151416", "1.2.246.562.20.93449615249", "1.2.246.562.20.71211555587", "1.2.246.562.20.63151373014", "1.2.246.562.20.28607215689", "1.2.246.562.20.98625140102", "1.2.246.562.20.57773109566", "1.2.246.562.20.794377423510", "1.2.246.562.20.87329764325", "1.2.246.562.20.264626273610", "1.2.246.562.20.62793426792", "1.2.246.562.20.700400641010", "1.2.246.562.20.14642434162", "1.2.246.562.20.20256875814", "1.2.246.562.20.26913033994", "1.2.246.562.20.65727428545", "1.2.246.562.20.27297264429", "1.2.246.562.20.42344751088", "1.2.246.562.20.30253267576", "1.2.246.562.20.64625472051", "1.2.246.562.20.70050222036", "1.2.246.562.20.34034752245", "1.2.246.562.20.18471338873", "1.2.246.562.20.527846613910", "1.2.246.562.20.29110456216", "1.2.246.562.20.40792449742", "1.2.246.562.20.532560584210", "1.2.246.562.20.42941508295", "1.2.246.562.20.56437504071", "1.2.246.562.20.21807437732", "1.2.246.562.20.79284611344", "1.2.246.562.20.81412106072", "1.2.246.562.20.67067976331", "1.2.246.562.20.34043565547", "1.2.246.562.20.20952581468", "1.2.246.562.20.72835435743", "1.2.246.562.20.30546744639", "1.2.246.562.20.66882980916", "1.2.246.562.20.66460649859", "1.2.246.562.20.67917123324", "1.2.246.562.20.385682310710", "1.2.246.562.20.81668539166", "1.2.246.562.20.32404281134", "1.2.246.562.20.64204425496", "1.2.246.562.20.50805977282", "1.2.246.562.20.760635296310", "1.2.246.562.20.68472892998", "1.2.246.562.20.20818145383", "1.2.246.562.20.54430878675", "1.2.246.562.20.54614744396", "1.2.246.562.20.39981770082", "1.2.246.562.20.38895243913", "1.2.246.562.20.57520197658", "1.2.246.562.20.28845417907", "1.2.246.562.20.87905129661", "1.2.246.562.20.19115504565", "1.2.246.562.20.28953600765", "1.2.246.562.20.16143022545", "1.2.246.562.20.68619088211", "1.2.246.562.20.52130525851", "1.2.246.562.20.85893505942", "1.2.246.562.20.34452387813", "1.2.246.562.20.736646181110", "1.2.246.562.20.93228945924", "1.2.246.562.20.42467272565", "1.2.246.562.20.55731695312", "1.2.246.562.20.667817241010", "1.2.246.562.20.14033702391", "1.2.246.562.20.72452955346", "1.2.246.562.20.45914023093", "1.2.246.562.20.57094939675", "1.2.246.562.20.41443296912", "1.2.246.562.20.30887008834", "1.2.246.562.20.12368312026", "1.2.246.562.20.94090162598", "1.2.246.562.20.75638282378", "1.2.246.562.20.46108729651", "1.2.246.562.20.59161103797", "1.2.246.562.20.15705125498", "1.2.246.562.20.21302032301", "1.2.246.562.20.48292003075", "1.2.246.562.20.16197981166", "1.2.246.562.20.83938036825", "1.2.246.562.20.169754244710", "1.2.246.562.20.77879972634", "1.2.246.562.20.74911871023", "1.2.246.562.20.27494421897", "1.2.246.562.20.97002389357", "1.2.246.562.20.76330675513", "1.2.246.562.20.81988969069", "1.2.246.562.20.59388645448", "1.2.246.562.20.42448762049", "1.2.246.562.20.221409898510", "1.2.246.562.20.80940695135", "1.2.246.562.20.96581101001", "1.2.246.562.20.96319247613", "1.2.246.562.20.96417790431", "1.2.246.562.20.97739999601", "1.2.246.562.20.87505983111", "1.2.246.562.20.707988752110", "1.2.246.562.20.67233140465", "1.2.246.562.20.38245444179", "1.2.246.562.20.60073220539", "1.2.246.562.20.771023327710", "1.2.246.562.20.626004172410", "1.2.246.562.20.826315665010", "1.2.246.562.20.73174385143", "1.2.246.562.20.91888723946", "1.2.246.562.20.11342326441", "1.2.246.562.20.16311446872", "1.2.246.562.20.10465628658", "1.2.246.562.20.37099896757", "1.2.246.562.20.99442324234", "1.2.246.562.20.31981863959", "1.2.246.562.20.77871331433", "1.2.246.562.20.14994036037", "1.2.246.562.20.42641018975", "1.2.246.562.20.58143720371", "1.2.246.562.20.16477047209", "1.2.246.562.20.952049325610", "1.2.246.562.20.32242627282", "1.2.246.562.20.48313741018", "1.2.246.562.20.49994381967", "1.2.246.562.20.56750830186", "1.2.246.562.20.73459179906", "1.2.246.562.20.72678209999", "1.2.246.562.20.85129931524", "1.2.246.562.20.41070881739", "1.2.246.562.20.61389105737", "1.2.246.562.20.324335749710", "1.2.246.562.20.50741006584", "1.2.246.562.20.33666068169", "1.2.246.562.20.74247800696", "1.2.246.562.20.97984940136", "1.2.246.562.20.94435007078", "1.2.246.562.20.91446306341", "1.2.246.562.20.79335189365", "1.2.246.562.20.863150619110", "1.2.246.562.20.94407992695", "1.2.246.562.20.43347090201", "1.2.246.562.20.75299216392", "1.2.246.562.20.24061033908", "1.2.246.562.20.39288288457", "1.2.246.562.20.12333444136", "1.2.246.562.20.48955800734", "1.2.246.562.20.61354373054", "1.2.246.562.20.72317309069", "1.2.246.562.20.96122211137", "1.2.246.562.20.39843103795", "1.2.246.562.20.45312531911", "1.2.246.562.20.24326595832", "1.2.246.562.20.32004472588", "1.2.246.562.20.918436840510", "1.2.246.562.20.91120853048", "1.2.246.562.20.54173485928", "1.2.246.562.20.81066922592", "1.2.246.562.20.152123951710", "1.2.246.562.20.68018078466", "1.2.246.562.20.16670184668", "1.2.246.562.20.53405122168", "1.2.246.562.20.88281432158", "1.2.246.562.20.35948023391", "1.2.246.562.20.44463962413", "1.2.246.562.20.98853525354", "1.2.246.562.20.162740601410", "1.2.246.562.20.34017962209", "1.2.246.562.20.626742109510", "1.2.246.562.20.52651095904", "1.2.246.562.20.97966170926", "1.2.246.562.20.85681642352", "1.2.246.562.20.73685077668", "1.2.246.562.20.212784176510", "1.2.246.562.20.20925707166", "1.2.246.562.20.91346436263", "1.2.246.562.20.23142574989", "1.2.246.562.20.82844722163", "1.2.246.562.20.80060380302", "1.2.246.562.20.66460345235", "1.2.246.562.20.28134726729", "1.2.246.562.20.131575284010", "1.2.246.562.20.100243209210", "1.2.246.562.20.17230518015", "1.2.246.562.20.85732657607", "1.2.246.562.20.78160947822", "1.2.246.562.20.43821681772", "1.2.246.562.20.43417129947", "1.2.246.562.20.61621175647", "1.2.246.562.20.55567192038", "1.2.246.562.20.84631341702", "1.2.246.562.20.366650624310", "1.2.246.562.20.30680164511", "1.2.246.562.20.99076391574", "1.2.246.562.20.51653327273", "1.2.246.562.20.87933624998", "1.2.246.562.20.35055486134", "1.2.246.562.20.83280582467", "1.2.246.562.20.79756206912", "1.2.246.562.20.44202631287", "1.2.246.562.20.97797775133", "1.2.246.562.20.968689443710", "1.2.246.562.20.61561689961", "1.2.246.562.20.94815694081", "1.2.246.562.20.53847060729", "1.2.246.562.20.46859836184", "1.2.246.562.20.46469191533", "1.2.246.562.20.94511755798", "1.2.246.562.20.18972230405", "1.2.246.562.20.64733124404", "1.2.246.562.20.70823899151", "1.2.246.562.20.15581181896", "1.2.246.562.20.58812057972", "1.2.246.562.20.59824905901", "1.2.246.562.20.57439701525", "1.2.246.562.20.17940019874", "1.2.246.562.20.84413257092", "1.2.246.562.20.668392523110", "1.2.246.562.20.37819890262", "1.2.246.562.20.32301719079", "1.2.246.562.20.92146071136", "1.2.246.562.20.463772422210", "1.2.246.562.20.73918594091", "1.2.246.562.20.14671982719", "1.2.246.562.20.20745495737", "1.2.246.562.20.33106223416", "1.2.246.562.20.85057354244", "1.2.246.562.20.89693914015", "1.2.246.562.20.73995676324", "1.2.246.562.20.50379738839", "1.2.246.562.20.81920066768", "1.2.246.562.20.24858082977", "1.2.246.562.20.74033099644", "1.2.246.562.20.48700264525", "1.2.246.562.20.82392443865", "1.2.246.562.20.85215430878", "1.2.246.562.20.35758279026", "1.2.246.562.20.42205014717", "1.2.246.562.20.78394668517", "1.2.246.562.20.60721133852", "1.2.246.562.20.55329268126", "1.2.246.562.20.15378526246", "1.2.246.562.20.96416420469", "1.2.246.562.20.11981697516", "1.2.246.562.20.62529746703", "1.2.246.562.20.253894038310", "1.2.246.562.20.90354345911", "1.2.246.562.20.53412187808", "1.2.246.562.20.59304791813", "1.2.246.562.20.96657384283", "1.2.246.562.20.61694526239", "1.2.246.562.20.43571779404", "1.2.246.562.20.49928130977", "1.2.246.562.20.74619827905", "1.2.246.562.20.98503472544", "1.2.246.562.20.85448511767", "1.2.246.562.20.944318251510", "1.2.246.562.20.69071836055", "1.2.246.562.20.51096055259", "1.2.246.562.20.90879060375", "1.2.246.562.20.40056841683", "1.2.246.562.20.19057192403", "1.2.246.562.20.607983642410", "1.2.246.562.20.98202668749", "1.2.246.562.20.62468091681", "1.2.246.562.20.960294663710", "1.2.246.562.20.34228166327", "1.2.246.562.20.95964746382", "1.2.246.562.20.64976651147", "1.2.246.562.20.14585074157", "1.2.246.562.20.82260201312", "1.2.246.562.20.31154249085", "1.2.246.562.20.76096478177", "1.2.246.562.20.18349321457", "1.2.246.562.20.65711265432", "1.2.246.562.20.38714632254", "1.2.246.562.20.44355886209", "1.2.246.562.20.297859715810", "1.2.246.562.20.47249937948", "1.2.246.562.20.21335682528", "1.2.246.562.20.994559054410", "1.2.246.562.20.27824128923", "1.2.246.562.20.52703856154", "1.2.246.562.20.14178739729", "1.2.246.562.20.66913796596", "1.2.246.562.20.34424256872", "1.2.246.562.20.149819803210", "1.2.246.562.20.95703989798", "1.2.246.562.20.73361373613", "1.2.246.562.20.66073256654", "1.2.246.562.20.53103450804", "1.2.246.562.20.82283286761", "1.2.246.562.20.14805823029", "1.2.246.562.20.52556854231", "1.2.246.562.20.90142514588", "1.2.246.562.20.39352534711", "1.2.246.562.20.99580079434", "1.2.246.562.20.21229006438", "1.2.246.562.20.65286658854", "1.2.246.562.20.60232838148", "1.2.246.562.20.21118610381", "1.2.246.562.20.61599498799", "1.2.246.562.20.53036030558", "1.2.246.562.20.14063215314", "1.2.246.562.20.87019308292", "1.2.246.562.20.36608665943", "1.2.246.562.20.66279549878", "1.2.246.562.20.97881912436", "1.2.246.562.20.773619653110", "1.2.246.562.20.99411580121", "1.2.246.562.20.77202885156", "1.2.246.562.20.61805133669", "1.2.246.562.20.364804250110", "1.2.246.562.20.765886428510", "1.2.246.562.20.34024075813", "1.2.246.562.20.32675920139", "1.2.246.562.20.42654486937", "1.2.246.562.20.602980522210", "1.2.246.562.20.866822362110", "1.2.246.562.20.43201781077", "1.2.246.562.20.931956521810", "1.2.246.562.20.48503922824", "1.2.246.562.20.799936678510", "1.2.246.562.20.39846775565", "1.2.246.562.20.926783116610", "1.2.246.562.20.52155727739", "1.2.246.562.20.652840958110", "1.2.246.562.20.918290575610", "1.2.246.562.20.82611288703", "1.2.246.562.20.86828153899", "1.2.246.562.20.82438373456", "1.2.246.562.20.528549963210", "1.2.246.562.20.73377392425", "1.2.246.562.20.94408931148", "1.2.246.562.20.74154681493", "1.2.246.562.20.32162247137", "1.2.246.562.20.36425334889", "1.2.246.562.20.61777747108", "1.2.246.562.20.47256855512", "1.2.246.562.20.64011852585", "1.2.246.562.20.77856054034", "1.2.246.562.20.63315529469", "1.2.246.562.20.48109086891", "1.2.246.562.20.85438880289", "1.2.246.562.20.42252993029", "1.2.246.562.20.73102001697", "1.2.246.562.20.86173579391", "1.2.246.562.20.41881740165", "1.2.246.562.20.24584318919", "1.2.246.562.20.94231781322", "1.2.246.562.20.18922362183", "1.2.246.562.20.37561547755", "1.2.246.562.20.79824567774", "1.2.246.562.20.93783748028", "1.2.246.562.20.65375259614", "1.2.246.562.20.66900780835", "1.2.246.562.20.70354139894", "1.2.246.562.20.30511191824", "1.2.246.562.20.19219651403", "1.2.246.562.20.74142140511", "1.2.246.562.20.67388518937", "1.2.246.562.20.73106785055", "1.2.246.562.20.42426379766", "1.2.246.562.20.25055647033", "1.2.246.562.20.63802715076", "1.2.246.562.20.16138343476", "1.2.246.562.20.81399346378", "1.2.246.562.20.73097991498", "1.2.246.562.20.39340836814", "1.2.246.562.20.72186461603", "1.2.246.562.20.95339314585", "1.2.246.562.20.42923057238", "1.2.246.562.20.212181787510", "1.2.246.562.20.93063698224", "1.2.246.562.20.65890487724", "1.2.246.562.20.16183363791", "1.2.246.562.20.51932769212", "1.2.246.562.20.66023957781", "1.2.246.562.20.17802234207", "1.2.246.562.20.11117119872", "1.2.246.562.20.68268636039", "1.2.246.562.20.59558260978", "1.2.246.562.20.56409541788", "1.2.246.562.20.71037075653", "1.2.246.562.20.20114294158", "1.2.246.562.20.12366915499", "1.2.246.562.20.71425644383", "1.2.246.562.20.63905877963", "1.2.246.562.20.87839892046", "1.2.246.562.20.72085953676", "1.2.246.562.20.17156108486", "1.2.246.562.20.239305598210", "1.2.246.562.20.571557003710", "1.2.246.562.20.14532047793", "1.2.246.562.20.67037682019", "1.2.246.562.20.80903287367", "1.2.246.562.20.18355547417", "1.2.246.562.20.16575114281", "1.2.246.562.20.890611480310", "1.2.246.562.20.28920752392", "1.2.246.562.20.58356462794", "1.2.246.562.20.30882652735", "1.2.246.562.20.11004775349", "1.2.246.562.20.80888789593", "1.2.246.562.20.96958111989", "1.2.246.562.20.66544141444", "1.2.246.562.20.69475625138", "1.2.246.562.20.29960832961", "1.2.246.562.20.13106894705", "1.2.246.562.20.44166462018", "1.2.246.562.20.98810084446", "1.2.246.562.20.37129772341", "1.2.246.562.20.62602562258", "1.2.246.562.20.30516106777", "1.2.246.562.20.85144405835", "1.2.246.562.20.50298245775", "1.2.246.562.20.58028594669", "1.2.246.562.20.771981326710", "1.2.246.562.20.95061517025", "1.2.246.562.20.28434202736", "1.2.246.562.20.66491656022", "1.2.246.562.20.68929672209", "1.2.246.562.20.79193251091", "1.2.246.562.20.83787507082", "1.2.246.562.20.26310522508", "1.2.246.562.20.79481545121", "1.2.246.562.20.66487574023", "1.2.246.562.20.62057631566", "1.2.246.562.20.38131460552", "1.2.246.562.20.89906293512", "1.2.246.562.20.519299073210", "1.2.246.562.20.16522000989", "1.2.246.562.20.34548685584", "1.2.246.562.20.32612170327", "1.2.246.562.20.92495781335", "1.2.246.562.20.56037804558", "1.2.246.562.20.24056771405", "1.2.246.562.20.72640588355", "1.2.246.562.20.61715233195", "1.2.246.562.20.76712604131", "1.2.246.562.20.61011034198", "1.2.246.562.20.73387685404", "1.2.246.562.20.49062692899", "1.2.246.562.20.47663495183", "1.2.246.562.20.82146114739", "1.2.246.562.20.29925948449", "1.2.246.562.20.49433837918", "1.2.246.562.20.82671077194", "1.2.246.562.20.52171871545", "1.2.246.562.20.790746589510", "1.2.246.562.20.13186730142", "1.2.246.562.20.18349607491", "1.2.246.562.20.52136729978", "1.2.246.562.20.79148792579", "1.2.246.562.20.32245048296", "1.2.246.562.20.13892303127", "1.2.246.562.20.32831241305", "1.2.246.562.20.80981315239", "1.2.246.562.20.24963088604", "1.2.246.562.20.87674184384", "1.2.246.562.20.19371322967", "1.2.246.562.20.39804210605", "1.2.246.562.20.70265474141", "1.2.246.562.20.31852232797", "1.2.246.562.20.21848519456", "1.2.246.562.20.70771231217", "1.2.246.562.20.90770703594", "1.2.246.562.20.47704376996", "1.2.246.562.20.35432479984", "1.2.246.562.20.91362724162", "1.2.246.562.20.319393378910", "1.2.246.562.20.61372556581", "1.2.246.562.20.18072530423", "1.2.246.562.20.11959520617", "1.2.246.562.20.62869187872", "1.2.246.562.20.35025874237", "1.2.246.562.20.718046393510", "1.2.246.562.20.30364207362", "1.2.246.562.20.21648443592", "1.2.246.562.20.61070004249", "1.2.246.562.20.50659985506", "1.2.246.562.20.350062351510", "1.2.246.562.20.73089016817", "1.2.246.562.20.22391617531", "1.2.246.562.20.90178857562", "1.2.246.562.20.82854388137", "1.2.246.562.20.34684226078", "1.2.246.562.20.13259302803", "1.2.246.562.20.97664097686", "1.2.246.562.20.69552279817", "1.2.246.562.20.40674075821", "1.2.246.562.20.45845601793", "1.2.246.562.20.67444342751", "1.2.246.562.20.17037860032", "1.2.246.562.20.50729612411", "1.2.246.562.20.99254250832", "1.2.246.562.20.94114033874", "1.2.246.562.20.14224068461", "1.2.246.562.20.33536012843", "1.2.246.562.20.71227724086", "1.2.246.562.20.73688308771", "1.2.246.562.20.88439314027", "1.2.246.562.20.64555393061", "1.2.246.562.20.64614044085", "1.2.246.562.20.51681534387", "1.2.246.562.20.21694926581", "1.2.246.562.20.216229191210", "1.2.246.562.20.743491195410", "1.2.246.562.20.30270873927", "1.2.246.562.20.20736737709", "1.2.246.562.20.54220353681", "1.2.246.562.20.51087915263", "1.2.246.562.20.45686225009", "1.2.246.562.20.42344321171", "1.2.246.562.20.96263893073", "1.2.246.562.20.31043640908", "1.2.246.562.20.93368659814", "1.2.246.562.20.89885683683", "1.2.246.562.20.22954256065", "1.2.246.562.20.61092266784", "1.2.246.562.20.35596396941", "1.2.246.562.20.73379594705", "1.2.246.562.20.81085200202", "1.2.246.562.20.90668014831", "1.2.246.562.20.69850505343", "1.2.246.562.20.97570518767", "1.2.246.562.20.34773644991", "1.2.246.562.20.62218894038", "1.2.246.562.20.37453630883", "1.2.246.562.20.33513456435", "1.2.246.562.20.67744380105", "1.2.246.562.20.63284149703", "1.2.246.562.20.21866258102", "1.2.246.562.20.49374890124", "1.2.246.562.20.61315696858", "1.2.246.562.20.25000083904", "1.2.246.562.20.24315884064", "1.2.246.562.20.24463276998", "1.2.246.562.20.68126723706", "1.2.246.562.20.24369692819", "1.2.246.562.20.85644309461", "1.2.246.562.20.23814468197", "1.2.246.562.20.652416722310", "1.2.246.562.20.76125738492", "1.2.246.562.20.95746983394", "1.2.246.562.20.36865180487", "1.2.246.562.20.34261737026", "1.2.246.562.20.40764097032", "1.2.246.562.20.96657153852", "1.2.246.562.20.78947488967", "1.2.246.562.20.98487808341", "1.2.246.562.20.83632848939", "1.2.246.562.20.83189095991", "1.2.246.562.20.41680052952", "1.2.246.562.20.73651234491", "1.2.246.562.20.72232645365", "1.2.246.562.20.73166586823", "1.2.246.562.20.78837204177", "1.2.246.562.20.40754876957", "1.2.246.562.20.62894781335", "1.2.246.562.20.38162043152", "1.2.246.562.20.367443941510", "1.2.246.562.20.66056176044", "1.2.246.562.20.39927120756", "1.2.246.562.20.79586634309", "1.2.246.562.20.14161395422", "1.2.246.562.20.94275139247", "1.2.246.562.20.30132025013", "1.2.246.562.20.58197099953", "1.2.246.562.20.57242688448", "1.2.246.562.20.49047168999", "1.2.246.562.20.34265748599", "1.2.246.562.20.97651160521", "1.2.246.562.20.20531405171", "1.2.246.562.20.87385722373", "1.2.246.562.20.79262941226", "1.2.246.562.20.21127872881", "1.2.246.562.20.13288817995", "1.2.246.562.20.10537221193", "1.2.246.562.20.81229167606", "1.2.246.562.20.164636113810", "1.2.246.562.20.709635280410", "1.2.246.562.20.59319662652", "1.2.246.562.20.40453109296", "1.2.246.562.20.31014506575", "1.2.246.562.20.84051083768", "1.2.246.562.20.61746834276", "1.2.246.562.20.52601243828", "1.2.246.562.20.48746948958", "1.2.246.562.20.22178852921", "1.2.246.562.20.95792548265", "1.2.246.562.20.85159338988", "1.2.246.562.20.76686657792", "1.2.246.562.20.23710191133", "1.2.246.562.20.34426677299", "1.2.246.562.20.20630019519", "1.2.246.562.20.86091352756", "1.2.246.562.20.78777184876", "1.2.246.562.20.44783897315", "1.2.246.562.20.65661132426", "1.2.246.562.20.40047295866", "1.2.246.562.20.77617263023", "1.2.246.562.20.81614763472", "1.2.246.562.20.70724716561", "1.2.246.562.20.70733701711", "1.2.246.562.20.42991927171", "1.2.246.562.20.42576184073", "1.2.246.562.20.21793622289", "1.2.246.562.20.52984708066", "1.2.246.562.20.75594058002", "1.2.246.562.20.90776676133", "1.2.246.562.20.10934768606", "1.2.246.562.20.54043381083", "1.2.246.562.20.26798191743", "1.2.246.562.20.63062425234", "1.2.246.562.20.31609116178", "1.2.246.562.20.17916409893", "1.2.246.562.20.56062572013", "1.2.246.562.20.432706670910", "1.2.246.562.20.46201939025", "1.2.246.562.20.37389907751", "1.2.246.562.20.85128088425", "1.2.246.562.20.57865831465", "1.2.246.562.20.910617100910", "1.2.246.562.20.44337701716", "1.2.246.562.20.72082958231", "1.2.246.562.20.33764614054", "1.2.246.562.20.41095103671", "1.2.246.562.20.80524618065", "1.2.246.562.20.82662116035", "1.2.246.562.20.98479822136", "1.2.246.562.20.88182076096", "1.2.246.562.20.236256954010", "1.2.246.562.20.38294399951", "1.2.246.562.20.92591163172", "1.2.246.562.20.82944132487", "1.2.246.562.20.24950016956", "1.2.246.562.20.710789834510", "1.2.246.562.20.17641941723", "1.2.246.562.20.99626017612", "1.2.246.562.20.32970486158", "1.2.246.562.20.14282414151", "1.2.246.562.20.93042935879", "1.2.246.562.20.49575807323", "1.2.246.562.20.36759345246", "1.2.246.562.20.66939610599", "1.2.246.562.20.70566432616", "1.2.246.562.20.98949824308", "1.2.246.562.20.57189397391", "1.2.246.562.20.18468800828", "1.2.246.562.20.60384364166", "1.2.246.562.20.31485501394", "1.2.246.562.20.42760216798", "1.2.246.562.20.80494454707", "1.2.246.562.20.53359265435", "1.2.246.562.20.25309930326", "1.2.246.562.20.87454687531", "1.2.246.562.20.58780451066", "1.2.246.562.20.919107242510", "1.2.246.562.20.47565334316", "1.2.246.562.20.16560729077", "1.2.246.562.20.96158384931", "1.2.246.562.20.81183573054", "1.2.246.562.20.68513989548", "1.2.246.562.20.26917908001", "1.2.246.562.20.62323683466", "1.2.246.562.20.835719756110", "1.2.246.562.20.41531814162", "1.2.246.562.20.68525211774", "1.2.246.562.20.83863932949", "1.2.246.562.20.87870016976", "1.2.246.562.20.73193315451", "1.2.246.562.20.43604079053", "1.2.246.562.20.386527362410", "1.2.246.562.20.94644667548", "1.2.246.562.20.20526423189", "1.2.246.562.20.70397726921", "1.2.246.562.20.84352986693", "1.2.246.562.20.38927177782", "1.2.246.562.20.89550684818", "1.2.246.562.20.41450061167", "1.2.246.562.20.45389593059", "1.2.246.562.20.72910725034", "1.2.246.562.20.59047178641", "1.2.246.562.20.91635303786", "1.2.246.562.20.78307094983", "1.2.246.562.20.15497711422", "1.2.246.562.20.92851071248", "1.2.246.562.20.99399686007", "1.2.246.562.20.28921354669", "1.2.246.562.20.102759489410", "1.2.246.562.20.87919378965", "1.2.246.562.20.55480036742", "1.2.246.562.20.46496569511", "1.2.246.562.20.52226541747", "1.2.246.562.20.95828122259", "1.2.246.562.20.84981772225", "1.2.246.562.20.23168881896", "1.2.246.562.20.91079355292", "1.2.246.562.20.81292046976", "1.2.246.562.20.53451038813", "1.2.246.562.20.82573029525", "1.2.246.562.20.60120130277", "1.2.246.562.20.74655270408", "1.2.246.562.20.74325780786", "1.2.246.562.20.47740062865", "1.2.246.562.20.14503565849", "1.2.246.562.20.59835674405", "1.2.246.562.20.52295303045", "1.2.246.562.20.11605015152", "1.2.246.562.20.98228253609", "1.2.246.562.20.74458400194", "1.2.246.562.20.73767446112", "1.2.246.562.20.33420311624", "1.2.246.562.20.46027886392", "1.2.246.562.20.75369992906", "1.2.246.562.20.31025331864", "1.2.246.562.20.55643032695", "1.2.246.562.20.828156565810", "1.2.246.562.20.29867436916", "1.2.246.562.20.59614379184", "1.2.246.562.20.90726144893", "1.2.246.562.20.64681195828", "1.2.246.562.20.169342908310", "1.2.246.562.20.32504527782", "1.2.246.562.20.39389827724", "1.2.246.562.20.56387621727", "1.2.246.562.20.45580368137", "1.2.246.562.20.95668010614", "1.2.246.562.20.19624367843", "1.2.246.562.20.92438279942", "1.2.246.562.20.227467204110", "1.2.246.562.20.71645340499", "1.2.246.562.20.258127949210", "1.2.246.562.20.80600520359", "1.2.246.562.20.40520074051", "1.2.246.562.20.22070455898", "1.2.246.562.20.16015045921", "1.2.246.562.20.59326920275", "1.2.246.562.20.77253130163", "1.2.246.562.20.99728830425", "1.2.246.562.20.59668560713", "1.2.246.562.20.419357471910", "1.2.246.562.20.327938253510", "1.2.246.562.20.84840892149", "1.2.246.562.20.624488469810", "1.2.246.562.20.77746119013", "1.2.246.562.20.48582305701", "1.2.246.562.20.19692559347", "1.2.246.562.20.48350346069", "1.2.246.562.20.66760060047", "1.2.246.562.20.31081146701", "1.2.246.562.20.65949888625", "1.2.246.562.20.68207598613", "1.2.246.562.20.24320432404", "1.2.246.562.20.67615038329", "1.2.246.562.20.60081971628", "1.2.246.562.20.91239782526", "1.2.246.562.20.89934356679", "1.2.246.562.20.76388825911", "1.2.246.562.20.15994816088", "1.2.246.562.20.889139238310", "1.2.246.562.20.135051789410", "1.2.246.562.20.566861559310", "1.2.246.562.20.51915426863", "1.2.246.562.20.40191370051", "1.2.246.562.20.38408705507", "1.2.246.562.20.42652239262", "1.2.246.562.20.87411035477", "1.2.246.562.20.26610632387", "1.2.246.562.20.97278007857", "1.2.246.562.20.22347060398", "1.2.246.562.20.98649026089", "1.2.246.562.20.24541501078", "1.2.246.562.20.95966429672", "1.2.246.562.20.79573104829", "1.2.246.562.20.82596431203", "1.2.246.562.20.33260216495", "1.2.246.562.20.56680679661", "1.2.246.562.20.38024344665", "1.2.246.562.20.67011167151", "1.2.246.562.20.34299144286", "1.2.246.562.20.73987674543", "1.2.246.562.20.67273271279", "1.2.246.562.20.28862859007", "1.2.246.562.20.80290248881", "1.2.246.562.20.393056883910", "1.2.246.562.20.70726201026", "1.2.246.562.20.82868208743", "1.2.246.562.20.29380227495", "1.2.246.562.20.82417971403", "1.2.246.562.20.32198223896", "1.2.246.562.20.88147973965", "1.2.246.562.20.27138656379", "1.2.246.562.20.759980493010", "1.2.246.562.20.47160362613", "1.2.246.562.20.75925183123", "1.2.246.562.20.44555890423", "1.2.246.562.20.74499868755", "1.2.246.562.20.89695915333", "1.2.246.562.20.92769775344", "1.2.246.562.20.11910905881", "1.2.246.562.20.71171106355", "1.2.246.562.20.15023913187", "1.2.246.562.20.86278915482", "1.2.246.562.20.59204276428", "1.2.246.562.20.82756577555", "1.2.246.562.20.82358751194", "1.2.246.562.20.11495428887", "1.2.246.562.20.64229221524", "1.2.246.562.20.32069030743", "1.2.246.562.20.46961132498", "1.2.246.562.20.90179293916", "1.2.246.562.20.65145766955", "1.2.246.562.20.67436657018", "1.2.246.562.20.38402012959", "1.2.246.562.20.46592759982", "1.2.246.562.20.38979395233", "1.2.246.562.20.97192559272", "1.2.246.562.20.73534293243", "1.2.246.562.20.712843910510", "1.2.246.562.20.352251352810", "1.2.246.562.20.42573281484", "1.2.246.562.20.35667971648", "1.2.246.562.20.21133043832", "1.2.246.562.20.32058285092", "1.2.246.562.20.18792225585", "1.2.246.562.20.25184034608", "1.2.246.562.20.65426027986", "1.2.246.562.20.22441277039", "1.2.246.562.20.92332072781", "1.2.246.562.20.62580304988", "1.2.246.562.20.80441659275", "1.2.246.562.20.38762924876", "1.2.246.562.20.47859594914", "1.2.246.562.20.33749226032", "1.2.246.562.20.60196203107", "1.2.246.562.20.24863099313", "1.2.246.562.20.545223663410", "1.2.246.562.20.54700014605", "1.2.246.562.20.72563186138", "1.2.246.562.20.62927824854", "1.2.246.562.20.75002169516", "1.2.246.562.20.497272228510", "1.2.246.562.20.69746637599", "1.2.246.562.20.93422693221", "1.2.246.562.20.79670389976", "1.2.246.562.20.89327647551", "1.2.246.562.20.87308456794", "1.2.246.562.20.954018523310", "1.2.246.562.20.27348112973", "1.2.246.562.20.21854206709", "1.2.246.562.20.80480969866", "1.2.246.562.20.40264303138", "1.2.246.562.20.89291071259", "1.2.246.562.20.34950080142", "1.2.246.562.20.67798159157", "1.2.246.562.20.52558738143", "1.2.246.562.20.63676994385", "1.2.246.562.20.43377100318", "1.2.246.562.20.29495980855", "1.2.246.562.20.51315456395", "1.2.246.562.20.57459455796", "1.2.246.562.20.14535244454", "1.2.246.562.20.22990104162", "1.2.246.562.20.69948314708", "1.2.246.562.20.17424129777", "1.2.246.562.20.47652769564", "1.2.246.562.20.12301578733", "1.2.246.562.20.53771396172", "1.2.246.562.20.69210992405", "1.2.246.562.20.384222297410", "1.2.246.562.20.16595131661", "1.2.246.562.20.64452756451", "1.2.246.562.20.662010637910", "1.2.246.562.20.32315499346", "1.2.246.562.20.31297313434", "1.2.246.562.20.30962472293", "1.2.246.562.20.73984886439", "1.2.246.562.20.66388356975", "1.2.246.562.20.76666446232", "1.2.246.562.20.70415614505", "1.2.246.562.20.10079139431", "1.2.246.562.20.82076826351", "1.2.246.562.20.329521821310", "1.2.246.562.20.74130428712", "1.2.246.562.20.95458373701", "1.2.246.562.20.99082098882", "1.2.246.562.20.55981595485", "1.2.246.562.20.60501613073", "1.2.246.562.20.84132091103", "1.2.246.562.20.49435507147", "1.2.246.562.20.18315638686", "1.2.246.562.20.82136021159", "1.2.246.562.20.65043716318", "1.2.246.562.20.45163603355", "1.2.246.562.20.47929625979", "1.2.246.562.20.79244293738", "1.2.246.562.20.84605436439", "1.2.246.562.20.58879592572", "1.2.246.562.20.53129643135", "1.2.246.562.20.83330890191", "1.2.246.562.20.99181604564", "1.2.246.562.20.60675800373", "1.2.246.562.20.45250524183", "1.2.246.562.20.89568459856", "1.2.246.562.20.42011418466", "1.2.246.562.20.24676131931", "1.2.246.562.20.758958709810", "1.2.246.562.20.81742504491", "1.2.246.562.20.84071316388", "1.2.246.562.20.93312319895", "1.2.246.562.20.79034283873", "1.2.246.562.20.84338679735", "1.2.246.562.20.52380863127", "1.2.246.562.20.72645689299", "1.2.246.562.20.18369729696", "1.2.246.562.20.63841302407", "1.2.246.562.20.12275008535", "1.2.246.562.20.38952669224", "1.2.246.562.20.45036268147", "1.2.246.562.20.60706226838", "1.2.246.562.20.97158680468", "1.2.246.562.20.87783239806", "1.2.246.562.20.71714704096", "1.2.246.562.20.81388276735", "1.2.246.562.20.67268814836", "1.2.246.562.20.548556833610", "1.2.246.562.20.611707616810", "1.2.246.562.20.33968621822", "1.2.246.562.20.130771783110", "1.2.246.562.20.94496963672", "1.2.246.562.20.27292114645", "1.2.246.562.20.77289306415", "1.2.246.562.20.20351466469", "1.2.246.562.20.78252112681", "1.2.246.562.20.78236383994", "1.2.246.562.20.96247717554", "1.2.246.562.20.78900430543", "1.2.246.562.20.19985426923", "1.2.246.562.20.96579608298", "1.2.246.562.20.21375704646", "1.2.246.562.20.35125867107", "1.2.246.562.20.31697959794", "1.2.246.562.20.29139855497", "1.2.246.562.20.49330179596", "1.2.246.562.20.42858307665", "1.2.246.562.20.49996423551", "1.2.246.562.20.19998898605", "1.2.246.562.20.57968552281", "1.2.246.562.20.156639141810", "1.2.246.562.20.94721859653", "1.2.246.562.20.70671354044", "1.2.246.562.20.59424068993", "1.2.246.562.20.20484435282", "1.2.246.562.20.78170932459", "1.2.246.562.20.18624104198", "1.2.246.562.20.38201386984", "1.2.246.562.20.25168439401", "1.2.246.562.20.31462324331", "1.2.246.562.20.55428838399", "1.2.246.562.20.23682592698", "1.2.246.562.20.223841514810", "1.2.246.562.20.77177610787", "1.2.246.562.20.12786437512", "1.2.246.562.20.70802633456", "1.2.246.562.20.69275388374", "1.2.246.562.20.61534460268", "1.2.246.562.20.85285825155", "1.2.246.562.20.44817703257", "1.2.246.562.20.77623436829", "1.2.246.562.20.994153098410", "1.2.246.562.20.60199745229", "1.2.246.562.20.520008279410", "1.2.246.562.20.62407895812", "1.2.246.562.20.78390129316", "1.2.246.562.20.974379830310", "1.2.246.562.20.12210487448", "1.2.246.562.20.993490115210", "1.2.246.562.20.96327501001", "1.2.246.562.20.96632402259", "1.2.246.562.20.95414193102", "1.2.246.562.20.45552099132", "1.2.246.562.20.39304895525", "1.2.246.562.20.73713742044", "1.2.246.562.20.81290614619", "1.2.246.562.20.36316566441");
            return res;
        } else {
            Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
            return valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("valinnanvaihe/{oid}/valintaperusteet")
    @ApiOperation(value = "Hakee valintaperusteet, jotka liittyvät valinnanvaiheeseen valintaryhmän kautta", response = ValinnanVaiheDTO.class)
    public List<ValintaperusteetDTO> valintaperusteet(@ApiParam(value = "Valinnanvaihe OID", required = true) @PathParam("oid") String oid) {
        Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
        Set<String> hakukohdeOids = valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
        return hakukohdeOids.stream().flatMap(hakukohdeOid -> {
            HakuparametritDTO hakuparametrit = new HakuparametritDTO();
            hakuparametrit.setHakukohdeOid(hakukohdeOid);
            return valintaperusteService.haeValintaperusteet(Arrays.asList(hakuparametrit)).stream();
        }).collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("valintaryhma/{oid}/vastuuorganisaatio")
    @ApiOperation(value = "Hakee valintaryhmän vastuuorganisaation oidin", response = String.class)
    public String valintaryhmaVastuuorganisaatio(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return valintaryhmaService.readByOid(oid).getVastuuorganisaatio().getOid();
    }
}
