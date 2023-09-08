package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.UPDATE_CRUD;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public abstract class AbstractValintaperusteetResource {
  protected static final Logger LOG =
      LoggerFactory.getLogger(AbstractValintaperusteetResource.class);
  @Autowired protected ValintaperusteService valintaperusteService;

  @Autowired protected HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired protected ValinnanVaiheService valinnanVaiheService;

  @Autowired protected ValintatapajonoService valintatapajonoService;

  @Autowired protected LaskentakaavaService laskentakaavaService;

  @Autowired protected ValintaperusteetModelMapper modelMapper;

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(
      value = "/valintatapajono/{hakukohdeOid}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee valintapajonot sijoittelulle")
  public List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(
      @PathVariable("hakukohdeOid") final String hakukohdeOid) {
    return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOid);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @PostMapping(
      value = "/valintatapajono",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee valintapajonot sijoittelulle")
  public Map<String, List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(
      @RequestBody final List<String> hakukohdeOids) {
    return valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeOids);
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/{hakukohdeOid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Hakee valintaperusteet")
  public List<ValintaperusteetDTO> haeValintaperusteet(
      @PathVariable("hakukohdeOid") final String hakukohdeOid,
      @Parameter(description = "Valinnanvaiheen järjestysluku")
          @RequestParam(value = "vaihe", required = false)
          final Integer valinnanVaiheJarjestysluku) {
    HakuparametritDTO hakuparametrit = new HakuparametritDTO();
    hakuparametrit.setHakukohdeOid(hakukohdeOid);
    if (valinnanVaiheJarjestysluku != null) {
      hakuparametrit.setValinnanVaiheJarjestysluku(valinnanVaiheJarjestysluku);
    }
    List<HakuparametritDTO> list = Arrays.asList(hakuparametrit);
    return valintaperusteService.haeValintaperusteet(list);
  }

  @GetMapping(value = "/hakijaryhma/{hakukohdeOid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ValintaperusteetHakijaryhmaDTO> haeHakijaryhmat(
      @PathVariable("hakukohdeOid") final String hakukohdeOid) {
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

  @PreAuthorize(UPDATE_CRUD)
  @PostMapping(
      value = "/tuoHakukohde",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "importoi hakukohde")
  public ResponseEntity<Object> tuoHakukohde(@RequestBody final HakukohdeImportDTO hakukohde) {
    if (hakukohde == null) {
      LOG.error("Valintaperusteet sai null hakukohteen importoitavaksi!");
      throw new RuntimeException("Valintaperusteet sai null hakukohteen importoitavaksi!");
    }
    try {
      valintaperusteService.tuoHakukohde(hakukohde);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      LOG.error(
          "Hakukohteen importointi valintaperusteisiin epaonnistui! {}",
          hakukohde.getHakukohdeOid());
      throw e;
    }
  }

  @PreAuthorize(READ_UPDATE_CRUD)
  @GetMapping(value = "/{oid}/automaattinenSiirto", produces = MediaType.APPLICATION_JSON_VALUE)
  public Boolean readAutomaattinenSijoitteluunSiirto(@PathVariable("oid") final String oid) {
    return valintatapajonoService.readAutomaattinenSijoitteluunSiirto(oid);
  }
}
