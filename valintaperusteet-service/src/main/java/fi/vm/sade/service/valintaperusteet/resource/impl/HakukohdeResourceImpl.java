package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;

import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.resource.ValintaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

@Component
@Path("hakukohde")
@Api(value = "/hakukohde", description = "Resurssi hakukohteiden käsittelyyn")
public class HakukohdeResourceImpl {
    private final static String HAKUKOHDE_VIITE_PREFIX = "{{hakukohde.";
    protected final static Logger LOG = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @Autowired
    HakukohdeService hakukohdeService;

    @Autowired
    HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    ValinnanVaiheService valinnanVaiheService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    LaskentakaavaService laskentakaavaService;

    @Autowired
    ValintatapajonoService valintatapajonoService;

    @Autowired
    HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    public HakukohdeResourceImpl() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteita. Joko kaikki tai päätason hakukohteet.", response = HakukohdeViiteDTO.class)
    public List<HakukohdeViiteDTO> query(
            @QueryParam("paataso") @DefaultValue("false") @ApiParam(name = "paataso", value = "Haetaanko päätason hakukohteet vai kaikki") boolean paataso) {
        List<HakukohdeViite> hakukohteet = null;
        if (paataso) {
            hakukohteet = hakukohdeService.findRoot();
        } else {
            hakukohteet = hakukohdeService.findAll();
        }
        return modelMapper.mapList(hakukohteet, HakukohdeViiteDTO.class);
    }

