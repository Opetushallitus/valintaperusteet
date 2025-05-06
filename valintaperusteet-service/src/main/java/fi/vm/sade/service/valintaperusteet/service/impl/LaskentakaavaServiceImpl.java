package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohteenValintaperusteDAO;
import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.SyotettavanarvontyyppiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LokalisoituTeksti;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;
import fi.vm.sade.service.valintaperusteet.model.Syotettavanarvontyyppi;
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintalaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.ActorService;
import fi.vm.sade.service.valintaperusteet.service.impl.util.HakukohteenValintaperusteetUtil;
import fi.vm.sade.service.valintaperusteet.service.impl.util.LaskentakaavaCache;
import fi.vm.sade.service.valintaperusteet.service.impl.util.ObjectGraphUtil;
import fi.vm.sade.service.valintaperusteet.service.impl.util.ValintaperusteetUtil;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LaskentakaavaServiceImpl implements LaskentakaavaService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(LaskentakaavaService.class.getName());
  private static final String r = "\\{\\{([A-Za-z0–9\\-_]+)\\.([A-Za-z0–9\\-_]+)\\}\\}";
  public static final Pattern pattern = Pattern.compile(r);

  @Autowired private GenericDAO genericDAO;

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Autowired private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private LaskentakaavaCache laskentakaavaCache;

  @Autowired private JarjestyskriteeriDAO jarjestyskriteeriDAO;

  @Autowired private HakijaryhmaDAO hakijaryhmaDAO;

  @Autowired private ValintakoeDAO valintakoeDAO;

  @Autowired private SyotettavanarvontyyppiDAO syotettavanarvontyyppiDAO;

  @Autowired private ActorService actorService;

  private Laskentakaava haeLaskentakaava(Long id) {
    Laskentakaava laskentakaava = laskentakaavaDAO.getLaskentakaava(id);
    if (laskentakaava == null) {
      throw new LaskentakaavaEiOleOlemassaException(
          "Laskentakaava (" + id + ") ei ole olemassa.", id);
    }
    return laskentakaava;
  }

  @Override
  @Transactional
  public Laskentakaava haeMallinnettuKaava(Long id) {
    return haeKokoLaskentakaava(id, false);
  }

  @Override
  public Laskentakaava update(Long id, LaskentakaavaCreateDTO incoming) {
    laskentakaavaCache.clear();
    try {
      Laskentakaava entity = modelMapper.map(incoming, Laskentakaava.class);
      asetaNullitOletusarvoiksi(entity.getFunktiokutsu());
      Laskentakaava managed = haeLaskentakaava(id);
      Set<Long> laskentakaavaIds = new HashSet<Long>();
      laskentakaavaIds.add(managed.getId());
      managed.setNimi(entity.getNimi());
      managed.setKuvaus(entity.getKuvaus());
      managed.setFunktiokutsu(
          updateFunktiokutsu(
              entity.getFunktiokutsu(),
              false,
              entity.getHakukohde(),
              entity.getValintaryhma(),
              laskentakaavaIds));
      if (!Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(managed).isEmpty()) {
        throw new LaskentakaavaEiValidiException(
            "Laskentakaava ei ole validi",
            Laskentakaavavalidaattori.validoiMallinnettuKaava(entity));
      }
      return managed;
    } catch (FunktiokutsuMuodostaaSilmukanException e) {
      throw new LaskentakaavaMuodostaaSilmukanException(
          "Laskentakaava "
              + id
              + " muodostaa silmukan "
              + "laskentakaavaan "
              + e.getLaskentakaavaId()
              + " kautta",
          e,
          id,
          e.getLaskentakaavaId());
    }
  }

  private void asetaNullitOletusarvoiksi(Funktiokutsu fk) {
    for (ValintaperusteViite vp : fk.getValintaperusteviitteet()) {
      if (vp.getOnPakollinen() == null) {
        vp.setOnPakollinen(false);
      }
      if (vp.getEpasuoraViittaus() == null) {
        vp.setEpasuoraViittaus(false);
      }
    }
    for (Arvokonvertteriparametri a : fk.getArvokonvertteriparametrit()) {
      if (a.getHylkaysperuste() == null) {
        a.setHylkaysperuste("false");
      }
    }
    for (Arvovalikonvertteriparametri a : fk.getArvovalikonvertteriparametrit()) {
      if (a.getPalautaHaettuArvo() == null) {
        a.setPalautaHaettuArvo("false");
      }
    }
    for (Syoteparametri s : fk.getSyoteparametrit()) {
      if (s.getArvo() == null) {
        s.setArvo("");
      }
    }
    if (fk.getValintaperusteviitteet().size() == 1) {
      fk.getValintaperusteviitteet().iterator().next().setIndeksi(1);
    }
    for (Funktioargumentti arg : fk.getFunktioargumentit()) {
      if (arg.getFunktiokutsuChild() != null) {
        asetaNullitOletusarvoiksi(arg.getFunktiokutsuChild());
      }
    }
  }

  private Funktiokutsu updateFunktiokutsu(
      Funktiokutsu incoming, boolean copy, HakukohdeViite hakukohde, Valintaryhma valintaryhma)
      throws FunktiokutsuMuodostaaSilmukanException {
    return updateFunktiokutsu(incoming, copy, hakukohde, valintaryhma, new HashSet<Long>());
  }

  private Funktiokutsu updateFunktiokutsu(
      Funktiokutsu incoming,
      boolean copy,
      HakukohdeViite hakukohde,
      Valintaryhma valintaryhma,
      Set<Long> laskentakaavaIds)
      throws FunktiokutsuMuodostaaSilmukanException {
    Funktiokutsu managed = new Funktiokutsu();
    managed.setFunktionimi(incoming.getFunktionimi());
    managed.setTallennaTulos(incoming.getTallennaTulos());
    managed.setTulosTunniste(incoming.getTulosTunniste());
    managed.setTulosTekstiEn(incoming.getTulosTekstiEn());
    managed.setTulosTekstiFi(incoming.getTulosTekstiFi());
    managed.setTulosTekstiSv(incoming.getTulosTekstiSv());
    managed.setOmaopintopolku(incoming.getOmaopintopolku());
    for (Funktioargumentti arg : incoming.getFunktioargumentit()) {
      Funktioargumentti newArg = new Funktioargumentti();
      newArg.setParent(managed);
      if (arg.getFunktiokutsuChild() != null) {
        newArg.setFunktiokutsuChild(
            updateFunktiokutsu(
                arg.getFunktiokutsuChild(), copy, hakukohde, valintaryhma, laskentakaavaIds));
      } else {
        Long laskentakaavaId = arg.getLaskentakaavaChild().getId();
        if (laskentakaavaIds.contains(laskentakaavaId)) {
          throw new FunktiokutsuMuodostaaSilmukanException(
              "Laskentakaavan "
                  + laskentakaavaId
                  + " funktiokutsu muodostaa silmukan "
                  + "laskentakaavaan ",
              managed.getFunktionimi(),
              laskentakaavaId);
        }
        Set<Long> newLaskentakaavaIds = new HashSet<Long>(laskentakaavaIds);
        newLaskentakaavaIds.add(laskentakaavaId);
        final Laskentakaava oldLaskentakaava =
            haeKokoLaskentakaavaJaTarkistaSilmukat(laskentakaavaId, newLaskentakaavaIds);
        if (hakukohde == null && valintaryhma == null) {
          newArg.setLaskentakaavaChild(oldLaskentakaava);
        } else {
          newArg.setLaskentakaavaChild(
              kopioiJosEiJoKopioitu(oldLaskentakaava, hakukohde, valintaryhma));
        }
      }
      newArg.setIndeksi(arg.getIndeksi());
      managed.getFunktioargumentit().add(newArg);
    }
    for (Arvokonvertteriparametri k : incoming.getArvokonvertteriparametrit()) {
      Arvokonvertteriparametri newParam = new Arvokonvertteriparametri();
      newParam.setArvo(k.getArvo());
      newParam.setHylkaysperuste(k.getHylkaysperuste());
      newParam.setPaluuarvo(k.getPaluuarvo());
      newParam.setFunktiokutsu(managed);
      if (k.getKuvaukset() != null) {
        TekstiRyhma ryhma = new TekstiRyhma();
        genericDAO.insert(ryhma, false);
        for (LokalisoituTeksti teksti : k.getKuvaukset().getTekstit()) {
          LokalisoituTeksti newTeksti = new LokalisoituTeksti();
          newTeksti.setKieli(teksti.getKieli());
          newTeksti.setTeksti(teksti.getTeksti());
          newTeksti.setRyhma(ryhma);
          genericDAO.insert(newTeksti, false);
          ryhma.getTekstit().add(newTeksti);
        }
        newParam.setKuvaukset(ryhma);
      }
      managed.getArvokonvertteriparametrit().add(newParam);
    }
    for (Arvovalikonvertteriparametri k : incoming.getArvovalikonvertteriparametrit()) {
      Arvovalikonvertteriparametri newParam = new Arvovalikonvertteriparametri();
      newParam.setMaxValue(k.getMaxValue());
      newParam.setMinValue(k.getMinValue());
      newParam.setPalautaHaettuArvo(k.getPalautaHaettuArvo());
      newParam.setPaluuarvo(k.getPaluuarvo());
      newParam.setFunktiokutsu(managed);
      newParam.setHylkaysperuste(k.getHylkaysperuste());
      if (k.getKuvaukset() != null) {
        TekstiRyhma ryhma = new TekstiRyhma();
        genericDAO.insert(ryhma, false);
        for (LokalisoituTeksti teksti : k.getKuvaukset().getTekstit()) {
          LokalisoituTeksti newTeksti = new LokalisoituTeksti();
          newTeksti.setKieli(teksti.getKieli());
          newTeksti.setTeksti(teksti.getTeksti());
          newTeksti.setRyhma(ryhma);
          genericDAO.insert(newTeksti, false);
          ryhma.getTekstit().add(newTeksti);
        }
        newParam.setKuvaukset(ryhma);
      }
      managed.getArvovalikonvertteriparametrit().add(newParam);
    }
    for (Syoteparametri s : incoming.getSyoteparametrit()) {
      Syoteparametri newParam = new Syoteparametri();
      newParam.setArvo(s.getArvo());
      newParam.setAvain(s.getAvain());
      newParam.setFunktiokutsu(managed);
      managed.getSyoteparametrit().add(newParam);
    }
    for (ValintaperusteViite vp : incoming.getValintaperusteviitteet()) {
      ValintaperusteViite newVp = new ValintaperusteViite();
      newVp.setEpasuoraViittaus(vp.getEpasuoraViittaus());
      newVp.setIndeksi(vp.getIndeksi() + 1);
      newVp.setKuvaus(vp.getKuvaus());
      newVp.setLahde(vp.getLahde());
      newVp.setOnPakollinen(vp.getOnPakollinen());
      newVp.setTunniste(vp.getTunniste());

      if (vp.getSyotettavanarvontyyppi() != null) {
        Syotettavanarvontyyppi syokoodi = vp.getSyotettavanarvontyyppi();
        Syotettavanarvontyyppi found = syotettavanarvontyyppiDAO.readByUri(syokoodi.getUri());

        if (found != null) {
          newVp.setSyotettavanarvontyyppi(found);
        } else {
          Syotettavanarvontyyppi uusikoodi = new Syotettavanarvontyyppi();
          uusikoodi.setUri(syokoodi.getUri());
          uusikoodi.setArvo(syokoodi.getArvo());
          uusikoodi.setNimiFi(syokoodi.getNimiFi());
          uusikoodi.setNimiEn(syokoodi.getNimiEn());
          uusikoodi.setNimiSv(syokoodi.getNimiSv());
          Syotettavanarvontyyppi inserted = syotettavanarvontyyppiDAO.insertOrUpdate(uusikoodi);
          newVp.setSyotettavanarvontyyppi(inserted);
        }
      }
      newVp.setTilastoidaan(vp.getTilastoidaan());

      newVp.setVaatiiOsallistumisen(vp.getVaatiiOsallistumisen());
      newVp.setSyotettavissaKaikille(vp.getSyotettavissaKaikille());
      newVp.setFunktiokutsu(managed);
      if (vp.getKuvaukset() != null) {
        TekstiRyhma ryhma = new TekstiRyhma();
        genericDAO.insert(ryhma, false);
        for (LokalisoituTeksti teksti : vp.getKuvaukset().getTekstit()) {
          LokalisoituTeksti newTeksti = new LokalisoituTeksti();
          newTeksti.setKieli(teksti.getKieli());
          newTeksti.setTeksti(teksti.getTeksti());
          newTeksti.setRyhma(ryhma);
          genericDAO.insert(newTeksti, false);
          ryhma.getTekstit().add(newTeksti);
        }
        newVp.setKuvaukset(ryhma);
      }
      managed.getValintaperusteviitteet().add(newVp);
    }
    return managed;
  }

  @Override
  public Laskentakaava insert(
      Laskentakaava laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
    laskentakaavaCache.clear();
    try {
      asetaNullitOletusarvoiksi(laskentakaava.getFunktiokutsu());
      Laskentakaava entity = modelMapper.map(laskentakaava, Laskentakaava.class);
      if (!Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(entity).isEmpty()) {
        throw new LaskentakaavaEiValidiException(
            "Laskentakaava ei ole validi",
            Laskentakaavavalidaattori.validoiMallinnettuKaava(entity));
      }
      HakukohdeViite hakukohde = null;
      Valintaryhma valintaryhma = null;
      if (StringUtils.isNotBlank(hakukohdeOid)) {
        hakukohde = hakukohdeViiteDAO.readForImport(hakukohdeOid);
        entity.setHakukohde(hakukohde);
      } else if (StringUtils.isNotBlank(valintaryhmaOid)) {
        valintaryhma = valintaryhmaDAO.readByOid(valintaryhmaOid);
        entity.setValintaryhma(valintaryhma);
      }
      entity.setFunktiokutsu(
          updateFunktiokutsu(entity.getFunktiokutsu(), false, hakukohde, valintaryhma));
      return laskentakaavaDAO.insert(entity);
    } catch (FunktiokutsuMuodostaaSilmukanException e) {
      throw new LaskentakaavaMuodostaaSilmukanException(
          "Laskentakaava  muodostaa silmukan "
              + "laskentakaavaan "
              + e.getLaskentakaavaId()
              + " funktiokutsun kautta",
          e,
          null,
          e.getLaskentakaavaId());
    }
  }

  @Override
  public void tyhjennaCache() {
    laskentakaavaCache.clear();
  }

  @Override
  public Optional<Laskentakaava> siirra(LaskentakaavaSiirraDTO dto) {
    if (dto.getUusinimi() != null) {
      dto.setNimi(dto.getUusinimi());
    }
    String valintaryhmaOid = null;
    String hakukohdeOid = null;
    if (dto.getValintaryhmaOid() != null && !dto.getValintaryhmaOid().isEmpty()) {
      Optional<Valintaryhma> ryhma =
          Optional.ofNullable(valintaryhmaDAO.readByOid(dto.getValintaryhmaOid()));
      if (ryhma.isPresent()) {
        valintaryhmaOid = ryhma.get().getOid();
      }
    }
    if (dto.getHakukohdeOid() != null && !dto.getHakukohdeOid().isEmpty()) {
      Optional<HakukohdeViite> hakukohde =
          Optional.ofNullable(hakukohdeViiteDAO.readForImport(dto.getHakukohdeOid()));
      if (hakukohde.isPresent()) {
        hakukohdeOid = hakukohde.get().getOid();
      }
    }
    if (valintaryhmaOid == null && hakukohdeOid == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(
        insert(modelMapper.map(dto, Laskentakaava.class), hakukohdeOid, valintaryhmaOid));
  }

  @Override
  public Optional<Laskentakaava> haeLaskentakaavaTaiSenKopioVanhemmilta(
      Long laskentakaavaId, HakukohdeViite hakukohde) {
    if (laskentakaavaId == null || hakukohde == null) {
      return Optional.empty();
    }
    Optional<Laskentakaava> kaava =
        (hakukohde.getLaskentakaava().stream()
                .filter(k -> onSamaTaiKopioSamastaKaavasta(laskentakaavaId, k)))
            .findFirst();

    if (kaava.isPresent()) {
      return kaava;
    } else {
      Set<Long> tarkistetutLaskentaKaavaIdt = getLaskentakaavaIds(hakukohde.getLaskentakaava());
      return haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(
          laskentakaavaId, hakukohde.getValintaryhma(), tarkistetutLaskentaKaavaIdt);
    }
  }

  @Override
  public Optional<Laskentakaava> haeLaskentakaavaTaiSenKopioVanhemmilta(
      Long laskentakaavaId, Valintaryhma valintaryhma) {
    if (laskentakaavaId == null || valintaryhma == null) {
      return Optional.empty();
    }
    return haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(
        laskentakaavaId, valintaryhma, new HashSet<>());
  }

  private boolean onSamaTaiKopioSamastaKaavasta(Long laskentakaavaId, Laskentakaava kaava) {
    return laskentakaavaId.equals(kaava.getId())
        || (kaava.getKopioLaskentakaavasta() != null
            && laskentakaavaId.equals(kaava.getKopioLaskentakaavasta().getId()));
  }

  private Set<Long> getLaskentakaavaIds(Set<Laskentakaava> set) {
    return set.stream().map(Laskentakaava::getId).collect(Collectors.toSet());
  }

  private Optional<Laskentakaava> haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(
      Long laskentakaavaId,
      Valintaryhma valintaryhma,
      final Set<Long> tarkistetutLaskentaKaavaIdt) {
    Optional<Laskentakaava> kaava =
        (valintaryhma.getLaskentakaava().stream()
                .filter(k -> !tarkistetutLaskentaKaavaIdt.contains(k.getId()))
                .filter(k -> onSamaTaiKopioSamastaKaavasta(laskentakaavaId, k)))
            .findFirst();

    if (kaava.isPresent()) {
      return kaava;
    } else if (valintaryhma.getYlavalintaryhma() != null) {
      tarkistetutLaskentaKaavaIdt.addAll(getLaskentakaavaIds(valintaryhma.getLaskentakaava()));
      return haeLaskentakaavaTaiSenKopioVanhemmiltaRecursion(
          laskentakaavaId, valintaryhma.getYlavalintaryhma(), tarkistetutLaskentaKaavaIdt);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Laskentakaava kopioiJosEiJoKopioitu(
      Laskentakaava lahdeLaskentakaava,
      HakukohdeViite kohdeHakukohde,
      Valintaryhma kohdeValintaryhma) {
    Optional<Laskentakaava> aikaisemminKopioituLaskentakaava;
    if (kohdeHakukohde != null) {
      aikaisemminKopioituLaskentakaava =
          haeLaskentakaavaTaiSenKopioVanhemmilta(lahdeLaskentakaava.getId(), kohdeHakukohde);
    } else {
      aikaisemminKopioituLaskentakaava =
          haeLaskentakaavaTaiSenKopioVanhemmilta(lahdeLaskentakaava.getId(), kohdeValintaryhma);
    }
    if (aikaisemminKopioituLaskentakaava.isPresent()) {
      LOGGER.info(
          "Käytetään laskentakaavan {} olemassaolevaa versiota {}: kohde hakukohde={}, kohde valintaryhma={}",
          lahdeLaskentakaava,
          aikaisemminKopioituLaskentakaava.get(),
          kohdeHakukohde,
          kohdeValintaryhma);
      return aikaisemminKopioituLaskentakaava.get();
    }
    LOGGER.info(
        "Kopioidaan laskentakaava {}: kohde hakukohde={}, kohde valintaryhma={}",
        lahdeLaskentakaava,
        kohdeHakukohde,
        kohdeValintaryhma);
    Laskentakaava copy = new Laskentakaava();
    copy.setKopioLaskentakaavasta(lahdeLaskentakaava);
    copy.setHakukohde(kohdeHakukohde);
    copy.setValintaryhma(kohdeValintaryhma);
    copy.setKuvaus(lahdeLaskentakaava.getKuvaus());
    copy.setTyyppi(lahdeLaskentakaava.getTyyppi());
    copy.setNimi(lahdeLaskentakaava.getNimi());
    copy.setOnLuonnos(lahdeLaskentakaava.getOnLuonnos());
    if (kohdeValintaryhma != null) {
      kohdeValintaryhma.getLaskentakaava().add(copy);
    }
    if (kohdeHakukohde != null) {
      kohdeHakukohde.getLaskentakaava().add(copy);
    }
    try {
      copy.setFunktiokutsu(
          updateFunktiokutsu(
              lahdeLaskentakaava.getFunktiokutsu(), true, kohdeHakukohde, kohdeValintaryhma));
    } catch (FunktiokutsuMuodostaaSilmukanException e) {
      throw new LaskentakaavaMuodostaaSilmukanException(
          "Laskentakaava  muodostaa silmukan laskentakaavaan "
              + e.getLaskentakaavaId()
              + " funktiokutsun  kautta",
          e,
          null,
          e.getLaskentakaavaId());
    }
    return laskentakaavaDAO.insert(copy);
  }

  @Override
  public Optional<Valintaryhma> valintaryhma(long id) {
    Optional<Laskentakaava> kaava =
        Optional.ofNullable(laskentakaavaDAO.getLaskentakaavaValintaryhma(id));
    return kaava.map(k -> Optional.ofNullable(k.getValintaryhma())).orElse(Optional.empty());
  }

  @Override
  public Optional<Laskentakaava> pelkkaKaava(Long key) {
    return Optional.ofNullable(laskentakaavaDAO.getLaskentakaava(key));
  }

  @Override
  public boolean poista(long id) {
    Optional<Laskentakaava> kaava = Optional.ofNullable(laskentakaavaDAO.getLaskentakaava(id));
    if (!kaava.isPresent()) {
      LOGGER.info(String.format("Poistettavaa laskentakaavaa %s ei löytynyt", id));
      return false;
    }
    LOGGER.info(
        String.format(
            "Yritetään poistaa laskentakaava %s %s", kaava.get().getId(), kaava.get().getNimi()));
    List<Jarjestyskriteeri> j = jarjestyskriteeriDAO.findByLaskentakaava(id);
    List<Hakijaryhma> h = hakijaryhmaDAO.findByLaskentakaava(id);
    List<Valintakoe> v = valintakoeDAO.findByLaskentakaava(id);
    if (j.isEmpty()
        && h.isEmpty()
        && v.isEmpty()
        && !laskentakaavaDAO.isReferencedByOtherLaskentakaavas(id)) {
      Laskentakaava l = kaava.get();
      l.getKopiot()
          .forEach(
              k -> {
                LOGGER.info(
                    String.format(
                        "Poistettavan laskentakaavan %s %s kopio on %s %s",
                        l.getId(), l.getNimi(), k.getId(), k.getNimi()));
                k.setKopioLaskentakaavasta(null);
                laskentakaavaDAO.update(k);
              });
      laskentakaavaDAO.remove(l);
      laskentakaavaDAO.flush();
      LOGGER.info(
          String.format(
              "Laskentakaava %s %s on poistettu", kaava.get().getId(), kaava.get().getNimi()));
      return true;
    } else {
      LOGGER.info(
          String.format(
              "Laskentakaavaa %s %s ei pystytty poistamaan",
              kaava.get().getId(), kaava.get().getNimi()));
      return false;
    }
  }

  @Override
  public Laskentakaava insert(
      LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
    return insert(
        modelMapper.map(laskentakaava, Laskentakaava.class), hakukohdeOid, valintaryhmaOid);
  }

  @Override
  public String haeHakuoid(String hakukohdeOid, String valintaryhmaOid) {
    final Optional<String> hakuoid;
    if (StringUtils.isNotBlank(hakukohdeOid)) {
      hakuoid = getHakuOid(hakukohdeViiteDAO.readForImport(hakukohdeOid));
    } else if (StringUtils.isNotBlank(valintaryhmaOid)) {
      hakuoid = getHakuOid(valintaryhmaDAO.readByOid(valintaryhmaOid));
    } else {
      hakuoid = Optional.empty();
    }
    return hakuoid.orElse("");
  }

  private Optional<String> getHakuOid(Valintaryhma valintaryhma) {
    if (valintaryhma.getHakuoid() != null) {
      return Optional.of(valintaryhma.getHakuoid());
    }
    if (valintaryhma.getYlavalintaryhma() != null) {
      return getHakuOid(valintaryhma.getYlavalintaryhma());
    }
    return Optional.empty();
  }

  private Optional<String> getHakuOid(HakukohdeViite hakukohde) {
    if (hakukohde.getHakuoid() != null) {
      return Optional.of(hakukohde.getHakuoid());
    }
    if (hakukohde.getValintaryhma() != null) {
      return getHakuOid(hakukohde.getValintaryhma());
    }
    return Optional.empty();
  }

  private Map<String, String> hakukohteenValintaperusteetMap(List<HakukohteenValintaperuste> vps) {
    Map<String, String> map = new HashMap<String, String>();
    for (HakukohteenValintaperuste vp : vps) {
      map.put(vp.getTunniste(), vp.getArvo());
    }
    return map;
  }

  private List<ValintaperusteDTO> convertToAvaimet(
      List<Funktiokutsu> hakukohteenFunktiokutsut,
      List<HakukohteenValintaperuste> hakukohteenValintaperusteet) {
    return convertToAvaimet(
        hakukohteenFunktiokutsut, hakukohteenValintaperusteetMap(hakukohteenValintaperusteet));
  }

  private List<ValintaperusteDTO> convertToAvaimet(
      List<Funktiokutsu> hakukohteenFunktiokutsut,
      Map<String, String> hakukohteenValintaperusteet) {
    Map<String, ValintaperusteDTO> valintaperusteet = new HashMap<>();
    for (Funktiokutsu kutsu : hakukohteenFunktiokutsut) {
      for (Funktiokutsu k : ObjectGraphUtil.extractObjectsOfType(kutsu, Funktiokutsu.class)) {
        valintaperusteet.putAll(ValintaperusteetUtil.haeAvaimet(k, hakukohteenValintaperusteet));
      }
    }
    List<ValintaperusteDTO> result = new ArrayList<>(valintaperusteet.values());
    return result;
  }

  @Override
  public List<ValintaperusteDTO> findAvaimetForHakukohde(String hakukohdeOid) {
    List<ValintaperusteDTO> result =
        convertToAvaimet(
            laskentakaavaDAO.findFunktiokutsuByHakukohdeOid(hakukohdeOid),
            hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohdeOid));
    return result;
  }

  @Override
  public Map<String, List<ValintaperusteDTO>> findAvaimetForHakukohteet(
      List<String> hakukohdeOidit) {
    Map<String, List<Funktiokutsu>> hakukohteidenFunktiokutsut =
        laskentakaavaDAO.findFunktiokutsuByHakukohdeOids(hakukohdeOidit);
    Map<String, List<HakukohteenValintaperuste>> hakukohteidenValintaperusteet =
        hakukohteenValintaperusteDAO.haeHakukohteidenValintaperusteet(hakukohdeOidit).stream()
            .collect(Collectors.groupingBy(vp -> vp.getHakukohde().getOid(), Collectors.toList()));
    return hakukohdeOidit.stream()
        .collect(
            Collectors.toMap(
                oid -> oid,
                oid ->
                    convertToAvaimet(
                        hakukohteidenFunktiokutsut.getOrDefault(oid, Collections.emptyList()),
                        hakukohteidenValintaperusteet.getOrDefault(oid, Collections.emptyList()))));
  }

  @Override
  public HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(String oid) {
    List<Funktiokutsu> funktiokutsut = laskentakaavaDAO.findFunktiokutsuByHakukohdeOid(oid);
    HakukohteenValintaperusteAvaimetDTO valintaperusteet =
        new HakukohteenValintaperusteAvaimetDTO();
    for (Funktiokutsu kutsu : funktiokutsut) {
      for (Funktiokutsu k : ObjectGraphUtil.extractObjectsOfType(kutsu, Funktiokutsu.class)) {
        HakukohteenValintaperusteetUtil.haeAvaimet(k, valintaperusteet);
      }
    }
    return valintaperusteet;
  }

  @Override
  @Transactional
  public Laskentakaava validoi(LaskentakaavaDTO dto) {
    Laskentakaava kaava = modelMapper.map(dto, Laskentakaava.class);
    asetaNullitOletusarvoiksi(kaava.getFunktiokutsu());
    return Laskentakaavavalidaattori.validoiMallinnettuKaava(laajennaAlakaavat(kaava));
  }

  @Override
  @Transactional
  public boolean onkoKaavaValidi(Laskentakaava laskentakaava) {
    return Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(laskentakaava).isEmpty();
  }

  @Override
  @Transactional
  public List<Laskentakaava> findKaavas(
      boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi) {
    return laskentakaavaDAO.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi);
  }

  private Laskentakaava laajennaAlakaavat(Laskentakaava laskentakaava) {
    laajennaAlakaavat(laskentakaava.getFunktiokutsu());
    return laskentakaava;
  }

  private void laajennaAlakaavat(Funktiokutsu funktiokutsu) {
    if (funktiokutsu != null && funktiokutsu.getFunktioargumentit() != null) {
      for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
        if (arg.getFunktiokutsuChild() != null) {
          laajennaAlakaavat(arg.getFunktiokutsuChild());
        } else if (arg.getLaskentakaavaChild() != null
            && arg.getLaskentakaavaChild().getId() != null) {
          arg.setLaskentakaavaChild(haeLaskentakaava(arg.getLaskentakaavaChild().getId()));
        }
      }
    }
  }

  private Laskentakaava haeKokoLaskentakaava(
      Long id, boolean laajennaAlakaavat, Set<Long> laskentakaavaIds)
      throws FunktiokutsuMuodostaaSilmukanException {
    Laskentakaava laskentakaava = haeLaskentakaava(id);
    if (laskentakaava != null) {
      laskentakaavaIds.add(laskentakaava.getId());
      genericDAO.detach(laskentakaava);
    }
    return laskentakaava;
  }

  private Laskentakaava haeKokoLaskentakaavaJaTarkistaSilmukat(Long id, Set<Long> laskentakaavaIds)
      throws FunktiokutsuMuodostaaSilmukanException {
    Laskentakaava laskentakaava = haeLaskentakaava(id);
    Collection<Laskentakaava> laskentakaavat =
        ObjectGraphUtil.extractObjectsOfType(laskentakaava, Laskentakaava.class);
    laskentakaavat.remove(laskentakaava);
    if (laskentakaavat.stream()
        .filter(k -> laskentakaavaIds.contains(k.getId()))
        .findAny()
        .isPresent()) {
      throw new FunktiokutsuMuodostaaSilmukanException(
          laskentakaava.getFunktiokutsu().getFunktionimi(), id);
    }
    return laskentakaava;
  }

  private Laskentakaava haeKokoLaskentakaava(Long id, boolean laajennaAlakaavat) {
    try {
      return haeKokoLaskentakaava(id, laajennaAlakaavat, new HashSet<Long>());
    } catch (FunktiokutsuMuodostaaSilmukanException e) {
      throw new LaskentakaavaMuodostaaSilmukanException(
          "Laskentakaava "
              + id
              + " muodostaa silmukan "
              + "laskentakaavaan "
              + e.getLaskentakaavaId()
              + " funktiokutsun  kautta",
          e,
          id,
          e.getLaskentakaavaId());
    }
  }

  private void validoiFunktiokutsuMoodiaVasten(
      final Funktiokutsu funktiokutsu, final Laskentamoodi laskentamoodi) {
    if (funktiokutsu != null) {
      if (!funktiokutsu.getFunktionimi().getLaskentamoodit().contains(laskentamoodi)) {
        switch (laskentamoodi) {
          case VALINTALASKENTA:
            throw new FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(
                "Funktiokutsua "
                    + funktiokutsu.getFunktionimi().name()
                    + " ei voida käyttää valintalaskennassa.",
                funktiokutsu.getFunktionimi());
          case VALINTAKOELASKENTA:
            throw new FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(
                "Funktiokutsua "
                    + funktiokutsu.getFunktionimi().name()
                    + " ei voida käyttää valintakoelaskennassa.",
                funktiokutsu.getFunktionimi());
        }
      }
      for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
        if (arg.getFunktiokutsuChild() != null) {
          validoiFunktiokutsuMoodiaVasten(arg.getFunktiokutsuChild(), laskentamoodi);
        } else if (arg.getLaskentakaavaChild() != null) {
          validoiFunktiokutsuMoodiaVasten(
              arg.getLaskentakaavaChild().getFunktiokutsu(), laskentamoodi);
        }
      }
    }
  }

  @Override
  @Transactional
  public Laskentakaava haeLaskettavaKaava(final Long id, final Laskentamoodi laskentamoodi) {
    Laskentakaava laskentakaava = laskentakaavaCache.get(id);
    if (laskentakaava == null) {
      laskentakaava = haeKokoLaskentakaava(id, true);
      laskentakaavaCache.addLaskentakaava(laskentakaava, id);
    }
    validoiFunktiokutsuMoodiaVasten(laskentakaava.getFunktiokutsu(), laskentamoodi);
    return laskentakaava;
  }
}
