package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.security.access.prepost.PreAuthorize;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;

/**
 * User: kwuoti Date: 15.4.2013 Time: 16.04
 */
@Path("valintakoe")
public interface ValintakoeResource {

	// @GET
	// @Path("/")
	// @Produces(MediaType.APPLICATION_JSON)
	// List<ValintakoeDTO> readAll();
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	List<ValintakoeDTO> readByOids(List<String> oids);

	@GET
	@Path("/{oid}")
	@Produces(MediaType.APPLICATION_JSON)
	ValintakoeDTO readByOid(@PathParam("oid") String oid);

	@POST
	@Path("/{oid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response update(@PathParam("oid") String oid, ValintakoeDTO valintakoe);

	@DELETE
	@Path("/{oid}")
	Response delete(@PathParam("oid") String oid);

}
