package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

@Component
@Path("hakijaryhma")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma", description = "Resurssi hakijaryhmien käsittelyyn")
public class HakijaryhmaResourceImpl implements HakijaryhmaResource {
    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    HakijaryhmaService hakijaryhmaService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    protected final static Logger LOGGER = LoggerFactory.getLogger(HakijaryhmaResourceImpl.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @Path("/haku")
    @ApiOperation(value = "Hakee hakijaryhmät annetulle hakukohde OID joukolle", response = HakijaryhmaDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Hakijaryhmää ei löydy"),})
    public List<HakijaryhmaValintatapajonoDTO> readByHakukohdeOids(List<String> hakukohdeOids) {
        long t0 = System.currentTimeMillis();
        try {
            if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
                // Haetaan hakuOid:lla
                LOGGER.error("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla");
                //return hakijaryhmaValintatapajonoService.findByHaku(hakuOid).stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
                throw new WebApplicationException(new RuntimeException("Yritettiin hakea hakijaryhmia tyhjalla hakukohde OID joukolla"), Response.Status.NOT_FOUND);
            } else {
                // Haetaan hakukohdeOid joukolla
                LOGGER.info("Haetaan hakukohdeOid joukolla {}", Arrays.toString(hakukohdeOids.toArray()));
                return hakijaryhmaValintatapajonoService.findByHakukohteet(hakukohdeOids).stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
            }
        } catch (HakijaryhmaEiOleOlemassaException e) {
            LOGGER.error("Hakijaryhmää ei löytynyt! {}", hakukohdeOids);
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("Hakijaryhmää ei saatu haettua!", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            LOGGER.info("Haku kesti {}ms", (System.currentTimeMillis() - t0));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @Path("/haku/{hakuOid}")
    @ApiOperation(value = "Hakee hakijaryhmät annetulle haku OID:lle", response = HakijaryhmaDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Hakijaryhmää ei löydy"),})
    public List<HakijaryhmaValintatapajonoDTO> readByHakuOid(@PathParam("hakuOid") String hakuOid) {
        LOGGER.info("Haetaan hakuOid:lla {}", hakuOid);
        long t0 = System.currentTimeMillis();
        try {
            if (hakuOid == null) {
                // Haetaan hakuOid:lla
                LOGGER.error("Yritettiin hakea hakijaryhmia tyhjalla haku OID:lla");
                //return hakijaryhmaValintatapajonoService.findByHaku(hakuOid).stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
                throw new WebApplicationException(new RuntimeException("Yritettiin hakea hakijaryhmia tyhjalla haku OID:lla"), Response.Status.NOT_FOUND);
            } else {
                return hakijaryhmaValintatapajonoService.findByHaku(hakuOid).stream().map(h -> modelMapper.map(h, HakijaryhmaValintatapajonoDTO.class)).collect(Collectors.toList());
            }
        } catch (HakijaryhmaEiOleOlemassaException e) {
            LOGGER.error("Hakijaryhmää ei löytynyt hakuoidilla {}", hakuOid);
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("Hakijaryhmää ei saatu haettua!", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            LOGGER.info("Haku kesti {}ms", (System.currentTimeMillis() - t0));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakijaryhmän OID:n perusteella", response = HakijaryhmaDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Hakijaryhmää ei löydy"),})
    public HakijaryhmaDTO read(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            return modelMapper.map(hakijaryhmaService.readByOid(oid), HakijaryhmaDTO.class);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{hakijaryhmaOid}/valintatapajono")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee hakijaryhmän ja siihen liittyvät valintatapajonot OID:n perusteella", response = HakijaryhmaValintatapajonoDTO.class)
    public List<HakijaryhmaValintatapajonoDTO> valintatapajonot(
            @ApiParam(value = "OID", required = true) @PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        try {
            return modelMapper.mapList(hakijaryhmaValintatapajonoService.findByHakijaryhma(hakijaryhmaOid), HakijaryhmaValintatapajonoDTO.class);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakijaryhmän", response = HakijaryhmaDTO.class)
    public HakijaryhmaDTO update(
            @ApiParam(value = "Päivitettävän hakijaryhmän OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Hakijaryhmän uudet tiedot", required = true) HakijaryhmaCreateDTO hakijaryhma) {
        Hakijaryhma h = hakijaryhmaService.update(oid, hakijaryhma);
        AUDIT.log(builder()
                .id(username())
                .hakijaryhmaOid(oid)
                .valintaryhmaOid(Optional.ofNullable(h.getValintaryhma()).map(v -> v.getOid()).orElse(null))
                .add("hakijaryhmanvalintatapajonot", Arrays.toString(Optional.ofNullable(h.getJonot()).orElse(Collections.<HakijaryhmaValintatapajono>emptySet())
                        .stream().map(v -> v.getOid()).toArray()))
                .add("valintatapajonoids", Arrays.toString(Optional.ofNullable(h.getValintatapajonoIds()).orElse(Collections.<String>emptyList())
                        .stream().toArray()))
                .add("kiintio", h.getKiintio())
                .add("kuvaus", h.getKuvaus())
                .add("laskentakaavaid", h.getLaskentakaavaId())
                .add("nimi", h.getNimi())
                .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_PAIVITYS)
                .build());
        return modelMapper.map(h, HakijaryhmaDTO.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää hakijaryhmät parametrina annetun listan mukaan", response = HakijaryhmaDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "OID-lista on tyhjä"),})
    public List<HakijaryhmaDTO> jarjesta(
            @ApiParam(value = "Hakijaryhmien uusi järjestys", required = true) List<String> oids) {
        try {
//            return modelMapper.mapList(hakijaryhmaService.jarjestaHakijaryhmat(oids), HakijaryhmaDTO.class);
            return null;
        } catch (HakijaryhmaOidListaOnTyhjaException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän OID:n perusteella")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "Poisto onnistui"),
            @ApiResponse(code = 403, message = "Hakijaryhmää ei voida poistaa, esim. se on peritty")})
    public Response delete(
            @ApiParam(value = "Poistettavan hakijaryhmän OID", required = true) @PathParam("oid") String oid) {
        try {
            hakijaryhmaService.deleteByOid(oid, false);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(oid)
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_POISTO)
                    .build());
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (HakijaryhmaaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
    }

    @PUT
    @Path("/siirra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response siirra(HakijaryhmaSiirraDTO dto) {
        Optional<Hakijaryhma> siirretty = hakijaryhmaService.siirra(dto);
        return siirretty.map(kaava ->
        {
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(kaava.getOid())
                    .valintaryhmaOid(Optional.ofNullable(kaava.getValintaryhma()).map(v -> v.getOid()).orElse(null))
                    .add("hakijaryhmanvalintatapajonot", Arrays.toString(Optional.ofNullable(kaava.getJonot()).orElse(Collections.<HakijaryhmaValintatapajono>emptySet())
                            .stream().map(v -> v.getOid()).toArray()))
                    .add("valintatapajonoids", Arrays.toString(Optional.ofNullable(kaava.getValintatapajonoIds()).orElse(Collections.<String>emptyList())
                            .stream().toArray()))
                    .add("kiintio", kaava.getKiintio())
                    .add("kuvaus", kaava.getKuvaus())
                    .add("laskentakaavaid", kaava.getLaskentakaavaId())
                    .add("nimi", kaava.getNimi(), dto.getNimi())
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_SIIRTO)
                    .build());
                return Response.status(Response.Status.ACCEPTED).entity(modelMapper.map(kaava, HakijaryhmaDTO.class)).build();
        }).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

}
