package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HakukohdeServiceImpl implements HakukohdeService {
  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private ValinnanVaiheDAO valinnanVaiheDAO;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private OidService oidService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

  @Autowired private ValintatapajonoService valintatapajonoService;

  @Override
  public List<HakukohdeViite> findAll() {
    return hakukohdeViiteDAO.findAll();
  }

  @Override
  public List<HakukohdeViite> haunHakukohteet(String hakuOid) {
    return hakukohdeViiteDAO.haunHakukohteet(hakuOid);
  }

  @Override
  public HakukohdeViite readByOid(String oid) {
    return haeHakukohdeViite(oid);
  }

  @Override
  public List<HakukohdeViite> readByOids(List<String> oids) {
    return hakukohdeViiteDAO.readByOids(oids);
  }

  @Override
  public List<HakukohdeViite> findRoot() {
    return hakukohdeViiteDAO.findRoot();
  }

  @Override
  public List<HakukohdeViite> findByValintaryhmaOid(String oid) {
    return hakukohdeViiteDAO.findByValintaryhmaOid(oid);
  }

  private HakukohdeViite haeHakukohdeViite(String oid) {
    HakukohdeViite hakukohdeViite = hakukohdeViiteDAO.readByOid(oid);
    if (hakukohdeViite == null) {
      throw new HakukohdeViiteEiOleOlemassaException(
          "Hakukohde (" + oid + ") ei ole olemassa.", oid);
    }
    return hakukohdeViite;
  }

  @Override
  public HakukohdeViite update(String oid, HakukohdeViiteCreateDTO incoming) throws Exception {
    HakukohdeViite managedObject = haeHakukohdeViite(oid);
    managedObject.setNimi(incoming.getNimi());
    managedObject.setHakuoid(incoming.getHakuoid());
    managedObject.setTarjoajaOid(incoming.getTarjoajaOid());
    managedObject.setTila(incoming.getTila());
    return managedObject;
  }

  @Override
  public HakukohdeViite insert(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid) {
    HakukohdeViite lisatty = modelMapper.map(hakukohde, HakukohdeViite.class);
    if (StringUtils.isNotBlank(valintaryhmaOid)) {
      Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
      lisatty.setValintaryhma(valintaryhma);
      lisatty = hakukohdeViiteDAO.insert(lisatty);
      String hakukohdeOid = lisatty.getOid();
      valinnanVaiheService.kopioiValinnanVaiheetParentilta(lisatty, valintaryhma, null);
      valintaryhma.getHakijaryhmat().stream()
          .forEach(
              hakijaryhma -> {
                hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(
                    hakukohdeOid, hakijaryhma.getOid());
              });
    } else {
      lisatty = hakukohdeViiteDAO.insert(lisatty);
    }
    return lisatty;
  }

  @Override
  public boolean kuuluuSijoitteluun(String oid) {
    return hakukohdeViiteDAO.kuuluuSijoitteluun(oid);
  }

  @Override
  public List<ValinnanVaihe> ilmanLaskentaa(String oid) {
    List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheDAO.valinnanVaiheetJaJonot(oid);
    // Poistetaan vaiheet joilla ei ole jonoa, jossa ei käytetä laskentaa
    List<ValinnanVaihe> valinnanVaiheetIlmanlaskentaa =
        valinnanVaiheet.stream()
            .filter(
                vv ->
                    vv.getJonot().stream()
                        .anyMatch(j -> Boolean.FALSE.equals(j.getKaytetaanValintalaskentaa())))
            .collect(Collectors.toList());

    // Järjestetään jonot vaiheiden sisällä
    for (ValinnanVaihe valinnanVaihe : valinnanVaiheetIlmanlaskentaa) {
      valinnanVaiheDAO.detach(valinnanVaihe);
      valinnanVaihe.setJonot(
          new LinkedHashSet<>(LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaihe.getJonot())));
    }
    return valinnanVaiheetIlmanlaskentaa;
  }

  @Override
  public List<ValinnanVaihe> vaiheetJaJonot(String oid) {
    List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.valinnanVaiheetJaJonot(oid);
    if (vaiheet == null) {
      return new ArrayList<>();
    }
    return vaiheet;
  }

  private ValintatapajonoCreateDTO vtjToCreateDto(Valintatapajono valintatapajono) {
    ValintatapajonoCreateDTO dto = new ValintatapajonoCreateDTO();
    dto.setAloituspaikat(valintatapajono.getAloituspaikat());
    dto.setNimi(valintatapajono.getNimi());
    dto.setKuvaus(valintatapajono.getKuvaus());
    dto.setTyyppi(valintatapajono.getTyyppi());
    dto.setSiirretaanSijoitteluun(valintatapajono.getSiirretaanSijoitteluun());
    dto.setTasapistesaanto(valintatapajono.getTasapistesaanto());
    dto.setAktiivinen(valintatapajono.getAktiivinen());
    dto.setValisijoittelu(valintatapajono.getValisijoittelu());
    dto.setautomaattinenSijoitteluunSiirto(valintatapajono.getautomaattinenSijoitteluunSiirto());
    dto.setEiVarasijatayttoa(valintatapajono.getEiVarasijatayttoa());
    dto.setKaikkiEhdonTayttavatHyvaksytaan(valintatapajono.getKaikkiEhdonTayttavatHyvaksytaan());
    dto.setVarasijat(valintatapajono.getVarasijat());
    dto.setPoissaOlevaTaytto(valintatapajono.getPoissaOlevaTaytto());
    dto.setPoistetaankoHylatyt(valintatapajono.isPoistetaankoHylatyt());
    dto.setVarasijojaKaytetaanAlkaen(valintatapajono.getVarasijojaKaytetaanAlkaen());
    dto.setVarasijojaTaytetaanAsti(valintatapajono.getVarasijojaTaytetaanAsti());
    dto.setEiLasketaPaivamaaranJalkeen(valintatapajono.getEiLasketaPaivamaaranJalkeen());
    dto.setKaytetaanValintalaskentaa(valintatapajono.getKaytetaanValintalaskentaa());
    dto.setTayttojono(
        valintatapajono.getVarasijanTayttojono() != null
            ? valintatapajono.getVarasijanTayttojono().getOid()
            : null);
    return dto;
  }

  private List<Pair<Integer, ValintatapajonoCreateDTO>> haeHakukohteenSiirrettavatValintatapajonot(
      List<ValinnanVaihe> vanhatValinnanVaiheet) {
    int suhteellinenIndeksi = 0;
    List<Pair<Integer, ValintatapajonoCreateDTO>> siirrettavatValintatapajonot = new LinkedList<>();
    for (ValinnanVaihe valinnanVaihe : vanhatValinnanVaiheet) {
      if (valinnanVaihe.getMaster() != null) {
        for (Valintatapajono valintatapajono : valinnanVaihe.getJonot()) {
          if (valintatapajono.getMaster() == null) {
            siirrettavatValintatapajonot.add(
                Pair.of(suhteellinenIndeksi, vtjToCreateDto(valintatapajono)));
          }
        }
        suhteellinenIndeksi++;
      }
    }
    return siirrettavatValintatapajonot;
  }

  @Override
  public HakukohdeViite siirraHakukohdeValintaryhmaan(
      String hakukohdeOid, String valintaryhmaOid, boolean siirretaanManuaalisesti) {
    HakukohdeViite hakukohdeViite = haeHakukohdeViite(hakukohdeOid);
    Valintaryhma valintaryhma = null;
    if (StringUtils.isNotBlank(valintaryhmaOid)) {
      valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    }

    hakukohdeViite.setManuaalisestiSiirretty(siirretaanManuaalisesti);

    if (!Objects.equals(valintaryhma, hakukohdeViite.getValintaryhma())) {
      List<ValinnanVaihe> vanhatValinnanVaiheet =
          valinnanVaiheService.findByHakukohde(hakukohdeOid);
      List<Pair<Integer, ValintatapajonoCreateDTO>> siirrettavatValintatapajonot =
          haeHakukohteenSiirrettavatValintatapajonot(vanhatValinnanVaiheet);
      for (ValinnanVaihe valinnanVaihe : vanhatValinnanVaiheet) {
        if (valinnanVaihe.getMaster() != null) {
          valinnanVaiheService.delete(valinnanVaihe);
        }
      }

      for (HakijaryhmaValintatapajono hakijaryhmaValintatapajono :
          hakijaryhmaValintatapajonoService.findByHakukohde(hakukohdeOid)) {
        if (hakijaryhmaValintatapajono.getHakijaryhma().getValintaryhma() != null) {
          hakijaryhmaValintatapajonoService.delete(hakijaryhmaValintatapajono);
        }
      }

      hakukohdeViite.setValintaryhma(valintaryhma);

      if (valintaryhma != null) {
        valinnanVaiheService.kopioiValinnanVaiheetParentilta(hakukohdeViite, valintaryhma, null);

        List<ValinnanVaihe> uudetValinnanVaiheet =
            valinnanVaiheService.findByHakukohde(hakukohdeOid);
        if (vanhatValinnanVaiheet.size() == uudetValinnanVaiheet.size()) {
          for (Pair<Integer, ValintatapajonoCreateDTO> p : siirrettavatValintatapajonot) {
            valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(
                uudetValinnanVaiheet.get(p.getLeft()).getOid(), p.getRight(), null);
          }
        }

        for (Hakijaryhma hakijaryhma : valintaryhma.getHakijaryhmat()) {
          hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(
              hakukohdeOid, hakijaryhma.getOid());
        }
      }
    }

    return haeHakukohdeViite(hakukohdeOid);
  }
}
