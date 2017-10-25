package fi.vm.sade.service.valintaperusteet.resource.impl;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.resource.HakijaryhmaValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.resource.ValintatapajonoResource;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import fi.vm.sade.generic.AuditLog;
import fi.vm.sade.service.valintaperusteet.util.ValintaResource;
import fi.vm.sade.service.valintaperusteet.util.ValintaperusteetOperation;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

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
    HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public HakijaryhmaValintatapajonoDTO read(String oid) {
        try {
            return modelMapper.map(hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoDTO.class);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Poistaa hakijaryhmän ja valintatapajonon välisen liitoksen")
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Liitosta ei voida poistaa, esim. se on peritty"),})
    public Response poistaHakijaryhma(@ApiParam(value = "OID", required = true) @PathParam("oid") String oid, @Context HttpServletRequest request) {
        try {
            hakijaryhmaValintatapajonoService.deleteByOid(oid, false);
            Changes changes = new Changes.Builder()
                    .removed("HAKIJARYHMA_VALINTATAPAJONO_LIITOS", oid).build();
            AuditLog.log(ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_POISTO, ValintaResource.HAKIJARYHMA_VALINTATAPAJONO, oid, changes, request);
            /*
            AUDIT.log(builder()
                    .id(username())
                    .hakijaryhmaOid(oid)
                    .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_POISTO)
                    .build());
            */
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

    @Override
    @POST
    @Path("/{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Päivittää hakijaryhmän ja valintatapajonon välistä liitosta")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Liitosta ei ole olemassa"),})
    public Response update(
            @ApiParam(value = "Päivitettävän liitoksen OID", required = true) @PathParam("oid") String oid,
            @ApiParam(value = "Liitoksen uudet tiedot", required = true) HakijaryhmaValintatapajonoDTO jono, @Context HttpServletRequest request) {
        try {
            HakijaryhmaValintatapajonoDTO beforeUpdate = modelMapper.map(hakijaryhmaValintatapajonoService.readByOid(oid), HakijaryhmaValintatapajonoDTO.class);
            HakijaryhmaValintatapajonoDTO afterUpdate = modelMapper.map(hakijaryhmaValintatapajonoService.update(oid, jono), HakijaryhmaValintatapajonoDTO.class);
            AuditLog.log(ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_PAIVITYS, ValintaResource.HAKIJARYHMA_VALINTATAPAJONO, oid, afterUpdate, beforeUpdate, request);
            /*
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
            */
            return Response.status(Response.Status.ACCEPTED).entity(afterUpdate).build();
        } catch (HakijaryhmaEiOleOlemassaException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jarjesta")
    @PreAuthorize(UPDATE_CRUD)
    @ApiOperation(value = "Järjestää valintatapajonon hakijaryhmät annetun OID-listan mukaan", response = ValintatapajonoDTO.class)
    public List<HakijaryhmaValintatapajonoDTO> jarjesta(@ApiParam(value = "OID-lista jonka mukaiseen järjestykseen valintatapajonon hakijaryhmät järjestetään", required = true) List<String> oids, @Context HttpServletRequest request) {
        List<HakijaryhmaValintatapajono> j = hakijaryhmaValintatapajonoService.jarjestaHakijaryhmat(oids);

        //For AuditLog
        String targetOid = "unknown";
        if(!j.isEmpty()) { targetOid = j.get(0).getHakukohdeViite().getOid(); }
        Map sortedOids = ImmutableMap.of("Oids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null));
        AuditLog.log(ValintaperusteetOperation.VALINTATAPAJONO_HAKIJARYHMAT_JARJESTA, ValintaResource.HAKIJARYHMA_VALINTATAPAJONO, targetOid,
                null, null, request, sortedOids);
        /*
        AUDIT.log(builder()
                .id(username())
                .add("valintatapajonooids", Optional.ofNullable(oids).map(List::toArray).map(Arrays::toString).orElse(null))
                .setOperaatio(ValintaperusteetOperation.HAKIJARYHMA_VALINTATAPAJONO_LIITOS_PAIVITYS)
                .build());
        */
        return modelMapper.mapList(j, HakijaryhmaValintatapajonoDTO.class);
    }
}
