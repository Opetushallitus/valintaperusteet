package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.ValintaryhmaId;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValintakoeKopioija;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Transactional
@Service
public class ValintakoeServiceImpl implements ValintakoeService {
  @Autowired private ValintakoeDAO valintakoeDAO;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private OidService oidService;

  @Autowired private LaskentakaavaService laskentakaavaService;

  private static ValintakoeKopioija kopioija = new ValintakoeKopioija();

  @Override
  public void deleteByOid(String oid) {
    Valintakoe valintakoe = haeValintakoeOidilla(oid);
    if (valintakoe.getMaster() != null) {
      throw new ValintakoettaEiVoiPoistaaException("Valintakoe on peritty.");
    }
    removeValintakoe(valintakoe);
  }

  private void removeValintakoe(Valintakoe valintakoe) {
    for (Valintakoe koe : valintakoe.getKopiot()) {
      removeValintakoe(koe);
    }
    valintakoeDAO.remove(valintakoe);
  }

  @Override
  public Valintakoe readByOid(String oid) {
    return haeValintakoeOidilla(oid);
  }

  @Override
  public List<Valintakoe> readByOids(Collection<String> oids) {
    return haeValintakoeOideilla(oids);
  }

  @Override
  public List<Valintakoe> readByTunnisteet(Collection<String> tunnisteet) {
    return haeValintakoeTunnisteilla(tunnisteet);
  }

  @Override
  public List<Valintakoe> readAll() {
    return valintakoeDAO.findAll();
  }

  private List<Valintakoe> haeValintakoeOideilla(Collection<String> oids) {
    List<Valintakoe> valintakoe = valintakoeDAO.readByOids(oids);
    if (valintakoe == null) {
      throw new ValintakoettaEiOleOlemassaException(
          "Valintakoetta (oideja " + Arrays.toString(oids.toArray()) + ") ei ole olemassa");
    }
    return valintakoe;
  }

  private Valintakoe haeValintakoeOidilla(String oid) {
    Valintakoe valintakoe = valintakoeDAO.readByOid(oid);
    if (valintakoe == null) {
      throw new ValintakoettaEiOleOlemassaException(
          "Valintakoetta (oid " + oid + ") ei ole olemassa");
    }
    return valintakoe;
  }

  private List<Valintakoe> haeValintakoeTunnisteilla(Collection<String> tunnisteet) {
    List<Valintakoe> valintakoe = valintakoeDAO.readByTunnisteet(tunnisteet);
    if (valintakoe == null) {
      throw new ValintakoettaEiOleOlemassaException(
          "Valintakoetta (tunnisteet "
              + Arrays.toString(tunnisteet.toArray())
              + ") ei ole olemassa");
    }
    return valintakoe;
  }

  @Override
  public List<Valintakoe> findValintakoeByValinnanVaihe(String oid) {
    return valintakoeDAO.findByValinnanVaihe(oid);
  }

  @Override
  public List<Valintakoe> findValintakoesByValinnanVaihes(List<ValinnanVaihe> vaiheet) {
    List<Valintakoe> kokeet = new ArrayList<Valintakoe>();
    for (ValinnanVaihe vaihe : vaiheet) {
      kokeet.addAll(valintakoeDAO.findByValinnanVaihe(vaihe.getOid()));
    }
    return kokeet;
  }

  @Override
  public Valintakoe lisaaValintakoeValinnanVaiheelle(
      String valinnanVaiheOid, ValintakoeCreateDTO koe) {
    ValinnanVaihe valinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
    if (!ValinnanVaiheTyyppi.VALINTAKOE.equals(valinnanVaihe.getValinnanVaiheTyyppi())) {
      throw new ValintakoettaEiVoiLisataException(
          "Valintakoetta ei voi lisätä valinnan vaiheelle, jonka "
              + "tyyppi on "
              + valinnanVaihe.getValinnanVaiheTyyppi().name());
    }
    boolean hasValintakoe =
        valinnanVaihe.getValintakokeet().stream()
            .anyMatch(k -> k.getTunniste().equals(koe.getTunniste()));
    if (hasValintakoe) {
      throw new ValintakoettaEiVoiLisataException(
          "Valinnanvaiheelle ei voi lisätä toista valintakoetta samalla tunnisteella "
              + koe.getTunniste());
    }
    Valintakoe valintakoe = new Valintakoe();
    valintakoe.setOid(oidService.haeValintakoeOid());
    valintakoe.setTunniste(koe.getTunniste());
    valintakoe.setNimi(koe.getNimi());
    valintakoe.setKuvaus(koe.getKuvaus());
    valintakoe.setValinnanVaihe(valinnanVaihe);
    valintakoe.setAktiivinen(koe.getAktiivinen());
    valintakoe.setLahetetaankoKoekutsut(koe.getLahetetaankoKoekutsut());
    valintakoe.setKutsutaankoKaikki(koe.getKutsutaankoKaikki());
    valintakoe.setKutsuttavienMaara(koe.getKutsuttavienMaara());
    valintakoe.setKutsunKohde(koe.getKutsunKohde());
    if (koe.getKutsunKohdeAvain() != null && !koe.getKutsunKohdeAvain().isEmpty()) {
      valintakoe.setKutsunKohdeAvain(koe.getKutsunKohdeAvain());
    }
    if (koe.getLaskentakaavaId() != null) {
      LaskentakaavaId id = new LaskentakaavaId(koe.getLaskentakaavaId());
      laskentakaavaService.haeLaskettavaKaava(id, Laskentamoodi.VALINTAKOELASKENTA);
      valintakoe.setLaskentakaavaId(id.id);
    }
    Valintakoe lisatty = valintakoeDAO.insert(valintakoe);
    for (ValinnanVaihe kopio : valinnanVaihe.getKopiot()) {
      lisaaValinnanVaiheelleKopioMasterValintakokeesta(kopio, lisatty);
    }
    return lisatty;
  }

