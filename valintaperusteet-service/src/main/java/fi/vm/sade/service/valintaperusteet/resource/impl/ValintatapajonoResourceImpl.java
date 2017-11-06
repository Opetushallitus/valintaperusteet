package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import com.google.common.collect.ImmutableMap;

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiLisataException;
import fi.vm.sade.sharedutils.AuditLog;
import fi.vm.sade.sharedutils.ValintaResource;
import fi.vm.sade.sharedutils.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("valintatapajono")
@PreAuthorize("isAuthenticated()")
@Api(value = "/valintatapajono", description = "Resurssi valintatapajonojen käsittelyyn")
public class ValintatapajonoResourceImpl {
    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoResourceImpl.class);

    @Autowired
    ValintatapajonoService valintatapajonoService;

    @Autowired
    HakijaryhmaService hakijaryhmaService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @Path("/kopiot")
    public Map<String, List<String>> findKopiot(@QueryParam("oid") List<String> oid) {
        try {
            return valintatapajonoService.findKopiot(oid);
        } catch (Exception e) {
            LOGGER.error("Virhe valintatapajonojen kopioiden hakemisessa!", e);
            throw e;
        }
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintatapajonon OID:n perusteella", response = ValintatapajonoDTO.class)
    public ValintatapajonoDTO readByOid(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}/jarjestyskriteeri")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee järjestyskriteerit valintatapajonon OID:n perusteella", response = JarjestyskriteeriDTO.class)
    public List<JarjestyskriteeriDTO> findJarjestyskriteeri(@ApiParam(value = "Valintatapajonon OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(jarjestyskriteeriService.findJarjestyskriteeriByJono(oid), JarjestyskriteeriDTO.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma/{hakijaryhmaOid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Liittää hakijaryhmän valintatapajonoon")
    public Response liitaHakijaryhma(
            @ApiParam(value = "Valintatapajonon OID, jolle hakijaryhmä liitetään", required = true) @PathParam("valintatapajonoOid") String valintatapajonoOid,
            @ApiParam(value = "Hakijaryhmän OID, joka valintatapajonoon liitetään", required = true) @PathParam("hakijaryhmaOid") String hakijaryhmaOid, @Context HttpServletRequest request) {
        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhmaOid);
            ImmutableMap<String, String> liitettavanHakijaryhmanOid = ImmutableMap.of("Liitettävän Hakijaryhmän Oid", hakijaryhmaOid);
            AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_LIITOS_HAKIJARYHMA, ValintaResource.VALINTATAPAJONO, valintatapajonoOid, null, null, request, liitettavanHakijaryhmanOid);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(hakijaryhmaOid)
                    .valintatapajonoOid(valintatapajonoOid)
                    .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_LIITOS_HAKIJARYHMA)
                    .build());
            */
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            LOGGER.error("Error linking hakijaryhma.", e);
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintatapajonoon liitetyt hakijaryhmät valintatapajonon OID:n perusteella")
    public List<HakijaryhmaValintatapajonoDTO> hakijaryhmat(
            @ApiParam(value = "Valintatapajonon OID", required = true) @PathParam("valintatapajonoOid") String valintatapajonoOid) {
        return modelMapper.mapList(hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(valintatapajonoOid), HakijaryhmaValintatapajonoDTO.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma")
    @ApiOperation(value = "Luo valintatapajonolle uuden hakijaryhmän")
    public Response insertHakijaryhma(
            @ApiParam(value = "Valintatapajonon OID, jolle hakijaryhmä lisätään", required = true) @PathParam("valintatapajonoOid") String valintatapajonoOid,
            @ApiParam(value = "Lisättävä hakijaryhmä", required = true) HakijaryhmaCreateDTO hakijaryhma, @Context HttpServletRequest request) {
        try {
            HakijaryhmaDTO lisattava = modelMapper.map(hakijaryhmaValintatapajonoService.lisaaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhma), HakijaryhmaDTO.class);
            AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_HAKIJARYHMA, ValintaResource.VALINTATAPAJONO, valintatapajonoOid, lisattava, null, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(lisattava.getOid())
                    .valintatapajonoOid(valintatapajonoOid)
                    .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_HAKIJARYHMA)
                    .build());
            */
            return Response.status(Response.Status.CREATED).entity(lisattava).build();
        } catch (LaskentakaavaOidTyhjaException e) {
            LOGGER.warn("Error creating hakijaryhma for valintatapajono: " + e.toString());
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        } catch (Exception e) {
            LOGGER.error("Error creating hakijaryhma for valintatapajono.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee kaikki valintatapajonot", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> findAll() {
        return modelMapper.mapList(valintatapajonoService.findAll(), ValintatapajonoDTO.class);
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää valintatapajonoa")
    public Response update(
            @ApiParam(value = "Päivitettävän valintatapajonon OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Päivitettävän valintatapajonon uudet tiedot", required = true) ValintatapajonoCreateDTO jono, @Context HttpServletRequest request) {
        try {
            ValintatapajonoDTO old = modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
            ValintatapajonoDTO update = modelMapper.map(valintatapajonoService.update(oid, jono), ValintatapajonoDTO.class);
            AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_PAIVITYS, ValintaResource.VALINTATAPAJONO, oid, update, old, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .valintatapajonoOid(oid)
                    .add("periytyy", update.getInheritance())
                    .add("prioriteetti", update.getPrioriteetti())
                    .add("aktiivinen", update.getAktiivinen())
                    .add("aloituspaikat", update.getAloituspaikat())
                    .add("automaattinenSijoitteluunSiirto", update.getautomaattinenSijoitteluunSiirto())
                    .add("eivarasijatayttoa", update.getEiVarasijatayttoa())
                    .add("kaikkiehdontayttavathyvaksytaan", update.getKaikkiEhdonTayttavatHyvaksytaan())
                    .add("kaytetaanvalintalaskentaa", update.getKaytetaanValintalaskentaa())
                    .add("kuvaus", update.getKuvaus())
                    .add("tyyppi", update.getTyyppi())
                    .add("nimi", update.getNimi())
                    .add("poissaolevataytto", update.getPoissaOlevaTaytto())
                    .add("poistetaankohylatyt", update.getPoistetaankoHylatyt())
                    .add("siirretaansijoitteluun", update.getSiirretaanSijoitteluun())
                    .add("tasapistesaanto", update.getTasapistesaanto())
                    .add("tayttojono", update.getTayttojono())
                    .add("valisijoittelu", update.getValisijoittelu())
                    .add("varasijat", update.getVarasijat())
                    .add("varasijatayttopaivat", update.getVarasijaTayttoPaivat())
                    .add("varasijojakaytetaanalkaen", update.getVarasijojaKaytetaanAlkaen())
                    .add("varasijojataytetaanasti", update.getVarasijojaTaytetaanAsti())
                    .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_PAIVITYS)
                    .build());
            */
            return Response.status(Response.Status.ACCEPTED).entity(update).build();
        } catch (ValintatapajonoaEiVoiLisataException e) {
            LOGGER.error("Error creating/updating valintatapajono.", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/jarjestyskriteeri")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää järjestyskriteerin valintatapajonolle")
    public Response insertJarjestyskriteeri(
            @ApiParam(value = "Valintatapajonon OID, jolle järjestyskriteeri lisätään", required = true) @PathParam("valintatapajonoOid") String valintatapajonoOid,
            @ApiParam(value = "Järjestyskriteeri ja laskentakaavaviite", required = true) JarjestyskriteeriInsertDTO jk, @Context HttpServletRequest request) {
        JarjestyskriteeriDTO insert = modelMapper.map(jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, jk.getJarjestyskriteeri(), null, jk.getLaskentakaavaId()), JarjestyskriteeriDTO.class);
        AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_JARJESTYSKRITEERI, ValintaResource.VALINTATAPAJONO, valintatapajonoOid, insert, null, request);
        /*AUDIT.log(builder()
                .id(username())
                .valintatapajonoOid(valintatapajonoOid)
                .jarjestyskriteeriOid(insert.getOid())
                .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_JARJESTYSKRITEERI)
                .build());
        */
        return Response.status(Response.Status.ACCEPTED).entity(insert).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonot annetun OID-listan mukaan", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> jarjesta(@ApiParam(value = "OID-lista jonka mukaiseen järjestykseen valintatapajonot järjestetään", required = true) List<String> oids, @Context HttpServletRequest request) {
        List<Valintatapajono> jarjestetytJonot = valintatapajonoService.jarjestaValintatapajonot(oids);
        ImmutableMap<String, String> jarjestetytOidit = ImmutableMap.of("Järjestetyt Oidit", oids.toArray().toString());
        AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_JARJESTA, ValintaResource.VALINTATAPAJONO, null, null, null, request, jarjestetytOidit);
        /*AUDIT.log(builder()
                .id(username())
                .add("valintatapajonooids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null))
                .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_JARJESTA)
                .build());*/
        return modelMapper.mapList(jarjestetytJonot, ValintatapajonoDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa valintatapajonon OID:n perusteella")
    public Response delete(@ApiParam(value = "Poistettavan valintatapajonon OID", required = true) @PathParam("oid") String oid, @Context HttpServletRequest request) {
        ValintatapajonoDTO poistettu = modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
        valintatapajonoService.deleteByOid(oid);
        AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_POISTO, ValintaResource.VALINTATAPAJONO, oid, null, poistettu, request);
        /*AUDIT.log(builder()
                .id(username())
                .valintatapajonoOid(oid)
                .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_POISTO)
                .build());*/
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