    @GET
    @Path("haku/{hakuOid}")
    @Produces(MediaType.APPLICATION_JSON)
    // @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee haun hakukohteet", response = HakukohdeViiteDTO.class)
    public List<HakukohdeViiteDTO> haunHakukohteet(
            @ApiParam(value = "hakuOid", required = true) @PathParam("hakuOid") String hakuOid) {
        return modelMapper.mapList(hakukohdeService.haunHakukohteet(hakuOid), HakukohdeViiteDTO.class);
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen OID:n perusteella", response = HakukohdeViiteDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Hakukohdetta ei löydy")})
    public HakukohdeViiteDTO queryFull(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            return modelMapper.map(hakukohdeService.readByOid(oid), HakukohdeViiteDTO.class);
        } catch (HakukohdeViiteEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("/hakukohteet")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteet OIDien perusteella", response = HakukohdeViiteDTO.class)
    public List<HakukohdeViiteDTO> hakukohteet(@ApiParam(value = "Lista hakukohdeOideja", required = true) List<String> hakukohdeOidit) {
        return hakukohdeOidit.stream().map((oid) -> modelMapper.map(
                hakukohdeService.readByOid(oid), HakukohdeViiteDTO.class)).collect(Collectors.toList());
    }

    @GET
    @Path("/{oid}/valintaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee valintaryhman OID:n perusteella", response = ValintaryhmaDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Valintaryhmaa ei löydy")})
    public ValintaryhmaDTO queryValintaryhma(@PathParam("oid") String oid) {
        final Optional<Valintaryhma> valintaryhmaByHakukohdeOid = hakukohdeViiteDAO.findValintaryhmaByHakukohdeOid(oid);
        if (valintaryhmaByHakukohdeOid.isPresent()) {
            return new ValintaryhmaDTO() {{
                setNimi(valintaryhmaByHakukohdeOid.get().getNimi());
                setOid(valintaryhmaByHakukohdeOid.get().getOid());
            }};
        } else {
            return new ValintaryhmaDTO();
        }
    }

    @POST
    @Path("/valintaryhmat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintaryhmät hakukohteiden OIDien perusteella", response = ValintaryhmaDTO.class)
    public Map<String,ValintaryhmaDTO> queryValintaryhmat(@ApiParam(value = "Lista hakukohdeOideja", required = true) List<String> hakukohdeOidit) {
        Map<String,ValintaryhmaDTO> map = new HashMap<>();
        hakukohdeOidit.forEach((oid) -> hakukohdeViiteDAO.findValintaryhmaByHakukohdeOid(oid).ifPresent(valintaryhma ->
            map.put(oid, new ValintaryhmaDTO() {{
                setNimi(valintaryhma.getNimi());
                setOid(valintaryhma.getOid());
            }})));
        return map;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää hakukohteen valintaryhmään (tai juureen, jos valintaryhmän OID:a ei ole annettu)")
    public Response insert(@ApiParam(value = "Lisättävä hakukohde ja valintaryhmä", required = true) HakukohdeInsertDTO hakukohde) {
        try {
            HakukohdeViiteDTO hkv = modelMapper.map(hakukohdeService.insert(hakukohde.getHakukohde(), hakukohde.getValintaryhmaOid()), HakukohdeViiteDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakuOid(hkv.getHakuoid())
                    .hakukohdeOid(hkv.getOid())
                    .valintaryhmaOid(hkv.getValintaryhmaOid())
                    .add("nimi", hkv.getNimi())
                    .add("tila", hkv.getTila())
                    .tarjoajaOid(hkv.getTarjoajaOid())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_LISAYS_VALINTARYHMA)
                    .build());
            return Response.status(Response.Status.CREATED).entity(hkv).build();
        } catch (Exception e) {
            LOG.warn("Hakukohdetta ei saatu lisättyä", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorDTO(e.getMessage())).build();
        }
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakukohdetta OID:n perusteella")
    public Response update(
            @ApiParam(value = "Päivitettävän hakukohteen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "hakukohteen uudet tiedot", required = true) HakukohdeViiteCreateDTO hakukohdeViite) {
        try {
            HakukohdeViiteDTO hkv = modelMapper.map(hakukohdeService.update(oid, hakukohdeViite), HakukohdeViiteDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakuOid(hkv.getHakuoid())
                    .hakukohdeOid(hkv.getOid())
                    .valintaryhmaOid(hkv.getValintaryhmaOid())
                    .add("nimi", hkv.getNimi())
                    .add("tila", hkv.getTila())
                    .tarjoajaOid(hkv.getTarjoajaOid())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_PAIVITYS)
                    .build());
            return Response.status(Response.Status.ACCEPTED).entity(hkv).build();
        } catch (Exception e) {
            LOG.warn("Hakukohdetta ei saatu päivitettyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{oid}/valinnanvaihe")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen valinnan vaiheet OID:n perusteella", response = ValinnanVaiheDTO.class)
    public List<ValinnanVaiheDTO> valinnanVaihesForHakukohde(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid,
                                                             @DefaultValue("false") @QueryParam("withValisijoitteluTieto") String withValisijoitteluTieto) {

        List<ValinnanVaiheDTO> valinnanVaiheetDTO = modelMapper.mapList(valinnanVaiheService.findByHakukohde(oid), ValinnanVaiheDTO.class);

        if (withValisijoitteluTieto.equalsIgnoreCase("true")) {

            /**
             * Fetch all the ValintatapaJono's for each ValinnanVaihe
             * and check if any of the ValintatapaJono has Valisijoittelu.
             * Sets hasValisijoittelu variable accordingly.
             */
            valinnanVaiheetDTO = valinnanVaiheetDTO.stream().map(vv -> {
                if (valintatapajonoService.findJonoByValinnanvaihe(vv.getOid())
                        .stream().anyMatch(Valintatapajono::getValisijoittelu)) {
                    vv.setHasValisijoittelu(true);
                } else {
                    vv.setHasValisijoittelu(false);
                }
                return vv;
            }).collect(Collectors.toList());

        }

        return valinnanVaiheetDTO;
    }

    @POST
    @Path("/valinnanvaiheet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteiden valinnan vaiheet OIDien perusteella", response = ValinnanVaiheDTO.class)
    public Map<String, List<ValinnanVaiheDTO>> valinnanVaiheetForHakukohteet(@ApiParam(value = "Hakukohde OIDit", required = true) List<String> hakukohdeOidit) {
        Map<String,List<ValinnanVaiheDTO>> map = new HashMap<>();
        hakukohdeOidit.forEach((oid) -> map.put(oid, modelMapper.mapList(valinnanVaiheService.findByHakukohde(oid), ValinnanVaiheDTO.class)));
        return map;
    }


    @Transactional
    @GET
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen valintakokeet OID:n perusteella", response = ValintakoeDTO.class)
    public List<ValintakoeDTO> valintakoesForHakukohde(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        HakukohdeViite viite = hakukohdeService.readByOid(oid);
        Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet = viite.getHakukohteenValintaperusteet();
        Map<String, String> tunnisteArvoPari = hakukohteenValintaperusteet.values().stream().collect(Collectors.toMap(t -> t.getTunniste(), t -> t.getArvo()));
        return modelMapper.mapList(valintakoeService
                .findValintakoesByValinnanVaihes(valinnanVaiheService.findByHakukohde(oid)), ValintakoeDTO.class).stream().map(
                vk -> {
                    if (Optional.ofNullable(vk.getTunniste()).orElse("").startsWith(HAKUKOHDE_VIITE_PREFIX)) {
                        String tunniste = vk.getTunniste().replace(HAKUKOHDE_VIITE_PREFIX, "").replace("}}", "");
                        vk.setSelvitettyTunniste(tunnisteArvoPari.get(tunniste));
                    } else {
                        vk.setSelvitettyTunniste(vk.getTunniste());
                    }
                    return vk;
                }
        ).collect(Collectors.toList());
    }

    @POST
    @Path("/valintakoe")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen valintakokeet OID:n perusteella", response = ValintakoeDTO.class)
    public List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(List<String> oids) {
        return oids.stream()
                .map(oid -> {
                    List<ValintakoeDTO> valintakoeDtos = modelMapper.mapList(
                            valintakoeService.findValintakoesByValinnanVaihes(valinnanVaiheService.findByHakukohde(oid.toString())),
                            ValintakoeDTO.class);
                    if (valintakoeDtos == null || valintakoeDtos.isEmpty()) {
                        return null;
                    }
                    return new HakukohdeJaValintakoeDTO(oid, valintakoeDtos);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{oid}/kuuluuSijoitteluun")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa tiedon, kuuluuko hakukohde sijoitteluun", response = Boolean.class)
    public Map<String, Boolean> kuuluuSijoitteluun(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("sijoitteluun", hakukohdeService.kuuluuSijoitteluun(oid));
        return map;
    }

    @GET
    @Path("/{oid}/ilmanlaskentaa")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa", response = ValintatapajonoDTO.class)
    public List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(@PathParam("oid") String oid) {
        List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs = modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid), ValinnanVaiheJonoillaDTO.class);
        // Lisää prioriteetit ja filtteröi sitten pois jonot jotka käyttävät laskentaa
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
    @Path("/{oid}/hakijaryhma")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen hakijaryhmät", response = HakijaryhmaDTO.class)
    public List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(hakijaryhmaValintatapajonoService.findByHakukohde(oid), HakijaryhmaValintatapajonoDTO.class);
    }

    @POST
    @Path("/hakijaryhmat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteiden hakijaryhmät", response = HakijaryhmaDTO.class)
    public Map<String, List<HakijaryhmaValintatapajonoDTO>> hakijaryhmat(@ApiParam(value = "Hakukohde OIDit", required = true) List<String> hakukohdeOidit) {
        Map<String, List<HakijaryhmaValintatapajonoDTO>> map = new HashMap<>();
        hakukohdeOidit.forEach((oid) -> map.put(oid, modelMapper.mapList(hakijaryhmaValintatapajonoService.findByHakukohde(oid), HakijaryhmaValintatapajonoDTO.class)));
        return map;
    }
    
    @GET
    @Path("/{oid}/laskentakaava")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen järjestyskriteerit", response = JarjestyskriteeriDTO.class)
    public List<JarjestyskriteeriDTO> findLaskentaKaavat(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(jarjestyskriteeriService.findByHakukohde(oid), JarjestyskriteeriDTO.class);
    }

    @GET
    @Path("/avaimet/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
    public List<ValintaperusteDTO> findAvaimet(@ApiParam(value = "Hakukohde OID", required = true) @PathParam("oid") String oid) {
        return laskentakaavaService.findAvaimetForHakukohde(oid);
    }

    @POST
    @Path("/avaimet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakukohteiden syötettävät tiedot", response = ValintaperusteDTO.class)
    public Map<String,List<ValintaperusteDTO>> findHakukohteidenAvaimet(@ApiParam(value = "Hakukohteiden OIDit", required = true) List<String> hakukohdeOidit) {
        Map<String,List<ValintaperusteDTO>> map = new HashMap<>();
        hakukohdeOidit.forEach((oid) -> map.put(oid, laskentakaavaService.findAvaimetForHakukohde(oid)));
        return map;
    }

    @GET
    @Path("{hakukohdeOid}/avaimet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    public HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(@PathParam("hakukohdeOid") String hakukohdeOid) {
        return laskentakaavaService.findHakukohteenAvaimet(hakukohdeOid);
    }

    @PUT
    @Path("/{hakukohdeOid}/valinnanvaihe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää valinnan vaiheen hakukohteelle")
    @ApiResponses(@ApiResponse(code = 400, message = "Valinnan vaiheen lisääminen epäonnistui"))
    public Response insertValinnanvaihe(
            @ApiParam(value = "Hakukohteen OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Edellisen valinnan vaiheen OID (jos valinnan vaihe halutaa lisätä tietyn vaiheen jälkeen, muussa tapauksessa uusi vaihe lisätään viimeiseksi)") @QueryParam("edellinenValinnanVaiheOid") String edellinenValinnanVaiheOid,
            @ApiParam(value = "Uusi valinnan vaihe", required = true) ValinnanVaiheCreateDTO valinnanVaihe) {
        try {
            ValinnanVaiheDTO lisatty = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheHakukohteelle(hakukohdeOid, valinnanVaihe, edellinenValinnanVaiheOid), ValinnanVaiheDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakukohdeOid(hakukohdeOid)
                    .valinnanvaiheOid(lisatty.getOid())
                    .add("periytyy", lisatty.getInheritance())
                    .add("nimi", lisatty.getNimi())
                    .add("kuvaus", lisatty.getKuvaus())
                    .add("aktiivinen", lisatty.getAktiivinen())
                    .add("valinnanvaihetyyppi", lisatty.getValinnanVaiheTyyppi())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_LISAYS_VALINNANVAIHE)
                    .build());
            return Response.status(Response.Status.CREATED).entity(lisatty).build();
        } catch (Exception e) {
            LOG.error("Error creating valinnanvaihe.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{hakukohdeOid}/hakijaryhma")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää hakijaryhmän hakukohteelle")
    @ApiResponses(@ApiResponse(code = 400, message = "Hakijaryhmän lisääminen epäonnistui"))
    public Response insertHakijaryhma(
            @ApiParam(value = "Hakukohteen OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakijaryhmä", required = true) HakijaryhmaCreateDTO hakijaryhma) {
        try {
            HakijaryhmaDTO lisatty = modelMapper.map(hakijaryhmaValintatapajonoService.lisaaHakijaryhmaHakukohteelle(hakukohdeOid, hakijaryhma), HakijaryhmaDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakukohdeOid(hakukohdeOid)
                    .hakijaryhmaOid(lisatty.getOid())
                    .add("kiintio", lisatty.getKiintio())
                    .add("nimi", lisatty.getNimi())
                    .add("kuvaus", lisatty.getKuvaus())
                    .add("laskentakaavaid", lisatty.getLaskentakaavaId())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_LISAYS_HAKIJARYHMA)
                    .build());
            return Response.status(Response.Status.CREATED).entity(lisatty).build();
        } catch (LaskentakaavaOidTyhjaException e) {
            LOG.warn("Error creating hakijaryhma for hakukohde: " + e.toString());
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        } catch (Exception e) {
            LOG.error("Error creating hakijaryhma for hakukohde.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{hakukohdeOid}/hakijaryhma/{hakijaryhmaOid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Liittää hakijaryhmän hakukohteelle")
    public Response liitaHakijaryhma(
            @ApiParam(value = "Hakukohteen OID, jolle hakijaryhmä liitetään", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Hakijaryhmän OID, joka valintatapajonoon liitetään", required = true) @PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        try {
            hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(hakukohdeOid, hakijaryhmaOid);
            AUDIT.log(builder()
                    .id(username())
                    .hakukohdeOid(hakukohdeOid)
                    .hakijaryhmaOid(hakijaryhmaOid)
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_LIITOS_HAKIJARYHMA)
                    .build());
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            LOG.error("Error linking hakijaryhma.", e);
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        }
    }

    @POST
    @Path("/{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakukohteen hakukohdekoodia")
    @ApiResponses(@ApiResponse(code = 400, message = "Päivittäminen epäonnistui"))
    public Response updateHakukohdekoodi(
            @ApiParam(value = "Hakukohde OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakukohdekoodi", required = true) KoodiDTO hakukohdekoodi) {
        try {
            KoodiDTO lisatty = modelMapper.map(hakukohdekoodiService.updateHakukohdeHakukohdekoodi(hakukohdeOid, hakukohdekoodi), KoodiDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakukohdeOid(hakukohdeOid)
                    .add("arvo", lisatty.getArvo())
                    .add("nimi_en", lisatty.getNimiEn())
                    .add("nimi_fi", lisatty.getNimiFi())
                    .add("nimi_sv", lisatty.getNimiSv())
                    .add("uri", lisatty.getUri())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_HAKUKOHDEKOODI_PAIVITYS)
                    .build());
            return Response.status(Response.Status.ACCEPTED).entity(lisatty).build();
        } catch (Exception e) {
            LOG.error("Error updating hakukohdekoodit.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{hakukohdeOid}/hakukohdekoodi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää hakukohdekoodin hakukohteelle")
    @ApiResponses(@ApiResponse(code = 400, message = "Lisääminen epäonnistui"))
    public Response insertHakukohdekoodi(
            @ApiParam(value = "Hakukohde OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakukohdekoodi", required = true) KoodiDTO hakukohdekoodi) {
        try {
            KoodiDTO lisatty = modelMapper.map(hakukohdekoodiService.lisaaHakukohdekoodiHakukohde(hakukohdeOid, hakukohdekoodi), KoodiDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakukohdeOid(hakukohdeOid)
                    .add("arvo", lisatty.getArvo())
                    .add("nimi_en", lisatty.getNimiEn())
                    .add("nimi_fi", lisatty.getNimiFi())
                    .add("nimi_sv", lisatty.getNimiSv())
                    .add("uri", lisatty.getUri())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_LISAYS_HAKUKOHDEKOODI)
                    .build());
            return Response.status(Response.Status.CREATED).entity(lisatty).build();
        } catch (Exception e) {
            LOG.error("Error inserting hakukohdekoodi.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{hakukohdeOid}/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Siirtää hakukohteen uuteen valintaryhmään (tai juureen, jos valintaryhmää ei anneta)")
    @ApiResponses(@ApiResponse(code = 400, message = "Siirtäminen epäonnistui"))
    public Response siirraHakukohdeValintaryhmaan(
            @ApiParam(value = "Hakukohde OID", required = true) @PathParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Uuden valintaryhmän OID") String valintaryhmaOid) {
        try {
            HakukohdeViiteDTO hakukohde = modelMapper.map(hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, true), HakukohdeViiteDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakuOid(hakukohde.getHakuoid())
                    .hakukohdeOid(hakukohde.getOid())
                    .valintaryhmaOid(hakukohde.getValintaryhmaOid())
                    .add("nimi", hakukohde.getNimi())
                    .add("tila", hakukohde.getTila())
                    .tarjoajaOid(hakukohde.getTarjoajaOid())
                    .setOperaatio(ValintaperusteetOperation.HAKUKOHDE_SIIRTO_VALINTARYHMAAN)
                    .build());
            return Response.status(Response.Status.ACCEPTED).entity(hakukohde).build();
        } catch (Exception e) {
            LOG.error("Error moving hakukohde to new valintaryhma.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