  private void lisaaValinnanVaiheelleKopioMasterValintakokeesta(
      ValinnanVaihe valinnanVaihe,
      Valintakoe masterValintakoe) {
    Valintakoe kopio = teeKopioMasterista(masterValintakoe, valinnanVaihe, null);
    Valintakoe lisatty = valintakoeDAO.insert(kopio);
    for (ValinnanVaihe vaihekopio : valinnanVaihe.getKopioValinnanVaiheet()) {
      lisaaValinnanVaiheelleKopioMasterValintakokeesta(vaihekopio, lisatty);
    }
  }

  @Override
  public Valintakoe update(String oid, ValintakoeDTO valintakoe) {
    Valintakoe managedObject = haeValintakoeOidilla(oid);
    boolean hasValintakoeTunniste =
        managedObject.getValinnanVaihe().getValintakokeet().stream()
            .filter(k -> !k.getOid().equals(oid))
            .anyMatch(k -> k.getTunniste().equals(valintakoe.getTunniste()));
    if (hasValintakoeTunniste) {
      throw new ValintakoettaEiVoiLisataException(
          "Valinnanvaiheelle ei voi lisätä toista valintakoetta samalla tunnisteella "
              + valintakoe.getTunniste());
    }
    Valintakoe incoming = new Valintakoe();
    incoming.setAktiivinen(valintakoe.getAktiivinen());
    incoming.setKuvaus(valintakoe.getKuvaus());
    incoming.setNimi(valintakoe.getNimi());
    incoming.setTunniste(valintakoe.getTunniste());
    incoming.setLahetetaankoKoekutsut(valintakoe.getLahetetaankoKoekutsut());
    incoming.setKutsutaankoKaikki(valintakoe.getKutsutaankoKaikki());
    incoming.setKutsuttavienMaara(valintakoe.getKutsuttavienMaara());
    incoming.setKutsunKohde(valintakoe.getKutsunKohde());
    if (valintakoe.getKutsunKohdeAvain() != null && !valintakoe.getKutsunKohdeAvain().isEmpty()) {
      incoming.setKutsunKohdeAvain(valintakoe.getKutsunKohdeAvain());
    }
    if (valintakoe.getLaskentakaavaId() != null) {
      LaskentakaavaId id = new LaskentakaavaId(valintakoe.getLaskentakaavaId());
      laskentakaavaService.haeLaskettavaKaava(id, Laskentamoodi.VALINTAKOELASKENTA);
      incoming.setLaskentakaavaId(id.id);
    }
    return LinkitettavaJaKopioitavaUtil.paivita(managedObject, incoming, kopioija);
  }

  @Override
  public void kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(
      ValinnanVaihe valinnanVaihe,
      ValinnanVaihe masterValinnanVaihe,
      JuureenKopiointiCache kopiointiCache) {
    List<Valintakoe> kokeet = valintakoeDAO.findByValinnanVaihe(masterValinnanVaihe.getOid());
    for (Valintakoe master : kokeet) {
      Valintakoe kopio = teeKopioMasterista(master, valinnanVaihe, kopiointiCache);
      Valintakoe lisatty = valintakoeDAO.insert(kopio);
      if (kopiointiCache != null) {
        kopiointiCache.kopioidutValintakokeet.put(master.getId(), lisatty);
      }
    }
  }

  private Valintakoe teeKopioMasterista(Valintakoe master,
                                        ValinnanVaihe valinnanVaihe,
                                        JuureenKopiointiCache kopiointiCache) {
    Valintakoe kopio = new Valintakoe();
    valinnanVaihe.addValintakoe(kopio);
    kopio.setOid(oidService.haeValintakoeOid());
    kopio.setAktiivinen(master.getAktiivinen());
    kopio.setKuvaus(master.getKuvaus());
    if (kopiointiCache == null) {
      kopio.setMaster(master);
      kopio.setLaskentakaavaId(master.getLaskentakaavaId());
    } else {
      if (master.getMaster() != null) {
        Valintakoe kopioituMaster =
                kopiointiCache.kopioidutValintakokeet.get(master.getMaster().getId());
        if (kopioituMaster == null) {
          throw new IllegalStateException(
                  "Ei löydetty lähdekokeen "
                          + master
                          + " masterille "
                          + master.getMaster()
                          + " kopiota");
        }
        kopio.setMaster(kopioituMaster);
      }
      if (master.getLaskentakaavaId() != null) {
        kopio.setLaskentakaavaId(laskentakaavaService.kopioiJosEiJoKopioitu(
                new LaskentakaavaId(master.getLaskentakaavaId()),
                new ValintaryhmaId(valinnanVaihe.getValintaryhma().getId()),
                kopiointiCache.kopioidutLaskentakaavat
        ).id);
      }
    }
    kopio.setNimi(master.getNimi());
    kopio.setTunniste(master.getTunniste());
    kopio.setKutsunKohdeAvain(master.getKutsunKohdeAvain());
    kopio.setLahetetaankoKoekutsut(master.getLahetetaankoKoekutsut());
    kopio.setKutsutaankoKaikki(master.getKutsutaankoKaikki());
    kopio.setKutsuttavienMaara(master.getKutsuttavienMaara());
    kopio.setKutsunKohde(master.getKutsunKohde());
    return kopio;
  }
}
