package fi.vm.sade.service.valintaperusteet.resource.impl;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.util.ValintaperusteetAudit.AUDIT;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.resource.ValintaperusteetResource;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.valinta.sharedutils.AuditLog;
import fi.vm.sade.valinta.sharedutils.ValintaResource;
import fi.vm.sade.valinta.sharedutils.ValintaperusteetOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@Path("valintaperusteet")
@PreAuthorize("isAuthenticated()")
@Api(
    value = "/valintaperusteet",
    description = "Resurssi laskentakaavojen ja funktiokutsujen käsittelyyn")
public class ValintaperusteetResourceImpl implements ValintaperusteetResource {
  private static final Logger LOG = LoggerFactory.getLogger(ValintaperusteetResourceImpl.class);
  @Autowired private ValintaperusteService valintaperusteService;

  @Autowired private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private ValintatapajonoService valintatapajonoService;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  private static final Logger LOGGER = LoggerFactory.getLogger(ValintaperusteetResourceImpl.class);

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("/valintatapajono/{hakukohdeOid}")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  @ApiOperation(value = "Hakee valintapajonot sijoittelulle", response = ValintatapajonoDTO.class)
  public List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(
      @ApiParam(value = "Hakukohde oid") @PathParam("hakukohdeOid") String hakukohdeOid) {
    return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOid);
  }

  @POST
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("/valintatapajono")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Override
  @ApiOperation(value = "Hakee valintapajonot sijoittelulle", response = ValintatapajonoDTO.class)
  public Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
      List<String> hakukohdeOids) {
    return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOids);
  }

  @GET
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("/{hakukohdeOid}")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Hakee valintaperusteet")
  @Override
  public List<ValintaperusteetDTO> haeValintaperusteet(
      @ApiParam(value = "Hakukohde OID") @PathParam("hakukohdeOid") String hakukohdeOid,
      @ApiParam(value = "Valinnanvaiheen järjestysluku") @QueryParam("vaihe")
          Integer valinnanVaiheJarjestysluku) {
    HakuparametritDTO hakuparametrit = new HakuparametritDTO();
    hakuparametrit.setHakukohdeOid(hakukohdeOid);
    if (valinnanVaiheJarjestysluku != null) {
      hakuparametrit.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
    }
    List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);
    return valintaperusteService.haeValintaperusteet(list);
  }

  @Override
  public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(String hakukohdeOid) {
    List<HakijaryhmaValintatapajono> hakukohteenRyhmat =
        hakijaryhmaValintatapajonoService.findByHakukohde(hakukohdeOid);
    List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
    vaiheet.stream()
        .forEachOrdered(
            vaihe -> {
              List<Valintatapajono> jonot =
                  valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
              jonot.stream()
                  .forEachOrdered(
                      jono ->
                          hakukohteenRyhmat.addAll(
                              hakijaryhmaValintatapajonoService.findHakijaryhmaByJono(
                                  jono.getOid())));
            });

    List<ValintaperusteetHakijaryhmaDTO> result = new ArrayList<>();
    for (int i = 0; i < hakukohteenRyhmat.size(); i++) {
      HakijaryhmaValintatapajono original = hakukohteenRyhmat.get(i);
      Laskentakaava laskentakaava =
          laskentakaavaService.haeLaskettavaKaava(
              original.getHakijaryhma().getLaskentakaava().getId(), Laskentamoodi.VALINTALASKENTA);
      ValintaperusteetHakijaryhmaDTO dto =
          modelMapper.map(original, ValintaperusteetHakijaryhmaDTO.class);
      // Asetetaan laskentakaavan nimi ensimmäisen funktiokutsun nimeksi
      laskentakaava
          .getFunktiokutsu()
          .getSyoteparametrit()
          .forEach(
              s -> {
                if (s.getAvain().equals("nimi")) {
                  s.setArvo(laskentakaava.getNimi());
                }
              });
      dto.setFunktiokutsu(
          modelMapper.map(laskentakaava.getFunktiokutsu(), ValintaperusteetFunktiokutsuDTO.class));
      dto.setNimi(original.getHakijaryhma().getNimi());
      dto.setKuvaus(original.getHakijaryhma().getKuvaus());
      dto.setPrioriteetti(i);
      dto.setKaytetaanRyhmaanKuuluvia(original.isKaytetaanRyhmaanKuuluvia());
      dto.setHakukohdeOid(hakukohdeOid);
      if (original.getValintatapajono() != null) {
        dto.setValintatapajonoOid(original.getValintatapajono().getOid());
      }
      result.add(dto);
    }
    return result;
  }

  @POST
  @PreAuthorize(UPDATE_CRUD)
  @Path("tuoHakukohde")
  @Consumes(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "importoi hakukohde")
  @Override
  public Response tuoHakukohde(
      @ApiParam(value = "Importoitava hakukohde") HakukohdeImportDTO hakukohde) {
    if (hakukohde == null) {
      LOG.error("Valintaperusteet sai null hakukohteen importoitavaksi!");
      throw new RuntimeException("Valintaperusteet sai null hakukohteen importoitavaksi!");
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
  @PreAuthorize(READ_UPDATE_CRUD)
  @Path("/{oid}/automaattinenSiirto")
  @Produces(MediaType.APPLICATION_JSON)
  public Boolean readAutomaattinenSijoitteluunSiirto(@PathParam("oid") String oid) {
    return valintatapajonoService.readAutomaattinenSijoitteluunSiirto(oid);
  }

  @POST
  @PreAuthorize(UPDATE_CRUD)
  @Path("/{oid}/automaattinenSiirto")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ValintatapajonoDTO updateAutomaattinenSijoitteluunSiirto(
      @PathParam("oid") String oid, Boolean arvo, @Context HttpServletRequest request) {
    ValintatapajonoDTO beforeUpdate =
        modelMapper.map(valintatapajonoService.readByOid(oid), ValintatapajonoDTO.class);
    ValintatapajonoDTO afterUpdate =
        modelMapper.map(
            valintatapajonoService.updateAutomaattinenSijoitteluunSiirto(oid, arvo),
            ValintatapajonoDTO.class);
    AuditLog.log(
        AUDIT,
        AuditLog.getUser(request),
        ValintaperusteetOperation.AUTOMAATTISEN_SIJOITTELUN_SIIRRON_PAIVITYS,
        ValintaResource.VALINTAPERUSTEET,
        oid,
        Changes.updatedDto(afterUpdate, beforeUpdate));
    return modelMapper.map(afterUpdate, ValintatapajonoDTO.class);
  }
}
