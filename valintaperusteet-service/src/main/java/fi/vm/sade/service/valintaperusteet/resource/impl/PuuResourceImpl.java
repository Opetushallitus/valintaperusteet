package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperustePuuDTO;
import fi.vm.sade.service.valintaperusteet.resource.PuuResource;
import fi.vm.sade.service.valintaperusteet.resource.ValintaryhmaResource;
import fi.vm.sade.service.valintaperusteet.service.PuuService;

@Component
@Path("puu")
@PreAuthorize("isAuthenticated()")
@Api(value = "/puu", description = "Resurssi valintaperustepuun hakemiseen")
public class PuuResourceImpl implements PuuResource {
    @Autowired
    private PuuService puuService;

    protected final static Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize(READ_UPDATE_CRUD)
    @ApiOperation(value = "Hakee valintaperustepuun annettujen parametrien perusteella", response = ValintaperustePuuDTO.class)
    public List<ValintaperustePuuDTO> search(@ApiParam(value = "Hakulauseke") @QueryParam("q") String searchString,
                                             @ApiParam(value = "Haun OID") @QueryParam("hakuOid") String hakuOid,
                                             @ApiParam(value = "Tila") @QueryParam("tila") List<String> tila,
                                             @ApiParam(value = "Hakukohteet") @QueryParam("hakukohteet") @DefaultValue("true") boolean hakukohteet, @QueryParam("kohdejoukko") @DefaultValue("") String kohdejoukko,
                                             @QueryParam("valintaryhma") @DefaultValue("") String valintaryhma) {
        return puuService.search(hakuOid, tila, searchString, hakukohteet, kohdejoukko, valintaryhma);
    }
}
