package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmatyyppikoodiService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HakijaryhmaServiceImpl implements HakijaryhmaService {
  @Autowired private HakijaryhmaDAO hakijaryhmaDAO;

  @Autowired private HakijaryhmaDAO hakijaryhmatyyppikoodiDAO;

  @Autowired private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

  @Autowired private GenericDAO genericDAO;

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private ValintatapajonoService valintapajonoService;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired private HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private OidService oidService;

  private Hakijaryhma haeHakijaryhma(String oid) {
    Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(oid);

    if (hakijaryhma == null) {
      throw new HakijaryhmaEiOleOlemassaException("Hakijaryhma (" + oid + ") ei ole olemassa", oid);
    }

    return hakijaryhma;
  }

  @Override
  public void deleteByOid(String oid, boolean skipInheritedCheck) {
    Hakijaryhma hakijaryhma = haeHakijaryhma(oid);
    deleteHakijaryhma(hakijaryhma, skipInheritedCheck);
  }

  private void deleteHakijaryhma(Hakijaryhma hakijaryhma, boolean skipInheritedCheck) {
    hakijaryhma
        .getKopioHakijaryhmat()
        .forEach(kopio -> deleteByOid(kopio.getOid(), skipInheritedCheck));
    hakijaryhma.setKopioHakijaryhmat(new HashSet<>());

    Hakijaryhma seuraava = hakijaryhma.getSeuraava();
    Hakijaryhma edellinen = hakijaryhma.getEdellinen();

    if (edellinen != null) {
      edellinen.setSeuraava(seuraava);
      hakijaryhmaDAO.update(edellinen);
    }

    hakijaryhma.setEdellinen(null);
    hakijaryhma.setSeuraava(null);
    hakijaryhmaDAO.update(hakijaryhma);

    if (seuraava != null) {
      seuraava.setEdellinen(edellinen);
      hakijaryhmaDAO.update(seuraava);
    }

    if (hakijaryhma.getJonot() != null) {
      hakijaryhma
          .getJonot()
          .forEach(
              j -> {
                hakijaryhmaValintatapajonoService.deleteByOid(j.getOid(), skipInheritedCheck);
              });
    }
    hakijaryhma.setJonot(null);
    hakijaryhmaDAO.remove(hakijaryhma);
  }

  @Override
  public List<Hakijaryhma> findByHakukohde(String oid) {
    return hakijaryhmaValintatapajonoDAO.findByHakukohde(oid).stream()
        .map(HakijaryhmaValintatapajono::getHakijaryhma)
        .collect(Collectors.toList());
  }

  @Override
  public List<Hakijaryhma> findByValintaryhma(String oid) {
    List<Hakijaryhma> valintaryhma = hakijaryhmaDAO.findByValintaryhma(oid);
    return LinkitettavaJaKopioitavaUtil.jarjesta(valintaryhma);
  }

  @Override
  public Hakijaryhma readByOid(String oid) {
    return haeHakijaryhma(oid);
  }

  @Override
  public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {
    hakijaryhmaValintatapajonoService.liitaHakijaryhmaValintatapajonolle(
        valintatapajonoOid, hakijaryhmaOid);
  }

  @Override
  public Hakijaryhma lisaaHakijaryhmaValintaryhmalle(
      String valintaryhmaOid, HakijaryhmaCreateDTO dto) {
    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    if (valintaryhma == null) {
      throw new ValintaryhmaEiOleOlemassaException(
          "Valintaryhmää (" + valintaryhmaOid + ") ei ole olemassa");
    }
    if (dto.getLaskentakaavaId() == null) {
      throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
    }
    Hakijaryhma edellinenHakijaryhma =
        hakijaryhmaDAO.haeValintaryhmanViimeinenHakijaryhma(valintaryhmaOid);
    Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
    hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
    hakijaryhma.setValintaryhma(valintaryhma);
    hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
    hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
    hakijaryhma.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
    hakijaryhma.setLaskentakaava(
        laskentakaavaService.haeMallinnettuKaava(hakijaryhma.getLaskentakaavaId()));
    if (dto.getHakijaryhmatyyppikoodi() != null) {
      hakijaryhma.setHakijaryhmatyyppikoodi(
          hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(
              dto.getHakijaryhmatyyppikoodi()));
    }
    hakijaryhma.setEdellinenHakijaryhma(edellinenHakijaryhma);
    Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
    LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);
    valintaryhma.getHakukohdeViitteet().stream()
        .forEach(
            hk -> {
              hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(
                  hk.getOid(), lisatty.getOid());
            });

    List<Valintaryhma> alaValintaryhmat =
        valintaryhmaService.findValintaryhmasByParentOid(valintaryhmaOid);
    for (Valintaryhma alavalintaryhma : alaValintaryhmat) {
      lisaaValintaryhmalleKopioMasterHakijaryhmasta(
          alavalintaryhma, lisatty, edellinenHakijaryhma, null);
    }
    return lisatty;
  }

  @Override
  public void kopioiHakijaryhmatMasterValintaryhmalta(
      String parentValintaryhmaOid,
      String childValintaryhmaoid,
      JuureenKopiointiCache kopiointiCache) {
    Valintaryhma childValintaryhma = valintaryhmaService.readByOid(childValintaryhmaoid);
    List<Hakijaryhma> byValintaryhma = hakijaryhmaDAO.findByValintaryhma(parentValintaryhmaOid);
    byValintaryhma.stream()
        .filter(Objects::nonNull)
        .forEach(
            parentHakijaryhma -> {
              lisaaValintaryhmalleKopioMasterHakijaryhmasta(
                  childValintaryhma, parentHakijaryhma, parentHakijaryhma, kopiointiCache);
            });
  }

  @Override
  public List<Hakijaryhma> jarjestaHakijaryhmat(List<String> hakijaryhmaOidit) {
    if (hakijaryhmaOidit.isEmpty()) {
      throw new HakijaryhmaOidListaOnTyhjaException("Valinnan vaiheiden OID-lista on tyhjä");
    }
    String ensimmainen = hakijaryhmaOidit.get(0);
    Hakijaryhma hakijaryhma = haeHakijaryhma(ensimmainen);
    if (hakijaryhma.getValintaryhma() != null) {
      return jarjestaHakijaryhmat(hakijaryhma.getValintaryhma(), hakijaryhmaOidit);
    } else {
      throw new ValintaryhmaEiOleOlemassaException(
          "Hakijaryhmällä " + ensimmainen + " ei ole valintaryhmää");
    }
  }

  private List<Hakijaryhma> jarjestaHakijaryhmat(
      Valintaryhma valintaryhma, List<String> hakijaryhmaOidit) {
    LinkedHashMap<String, Hakijaryhma> alkuperainenJarjestys =
        LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
            LinkitettavaJaKopioitavaUtil.jarjesta(
                hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid())));
    LinkedHashMap<String, Hakijaryhma> jarjestetty =
        LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(
            alkuperainenJarjestys, hakijaryhmaOidit);
    return new ArrayList<>(jarjestetty.values());
  }

  private void lisaaValintaryhmalleKopioMasterHakijaryhmasta(
      Valintaryhma valintaryhma,
      Hakijaryhma masterHakijaryhma,
      Hakijaryhma edellinenHakijaryhma,
      JuureenKopiointiCache kopiointiCache) {
    Hakijaryhma kopio = luoKopioHakijaryhmasta(valintaryhma, masterHakijaryhma, kopiointiCache);
    kopio.setValintaryhma(valintaryhma);
    List<Hakijaryhma> ryhmat =
        LinkitettavaJaKopioitavaUtil.jarjesta(
            hakijaryhmaDAO.findByValintaryhma(valintaryhma.getOid()));
    Hakijaryhma lisatty = lisaaKopio(kopio, edellinenHakijaryhma, ryhmat);
    valintaryhma.getHakijaryhmat().add(lisatty);
    if (kopiointiCache != null) {
      kopiointiCache.kopioidutHakijaryhmat.put(masterHakijaryhma.getId(), lisatty);
    }
    List<Valintaryhma> alavalintaryhmat =
        valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
    alavalintaryhmat.forEach(
        alavalintaryhma -> {
          lisaaValintaryhmalleKopioMasterHakijaryhmasta(
              alavalintaryhma, lisatty, lisatty.getEdellinenHakijaryhma(), kopiointiCache);
        });
    valintaryhma
        .getHakukohdeViitteet()
        .forEach(
            hk -> {
              hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(
                  hk.getOid(), lisatty.getOid());
            });
  }

  private Hakijaryhma lisaaKopio(
      Hakijaryhma kopio, Hakijaryhma edellinenMasterHakijaryhma, List<Hakijaryhma> ryhmat) {
    List<Hakijaryhma> jarjestetty = LinkitettavaJaKopioitavaUtil.jarjesta(ryhmat);
    Hakijaryhma edellinenHakijaryhma =
        LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(
            edellinenMasterHakijaryhma, jarjestetty);
    if (edellinenHakijaryhma == null && !ryhmat.isEmpty()) {
      edellinenHakijaryhma = jarjestetty.get(jarjestetty.size() - 1);
    }
    kopio.setEdellinenHakijaryhma(edellinenHakijaryhma);
    Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);
    LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, lisatty);
    return kopio;
  }

  private Hakijaryhma luoKopioHakijaryhmasta(
      Valintaryhma kohdeValintaryhma,
      Hakijaryhma hakijaryhma,
      JuureenKopiointiCache kopiointiCache) {
    Hakijaryhma kopio = new Hakijaryhma();
    kopio.setOid(oidService.haeHakijaryhmaOid());
    kopio.setMasterHakijaryhma(hakijaryhma);
    kopio.setEdellinenHakijaryhma(hakijaryhma.getEdellinenHakijaryhma());
    kopio.setKiintio(hakijaryhma.getKiintio());
    kopio.setKuvaus(hakijaryhma.getKuvaus());
    kopio.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
    if (kopiointiCache != null
        && kopiointiCache.kopioidutLaskentakaavat.containsKey(hakijaryhma.getLaskentakaavaId())) {
      kopio.setLaskentakaava(
          kopiointiCache.kopioidutLaskentakaavat.get(hakijaryhma.getLaskentakaavaId()));
    } else {
      kopio.setLaskentakaava(
          laskentakaavaService
              .haeLaskentakaavaTaiSenKopioVanhemmilta(
                  hakijaryhma.getLaskentakaavaId(), kohdeValintaryhma)
              .orElse(hakijaryhma.getLaskentakaava()));
    }
    kopio.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
    kopio.setNimi(hakijaryhma.getNimi());
    kopio.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
    kopio.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
    return kopio;
  }

  @Override
  public Hakijaryhma update(String oid, HakijaryhmaCreateDTO dto) {
    if (dto.getLaskentakaavaId() == null) {
      throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
    }
    Hakijaryhma managedObject = haeHakijaryhma(oid);
    managedObject.setKaytaKaikki(dto.isKaytaKaikki());
    managedObject.setKiintio(dto.getKiintio());
    managedObject.setKuvaus(dto.getKuvaus());
    managedObject.setNimi(dto.getNimi());
    managedObject.setTarkkaKiintio(dto.isTarkkaKiintio());
    managedObject.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
    managedObject.setLaskentakaava(
        laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
    if (dto.getHakijaryhmatyyppikoodi() != null) {
      managedObject.setHakijaryhmatyyppikoodi(
          hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(
              dto.getHakijaryhmatyyppikoodi()));
    }
    hakijaryhmaDAO.update(managedObject);
    return managedObject;
  }

  @Override
  public Optional<Hakijaryhma> siirra(HakijaryhmaSiirraDTO dto) {
    if (dto.getUusinimi() != null) {
      dto.setNimi(dto.getUusinimi());
    }
    Optional<Valintaryhma> ryhma =
        Optional.ofNullable(valintaryhmaService.readByOid(dto.getValintaryhmaOid()));
    if (!ryhma.isPresent()) {
      return Optional.empty();
    }
    return Optional.ofNullable(lisaaHakijaryhmaValintaryhmalle(ryhma.get().getOid(), dto));
  }

  public void deleteHakijaryhmaValintatajono(HakijaryhmaValintatapajono entity) {
    for (HakijaryhmaValintatapajono hakijaryhma : entity.getKopiot()) {
      deleteHakijaryhmaValintatajono(hakijaryhma);
    }
    if (entity.getSeuraava() != null) {
      HakijaryhmaValintatapajono seuraava = entity.getSeuraava();
      seuraava.setEdellinen(entity.getEdellinen());
    }
    hakijaryhmaValintatapajonoDAO.remove(entity);
  }
}
