package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.kaava.Laskentakaavavalidaattori;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohteenValintaperusteDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumentinLapsiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumenttiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViiteId;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.ValintaryhmaId;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintalaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiValidiException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaMuodostaaSilmukanException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class LaskentakaavaServiceImpl implements LaskentakaavaService {
  private static final String HAKUKOHDE_VIITE_START = "{{hakukohde.";

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Autowired private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Override
  public Laskentakaava haeMallinnettuKaava(LaskentakaavaId id) {
    return laskentakaavaDAO.read(id);
  }

  @Override
  @Transactional
  public Pair<Laskentakaava, Laskentakaava> update(LaskentakaavaId id, LaskentakaavaCreateDTO dto) {
    Laskentakaava vanha = laskentakaavaDAO.read(id);
    kopioiAlikaavat(dto.getFunktiokutsu(), vanha.getHakukohdeViiteId(), vanha.getValintaryhmaId(), id);
    Laskentakaava uusi = laskentakaavaDAO.update(id, dto);
    if (!Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(uusi).isEmpty()) {
      throw new LaskentakaavaEiValidiException(
              "Laskentakaava ei ole validi",
              Laskentakaavavalidaattori.validoiMallinnettuKaava(uusi));
    }
    return Pair.of(vanha, uusi);
  }

  @Override
  @Transactional
  public Laskentakaava insert(LaskentakaavaInsertDTO dto) {
    HakukohdeViiteId hakukohdeViiteId = null;
    ValintaryhmaId valintaryhmaId = null;

    if (dto.getHakukohdeOid() != null && !dto.getHakukohdeOid().isEmpty()) {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readForImport(dto.getHakukohdeOid());
      if (hakukohde != null) {
        hakukohdeViiteId = new HakukohdeViiteId(hakukohde.getId());
      }
    }
    if (dto.getValintaryhmaOid() != null && !dto.getValintaryhmaOid().isEmpty()) {
      Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(dto.getValintaryhmaOid());
      if (valintaryhma != null) {
        valintaryhmaId = new ValintaryhmaId(valintaryhma.getId());
      }
    }

    if (hakukohdeViiteId == null && valintaryhmaId == null) {
      throw new IllegalArgumentException("Joko hakukohde tai valintaryhmä tulee antaa");
    }

    kopioiAlikaavat(dto.getLaskentakaava().getFunktiokutsu(), hakukohdeViiteId, valintaryhmaId, null);

    Laskentakaava uusi = laskentakaavaDAO.insert(
            dto.getLaskentakaava(),
            null,
            valintaryhmaId,
            hakukohdeViiteId
    );
    if (!Laskentakaavavalidaattori.onkoMallinnettuKaavaValidi(uusi).isEmpty()) {
      throw new LaskentakaavaEiValidiException(
              "Laskentakaava ei ole validi",
              Laskentakaavavalidaattori.validoiMallinnettuKaava(uusi));
    }
    return uusi;
  }

  @Override
  public Laskentakaava siirra(LaskentakaavaSiirraDTO dto) {
    if (dto.getUusinimi() != null) {
      dto.setNimi(dto.getUusinimi());
    }
    LaskentakaavaInsertDTO insertDto = new LaskentakaavaInsertDTO();
    insertDto.setLaskentakaava(dto);
    insertDto.setHakukohdeOid(dto.getHakukohdeOid());
    insertDto.setValintaryhmaOid(dto.getValintaryhmaOid());
    return insert(insertDto);
  }

  private void kopioiAlikaavat(FunktioargumenttiDTO fa,
                               HakukohdeViiteId hakukohdeViiteId,
                               ValintaryhmaId valintaryhmaId,
                               LaskentakaavaId rootLaskentakaavaId) {

    if (fa.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
      kopioiAlikaavat(fa.getLapsi(), hakukohdeViiteId, valintaryhmaId, rootLaskentakaavaId);
    }
    if (fa.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI)) {
      fa.getLapsi().setId(kopioiJosEiJoKopioitu(
              new LaskentakaavaId(fa.getLapsi().getId()),
              hakukohdeViiteId,
              valintaryhmaId,
              null,
              rootLaskentakaavaId
      ).id);
    }
  }

  private void kopioiAlikaavat(FunktioargumentinLapsiDTO funktiokutsuDTO,
                               HakukohdeViiteId hakukohdeViiteId,
                               ValintaryhmaId valintaryhmaId,
                               LaskentakaavaId rootLaskentakaavaId) {
    funktiokutsuDTO.getFunktioargumentit().forEach(fa -> this.kopioiAlikaavat(fa, hakukohdeViiteId, valintaryhmaId, rootLaskentakaavaId));
  }

  private void kopioiAlikaavat(FunktiokutsuDTO funktiokutsuDTO,
                               HakukohdeViiteId hakukohdeViiteId,
                               ValintaryhmaId valintaryhmaId,
                               LaskentakaavaId rootLaskentakaavaId) {
    funktiokutsuDTO.getFunktioargumentit().forEach(fa -> this.kopioiAlikaavat(fa, hakukohdeViiteId, valintaryhmaId, rootLaskentakaavaId));
  }

  private LaskentakaavaId kopioCacheGet(Map<ValintaryhmaId, Map<LaskentakaavaId, LaskentakaavaId>> cache,
                                        ValintaryhmaId valintaryhmaId,
                                        LaskentakaavaId laskentakaavaId) {
    if (valintaryhmaId == null || cache == null || !cache.containsKey(valintaryhmaId)) {
      return null;
    }
    return cache.get(valintaryhmaId).get(laskentakaavaId);
  }

  private void kopioCachePut(Map<ValintaryhmaId, Map<LaskentakaavaId, LaskentakaavaId>> cache,
                             ValintaryhmaId valintaryhmaId,
                             LaskentakaavaId laskentakaavaId,
                             LaskentakaavaId kopionId) {
    if (valintaryhmaId != null && cache != null) {
      if (!cache.containsKey(valintaryhmaId)) {
        cache.put(valintaryhmaId, new HashMap<>());
      }
      cache.get(valintaryhmaId).put(laskentakaavaId, kopionId);
    }
  }

  private void checkCycles(LaskentakaavaId rootLaskentakaavaId, Laskentakaava laskentakaava) {
    LinkedList<Funktiokutsu> stack = new LinkedList<>();
    stack.push(laskentakaava.getFunktiokutsu());
    while (!stack.isEmpty()) {
      Funktiokutsu fk = stack.pop();
      for (Funktioargumentti fa : fk.getFunktioargumentit()) {
        if (fa.getFunktiokutsuChild() == null) {
          if (rootLaskentakaavaId.equals(fa.getLaskentakaavaChild().getId())) {
            throw new LaskentakaavaMuodostaaSilmukanException(rootLaskentakaavaId, laskentakaava.getId());
          }
          stack.push(fa.getLaskentakaavaChild().getFunktiokutsu());
        } else {
          stack.push(fa.getFunktiokutsuChild());
        }
      }
    }
  }

  private LaskentakaavaId kopioiJosEiJoKopioitu(LaskentakaavaId id,
                                                HakukohdeViiteId hakukohdeViiteId,
                                                ValintaryhmaId valintaryhmaId,
                                                Map<ValintaryhmaId, Map<LaskentakaavaId, LaskentakaavaId>> cache,
                                                LaskentakaavaId rootLaskentakaavaId) {
    LaskentakaavaId kopioIdFromCache = kopioCacheGet(cache, valintaryhmaId, id);
    if (kopioIdFromCache != null) {
      return kopioIdFromCache;
    }
    Laskentakaava kopio = laskentakaavaDAO.etsiKaavaTaiSenKopio(id, hakukohdeViiteId, valintaryhmaId);
    if (kopio != null) {
      if (rootLaskentakaavaId != null) {
        checkCycles(rootLaskentakaavaId, kopio);
      }
      kopioCachePut(cache, valintaryhmaId, id, kopio.getId());
      return kopio.getId();
    }
    Laskentakaava laskentakaava = laskentakaavaDAO.read(id);
    if (rootLaskentakaavaId != null) {
      checkCycles(rootLaskentakaavaId, laskentakaava);
    }
    FunktiokutsuDTO funktiokutsuDTO = modelMapper.fkToDto(laskentakaava.getFunktiokutsu());
    kopioiAlikaavat(funktiokutsuDTO, hakukohdeViiteId, valintaryhmaId, rootLaskentakaavaId);
    LaskentakaavaId uudenKopionId = laskentakaavaDAO.insert(new LaskentakaavaCreateDTO(
                    laskentakaava.getOnLuonnos(),
                    laskentakaava.getNimi(),
                    laskentakaava.getKuvaus(),
                    funktiokutsuDTO
            ),
            id,
            valintaryhmaId,
            hakukohdeViiteId
    ).getId();
    kopioCachePut(cache, valintaryhmaId, id, uudenKopionId);
    return uudenKopionId;
  }

  @Override
  public LaskentakaavaId kopioiJosEiJoKopioitu(LaskentakaavaId id,
                                               HakukohdeViiteId hakukohdeViiteId) {
    return kopioiJosEiJoKopioitu(id, hakukohdeViiteId, null, null, null);
  }

  @Override
  public LaskentakaavaId kopioiJosEiJoKopioitu(LaskentakaavaId id,
                                               ValintaryhmaId valintaryhmaId,
                                               Map<ValintaryhmaId, Map<LaskentakaavaId, LaskentakaavaId>> cache) {
    return kopioiJosEiJoKopioitu(id, null, valintaryhmaId, cache, null);
  }

  @Override
  public List<LaskentakaavaId> irrotaHakukohteesta(HakukohdeViiteId hakukohdeViiteId) {
    return laskentakaavaDAO.irrotaHakukohteesta(hakukohdeViiteId);
  }

  @Override
  public void liitaHakukohteeseen(HakukohdeViiteId hakukohdeViiteId, List<LaskentakaavaId> laskentakaavaIds) {
    laskentakaavaDAO.liitaHakukohteeseen(hakukohdeViiteId, laskentakaavaIds);
  }

  @Override
  public boolean poista(LaskentakaavaId id) {
    return laskentakaavaDAO.delete(id);
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

  @Override
  public List<ValintaperusteDTO> findAvaimetForHakukohde(String hakukohdeOid) {
    Map<String, String> hakukohteenValintaperusteet = hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohdeOid).stream()
            .collect(Collectors.toMap(
                    HakukohteenValintaperuste::getTunniste,
                    HakukohteenValintaperuste::getArvo
            ));
    return laskentakaavaDAO.findHakukohteenKaavat(hakukohdeOid).stream()
            .map(Laskentakaava::getFunktiokutsu)
            .distinct()
            .flatMap(kutsu -> haeValintaperusteetRekursiivisesti(kutsu, hakukohteenValintaperusteet))
            .collect(Collectors.toList());
  }

  private String haeTunniste(String mustache, Map<String, String> hakukohteenValintaperusteet) {
    if (mustache.startsWith(HAKUKOHDE_VIITE_START)) {
      String tunniste = mustache.substring(HAKUKOHDE_VIITE_START.length(), mustache.length() - 2);
      return hakukohteenValintaperusteet.get(tunniste);
    } else {
      return mustache;
    }
  }

  private Stream<ValintaperusteDTO> haeValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu,
                                                                       Map<String, String> hakukohteenValintaperusteet) {
    return Stream.concat(
            funktiokutsu.getValintaperusteviitteet().stream()
                    .filter(vpv -> Valintaperustelahde.SYOTETTAVA_ARVO.equals(vpv.getLahde())
                            || Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO.equals(vpv.getLahde()))
                    .map(vpv -> {
                      ValintaperusteDTO valintaperuste = new ValintaperusteDTO();
                      valintaperuste.setFunktiotyyppi(funktiokutsu.getFunktionimi().getTyyppi());
                      if (vpv.isEpasuoraViittaus()) {
                        valintaperuste.setTunniste(hakukohteenValintaperusteet.get(vpv.getTunniste()));
                        valintaperuste.setOsallistuminenTunniste(
                                valintaperuste.getTunniste() + ValintaperusteViite.OSALLISTUMINEN_POSTFIX);
                      } else {
                        valintaperuste.setTunniste(vpv.getTunniste());
                        valintaperuste.setOsallistuminenTunniste(vpv.getOsallistuminenTunniste());
                      }
                      valintaperuste.setVaatiiOsallistumisen(vpv.isVaatiiOsallistumisen());
                      valintaperuste.setSyotettavissaKaikille(vpv.isSyotettavissaKaikille());
                      valintaperuste.setKuvaus(vpv.getKuvaus());
                      valintaperuste.setLahde(Valintaperustelahde.SYOTETTAVA_ARVO);
                      valintaperuste.setOnPakollinen(vpv.isOnPakollinen());
                      valintaperuste.setTilastoidaan(vpv.isTilastoidaan());
                      if (null != vpv.getSyotettavanarvontyyppi()) {
                        valintaperuste.setSyötettavanArvonTyyppi(
                                modelMapper.map(vpv.getSyotettavanarvontyyppi(), KoodiDTO.class));
                      }

                      List<String> arvot = funktiokutsu.getArvokonvertteriparametrit().stream()
                              .map(akp -> haeTunniste(akp.getArvo(), hakukohteenValintaperusteet))
                              .collect(Collectors.toList());
                      valintaperuste.setArvot(arvot.isEmpty() ? null : arvot);

                      Optional<Pair<BigDecimal, BigDecimal>> minmax = funktiokutsu.getArvovalikonvertteriparametrit().stream()
                              .map(avkp -> Pair.of(
                                      new BigDecimal(haeTunniste(avkp.getMinValue(), hakukohteenValintaperusteet).replace(',', '.')),
                                      new BigDecimal(haeTunniste(avkp.getMaxValue(), hakukohteenValintaperusteet).replace(',', '.'))
                              ))
                              .reduce((currentMinmax, p) -> Pair.of(
                                      currentMinmax.getLeft().compareTo(p.getLeft()) < 0 ? currentMinmax.getLeft() : p.getLeft(),
                                      currentMinmax.getRight().compareTo(p.getRight()) > 0 ? currentMinmax.getRight() : p.getRight()
                              ));
                      valintaperuste.setMin(minmax.map(p -> p.getLeft().toString()).orElse(null));
                      valintaperuste.setMax(minmax.map(p -> p.getRight().toString()).orElse(null));

                      return valintaperuste;
                    }),
            funktiokutsu.getFunktioargumentit().stream()
                    .flatMap(fa -> {
                      if (fa.getFunktiokutsuChild() == null) {
                        return haeValintaperusteetRekursiivisesti(
                                fa.getLaskentakaavaChild().getFunktiokutsu(),
                                hakukohteenValintaperusteet
                        );
                      } else {
                        return haeValintaperusteetRekursiivisesti(
                                fa.getFunktiokutsuChild(),
                                hakukohteenValintaperusteet
                        );
                      }
                    })
    );
  }

  @Override
  public HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(String oid) {
    HakukohteenValintaperusteAvaimetDTO valintaperusteet = new HakukohteenValintaperusteAvaimetDTO();
    laskentakaavaDAO.findHakukohteenKaavat(oid).stream()
            .map(Laskentakaava::getFunktiokutsu)
            .distinct()
            .forEach(funktiokutsu -> haeHakukohteenValintaperusteetRekursiivisesti(funktiokutsu, valintaperusteet));
    return valintaperusteet;
  }

  private void haeHakukohteenValintaperusteetRekursiivisesti(Funktiokutsu funktiokutsu,
                                                             HakukohteenValintaperusteAvaimetDTO valintaperusteet) {
    funktiokutsu.getArvokonvertteriparametrit().forEach(akp -> {
      if (akp.getArvo().startsWith(HAKUKOHDE_VIITE_START)) {
        valintaperusteet.getArvot().add(akp.getArvo());
      }
      if (akp.getHylkaysperuste().startsWith(HAKUKOHDE_VIITE_START)) {
        valintaperusteet.getHylkaysperusteet().add(akp.getHylkaysperuste());
      }
    });
    funktiokutsu.getArvovalikonvertteriparametrit().forEach(avkp -> {
      if (avkp.getMinValue().startsWith(HAKUKOHDE_VIITE_START)) {
        valintaperusteet.getMinimit().add(avkp.getMinValue());
      }
      if (avkp.getMaxValue().startsWith(HAKUKOHDE_VIITE_START)) {
        valintaperusteet.getMaksimit().add(avkp.getMaxValue());
      }
      if (avkp.getPalautaHaettuArvo().startsWith(HAKUKOHDE_VIITE_START)) {
        valintaperusteet.getPalautaHaettutArvot().add(avkp.getPalautaHaettuArvo());
      }
    });
    funktiokutsu.getValintaperusteviitteet().forEach(vp -> {
      if (vp.getLahde().equals(Valintaperustelahde.HAKUKOHTEEN_ARVO) ||
              vp.getLahde().equals(Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO)) {
        valintaperusteet.getTunnisteet().add(vp.getTunniste());
      }
    });

    funktiokutsu.getFunktioargumentit().forEach(fa -> {
      if (fa.getFunktiokutsuChild() == null) {
        haeHakukohteenValintaperusteetRekursiivisesti(fa.getLaskentakaavaChild().getFunktiokutsu(), valintaperusteet);
      } else {
        haeHakukohteenValintaperusteetRekursiivisesti(fa.getFunktiokutsuChild(), valintaperusteet);
      }
    });
  }

  @Override
  @Transactional
  public List<Laskentakaava> findKaavas(boolean all,
                                        String valintaryhmaOid,
                                        String hakukohdeOid,
                                        Funktiotyyppi tyyppi) {
    return laskentakaavaDAO.findKaavas(all, valintaryhmaOid, hakukohdeOid, tyyppi);
  }

  private void validoiFunktiokutsuMoodiaVasten(Funktiokutsu funktiokutsu, Laskentamoodi laskentamoodi) {
      if (!funktiokutsu.getFunktionimi().getLaskentamoodit().contains(laskentamoodi)) {
        switch (laskentamoodi) {
          case VALINTALASKENTA:
            throw new FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(
                "Funktiokutsua "
                    + funktiokutsu.getFunktionimi().name()
                    + ", id "
                    + funktiokutsu.getId().id
                    + " ei voida käyttää valintalaskennassa.",
                funktiokutsu.getId(),
                funktiokutsu.getFunktionimi());
          case VALINTAKOELASKENTA:
            throw new FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(
                "Funktiokutsua "
                    + funktiokutsu.getFunktionimi().name()
                    + ", id "
                    + funktiokutsu.getId().id
                    + " ei voida käyttää valintakoelaskennassa.",
                funktiokutsu.getId(),
                funktiokutsu.getFunktionimi());
        }
      }
      for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
        if (arg.getFunktiokutsuChild() != null) {
          validoiFunktiokutsuMoodiaVasten(arg.getFunktiokutsuChild(), laskentamoodi);
        } else if (arg.getLaskentakaavaChild() != null) {
          validoiFunktiokutsuMoodiaVasten(arg.getLaskentakaavaChild().getFunktiokutsu(), laskentamoodi);
        }
      }
  }

  @Override
  @Transactional
  public Laskentakaava haeLaskettavaKaava(LaskentakaavaId id, Laskentamoodi laskentamoodi) {
    Laskentakaava laskentakaava = laskentakaavaDAO.read(id);
    validoiFunktiokutsuMoodiaVasten(laskentakaava.getFunktiokutsu(), laskentamoodi);
    return laskentakaava;
  }
}
