package fi.vm.sade.service.valintaperusteet.resource.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeImportDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeJaValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetFunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetHakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;

/**
 * 
 * @author jussi jartamo
 *
 */
@Component
@Path("valintalaskentakoostepalvelu")
public class ValintalaskentakoostepalveluResourceImpl {
	private static final Logger LOG = LoggerFactory
			.getLogger(ValintaperusteetResourceImpl.class);
	@Autowired
	private ValintaperusteService valintaperusteService;

	@Autowired
	private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

	@Autowired
	private ValinnanVaiheService valinnanVaiheService;

	@Autowired
	private ValintatapajonoService valintatapajonoService;

	@Autowired
	private LaskentakaavaService laskentakaavaService;

	@Autowired
	private ValintaperusteetModelMapper modelMapper;
	
	@Autowired
	private HakukohdeService hakukohdeService;

	@Autowired
	private ValintakoeService valintakoeService;

	@Transactional
	@POST
	@Path("valintakoe/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Hakee valintakokeen OID:n perusteella", response = ValintakoeDTO.class)
	public List<ValintakoeDTO> readByOids(
			@ApiParam(value = "OID", required = true) List<String> oids) {
		return modelMapper.mapList(valintakoeService.readByOids(oids),
				ValintakoeDTO.class);
	}
	
	@GET
	@Path("hakukohde/haku/{hakuOid}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Hakee haun hakukohteet", response = HakukohdeViiteDTO.class)
	public List<HakukohdeViiteDTO> haunHakukohteet(
			@ApiParam(value = "hakuOid", required = true) @PathParam("hakuOid") String hakuOid) {
		return modelMapper.mapList(hakukohdeService.haunHakukohteet(hakuOid),
				HakukohdeViiteDTO.class);
	}
	@GET
	@Path("hakukohde/{oid}/ilmanlaskentaa")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Palauttaa valintatapajonot, jossa ei käytetä laskentaa", response = ValintatapajonoDTO.class)
	public List<ValinnanVaiheJonoillaDTO> ilmanLaskentaa(
			@PathParam("oid") String oid) {
		return modelMapper.mapList(hakukohdeService.ilmanLaskentaa(oid),
				ValinnanVaiheJonoillaDTO.class);
	}
	@GET
	@Path("hakukohde/avaimet/{oid}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Hakee hakukohteen syötettävät tiedot", response = ValintaperusteDTO.class)
	public List<ValintaperusteDTO> findAvaimet(
			@ApiParam(value = "Hakukohde OID", required = true) @PathParam("oid") String oid) {
		return laskentakaavaService.findAvaimetForHakukohde(oid);
	}
	@POST
	@Path("hakukohde/valintakoe")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Hakee hakukohteen valintakokeet OID:n perusteella", response = ValintakoeDTO.class)
	public List<HakukohdeJaValintakoeDTO> valintakoesForHakukohteet(
			List<String> oids) {
		return oids.stream()
				//
				.map(oid -> {
					List<ValintakoeDTO> valintakoeDtos = modelMapper.mapList(
							valintakoeService
									.findValintakoesByValinnanVaihes(valinnanVaiheService
											.findByHakukohde(oid.toString())),
							ValintakoeDTO.class);
					if (valintakoeDtos == null || valintakoeDtos.isEmpty()) {
						return null;
					}
					// LOG.error("{}", new GsonBuilder().setPrettyPrinting()
					// .create().toJson(valintakoeDtos));
					return new HakukohdeJaValintakoeDTO(oid, valintakoeDtos);
				})
				//
				.filter(Objects::nonNull)
				//
				.collect(Collectors.toList());
	}
	
	@GET
	@Path("valintaperusteet/{hakukohdeOid}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Hakee valintaperusteet")
	public List<ValintaperusteetDTO> haeValintaperusteet(
			@ApiParam(value = "Hakukohde OID") @PathParam("hakukohdeOid") String hakukohdeOid,
			@ApiParam(value = "Valinnanvaiheen järjestysluku") @QueryParam("vaihe") Integer valinnanVaiheJarjestysluku) {

		HakuparametritDTO hakuparametrit = new HakuparametritDTO();
		hakuparametrit.setHakukohdeOid(hakukohdeOid);
		if (valinnanVaiheJarjestysluku != null) {
			hakuparametrit
					.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
		}
		List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);

		return valintaperusteService.haeValintaperusteet(list);
	}
	
	@POST
	@Path("valintaperusteet/tuoHakukohde")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "importoi hakukohde")
	public Response tuoHakukohde(
			@ApiParam(value = "Importoitava hakukohde") HakukohdeImportDTO hakukohde) {
		if (hakukohde == null) {
			LOG.error("Valintaperusteet sai null hakukohteen importoitavaksi!");
			throw new RuntimeException(
					"Valintaperusteet sai null hakukohteen importoitavaksi!");
		}
		try {
			valintaperusteService.tuoHakukohde(hakukohde);
			return Response.ok().build();
		} catch (Exception e) {
			LOG.error(
					"Hakukohteen importointi valintaperusteisiin epaonnistui! {}",
					hakukohde.getHakukohdeOid());
			throw e;
		}
	}
	
	@GET
    @Path("valintaperusteet/hakijaryhma/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
	public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(@PathParam("hakukohdeOid") String hakukohdeOid) {
		List<HakijaryhmaValintatapajono> hakukohteenRyhmat = hakijaryhmaValintatapajonoService
				.findByHakukohde(hakukohdeOid);
		List<ValinnanVaihe> vaiheet = valinnanVaiheService
				.findByHakukohde(hakukohdeOid);
		vaiheet.stream().forEachOrdered(
				vaihe -> {
					List<Valintatapajono> jonot = valintatapajonoService
							.findJonoByValinnanvaihe(vaihe.getOid());
					jonot.stream().forEachOrdered(
							jono -> hakukohteenRyhmat
									.addAll(hakijaryhmaValintatapajonoService
											.findHakijaryhmaByJono(jono
													.getOid())));
				});

		List<ValintaperusteetHakijaryhmaDTO> result = new ArrayList<>();
		for (int i = 0; i < hakukohteenRyhmat.size(); i++) {
			HakijaryhmaValintatapajono original = hakukohteenRyhmat.get(i);
			Laskentakaava laskentakaava = laskentakaavaService
					.haeLaskettavaKaava(original.getHakijaryhma()
							.getLaskentakaava().getId(),
							Laskentamoodi.VALINTALASKENTA);

			ValintaperusteetHakijaryhmaDTO dto = modelMapper.map(original,
					ValintaperusteetHakijaryhmaDTO.class);

			// Asetetaan laskentakaavan nimi ensimmäisen funktiokutsun nimeksi
			laskentakaava.getFunktiokutsu().getSyoteparametrit().forEach(s -> {
				if (s.getAvain().equals("nimi")) {
					s.setArvo(laskentakaava.getNimi());
				}
			});

			dto.setFunktiokutsu(modelMapper.map(
					laskentakaava.getFunktiokutsu(),
					ValintaperusteetFunktiokutsuDTO.class));
			dto.setNimi(original.getHakijaryhma().getNimi());
			dto.setKuvaus(original.getHakijaryhma().getKuvaus());
			dto.setPrioriteetti(i);
            dto.setKaytetaanRyhmaanKuuluvia(original.isKaytetaanRyhmaanKuuluvia());
			dto.setHakukohdeOid(hakukohdeOid);
			if (original.getValintatapajono() != null) {
				dto.setValintatapajonoOid(original.getValintatapajono()
						.getOid());
			}
			result.add(dto);

		}
		return result;
	}
}
