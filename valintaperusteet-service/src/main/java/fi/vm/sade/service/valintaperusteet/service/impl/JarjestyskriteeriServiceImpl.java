package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintalaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.util.JarjestyskriteeriKopioija;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JarjestyskriteeriServiceImpl implements JarjestyskriteeriService {
  @Autowired private ValintatapajonoService valintatapajonoService;

  @Autowired private JarjestyskriteeriDAO jarjestyskriteeriDAO;

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private OidService oidService;

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  private static JarjestyskriteeriKopioija kopioija = new JarjestyskriteeriKopioija();

  private Jarjestyskriteeri haeJarjestyskriteeri(String oid) {
    Jarjestyskriteeri jarjestyskriteeri = jarjestyskriteeriDAO.readByOid(oid);
    if (jarjestyskriteeri == null) {
      throw new JarjestyskriteeriEiOleOlemassaException(
          "Järjestyskriteeri (" + oid + ") ei ole olemassa", oid);
    }
    return jarjestyskriteeri;
  }

  private void validoiFunktiokutsuJarjestyskriteeriaVarten(Funktiokutsu funktiokutsu) {
    if (funktiokutsu != null) {
      if (!funktiokutsu
          .getFunktionimi()
          .getLaskentamoodit()
          .contains(Laskentamoodi.VALINTALASKENTA)) {
        throw new FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(
            "Funktiokutsua "
                + funktiokutsu.getFunktionimi().name()
                + ", id "
                + funktiokutsu.getId()
                + " ei voida käyttää valintalaskennassa.",
            funktiokutsu.getId(),
            funktiokutsu.getFunktionimi());
      }
      for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
        if (arg.getFunktiokutsuChild() != null) {
          validoiFunktiokutsuJarjestyskriteeriaVarten(arg.getFunktiokutsuChild());
        } else if (arg.getLaskentakaavaChild() != null) {
          validoiFunktiokutsuJarjestyskriteeriaVarten(
              arg.getLaskentakaavaChild().getFunktiokutsu());
        }
      }
    }
  }

  private void validoiLaskentakaavaJarjestyskriteeriaVarten(Laskentakaava laskentakaava) {
    if (laskentakaava == null) {
      throw new LaskentakaavaEiOleOlemassaException("Laskentakaavaa ei ole olemassa", null);
    }
  }

  @Override
  public Jarjestyskriteeri update(
      String oid, JarjestyskriteeriCreateDTO dto, Long laskentakaavaId) {
    Jarjestyskriteeri entity = modelMapper.map(dto, Jarjestyskriteeri.class);
    Jarjestyskriteeri managedObject = haeJarjestyskriteeri(oid);
    if (laskentakaavaId != null) {
      Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(laskentakaavaId);
      validoiLaskentakaavaJarjestyskriteeriaVarten(laskentakaava);
      entity.setLaskentakaava(laskentakaava);
    } else {
      throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
    }
    return LinkitettavaJaKopioitavaUtil.paivita(managedObject, entity, kopioija);
  }

  @Override
  public List<Jarjestyskriteeri> findJarjestyskriteeriByJono(String oid) {
    return jarjestyskriteeriDAO.findByJono(oid);
  }

  @Override
  public List<Jarjestyskriteeri> findByHakukohde(String oid) {
    return jarjestyskriteeriDAO.findByHakukohde(oid);
  }

  @Override
  public Jarjestyskriteeri lisaaJarjestyskriteeriValintatapajonolle(
      String valintatapajonoOid,
      JarjestyskriteeriCreateDTO dto,
      String edellinenValintatapajonoOid,
      Long laskentakaavaOid) {
    Jarjestyskriteeri jarjestyskriteeri = modelMapper.map(dto, Jarjestyskriteeri.class);
    if (laskentakaavaOid != null) {
      Laskentakaava laskentakaava = laskentakaavaDAO.getLaskentakaava(laskentakaavaOid);
      validoiLaskentakaavaJarjestyskriteeriaVarten(laskentakaava);
      jarjestyskriteeri.setLaskentakaava(laskentakaava);
    } else {
      throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
    }
    Valintatapajono valintatapajono = valintatapajonoService.readByOid(valintatapajonoOid);
    Jarjestyskriteeri edellinenJarjestyskriteeri = null;
    if (StringUtils.isNotBlank(edellinenValintatapajonoOid)) {
      edellinenJarjestyskriteeri = haeJarjestyskriteeri(edellinenValintatapajonoOid);
    } else {
      edellinenJarjestyskriteeri =
          jarjestyskriteeriDAO.haeValintatapajononViimeinenJarjestyskriteeri(valintatapajonoOid);
    }
    jarjestyskriteeri.setOid(oidService.haeJarjestyskriteeriOid());
    jarjestyskriteeri.setValintatapajono(valintatapajono);
    jarjestyskriteeri.setEdellinen(edellinenJarjestyskriteeri);
    Jarjestyskriteeri lisatty = jarjestyskriteeriDAO.insert(jarjestyskriteeri);
    LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenJarjestyskriteeri, lisatty);
    for (Valintatapajono kopio : valintatapajono.getKopiot()) {
      lisaaValintatapajonolleKopioMasterJarjestyskriteerista(
          kopio, lisatty, lisatty.getLaskentakaava(), edellinenJarjestyskriteeri);
    }
    return lisatty;
  }

  private void lisaaValintatapajonolleKopioMasterJarjestyskriteerista(
      Valintatapajono valintatapajono,
      Jarjestyskriteeri masterJarjestyskriteeri,
      Laskentakaava laskentakaava,
      Jarjestyskriteeri edellinenMasterJarjestyskriteeri) {
    Jarjestyskriteeri kopio =
        teeKopioMasterista(valintatapajono, masterJarjestyskriteeri, laskentakaava, null);
    kopio.setValintatapajono(valintatapajono);
    kopio.setOid(oidService.haeJarjestyskriteeriOid());
    List<Jarjestyskriteeri> jonot = jarjestyskriteeriDAO.findByJono(valintatapajono.getOid());
    Jarjestyskriteeri edellinenJarjestyskriteeri =
        LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(
            edellinenMasterJarjestyskriteeri, jonot);
    kopio.setEdellinen(edellinenJarjestyskriteeri);
    Jarjestyskriteeri lisatty = jarjestyskriteeriDAO.insert(kopio);
    LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenJarjestyskriteeri, lisatty);
    for (Valintatapajono jonokopio : valintatapajono.getKopiot()) {
      lisaaValintatapajonolleKopioMasterJarjestyskriteerista(
          jonokopio, lisatty, laskentakaava, lisatty.getEdellinen());
    }
  }

  private Jarjestyskriteeri teeKopioMasterista(
      Valintatapajono jono, Jarjestyskriteeri master, JuureenKopiointiCache kopiointiCache) {
    Laskentakaava kopioiJosEiJoKopioitu =
        laskentakaavaService.kopioiJosEiJoKopioitu(
            master.getLaskentakaava(),
            jono.getValinnanVaihe().getHakukohdeViite(),
            jono.getValinnanVaihe().getValintaryhma());
    return teeKopioMasterista(jono, master, kopioiJosEiJoKopioitu, kopiointiCache);
  }

  private Jarjestyskriteeri teeKopioMasterista(
      Valintatapajono jono,
      Jarjestyskriteeri master,
      Laskentakaava laskentakaava,
      JuureenKopiointiCache kopiointiCache) {
    Jarjestyskriteeri kopio = new Jarjestyskriteeri();
    kopio.setAktiivinen(master.getAktiivinen());
    kopio.setValintatapajono(jono);
    kopio.setLaskentakaava(laskentakaava);
    kopio.setMetatiedot(master.getMetatiedot());
    if (kopiointiCache == null) {
      kopio.setMaster(master);
    } else {
      if (master.getMaster() != null) {
        Jarjestyskriteeri kopioituMaster =
            kopiointiCache.kopioidutJarjestyskriteerit.get(master.getMaster().getId());
        if (kopioituMaster == null) {
          throw new IllegalStateException(
              "Ei löydetty lähdejärjestyskriteetin "
                  + master
                  + " masterille "
                  + master.getMaster()
                  + " kopiota");
        }
        kopio.setMaster(kopioituMaster);
      }
    }
    return kopio;
  }

  @Override
  public List<Jarjestyskriteeri> jarjestaKriteerit(List<String> oids) {
    if (oids.isEmpty()) {
      throw new JarjestyskriteeriOidListaOnTyhjaException("Jarjestyskriteeri OID-lista on tyhjä");
    }
    Jarjestyskriteeri ensimmainen = haeJarjestyskriteeri(oids.get(0));
    return jarjestaKriteerit(ensimmainen.getValintatapajono(), oids);
  }

  private List<Jarjestyskriteeri> jarjestaKriteerit(Valintatapajono jono, List<String> oids) {
    List<Jarjestyskriteeri> jarjestetty =
        LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
            jarjestyskriteeriDAO.findByJono(jono.getOid()), oids);
    for (Valintatapajono kopio : jono.getKopiot()) {
      jarjestaKopioValintatapajononKriteerit(kopio, jarjestetty);
    }
    return jarjestetty;
  }

  private void jarjestaKopioValintatapajononKriteerit(
      Valintatapajono jono, List<Jarjestyskriteeri> uusiMasterJarjestys) {
    List<Jarjestyskriteeri> jarjestetty =
        LinkitettavaJaKopioitavaUtil.jarjestaUudelleenMasterJarjestyksenMukaan(
            jarjestyskriteeriDAO.findByJono(jono.getOid()), uusiMasterJarjestys);
    for (Valintatapajono kopio : jono.getKopiot()) {
      jarjestaKopioValintatapajononKriteerit(kopio, jarjestetty);
    }
  }

  @Override
  public void deleteByOid(String oid) {
    Jarjestyskriteeri jarjestyskriteeri = haeJarjestyskriteeri(oid);
    if (jarjestyskriteeri.getMaster() != null) {
      throw new JarjestyskriteeriaEiVoiPoistaaException("Jarjestyskriteeri on peritty.");
    }
    delete(jarjestyskriteeri);
  }

  @Override
  public void delete(Jarjestyskriteeri jarjestyskriteeri) {
    for (Jarjestyskriteeri jk : jarjestyskriteeri.getKopiot()) {
      delete(jk);
    }
    jarjestyskriteeri.setKopiot(new HashSet<Jarjestyskriteeri>());

    Jarjestyskriteeri edellinen = jarjestyskriteeri.getEdellinen();
    Jarjestyskriteeri seuraava = jarjestyskriteeri.getSeuraava();

    if (edellinen != null) {
      edellinen.setSeuraava(null);
      jarjestyskriteeriDAO.update(edellinen);
    }

    jarjestyskriteeri.setEdellinen(null);
    jarjestyskriteeri.setSeuraava(null);
    jarjestyskriteeriDAO.update(jarjestyskriteeri);

    if (seuraava != null) {
      seuraava.setEdellinen(edellinen);
      jarjestyskriteeriDAO.update(seuraava);

      if (edellinen != null) {
        edellinen.setSeuraava(seuraava);
        jarjestyskriteeriDAO.update(edellinen);
      }
    }

    jarjestyskriteeri = jarjestyskriteeriDAO.readByOid(jarjestyskriteeri.getOid());
    jarjestyskriteeriDAO.remove(jarjestyskriteeri);
  }

  @Override
  public Jarjestyskriteeri readByOid(String oid) {
    return haeJarjestyskriteeri(oid);
  }

  @Override
  public void kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(
      Valintatapajono valintatapajono,
      Valintatapajono masterValintatapajono,
      JuureenKopiointiCache kopiointiCache) {
    Jarjestyskriteeri jk =
        jarjestyskriteeriDAO.haeValintatapajononViimeinenJarjestyskriteeri(
            masterValintatapajono.getOid());
    kopioiJarjestyskriteeritRekursiivisesti(valintatapajono, jk, kopiointiCache);
  }

  private Jarjestyskriteeri kopioiJarjestyskriteeritRekursiivisesti(
      Valintatapajono valintatapajono,
      Jarjestyskriteeri master,
      JuureenKopiointiCache kopiointiCache) {
    if (master == null) {
      return null;
    }
    Jarjestyskriteeri kopio = teeKopioMasterista(valintatapajono, master, kopiointiCache);
    kopio.setOid(oidService.haeJarjestyskriteeriOid());
    valintatapajono.addJarjestyskriteeri(kopio);
    Jarjestyskriteeri edellinen =
        kopioiJarjestyskriteeritRekursiivisesti(
            valintatapajono, master.getEdellinen(), kopiointiCache);
    if (edellinen != null) {
      kopio.setEdellinen(edellinen);
      edellinen.setSeuraava(kopio);
    }
    Jarjestyskriteeri lisatty = jarjestyskriteeriDAO.insert(kopio);
    if (kopiointiCache != null) {
      kopiointiCache.kopioidutJarjestyskriteerit.put(master.getId(), lisatty);
    }
    return lisatty;
  }
}
