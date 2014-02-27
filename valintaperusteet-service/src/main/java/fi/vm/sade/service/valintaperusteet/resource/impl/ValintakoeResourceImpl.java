package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.resource.ValintakoeResource;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;

/**
 * User: kwuoti Date: 15.4.2013 Time: 16.04
 */
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
	public ValintakoeDTO readByOid(
			@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
		return modelMapper.map(valintakoeService.readByOid(oid),
				ValintakoeDTO.class);
	}

	@POST
	@Path("/{oid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize(UPDATE_CRUD)
	@ApiOperation(value = "Päivittää valintakoetta")
	public Response update(
			@ApiParam(value = "OID", required = true) @PathParam("oid") String oid,
			@ApiParam(value = "Valintakokeen uudet tiedot", required = true) ValintakoeDTO valintakoe) {
		ValintakoeDTO update = modelMapper.map(
				valintakoeService.update(oid, valintakoe), ValintakoeDTO.class);
		return Response.status(Response.Status.ACCEPTED).entity(update).build();
	}

	@DELETE
	@Path("/{oid}")
	@PreAuthorize(CRUD)
	@ApiOperation(value = "Poistaa valintakokeen OID:n perusteella")
	public Response delete(
			@ApiParam(value = "OID", required = true) @PathParam("oid") String oid) {
		valintakoeService.deleteByOid(oid);
		return Response.status(Response.Status.ACCEPTED).build();
	}

}
