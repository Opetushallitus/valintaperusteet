package fi.vm.sade.service.valintaperusteet.resource.impl;

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
    private static final Logger LOG = LoggerFactory.getLogger(ValintaperusteetResourceImpl.class);
    private final static String HAKUKOHDE_VIITE_PREFIX = "{{hakukohde.";
    @Autowired
    private ValintaperusteService valintaperusteService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintakoeService valintakoeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/valintatapajono/kopiot")
    public Map<String, List<String>> findKopiot(@QueryParam("oid") List<String> oid) {
        try {
            return valintatapajonoService.findKopiot(oid);
        } catch (Exception e) {
            LOG.error("Virhe valintatapajonojen kopioiden hakemisessa!", e);
            return new HashMap<>();
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
            final List<HakijaryhmaValintatapajono> byHakukohteet = hakijaryhmaValintatapajonoService.findByHakukohteet(hakukohdeOids);
            LinkitettavaJaKopioitavaUtil.jarjesta(byHakukohteet);
            final List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs = byHakukohteet.stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
            IntStream.range(0, hakijaryhmaValintatapajonoDTOs.size()).forEach(i -> {
                hakijaryhmaValintatapajonoDTOs.get(i).setPrioriteetti(i);
            });
            return hakijaryhmaValintatapajonoDTOs;
        } catch (HakijaryhmaEiOleOlemassaException e) {
            LOG.error("Hakijaryhmää ei löytynyt!", e);
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            LOG.error("Hakijaryhmää ei saatu haettua!", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            LOG.info("Haku kesti {}ms", (System.currentTimeMillis() - started));
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
        for (ValinnanVaiheJonoillaDTO vaihe : valinnanVaiheJonoillaDTOs) {
            if (vaihe.getJonot() != null) {
                int i = 0;
                Set<ValintatapajonoDTO> ilmanLaskentaaJonot = new HashSet<>();
                for (ValintatapajonoDTO jono : vaihe.getJonot()) {
                    jono.setPrioriteetti(i);
                    if (!jono.getKaytetaanValintalaskentaa()) {
                        ilmanLaskentaaJonot.add(jono);
                    }
                    i++;
                }
                vaihe.setJonot(ilmanLaskentaaJonot);
            }
        }
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
            LOG.error("Hakukohteen importointi valintaperusteisiin epaonnistui! {}", hakukohde.getHakukohdeOid());
            throw e;
        }
    }

    @GET
    @Path("valintaperusteet/hakijaryhma/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(@PathParam("hakukohdeOid") String hakukohdeOid) {
        List<HakijaryhmaValintatapajono> hakukohteenRyhmat = hakijaryhmaValintatapajonoService.findByHakukohde(hakukohdeOid);
        List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        vaiheet.stream().forEachOrdered(
                vaihe -> {
                    List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
                    jonot.stream().forEachOrdered(jono ->
                            hakukohteenRyhmat.addAll(hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(jono.getOid())));
                });
        List<ValintaperusteetHakijaryhmaDTO> result = new ArrayList<>();
        for (int i = 0; i < hakukohteenRyhmat.size(); i++) {
            HakijaryhmaValintatapajono original = hakukohteenRyhmat.get(i);
            Laskentakaava laskentakaava = laskentakaavaService.haeLaskettavaKaava(original.getHakijaryhma().getLaskentakaava().getId(), Laskentamoodi.VALINTALASKENTA);
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
    @ApiOperation(value = "Hakee hakukohteet, jotka liittyvät valinnanvaiheeseen", response = ValinnanVaiheDTO.class)
    public Set<String> hakukohteet(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
        return valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("valinnanvaihe/{oid}/valintaperusteet")
    @ApiOperation(value = "Hakee hakukohteet, jotka liittyvät valinnanvaiheeseen", response = ValinnanVaiheDTO.class)
    public List<ValintaperusteetDTO> valintaperusteet(@ApiParam(value = "Valinnanvaihe OID", required = true) @PathParam("oid") String oid) {
        Set<String> valintaryhmaoids = valinnanVaiheService.getValintaryhmaOids(oid);
        Set<String> hakukohdeOids = valintaryhmaService.findHakukohdesRecursive(valintaryhmaoids);
        return hakukohdeOids.stream().flatMap(hakukohdeOid -> {
            HakuparametritDTO hakuparametrit = new HakuparametritDTO();
            hakuparametrit.setHakukohdeOid(hakukohdeOid);
            return valintaperusteService.haeValintaperusteet(Arrays.asList(hakuparametrit)).stream();
        }).collect(Collectors.toList());
    }
}
