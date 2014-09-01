package fi.vm.sade.service.valintaperusteet.resource.impl;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeImportDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.service.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.resource.ValintaperusteetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;

@Component
@Path("valintaperusteet")
@PreAuthorize("isAuthenticated()")
@Api(value = "/valintaperusteet", description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class ValintaperusteetResourceImpl implements ValintaperusteetResource {

    @Autowired
    private ValintaperusteService valintaperusteService;

    private final static Logger LOGGER = LoggerFactory.getLogger(ValintaperusteetResourceImpl.class);

    @GET
    @Path("valintatapajono/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintapajonot sijoittelulle", response = ValintatapajonoDTO.class)
    public List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(
            @ApiParam(value = "Hakukohde oid") @PathParam("hakukohdeOid") String hakukohdeOid) {
        return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOid);
    }

    @GET
    @Path("{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hakee valintaperusteet")
    @Override
    @PreAuthorize(READ_UPDATE_CRUD)
    public List<ValintaperusteetDTO> haeValintaperusteet(@ApiParam(value = "Hakukohde OID") @PathParam("hakukohdeOid") String hakukohdeOid,
                                                         @ApiParam(value = "Valinnanvaiheen järjestysluku") @QueryParam("vaihe") Integer valinnanVaiheJarjestysluku) {

        HakuparametritDTO hakuparametrit = new HakuparametritDTO();
        hakuparametrit.setHakukohdeOid(hakukohdeOid);
        if(valinnanVaiheJarjestysluku != null) {
            hakuparametrit.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
        }
        List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);

        return valintaperusteService.haeValintaperusteet(list);
    }

    @POST
    @Path("tuoHakukohde")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "importoi hakukohde")
    @Override
    @PreAuthorize(CRUD)
    public Response tuoHakukohde(
            @ApiParam(value = "Importoitava hakukohde") HakukohdeImportDTO hakukohde) {
        valintaperusteService.tuoHakukohde(hakukohde);
        return Response.ok().build();
    }


}
