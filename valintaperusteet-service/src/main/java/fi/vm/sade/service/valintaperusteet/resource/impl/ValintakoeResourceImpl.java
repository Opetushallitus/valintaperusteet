package fi.vm.sade.service.valintaperusteet.resource.impl;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.resource.ValintakoeResource;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.generic.AuditLog;
import fi.vm.sade.service.valintaperusteet.util.ValintaResource;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.*;

@Component
@Path("valintakoe")
@PreAuthorize("isAuthenticated()")
@Api(value = "/valintakoe", description = "Resurssi valintakokeiden käsittelyyn")
public class ValintakoeResourceImpl implements ValintakoeResource {

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    /**
     * @Transactional Heittaa lazy initin. Ehka modelmapper servicen puolelle?
     */
    @Transactional
    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintakokeen OID:n perusteella", response = ValintakoeDTO.class)
    public ValintakoeDTO readByOid(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        return modelMapper.map(valintakoeService.readByOid(oid), ValintakoeDTO.class);
    }

    @Transactional
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintakokeen OID:n perusteella", response = ValintakoeDTO.class)
    public List<ValintakoeDTO> readByOids(@ApiParam(value = "OID", required = true) List<String> oids) {
        return modelMapper.mapList(valintakoeService.readByOids(oids), ValintakoeDTO.class);
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää valintakoetta")
    public Response update(
            @ApiParam(value = "OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Valintakokeen uudet tiedot", required = true) ValintakoeDTO valintakoe, @Context HttpServletRequest request) {
        ValintakoeDTO old = modelMapper.map(valintakoeService.readByOid(oid), ValintakoeDTO.class);
        ValintakoeDTO update = modelMapper.map(valintakoeService.update(oid, valintakoe), ValintakoeDTO.class);
        AuditLog.log(ValintaperusteetOperation.VALINTAKOE_PAIVITYS, ValintaResource.VALINTAKOE, oid, update, old, request);
        /*AUDIT.log(builder()
                .id(username())
                .valintakoeOid(oid)
                .add("aktiivinen", update.getAktiivinen())
                .add("selvitettytunniste", update.getSelvitettyTunniste())
                .add("kuvaus", update.getKuvaus())
                .add("nimi", update.getNimi())
                .add("tunniste", update.getTunniste())
                .add("kutsutaankokaikki", update.getKutsutaankoKaikki())
                .add("lahetetaankokoekutsut", update.getLahetetaankoKoekutsut())
                .add("kutsunkohdeavain", update.getKutsunKohdeAvain())
                .setOperaatio(ValintaperusteetOperation.VALINTAKOE_PAIVITYS)
                .build());*/
        return Response.status(Response.Status.ACCEPTED).entity(update).build();
    }

    @DELETE
    @Path("/{oid}")
    @PreAuthorize(CRUD)
    @ApiOperation(value = "Poistaa valintakokeen OID:n perusteella")
    public Response delete(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid, @Context HttpServletRequest request) {
        ValintakoeDTO old = modelMapper.map(valintakoeService.readByOid(oid), ValintakoeDTO.class);
        valintakoeService.deleteByOid(oid);
        AuditLog.log(ValintaperusteetOperation.VALINTAKOE_POISTO, ValintaResource.VALINTAKOE, oid, null, old, request);
        /*AUDIT.log(builder()
                .id(username())
                .valintakoeOid(oid)
                .setOperaatio(ValintaperusteetOperation.VALINTAKOE_POISTO)
                .build());*/
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
