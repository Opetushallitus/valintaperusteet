package fi.vm.sade.service.valintaperusteet.resource.impl;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.resource.ValintaperusteetResource;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
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
//@PreAuthorize("isAuthenticated()")
@Api(value = "/valintaperusteet", description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class ValintaperusteetResourceImpl implements ValintaperusteetResource {

    @Autowired
    private ValintaperusteService valintaperusteService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(ValintaperusteetResourceImpl.class);

    @GET
    @Path("valintatapajono/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
//    @PreAuthorize(READ_UPDATE_CRUD)
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
//    @PreAuthorize(READ_UPDATE_CRUD)
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

    @Override
    public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(String hakukohdeOid) {
        List<HakijaryhmaValintatapajono> hakukohteenRyhmat = hakijaryhmaValintatapajonoService.findByHakukohde(hakukohdeOid);
        List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        vaiheet.stream().forEachOrdered(vaihe -> {
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
            jonot.stream().forEachOrdered(jono -> hakukohteenRyhmat.addAll(hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(jono.getOid())));
        });

        List<ValintaperusteetHakijaryhmaDTO> result = new ArrayList<>();
        for(int i = 0; i < hakukohteenRyhmat.size(); i++) {
            HakijaryhmaValintatapajono original = hakukohteenRyhmat.get(i);
            ValintaperusteetHakijaryhmaDTO dto = modelMapper.map(original, ValintaperusteetHakijaryhmaDTO.class);
            dto.setFunktiokutsu(modelMapper.map(original.getHakijaryhma().getLaskentakaava().getFunktiokutsu(), ValintaperusteetFunktiokutsuDTO.class));
            dto.setPrioriteetti(i);
            if(original.getValintatapajono() != null) {
                dto.setValintatapajonoOid(original.getValintatapajono().getOid());
            }
            result.add(dto);

        }
        return result;
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
