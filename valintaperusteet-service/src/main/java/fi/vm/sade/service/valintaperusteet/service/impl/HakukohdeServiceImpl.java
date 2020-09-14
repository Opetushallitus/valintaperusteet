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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;
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
      valinnanVaihe.setJonot(LinkitettavaJaKopioitavaUtil.jarjestaSet(valinnanVaihe.getJonot()));
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

  @Override
  public void deleteByOid(String oid) {
    HakukohdeViite hakukohde = haeHakukohdeViite(oid);
    List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohde.getOid());
    for (ValinnanVaihe vv : vaiheet) {
      valinnanVaiheService.delete(vv);
    }
    hakukohdeViiteDAO.remove(hakukohde);
    // Hakukohteiden tuonti saattaa feilata ilman flushausta, jos hakukohde siirretään uuden
    // valintaryhmän alle
    hakukohdeViiteDAO.flush();
  }

  @Override
  public HakukohdeViite siirraHakukohdeValintaryhmaan(
      String hakukohdeOid, String valintaryhmaOid, boolean siirretaanManuaalisesti) {
    HakukohdeViite hakukohdeViite = haeHakukohdeViite(hakukohdeOid);
    Valintaryhma valintaryhma = null;
    if (StringUtils.isNotBlank(valintaryhmaOid)) {
      valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    }
    if ((valintaryhma != null ^ hakukohdeViite.getValintaryhma() != null)
        || (valintaryhma != null
            && hakukohdeViite.getValintaryhma() != null
            && !valintaryhma.getOid().equals(hakukohdeViite.getValintaryhma().getOid()))) {
      final List<Indexed<Valintatapajono>> siirrettavatHakukohteenValintatapajonot =
          haeHakukohteenSiirrettavatValintatapajonot(hakukohdeViite);
      poistaHakukohteenPeriytyvatValinnanVaiheetJaHakijaryhmat(hakukohdeOid);
      List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      // Käydään läpi kaikki ei-periytyvät valinnan vaiheet ja asetetaan hakukohdeviittaus
      // tilapäisesti
      // nulliksi
      for (ValinnanVaihe vv : valinnanVaiheet) {
        vv.setHakukohdeViite(null);
      }
      List<Laskentakaava> kaavat =
          laskentakaavaService.findKaavas(true, null, hakukohdeViite.getOid(), null);
      // Poistetaan hakukohteen kaavoilta viittaus vanhaan hakukohteeseen
      kaavat.stream()
          .forEach(
              kaava -> {
                kaava.setHakukohde(null);
                laskentakaavaDAO.update(kaava);
              });
      // Poistetaan vanha hakukohde
      deleteByOid(hakukohdeOid);
      // Luodaan uusi hakukohde
      HakukohdeViite lisatty =
          insert(
              modelMapper.map(hakukohdeViite, HakukohdeViiteCreateDTO.class),
              valintaryhma != null ? valintaryhma.getOid() : null);
      // Lisätään kaavat takaisin uudelleen luodulle hakukohteelle
      kaavat.stream()
          .forEach(
              kaava -> {
                kaava.setHakukohde(lisatty);
                laskentakaavaDAO.update(kaava);
              });
      lisatty.setManuaalisestiSiirretty(siirretaanManuaalisesti);
      if (hakukohdeViite.getHakukohdekoodi() != null) {
        Hakukohdekoodi koodi = hakukohdeViite.getHakukohdekoodi();
        lisatty.setHakukohdekoodi(koodi);
      }
      lisatty.getValintakokeet().addAll(hakukohdeViite.getValintakokeet());
      for (String key : hakukohdeViite.getHakukohteenValintaperusteet().keySet()) {
        HakukohteenValintaperuste peruste =
            hakukohdeViite.getHakukohteenValintaperusteet().get(key);
        HakukohteenValintaperuste lisattava = new HakukohteenValintaperuste();
        lisattava.setArvo(peruste.getArvo());
        lisattava.setKuvaus(peruste.getKuvaus());
        lisattava.setTunniste(peruste.getTunniste());
        lisattava.setHakukohde(lisatty);
        lisatty.getHakukohteenValintaperusteet().put(key, lisattava);
      }
      ValinnanVaihe viimeinenValinnanVaihe =
          valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(hakukohdeOid);
      if (!valinnanVaiheet.isEmpty()) {
        valinnanVaiheet.get(0).setEdellinen(viimeinenValinnanVaihe);
        if (viimeinenValinnanVaihe != null) {
          viimeinenValinnanVaihe.setSeuraava(valinnanVaiheet.get(0));
        }
        // Asetetaan hakukohteen omat valinnan vaiheet viittaamaan taas uuteen hakukohteeseen
        for (ValinnanVaihe vv : valinnanVaiheet) {
          vv.setHakukohdeViite(lisatty);
          lisatty.getValinnanvaiheet().add(vv);
        }
      }
      if (hakukohteenSiirrettavatValintatapajonotVoidaanSiirtaa(hakukohdeViite, lisatty)) {
        siirrettavatHakukohteenValintatapajonot.forEach(
            indexed -> siirraHakukohteenValintatapajono(indexed.value, lisatty, indexed.idx));
      }
      return lisatty;
    } else {
      hakukohdeViite.setManuaalisestiSiirretty(siirretaanManuaalisesti);
      return hakukohdeViite;
    }
  }

  private void siirraHakukohteenValintatapajono(
      Valintatapajono vanhaValintatapajono,
      HakukohdeViite uusiHakukohde,
      int valinnanvaiheenIndeksi) {
    final ValinnanVaihe uusiValinnanvaihe =
        LinkitettavaJaKopioitavaUtil.jarjesta(uusiHakukohde.getValinnanvaiheet())
            .get(valinnanvaiheenIndeksi);

    ValintatapajonoCreateDTO uusiValintatapajono = new ValintatapajonoCreateDTO();
    uusiValintatapajono.setAloituspaikat(vanhaValintatapajono.getAloituspaikat());
    uusiValintatapajono.setNimi(vanhaValintatapajono.getNimi());
    uusiValintatapajono.setKuvaus(vanhaValintatapajono.getKuvaus());
    uusiValintatapajono.setTyyppi(vanhaValintatapajono.getTyyppi());
    uusiValintatapajono.setSiirretaanSijoitteluun(vanhaValintatapajono.getSiirretaanSijoitteluun());
    uusiValintatapajono.setTasapistesaanto(vanhaValintatapajono.getTasapistesaanto());
    uusiValintatapajono.setAktiivinen(vanhaValintatapajono.getAktiivinen());
    uusiValintatapajono.setValisijoittelu(vanhaValintatapajono.getValisijoittelu());
    uusiValintatapajono.setautomaattinenSijoitteluunSiirto(
        vanhaValintatapajono.getautomaattinenSijoitteluunSiirto());
    uusiValintatapajono.setEiVarasijatayttoa(vanhaValintatapajono.getEiVarasijatayttoa());
    uusiValintatapajono.setKaikkiEhdonTayttavatHyvaksytaan(
        vanhaValintatapajono.getKaikkiEhdonTayttavatHyvaksytaan());
    uusiValintatapajono.setVarasijat(vanhaValintatapajono.getVarasijat());
    uusiValintatapajono.setPoissaOlevaTaytto(vanhaValintatapajono.getPoissaOlevaTaytto());
    uusiValintatapajono.setPoistetaankoHylatyt(vanhaValintatapajono.isPoistetaankoHylatyt());
    uusiValintatapajono.setVarasijojaKaytetaanAlkaen(
        vanhaValintatapajono.getVarasijojaKaytetaanAlkaen());
    uusiValintatapajono.setVarasijojaTaytetaanAsti(
        vanhaValintatapajono.getVarasijojaTaytetaanAsti());
    uusiValintatapajono.setEiLasketaPaivamaaranJalkeen(
        vanhaValintatapajono.getEiLasketaPaivamaaranJalkeen());
    uusiValintatapajono.setKaytetaanValintalaskentaa(
        vanhaValintatapajono.getKaytetaanValintalaskentaa());
    uusiValintatapajono.setTayttojono(
        vanhaValintatapajono.getVarasijanTayttojono() != null
            ? vanhaValintatapajono.getVarasijanTayttojono().getOid()
            : null);

    valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(
        uusiValinnanvaihe.getOid(), uusiValintatapajono, null);
  }

  private boolean hakukohteenSiirrettavatValintatapajonotVoidaanSiirtaa(
      HakukohdeViite vanhaHakukohde, HakukohdeViite uusiHakukohde) {
    return vanhaHakukohde.getValinnanvaiheet().size() == uusiHakukohde.getValinnanvaiheet().size();
  }

  private static class Indexed<T> {
    public final int idx;
    public final T value;

    public Indexed(int idx, T value) {
      this.idx = idx;
      this.value = value;
    }
  }

  private List<Indexed<Valintatapajono>> haeHakukohteenSiirrettavatValintatapajonot(
      HakukohdeViite vanhaHakukohde) {
    final List<ValinnanVaihe> valinnanvaiheet =
        LinkitettavaJaKopioitavaUtil.jarjesta(vanhaHakukohde.getValinnanvaiheet());
    return IntStream.range(0, valinnanvaiheet.size())
        .mapToObj(idx -> new Indexed<ValinnanVaihe>(idx, valinnanvaiheet.get(idx)))
        .flatMap(
            indexed ->
                indexed.value.getJonot().stream()
                    .filter(
                        vanhaValintatapajono ->
                            vanhaValintatapajono.getMasterValintatapajono() == null)
                    .map(
                        vanhaValintatapajono ->
                            new Indexed<Valintatapajono>(indexed.idx, vanhaValintatapajono)))
        .collect(Collectors.toList());
  }

  private void poistaHakukohteenPeriytyvatValinnanVaiheetJaHakijaryhmat(String hakukohdeOid) {
    List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
    // Poistetaan kaikki periytyvät valinnan vaiheet
    for (ValinnanVaihe vv : valinnanVaiheet) {
      if (vv.getMasterValinnanVaihe() != null) {
        valinnanVaiheService.deleteByOid(vv.getOid());
      }
    }
  }
}
