package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.service.valintaperusteet.resource.ValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.42 To
 * change this template use File | Settings | File Templates.
 */
@Component
@Path("valintatapajono")
@PreAuthorize("isAuthenticated()")
@Api(value = "/valintatapajono", description = "Resurssi valintatapajonojen käsittelyyn")
public class ValintatapajonoResourceImpl implements ValintatapajonoResource {

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
    public List<JarjestyskriteeriDTO> findJarjestyskriteeri(
            @ApiParam(value = "Valintatapajonon OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.mapList(jarjestyskriteeriService.findJarjestyskriteeriByJono(oid),
                JarjestyskriteeriDTO.class);
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
        return modelMapper.mapList(hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(valintatapajonoOid),
                HakijaryhmaValintatapajonoDTO.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{valintatapajonoOid}/hakijaryhma")
    @ApiOperation(value = "Luo valintatapajonolle uuden hakijaryhmän")
    public Response insertHakijaryhma(
            @ApiParam(value = "Valintatapajonon OID, jolle hakijaryhmä lisätään", required = true) @PathParam("valintatapajonoOid") String valintatapajonoOid,
            @ApiParam(value = "Lisättävä hakijaryhmä", required = true) HakijaryhmaCreateDTO hakijaryhma)  {
            try {
                HakijaryhmaDTO lisattava = modelMapper.map(hakijaryhmaValintatapajonoService.lisaaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhma), HakijaryhmaDTO.class);
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
            throws IOException, JSONException {

        JarjestyskriteeriDTO insert = modelMapper.map(
                jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid,
                        jk.getJarjestyskriteeri(), null, jk.getLaskentakaavaId()), JarjestyskriteeriDTO.class);
        return Response.status(Response.Status.ACCEPTED).entity(insert).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonot annetun OID-listan mukaan", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> jarjesta(
            @ApiParam(value = "OID-lista jonka mukaiseen järjestykseen valintatapajonot järjestetään", required = true) List<String> oids) {
        return modelMapper.mapList(valintatapajonoService.jarjestaValintatapajonot(oids), ValintatapajonoDTO.class);
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa valintatapajonon OID:n perusteella")
    public Response delete(
            @ApiParam(value = "Poistettavan valintatapajonon OID", required = true) @PathParam("oid") String oid) {
        valintatapajonoService.deleteByOid(oid);
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
