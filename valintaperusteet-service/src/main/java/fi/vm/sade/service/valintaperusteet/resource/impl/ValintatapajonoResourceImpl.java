package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.io.IOException;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

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
            return new HashMap<>();
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
            @ApiParam(value = "Hakijaryhmän OID, joka valintatapajonoon liitetään", required = true) @PathParam("hakijaryhmaOid") String hakijaryhmaOid) {
        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhmaOid);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(hakijaryhmaOid)
                    .valintatapajonoOid(valintatapajonoOid)
                    .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_LIITOS_HAKIJARYHMA)
                    .build());
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
            @ApiParam(value = "Lisättävä hakijaryhmä", required = true) HakijaryhmaCreateDTO hakijaryhma) {
        try {
            HakijaryhmaDTO lisattava = modelMapper.map(hakijaryhmaValintatapajonoService.lisaaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhma), HakijaryhmaDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(lisattava.getOid())
                    .valintatapajonoOid(valintatapajonoOid)
                    .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_HAKIJARYHMA)
                    .build());
            return Response.status(Response.Status.CREATED).entity(lisattava).build();
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
            @ApiParam(value = "Päivitettävän valintatapajonon uudet tiedot", required = true) ValintatapajonoCreateDTO jono) {
        ValintatapajonoDTO update = modelMapper.map(valintatapajonoService.update(oid, jono), ValintatapajonoDTO.class);
        AUDIT.log(builder()
                .id(username())
                .valintatapajonoOid(oid)
                .add("periytyy", update.getInheritance())
                .add("prioriteetti",update.getPrioriteetti())
                .add("aktiivinen",update.getAktiivinen())
                .add("aloituspaikat",update.getAloituspaikat())
                .add("automaattinenlaskentaansiirto",update.getAutomaattinenLaskentaanSiirto())
                .add("eivarasijatayttoa", update.getEiVarasijatayttoa())
                .add("kaikkiehdontayttavathyvaksytaan", update.getKaikkiEhdonTayttavatHyvaksytaan())
                .add("kaytetaanvalintalaskentaa", update.getKaytetaanValintalaskentaa())
                .add("kuvaus", update.getKuvaus())
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
        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/jarjestyskriteeri")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Lisää järjestyskriteerin valintatapajonolle")
    public Response insertJarjestyskriteeri(
            @ApiParam(value = "Valintatapajonon OID, jolle järjestyskriteeri lisätään", required = true) @PathParam("valintatapajonoOid") String valintatapajonoOid,
            @ApiParam(value = "Järjestyskriteeri ja laskentakaavaviite", required = true) JarjestyskriteeriInsertDTO jk)
            throws IOException {
        JarjestyskriteeriDTO insert = modelMapper.map(jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, jk.getJarjestyskriteeri(), null, jk.getLaskentakaavaId()), JarjestyskriteeriDTO.class);
        AUDIT.log(builder()
                .id(username())
                .valintatapajonoOid(valintatapajonoOid)
                .jarjestyskriteeriOid(insert.getOid())
                .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_LISAYS_JARJESTYSKRITEERI)
                .build());
        return Response.status(Response.Status.ACCEPTED).entity(insert).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonot annetun OID-listan mukaan", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> jarjesta(@ApiParam(value = "OID-lista jonka mukaiseen järjestykseen valintatapajonot järjestetään", required = true) List<String> oids) {
        List<Valintatapajono> j = valintatapajonoService.jarjestaValintatapajonot(oids);
        AUDIT.log(builder()
                .id(username())
                .add("valintatapajonooids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null))
                .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_JARJESTA)
                .build());
        return modelMapper.mapList(j, ValintatapajonoDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa valintatapajonon OID:n perusteella")
    public Response delete(@ApiParam(value = "Poistettavan valintatapajonon OID", required = true) @PathParam("oid") String oid) {
        valintatapajonoService.deleteByOid(oid);
        AUDIT.log(builder()
                .id(username())
                .valintatapajonoOid(oid)
                .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_POISTO)
                .build());
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
