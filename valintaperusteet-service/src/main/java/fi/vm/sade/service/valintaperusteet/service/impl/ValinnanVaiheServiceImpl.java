package fi.vm.sade.service.valintaperusteet.service.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValinnanVaiheKopioija;
import fi.vm.sade.service.valintaperusteet.util.ValinnanVaiheUtil;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ValinnanVaiheServiceImpl implements ValinnanVaiheService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ValinnanVaiheServiceImpl.class.getName());

  @Autowired private OidService oidService;

  @Lazy @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValinnanVaiheDAO valinnanVaiheDAO;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private ValintatapajonoService valintatapajonoService;

  @Autowired private ValintakoeService valintakoeService;

  private static ValinnanVaiheKopioija kopioija = new ValinnanVaiheKopioija();

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private ValintakoeDAO valintakoeDAO;

  @Override
  public ValinnanVaihe update(String oid, ValinnanVaiheCreateDTO dto) {
    ValinnanVaihe entity = modelMapper.map(dto, ValinnanVaihe.class);
    ValinnanVaihe managed = haeVaiheOidilla(oid);

    return LinkitettavaJaKopioitavaUtil.paivita(managed, entity, kopioija);
  }

  @Override
  public ValinnanVaihe readByOid(String oid) {
    return haeVaiheOidilla(oid);
  }

  @Override
  public List<ValinnanVaihe> findByHakukohde(String oid) {
    return valinnanVaiheDAO.findByHakukohde(oid);
  }

  private void lisaaHakukohteelleKopioMasterValinnanVaiheesta(
      HakukohdeViite hakukohde,
      ValinnanVaihe masterValinnanVaihe,
      ValinnanVaihe edellinenMasterValinnanVaihe) {
    ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(masterValinnanVaihe, null);
    kopio.setHakukohdeViite(hakukohde);
    kopio.setOid(oidService.haeValinnanVaiheOid());
    List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohde.getOid());
    kopio.setEdellinenValinnanVaihe(
        LinkitettavaJaKopioitavaUtil.kopioTaiViimeinen(edellinenMasterValinnanVaihe, vaiheet));
    valinnanVaiheDAO.insert(kopio);
  }

  private void lisaaValintaryhmalleKopioMasterValinnanVaiheesta(
      Valintaryhma valintaryhma,
      ValinnanVaihe masterValinnanVaihe,
      ValinnanVaihe edellinenMasterValinnanVaihe) {

    ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(masterValinnanVaihe, null);
    kopio.setValintaryhma(valintaryhma);
    kopio.setOid(oidService.haeValinnanVaiheOid());
    List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByValintaryhma(valintaryhma.getOid());
    kopio.setEdellinenValinnanVaihe(
        LinkitettavaJaKopioitavaUtil.kopioTaiViimeinen(edellinenMasterValinnanVaihe, vaiheet));
    ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
    List<Valintaryhma> alavalintaryhmat =
        valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
    for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
      lisaaValintaryhmalleKopioMasterValinnanVaiheesta(
          alavalintaryhma, lisatty, lisatty.getEdellinenValinnanVaihe());
    }
    List<HakukohdeViite> hakukohteet =
        hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
    for (HakukohdeViite hakukohde : hakukohteet) {
      lisaaHakukohteelleKopioMasterValinnanVaiheesta(
          hakukohde, lisatty, lisatty.getEdellinenValinnanVaihe());
    }
  }

  @Override
  public ValinnanVaihe lisaaValinnanVaiheValintaryhmalle(
      String valintaryhmaOid, ValinnanVaiheCreateDTO dto, String edellinenValinnanVaiheOid) {
    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    if (valintaryhma == null) {
      throw new ValintaryhmaEiOleOlemassaException(
          "Valintaryhmää (" + valintaryhmaOid + ") ei ole olemassa");
    }
    ValinnanVaihe edellinenValinnanVaihe = null;
    if (StringUtils.isNotBlank(edellinenValinnanVaiheOid)) {
      edellinenValinnanVaihe = haeVaiheOidilla(edellinenValinnanVaiheOid);
      tarkistaValinnanVaiheKuuluuValintaryhmaan(edellinenValinnanVaihe, valintaryhma);
    } else {
      edellinenValinnanVaihe =
          valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(valintaryhmaOid);
    }
    ValinnanVaihe valinnanVaihe = modelMapper.map(dto, ValinnanVaihe.class);
    valinnanVaihe.setOid(oidService.haeValinnanVaiheOid());
    valinnanVaihe.setValintaryhma(valintaryhma);
    valinnanVaihe.setEdellinenValinnanVaihe(edellinenValinnanVaihe);
    ValinnanVaihe lisatty = valinnanVaiheDAO.insert(valinnanVaihe);
    List<Valintaryhma> alaValintaryhmat =
        valintaryhmaService.findValintaryhmasByParentOid(valintaryhmaOid);
    for (Valintaryhma alavalintaryhma : alaValintaryhmat) {
      lisaaValintaryhmalleKopioMasterValinnanVaiheesta(
          alavalintaryhma, lisatty, edellinenValinnanVaihe);
    }
    List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
    for (HakukohdeViite hakukohde : hakukohteet) {
      lisaaHakukohteelleKopioMasterValinnanVaiheesta(hakukohde, lisatty, edellinenValinnanVaihe);
    }
    return lisatty;
  }

  @Override
  public ValinnanVaihe lisaaValinnanVaiheHakukohteelle(
      String hakukohdeOid, ValinnanVaiheCreateDTO dto, String edellinenValinnanVaiheOid) {
    HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
    ValinnanVaihe edellinenValinnanVaihe = null;
    if (StringUtils.isNotBlank(edellinenValinnanVaiheOid)) {
      edellinenValinnanVaihe = haeVaiheOidilla(edellinenValinnanVaiheOid);
      tarkistaValinnanVaiheKuuluuHakukohteeseen(edellinenValinnanVaihe, hakukohde);
    } else {
      edellinenValinnanVaihe = valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(hakukohdeOid);
    }
    ValinnanVaihe valinnanVaihe = modelMapper.map(dto, ValinnanVaihe.class);
    valinnanVaihe.setOid(oidService.haeValinnanVaiheOid());
    valinnanVaihe.setHakukohdeViite(hakukohde);
    valinnanVaihe.setEdellinenValinnanVaihe(edellinenValinnanVaihe);
    ValinnanVaihe lisatty = valinnanVaiheDAO.insert(valinnanVaihe);
    return lisatty;
  }

  private ValinnanVaihe haeVaiheOidilla(String oid) {
    ValinnanVaihe valinnanVaihe = valinnanVaiheDAO.readByOid(oid);
    if (valinnanVaihe == null) {
      throw new ValinnanVaiheEiOleOlemassaException(
          "Valinnan vaihetta (" + oid + ") ei ole " + "olemassa", oid);
    }
    return valinnanVaihe;
  }

  private static void tarkistaValinnanVaiheKuuluuValintaryhmaan(
      ValinnanVaihe valinnanVaihe, Valintaryhma valintaryhma) {
    if (!valintaryhma.equals(valinnanVaihe.getValintaryhma())) {
      throw new ValinnanVaiheEiKuuluValintaryhmaanException(
          "Valinnan vaihe ("
              + valinnanVaihe.getOid()
              + ") ei "
              + "kuulu valintaryhmään ("
              + valintaryhma.getOid()
              + ")",
          valinnanVaihe.getOid(),
          valintaryhma.getOid());
    }
  }

  private static void tarkistaValinnanVaiheKuuluuHakukohteeseen(
      ValinnanVaihe valinnanVaihe, HakukohdeViite hakukohdeViite) {
    if (!hakukohdeViite.equals(valinnanVaihe.getHakukohdeViite())) {
      throw new ValinnanVaiheEiKuuluHakukohteeseenException(
          "Valinnan vaihe ("
              + valinnanVaihe.getOid()
              + ") ei "
              + "kuulu hakukohteeseen ("
              + hakukohdeViite.getOid()
              + ")",
          valinnanVaihe.getOid(),
          hakukohdeViite.getOid());
    }
  }

  @Override
  public ValinnanVaiheDTO delete(String valinnanVaiheOid) {
    ValinnanVaihe valinnanVaihe = haeVaiheOidilla(valinnanVaiheOid);
    ValinnanVaiheDTO dto = modelMapper.map(valinnanVaihe, ValinnanVaiheDTO.class);
    delete(valinnanVaihe);
    return dto;
  }

  @Override
  public void delete(ValinnanVaihe valinnanVaihe) {
    valinnanVaiheDAO.delete(valinnanVaihe);
  }

  @Override
  public List<ValinnanVaihe> jarjestaValinnanVaiheet(List<String> valinnanVaiheOidit) {
    if (valinnanVaiheOidit.isEmpty()) {
      throw new ValinnanVaiheOidListaOnTyhjaException("Valinnan vaiheiden OID-lista on tyhjä");
    }
    String ensimmainen = valinnanVaiheOidit.get(0);
    ValinnanVaihe valinnanVaihe = haeVaiheOidilla(ensimmainen);
    if (valinnanVaihe.getValintaryhma() != null) {
      return jarjestaValinnanVaiheet(valinnanVaihe.getValintaryhma(), valinnanVaiheOidit);
    } else {
      return valinnanVaiheDAO.jarjestaUudelleen(
          valinnanVaihe.getHakukohdeViite(), valinnanVaiheOidit);
    }
  }

  private void jarjestaAlavalintaryhmanValinnanVaiheet(
      Valintaryhma valintaryhma, List<ValinnanVaihe> uusiMasterJarjestys) {
    try {
      List<ValinnanVaihe> jarjestetty =
          valinnanVaiheDAO.jarjestaUudelleenMasterJarjestyksenMukaan(
              valintaryhma, uusiMasterJarjestys);
      List<Valintaryhma> alavalintaryhmat =
          valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
      for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
        jarjestaAlavalintaryhmanValinnanVaiheet(alavalintaryhma, jarjestetty);
      }
      List<HakukohdeViite> hakukohteet =
          hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
      for (HakukohdeViite hakukohde : hakukohteet) {
        valinnanVaiheDAO.jarjestaUudelleenMasterJarjestyksenMukaan(hakukohde, jarjestetty);
      }
    } catch (Exception e) {
      LOGGER.error(
          "Valintaryhmän {} {} valinnanvaiheiden järjestäminen epäonnistui: ",
          valintaryhma.getNimi(),
          valintaryhma.getOid(),
          e);
      throw e;
    }
  }

  private List<ValinnanVaihe> jarjestaValinnanVaiheet(
      Valintaryhma valintaryhma, List<String> valinnanVaiheOidit) {
    List<ValinnanVaihe> jarjestetty =
        valinnanVaiheDAO.jarjestaUudelleen(valintaryhma, valinnanVaiheOidit);
    List<Valintaryhma> alavalintaryhmat =
        valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
    for (Valintaryhma alavalintaryhma : alavalintaryhmat) {
      jarjestaAlavalintaryhmanValinnanVaiheet(alavalintaryhma, jarjestetty);
    }
    List<HakukohdeViite> hakukohteet =
        hakukohdeService.findByValintaryhmaOid(valintaryhma.getOid());
    for (HakukohdeViite hakukohde : hakukohteet) {
      valinnanVaiheDAO.jarjestaUudelleenMasterJarjestyksenMukaan(hakukohde, jarjestetty);
    }
    return jarjestetty;
  }

  public List<ValinnanVaihe> findByValintaryhma(String oid) {
    return valinnanVaiheDAO.findByValintaryhma(oid);
  }

  @Override
  public void kopioiValinnanVaiheetParentilta(
      Valintaryhma valintaryhma,
      Valintaryhma parentValintaryhma,
      JuureenKopiointiCache kopiointiCache) {
    if (parentValintaryhma != null) {
      List<ValinnanVaihe> valinnanVaiheet =
          valinnanVaiheDAO.findByValintaryhma(parentValintaryhma.getOid());
      Collections.reverse(valinnanVaiheet);
      for (ValinnanVaihe valinnanVaihe : valinnanVaiheet) {
        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(valinnanVaihe, kopiointiCache);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        kopio.setValintaryhma(valintaryhma);
        valintaryhma.addValinnanVaihe(kopio);
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        if (kopiointiCache != null) {
          kopiointiCache.kopioidutValinnanVaiheet.put(valinnanVaihe.getId(), lisatty);
        }
        valintatapajonoService.kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(
            lisatty, valinnanVaihe, kopiointiCache);
        valintakoeService.kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(
            lisatty, valinnanVaihe, kopiointiCache);
      }
    }
  }

  @Override
  public void kopioiValinnanVaiheetParentilta(
      HakukohdeViite hakukohde,
      Valintaryhma parentValintaryhma,
      JuureenKopiointiCache kopiointiCache) {
    if (parentValintaryhma != null) {
      List<ValinnanVaihe> valinnanVaiheet =
          valinnanVaiheDAO.findByValintaryhma(parentValintaryhma.getOid());
      Collections.reverse(valinnanVaiheet);
      for (ValinnanVaihe valinnanVaihe : valinnanVaiheet) {
        ValinnanVaihe kopio = ValinnanVaiheUtil.teeKopioMasterista(valinnanVaihe, kopiointiCache);
        kopio.setOid(oidService.haeValinnanVaiheOid());
        hakukohde.addValinnanVaihe(kopio);
        ValinnanVaihe lisatty = valinnanVaiheDAO.insert(kopio);
        if (kopiointiCache != null) {
          kopiointiCache.kopioidutValinnanVaiheet.put(valinnanVaihe.getId(), lisatty);
        }
        valintatapajonoService.kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(
            lisatty, valinnanVaihe, kopiointiCache);
        valintakoeService.kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(
            lisatty, valinnanVaihe, kopiointiCache);
      }
    }
  }

  @Override
  public boolean kuuluuSijoitteluun(String oid) {
    return valinnanVaiheDAO.kuuluuSijoitteluun(oid);
  }

  @Override
  public Set<String> getValintaryhmaOids(String oid) {
    Set<String> res = Sets.newHashSet();
    getValintaryhmaOids(readByOid(oid), res);
    return res;
  }

  // XXX: This recursive call is super slow, rather than making single queries for each
  // ValinnanVaihe object
  // XXX: implement interface that takes batch of these objects and processes these in a single run
  private void getValintaryhmaOids(ValinnanVaihe valinnanvaihe, Set<String> res) {
    if (valinnanvaihe.getAktiivinen() && valinnanvaihe.getValintaryhma() != null)
      res.add(valinnanvaihe.getValintaryhma().getOid());
    for (ValinnanVaihe child : valinnanvaihe.getKopioValinnanVaiheet()) {
      getValintaryhmaOids(child, res);
    }
  }
}
