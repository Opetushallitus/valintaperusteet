package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
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

import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoUpdateDTO;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.resource.ValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;

import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.*;
import static fi.vm.sade.auditlog.valintaperusteet.LogMessage.builder;
import fi.vm.sade.auditlog.valintaperusteet.ValintaperusteetOperation;

@Component
@Path("hakijaryhma_valintatapajono")
@PreAuthorize("isAuthenticated()")
@Api(value = "/hakijaryhma_valintatapajono", description = "Resurssi hakijaryhmien ja valintatapajonojen välisten liitosten käsittelyyn")
public class HakijaryhmaValintatapajonoResourceImpl implements HakijaryhmaValintatapajonoResource {
    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoResource.class);

    @Autowired
    ValintatapajonoService jonoService;

    @Autowired
    ValintakoeService valintakoeService;

    @Autowired
    HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public HakijaryhmaValintatapajonoUpdateDTO read(String oid) {
        try {
            return modelMapper.map(hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoUpdateDTO.class);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän ja valintatapajonon välisen liitoksen")
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Liitosta ei voida poistaa, esim. se on peritty"),})
    public Response poistaHakijaryhma(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
        try {
            hakijaryhmaValintatapajonoService.deleteByOid(oid, false);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(oid)
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_POISTO)
                    .build());
            return Response.status(Response.Status.OK).build();
        } catch (HakijaryhmaaEiVoiPoistaaException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            LOGGER.error("Error removing hakijaryhma.", e);
            Map map = new HashMap();
            map.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
        }
    }

    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakijaryhmän ja valintatapajonon välistä liitosta")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Liitosta ei ole olemassa"),})
    public Response update(
            @ApiParam(value = "Päivitettävän liitoksen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Liitoksen uudet tiedot", required = true) HakijaryhmaValintatapajonoUpdateDTO jono) {
        try {
            HakijaryhmaValintatapajonoDTO update = modelMapper.map(hakijaryhmaValintatapajonoService.update(oid, jono), HakijaryhmaValintatapajonoDTO.class);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaValintatapajonoOid(update.getOid())
                    .add("aktiivinen", update.getAktiivinen())
                    .add("kiintio", update.getKiintio())
                    .add("kuvaus", update.getKuvaus())
                    .add("nimi", update.getNimi())
                    .add("prioriteetti", update.getPrioriteetti())
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_PAIVITYS)
                    .build());
            return Response.status(Response.Status.ACCEPTED).entity(update).build();
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonon hakijaryhmät argumentin mukaiseen järjestykseen")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "OID-lista on tyhjä"),})
    public List<HakijaryhmaValintatapajonoUpdateDTO> jarjesta(
            @ApiParam(value = "Päivitettävän liitoksen oid", required = true) @PathParam("oid") String hakijaryhmaValintatapajonoOid,
            @ApiParam(value = "Hakijaryhmien uusi järjestys", required = true) List<String> oids) {
        try {
            List<HakijaryhmaValintatapajono> hj = hakijaryhmaValintatapajonoService.jarjestaHakijaryhmat(hakijaryhmaValintatapajonoOid, oids);
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaValintatapajonoOid(hakijaryhmaValintatapajonoOid)
                    .add("hakijaryhmanValintatapajonot", Arrays.toString(Optional.ofNullable(hj).orElse(Collections.<HakijaryhmaValintatapajono>emptyList())
                            .stream().map(v -> v.getOid()).toArray()))
                    .setOperaatio(ValintaperusteetOperation.VALINTATAPAJONO_HAKIJARYHMAT_JARJESTA)
                    .build());
            return modelMapper.mapList(hj, HakijaryhmaValintatapajonoUpdateDTO.class);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

}
