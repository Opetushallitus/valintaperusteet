package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaValintatapajonoOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaValintatapajonoKopioija;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaValintatapajonoUtil;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HakijaryhmaValintatapajonoServiceImpl implements HakijaryhmaValintatapajonoService {
  @Autowired private HakijaryhmaDAO hakijaryhmaDAO;

  @Autowired private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

  @Autowired private OidService oidService;

  @Lazy @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private ValintatapajonoService valintapajonoService;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

  private static HakijaryhmaValintatapajonoKopioija kopioija =
      new HakijaryhmaValintatapajonoKopioija();

  private HakijaryhmaValintatapajono haeHakijaryhmaValintatapajono(String oid) {
    HakijaryhmaValintatapajono hakijaryhma = hakijaryhmaValintatapajonoDAO.readByOid(oid);
    if (hakijaryhma == null) {
      throw new HakijaryhmaEiOleOlemassaException(
          "Hakijaryhmavalintatapajono (" + oid + ") ei ole olemassa", oid);
    }
    return hakijaryhma;
  }

  @Override
  public List<HakijaryhmaValintatapajono> findHakijaryhmaByJono(String oid) {
    return hakijaryhmaValintatapajonoDAO.findByValintatapajono(oid);
  }

  @Override
  public List<HakijaryhmaValintatapajono> findHakijaryhmaByJonos(List<String> oids) {
    List<HakijaryhmaValintatapajono> byJonos =
        hakijaryhmaValintatapajonoDAO.findByValintatapajonos(oids);
    return byJonos;
  }

  @Override
  public HakijaryhmaValintatapajono readByOid(String oid) {
    return haeHakijaryhmaValintatapajono(oid);
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> hakukohdeOids) {
    return hakijaryhmaValintatapajonoDAO.findByHakukohteet(hakukohdeOids);
  }

  @Override
  public Hakijaryhma lisaaHakijaryhmaValintatapajonolle(
      String valintatapajonoOid, HakijaryhmaCreateDTO dto) {
    if (dto.getLaskentakaavaId() == null) {
      throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
    }
    Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
    Valintatapajono valintatapajono = valintapajonoService.readByOid(valintatapajonoOid);
    HakijaryhmaValintatapajono edellinenHakijaryhma =
        hakijaryhmaValintatapajonoDAO.haeValintatapajononViimeinenHakijaryhma(valintatapajonoOid);
    hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
    hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
    hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
    hakijaryhma.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
    hakijaryhma.setLaskentakaava(
        laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
    hakijaryhma.setHakijaryhmatyyppikoodi(
        hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(
            dto.getHakijaryhmatyyppikoodi()));
    Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
    HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
    jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
    jono.setHakijaryhma(hakijaryhma);
    jono.setValintatapajono(valintatapajono);
    jono.setAktiivinen(true);
    jono.setEdellinen(edellinenHakijaryhma);
    jono.setKiintio(hakijaryhma.getKiintio());
    jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
    jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
    jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
    jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
    hakijaryhmaValintatapajonoDAO.insert(jono);
    for (Valintatapajono kopio : valintatapajono.getKopioValintatapajonot()) {
      lisaaValintatapajonolleKopioMasterHakijaryhmasta(kopio, jono, edellinenHakijaryhma);
    }
    return lisatty;
  }

  @Override
  public void kopioiValintatapajononHakijaryhmaValintatapajonot(
      Valintatapajono lahdeValintatapajono,
      Valintatapajono kohdeValintatapajono,
      JuureenKopiointiCache kopiointiCache) {
    List<HakijaryhmaValintatapajono> jonot =
        hakijaryhmaValintatapajonoDAO.findByValintatapajono(lahdeValintatapajono.getOid());
    Collections.reverse(jonot);
    for (HakijaryhmaValintatapajono jono : jonot) {
      HakijaryhmaValintatapajono kopio =
          HakijaryhmaValintatapajonoUtil.teeKopioMasterista(jono, kopiointiCache);
      kopio.setValintatapajono(kohdeValintatapajono);
      kopio.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
      HakijaryhmaValintatapajono lisatty = hakijaryhmaValintatapajonoDAO.insert(kopio);
      kohdeValintatapajono.getHakijaryhmat().add(lisatty);
      if (kopiointiCache != null) {
        kopiointiCache.kopioidutHakijaryhmaValintapajonot.put(jono.getId(), lisatty);
      }
    }
  }

  private void lisaaValintatapajonolleKopioMasterHakijaryhmasta(
      Valintatapajono valintatapajono,
      HakijaryhmaValintatapajono master,
      HakijaryhmaValintatapajono edellinenMaster) {
    HakijaryhmaValintatapajono kopio =
        HakijaryhmaValintatapajonoUtil.teeKopioMasterista(master, null);
    kopio.setValintatapajono(valintatapajono);
    kopio.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
    List<HakijaryhmaValintatapajono> jonot =
        hakijaryhmaValintatapajonoDAO.findByValintatapajono(valintatapajono.getOid());
    kopio.setEdellinen(LinkitettavaJaKopioitavaUtil.kopioTaiViimeinen(edellinenMaster, jonot));
    HakijaryhmaValintatapajono lisatty = hakijaryhmaValintatapajonoDAO.insert(kopio);
    for (Valintatapajono jonokopio : valintatapajono.getKopioValintatapajonot()) {
      lisaaValintatapajonolleKopioMasterHakijaryhmasta(jonokopio, lisatty, lisatty.getEdellinen());
    }
  }

  @Override
  public Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, HakijaryhmaCreateDTO dto) {
    if (dto.getLaskentakaavaId() == null) {
      throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
    }
    Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
    hakijaryhma.setId(null);
    HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
    HakijaryhmaValintatapajono edellinenHakijaryhma =
        hakijaryhmaValintatapajonoDAO.haeHakukohteenViimeinenHakijaryhma(hakukohdeOid);
    hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
    hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
    hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
    hakijaryhma.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
    hakijaryhma.setLaskentakaava(
        laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
    hakijaryhma.setHakijaryhmatyyppikoodi(
        hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(
            dto.getHakijaryhmatyyppikoodi()));
    Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
    HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
    jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
    jono.setHakijaryhma(hakijaryhma);
    jono.setHakukohdeViite(hakukohde);
    jono.setAktiivinen(true);
    jono.setEdellinen(edellinenHakijaryhma);
    jono.setKiintio(hakijaryhma.getKiintio());
    jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
    jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
    jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
    jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
    hakijaryhmaValintatapajonoDAO.insert(jono);
    return lisatty;
  }

  @Override
  public HakijaryhmaValintatapajonoDTO delete(String hakijaryhmaValintatapajonoOid) {
    HakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        haeHakijaryhmaValintatapajono(hakijaryhmaValintatapajonoOid);
    if (hakijaryhmaValintatapajono.getMaster() != null) {
      throw new HakijaryhmaaEiVoiPoistaaException("HakijaryhmaValintatapajono on peritty.");
    }
    HakijaryhmaValintatapajonoDTO dto =
        modelMapper.map(hakijaryhmaValintatapajono, HakijaryhmaValintatapajonoDTO.class);
    delete(hakijaryhmaValintatapajono);
    return dto;
  }

  @Override
  public void delete(HakijaryhmaValintatapajono hakijaryhmaValintatapajono) {
    hakijaryhmaValintatapajonoDAO.delete(hakijaryhmaValintatapajono);
  }

  // CRUD
  @Override
  public HakijaryhmaValintatapajono update(String oid, HakijaryhmaValintatapajonoDTO dto) {
    HakijaryhmaValintatapajono managedObject = haeHakijaryhmaValintatapajono(oid);
    HakijaryhmaValintatapajono updatedJono = new HakijaryhmaValintatapajono();
    updatedJono.setOid(dto.getOid());
    updatedJono.setAktiivinen(dto.getAktiivinen());
    updatedJono.setKiintio(dto.getKiintio());
    updatedJono.setKaytaKaikki(dto.isKaytaKaikki());
    updatedJono.setTarkkaKiintio(dto.isTarkkaKiintio());
    updatedJono.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
    updatedJono.setHakijaryhmatyyppikoodi(
        hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(
            dto.getHakijaryhmatyyppikoodi()));
    return LinkitettavaJaKopioitavaUtil.paivita(managedObject, updatedJono, kopioija);
  }

  @Override
  public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {
    Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(hakijaryhmaOid);
    Valintatapajono valintatapajono = valintapajonoService.readByOid(valintatapajonoOid);
    HakijaryhmaValintatapajono edellinenHakijaryhma =
        hakijaryhmaValintatapajonoDAO.haeValintatapajononViimeinenHakijaryhma(valintatapajonoOid);
    HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
    jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
    jono.setHakijaryhma(hakijaryhma);
    jono.setValintatapajono(valintatapajono);
    jono.setAktiivinen(true);
    jono.setEdellinen(edellinenHakijaryhma);
    jono.setKiintio(hakijaryhma.getKiintio());
    jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
    jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
    jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
    jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
    hakijaryhmaValintatapajonoDAO.insert(jono);
    for (Valintatapajono kopio : valintatapajono.getKopioValintatapajonot()) {
      lisaaValintatapajonolleKopioMasterHakijaryhmasta(kopio, jono, edellinenHakijaryhma);
    }
  }

  @Override
  public void liitaHakijaryhmaHakukohteelle(String hakukohdeOid, String hakijaryhmaOid) {
    Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(hakijaryhmaOid);
    HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
    HakijaryhmaValintatapajono edellinenHakijaryhma =
        hakijaryhmaValintatapajonoDAO.haeHakukohteenViimeinenHakijaryhma(hakukohdeOid);
    HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
    jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
    jono.setHakijaryhma(hakijaryhma);
    jono.setHakukohdeViite(hakukohde);
    jono.setAktiivinen(true);
    jono.setEdellinen(edellinenHakijaryhma);
    jono.setKiintio(hakijaryhma.getKiintio());
    jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
    jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
    jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
    jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
    hakijaryhmaValintatapajonoDAO.insert(jono);
  }

  @Override
  public List<HakijaryhmaValintatapajono> findByHakukohde(String oid) {
    return hakijaryhmaValintatapajonoDAO.findByHakukohde(oid);
  }

  @Override
  public List<HakijaryhmaValintatapajono> jarjestaHakijaryhmat(List<String> hakijaryhmajonoOidit) {
    if (hakijaryhmajonoOidit.isEmpty()) {
      throw new HakijaryhmaValintatapajonoOidListaOnTyhjaException(
          "Valintatapajonojen OID-lista on tyhjä");
    }
    return hakijaryhmaValintatapajonoDAO.jarjestaUudelleen(
        haeHakijaryhmaValintatapajono(hakijaryhmajonoOidit.get(0)).getHakukohdeViite(),
        hakijaryhmajonoOidit);
  }
}
