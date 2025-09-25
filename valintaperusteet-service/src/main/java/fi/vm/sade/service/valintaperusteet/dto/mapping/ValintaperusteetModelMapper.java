package fi.vm.sade.service.valintaperusteet.dto.mapping;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.model.*;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.model.Abstraktivalidointivirhe;
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.*;
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.Virhetyyppi;
import java.util.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValintaperusteetModelMapper extends ModelMapper {
  private static final Logger LOG = LoggerFactory.getLogger(ValintaperusteetModelMapper.class);

  public ValintaperusteetModelMapper() {
    super();

    // Validointivirheet
    final Converter<List<ValidointivirheDTO>, List<Abstraktivalidointivirhe>> virheListConverter =
        new Converter<List<ValidointivirheDTO>, List<Abstraktivalidointivirhe>>() {
          public List<Abstraktivalidointivirhe> convert(
              MappingContext<List<ValidointivirheDTO>, List<Abstraktivalidointivirhe>> context) {
            List<Abstraktivalidointivirhe> result = new ArrayList<Abstraktivalidointivirhe>();
            for (int i = 0; i < context.getSource().size(); i++) {
              ValidointivirheDTO dto = context.getSource().get(i);
              Validointivirhe virhe =
                  new Validointivirhe(
                      Virhetyyppi.valueOf(dto.getVirhetyyppi().name()), dto.getVirheviesti());
              result.add(virhe);
            }
            return result;
          }
        };

    final Converter<Set<ValintaperusteViite>, List<ValintaperusteViiteDTO>>
        valintaperusteViiteToDtoConverter =
            new Converter<Set<ValintaperusteViite>, List<ValintaperusteViiteDTO>>() {
              public List<ValintaperusteViiteDTO> convert(
                  MappingContext<Set<ValintaperusteViite>, List<ValintaperusteViiteDTO>> context) {
                List<ValintaperusteViiteDTO> result = new LinkedList<ValintaperusteViiteDTO>();
                for (ValintaperusteViite arg : context.getSource()) {
                  ValintaperusteViiteDTO dto = map(arg, ValintaperusteViiteDTO.class);
                  result.add(dto);
                }
                return result;
              }
            };

    final Converter<List<ValintaperusteViiteDTO>, Set<ValintaperusteViite>>
        dtoToValintaperusteViiteConverter =
            new Converter<List<ValintaperusteViiteDTO>, Set<ValintaperusteViite>>() {
              public Set<ValintaperusteViite> convert(
                  MappingContext<List<ValintaperusteViiteDTO>, Set<ValintaperusteViite>> context) {
                Set<ValintaperusteViite> result = new TreeSet<ValintaperusteViite>();
                for (int i = 0; i < context.getSource().size(); i++) {
                  ValintaperusteViiteDTO arg = context.getSource().get(i);
                  arg.setIndeksi(i + 1);
                  ValintaperusteViite viite = map(arg, ValintaperusteViite.class);
                  if ((viite.getEpasuoraViittaus() == null || !viite.getEpasuoraViittaus())
                      && Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO.equals(viite.getLahde())) {
                    LOG.info(
                        String.format(
                            "Pakotetaan epasuoraViittaus arvoon true, koska viitteen %d lähde on %s",
                            viite.getId(), Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO));
                    viite.setEpasuoraViittaus(true);
                  }
                  viite.setIndeksi(arg.getIndeksi());
                  result.add(viite);
                }
                return result;
              }
            };

    final Converter<Set<Arvovalikonvertteriparametri>, List<ArvovalikonvertteriparametriDTO>>
        arvovalikonvertteriparametriToDtoConverter =
            new Converter<
                Set<Arvovalikonvertteriparametri>, List<ArvovalikonvertteriparametriDTO>>() {
              public List<ArvovalikonvertteriparametriDTO> convert(
                  MappingContext<
                          Set<Arvovalikonvertteriparametri>, List<ArvovalikonvertteriparametriDTO>>
                      context) {
                Set<ArvovalikonvertteriparametriDTO> resultSet = new TreeSet<>();
                for (Arvovalikonvertteriparametri a : context.getSource()) {
                  resultSet.add(map(a, ArvovalikonvertteriparametriDTO.class));
                }
                List<ArvovalikonvertteriparametriDTO> result = new LinkedList<>();
                result.addAll(resultSet);
                return result;
              }
            };

    final Converter<List<ArvovalikonvertteriparametriDTO>, Set<Arvovalikonvertteriparametri>>
        dtoToArvovalikonvertteriparametriConverter =
            new Converter<
                List<ArvovalikonvertteriparametriDTO>, Set<Arvovalikonvertteriparametri>>() {
              public Set<Arvovalikonvertteriparametri> convert(
                  MappingContext<
                          List<ArvovalikonvertteriparametriDTO>, Set<Arvovalikonvertteriparametri>>
                      context) {
                Set<Arvovalikonvertteriparametri> result = new TreeSet<>();
                result.addAll(mapList(context.getSource(), Arvovalikonvertteriparametri.class));
                return result;
              }
            };

    final Converter<List<FunktioargumenttiDTO>, Set<Funktioargumentti>>
        dtoToFunktioargumenttiConverter =
            new Converter<List<FunktioargumenttiDTO>, Set<Funktioargumentti>>() {
              public Set<Funktioargumentti> convert(
                  MappingContext<List<FunktioargumenttiDTO>, Set<Funktioargumentti>> context) {
                Set<Funktioargumentti> result = new TreeSet<Funktioargumentti>();
                for (int i = 0; i < context.getSource().size(); i++) {
                  FunktioargumenttiDTO arg = context.getSource().get(i);
                  arg.setIndeksi(i + 1);
                  Funktioargumentti funktioargumentti = new Funktioargumentti();
                  if (arg.getLapsi() != null
                      && arg.getLapsi()
                          .getLapsityyppi()
                          .equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
                    asetaIndeksitRekursiivisesti(arg.getLapsi());
                    FunktiokutsuDTO dto = map(arg.getLapsi(), FunktiokutsuDTO.class);
                    Funktiokutsu kutsu = convertFromDto(dto);
                    funktioargumentti.setFunktiokutsuChild(kutsu);
                  }
                  if (arg.getLapsi() != null
                      && arg.getLapsi()
                          .getLapsityyppi()
                          .equals(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI)) {
                    LaskentakaavaListDTO dto = map(arg.getLapsi(), LaskentakaavaListDTO.class);
                    Laskentakaava kaava = convertFromDto(dto);
                    funktioargumentti.setLaskentakaavaChild(kaava);
                  }
                  funktioargumentti.setIndeksi(arg.getIndeksi());
                  result.add(funktioargumentti);
                }
                return result;
              }
            };

    final Converter<Set<Funktioargumentti>, List<FunktioargumenttiDTO>>
        funktioargumenttiToDtoConverter =
            new Converter<Set<Funktioargumentti>, List<FunktioargumenttiDTO>>() {
              public List<FunktioargumenttiDTO> convert(
                  MappingContext<Set<Funktioargumentti>, List<FunktioargumenttiDTO>> context) {
                List<FunktioargumenttiDTO> result = new LinkedList<FunktioargumenttiDTO>();
                for (Funktioargumentti arg : context.getSource()) {
                  FunktioargumenttiDTO dto = new FunktioargumenttiDTO();
                  if (arg.getFunktiokutsuChild() != null) {
                    FunktioargumentinLapsiDTO lapsi =
                        asetaFunktioArgumenttiLapsetRekursiivisesti(arg.getFunktiokutsuChild());
                    lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
                    lapsi.setFunktionimi(arg.getFunktiokutsuChild().getFunktionimi());
                    dto.setLapsi(lapsi);
                  }
                  if (arg.getLaskentakaavaChild() != null) {
                    FunktioargumentinLapsiDTO lapsi =
                        map(arg.getLaskentakaavaChild(), FunktioargumentinLapsiDTO.class);
                    lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
                    lapsi.setTyyppi(arg.getLaskentakaavaChild().getTyyppi());
                    dto.setLapsi(lapsi);
                  }
                  dto.setIndeksi(arg.getIndeksi());
                  result.add(dto);
                }

                return result.stream()
                    .sorted(Comparator.comparingInt(FunktioargumenttiDTO::getIndeksi))
                    .toList();
              }
            };

    final Converter<Set<Funktioargumentti>, Set<ValintaperusteetFunktioargumenttiDTO>>
        funktioargumenttiToValintaperusteetDtoConverter =
            new Converter<Set<Funktioargumentti>, Set<ValintaperusteetFunktioargumenttiDTO>>() {
              public Set<ValintaperusteetFunktioargumenttiDTO> convert(
                  MappingContext<Set<Funktioargumentti>, Set<ValintaperusteetFunktioargumenttiDTO>>
                      context) {
                Set<ValintaperusteetFunktioargumenttiDTO> result =
                    new HashSet<ValintaperusteetFunktioargumenttiDTO>();
                for (Funktioargumentti arg : context.getSource()) {
                  ValintaperusteetFunktioargumenttiDTO dto =
                      new ValintaperusteetFunktioargumenttiDTO();
                  if (arg.getFunktiokutsuChild() != null) {
                    dto.setFunktiokutsu(
                        map(arg.getFunktiokutsuChild(), ValintaperusteetFunktiokutsuDTO.class));
                  } else if (arg.getLaskentakaavaChild() != null) {
                    dto.setFunktiokutsu(
                        map(
                            arg.getLaskentakaavaChild().getFunktiokutsu(),
                            ValintaperusteetFunktiokutsuDTO.class));
                  }
                  dto.setIndeksi(arg.getIndeksi());
                  dto.setId(arg.getId());
                  result.add(dto);
                }
                return result;
              }
            };

    final Converter<Set<ValintaperusteetFunktioargumenttiDTO>, Set<Funktioargumentti>>
        valintaperusteetDtoFunktioargumenttiToConverter =
            new Converter<Set<ValintaperusteetFunktioargumenttiDTO>, Set<Funktioargumentti>>() {
              public Set<Funktioargumentti> convert(
                  MappingContext<Set<ValintaperusteetFunktioargumenttiDTO>, Set<Funktioargumentti>>
                      context) {
                Set<Funktioargumentti> result = new HashSet<Funktioargumentti>();
                for (ValintaperusteetFunktioargumenttiDTO dto : context.getSource()) {
                  Funktioargumentti arg = new Funktioargumentti();
                  arg.setFunktiokutsuChild(map(dto.getFunktiokutsu(), Funktiokutsu.class));
                  arg.setIndeksi(dto.getIndeksi());
                  arg.setId(dto.getId());
                  result.add(arg);
                }
                return result;
              }
            };

    final Converter<Valintakoe, Boolean> valintakoeKutsutaankoKaikkiConverter =
        new Converter<Valintakoe, Boolean>() {
          public Boolean convert(MappingContext<Valintakoe, Boolean> context) {
            switch (context.getSource().getKutsunKohde()) {
              case HAKIJAN_VALINTA:
                return Boolean.FALSE;
              default:
                return context.getSource().getKutsutaankoKaikki();
            }
          }
        };

    final Converter<Valintakoe, Boolean> valintakoePerittyConverter =
        new Converter<Valintakoe, Boolean>() {
          public Boolean convert(MappingContext<Valintakoe, Boolean> context) {
            if (context.getSource().getMasterValintakoe() != null) {
              return true;
            } else {
              return false;
            }
          }
        };

    final Provider<Set> linkedHashSetProvider =
        new Provider<Set>() {
          public Set get(Provider.ProvisionRequest<Set> request) {
            Class<?> klass = request.getRequestedType();
            return klass.isAssignableFrom(Set.class) ? new LinkedHashSet<>() : null;
          }
        };
    this.getConfiguration().setProvider(linkedHashSetProvider);

    // Perus DTO mäppäykset
    this.addMappings(
        new PropertyMap<Hakijaryhma, HakijaryhmaDTO>() {
          @Override
          protected void configure() {
            map().setLaskentakaavaId(source.getLaskentakaavaId());
          }
        });
    this.addMappings(
        new PropertyMap<HakijaryhmaDTO, Hakijaryhma>() {
          @Override
          protected void configure() {
            map().setLaskentakaavaId(source.getLaskentakaavaId());
          }
        });
    this.addMappings(
        new PropertyMap<Valintakoe, ValintakoeDTO>() {
          @Override
          protected void configure() {
            map().setLaskentakaavaId(source.getLaskentakaava().getId());
          }
        });
    this.addMappings(
        new PropertyMap<Jarjestyskriteeri, JarjestyskriteeriDTO>() {
          @Override
          protected void configure() {
            map().setLaskentakaavaId(source.getLaskentakaava().getId());
          }
        });

    this.addMappings(
        new PropertyMap<Valintatapajono, ValintatapajonoDTO>() {
          // huom: prioriteetti jää tässä tyhjäksi yksittäiselle valintatapajonolle, jolloin
          // se saa default-arvon eli 0. Prioriteetit lisätään vain mapList-metodissa alla.
          // Asetetaan tässä arvoksi -1 sen merkiksi että kyseessä ei ole todellinen prioriteetti.

          @Override
          protected void configure() {
            map().setTayttojono(source.getVarasijanTayttojono().getOid());
            map().setPrioriteetti(-1);
          }
        });

    this.addMappings(
        new PropertyMap<HakijaryhmaValintatapajono, HakijaryhmaValintatapajonoDTO>() {
          @Override
          protected void configure() {
            map().setNimi(source.getHakijaryhma().getNimi());
            map().setKuvaus(source.getHakijaryhma().getKuvaus());
            map().setOid(source.getOid());
            map().setMasterOid(source.getHakijaryhma().getOid());
          }
        });

    this.addMappings(
        new PropertyMap<HakijaryhmaValintatapajono, LinkitettyHakijaryhmaValintatapajonoDTO>() {
          @Override
          protected void configure() {
            map().setNimi(source.getHakijaryhma().getNimi());
            map().setKuvaus(source.getHakijaryhma().getKuvaus());
            map().setOid(source.getOid());
            map().setMasterOid(source.getHakijaryhma().getOid());
          }
        });

    this.addMappings(
        new PropertyMap<Valintatapajono, ValintatapajonoCreateDTO>() {
          @Override
          protected void configure() {
            map().setTayttojono(source.getVarasijanTayttojono().getOid());
          }
        });

    this.addMappings(
        new PropertyMap<Jarjestyskriteeri, ValintaperusteetJarjestyskriteeriDTO>() {
          @Override
          protected void configure() {
            map().setNimi(source.getMetatiedot());
          }
        });
    this.addMappings(
        new PropertyMap<ValintaperusteetJarjestyskriteeriDTO, Jarjestyskriteeri>() {
          @Override
          protected void configure() {
            map().setMetatiedot(source.getNimi());
          }
        });

    this.addMappings(
        new PropertyMap<Laskentakaava, FunktioargumentinLapsiDTO>() {
          @Override
          protected void configure() {
            map().setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
          }
        });

    this.addMappings(
        new PropertyMap<Valintakoe, ValintakoeDTO>() {
          @Override
          protected void configure() {
            using(valintakoeKutsutaankoKaikkiConverter).map(source).setKutsutaankoKaikki(null);
            using(valintakoePerittyConverter).map(source).setPeritty(null);
          }
        });

    this.addMappings(
        new PropertyMap<Funktiokutsu, FunktioargumentinLapsiDTO>() {
          @Override
          protected void configure() {
            map().setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
            using(arvovalikonvertteriparametriToDtoConverter)
                .map(source.getArvovalikonvertteriparametrit())
                .setArvovalikonvertteriparametrit(null);
          }
        });

    this.addMappings(
        new PropertyMap<FunktioargumentinLapsiDTO, Funktiokutsu>() {
          @Override
          protected void configure() {
            using(virheListConverter).map(source.getValidointivirheet()).setValidointivirheet(null);
            using(dtoToArvovalikonvertteriparametriConverter)
                .map(source.getArvovalikonvertteriparametrit())
                .setArvovalikonvertteriparametrit(null);
          }
        });

    this.addMappings(
        new PropertyMap<Funktiokutsu, FunktiokutsuDTO>() {
          @Override
          protected void configure() {
            using(funktioargumenttiToDtoConverter)
                .map(source.getFunktioargumentit())
                .setFunktioargumentit(null);
            using(valintaperusteViiteToDtoConverter)
                .map(source.getValintaperusteviitteet())
                .setValintaperusteviitteet(null);
            using(arvovalikonvertteriparametriToDtoConverter)
                .map(source.getArvovalikonvertteriparametrit())
                .setArvovalikonvertteriparametrit(null);
          }
        });

    this.addMappings(
        new PropertyMap<FunktiokutsuDTO, Funktiokutsu>() {
          @Override
          protected void configure() {
            using(dtoToFunktioargumenttiConverter)
                .map(source.getFunktioargumentit())
                .setFunktioargumentit(null);
            using(virheListConverter).map(source.getValidointivirheet()).setValidointivirheet(null);
            using(dtoToValintaperusteViiteConverter)
                .map(source.getValintaperusteviitteet())
                .setValintaperusteviitteet(null);
            using(dtoToArvovalikonvertteriparametriConverter)
                .map(source.getArvovalikonvertteriparametrit())
                .setArvovalikonvertteriparametrit(null);
          }
        });

    this.addMappings(
        new PropertyMap<Funktiokutsu, ValintaperusteetFunktiokutsuDTO>() {
          @Override
          protected void configure() {
            using(funktioargumenttiToValintaperusteetDtoConverter)
                .map(source.getFunktioargumentit())
                .setFunktioargumentit(null);
          }
        });

    this.addMappings(
        new PropertyMap<ValintaperusteetFunktiokutsuDTO, Funktiokutsu>() {
          @Override
          protected void configure() {
            using(valintaperusteetDtoFunktioargumenttiToConverter)
                .map(source.getFunktioargumentit())
                .setFunktioargumentit(null);
          }
        });
  }

  public Funktiokutsu convertFromDto(FunktiokutsuDTO dto) {
    return map(dto, Funktiokutsu.class);
  }

  public Laskentakaava convertFromDto(LaskentakaavaListDTO dto) {
    return map(dto, Laskentakaava.class);
  }

  public FunktioargumentinLapsiDTO asetaFunktioArgumenttiLapsetRekursiivisesti(Funktiokutsu kutsu) {
    FunktioargumentinLapsiDTO parent = map(kutsu, FunktioargumentinLapsiDTO.class);
    List<FunktioargumenttiDTO> result = new LinkedList<FunktioargumenttiDTO>();
    for (Funktioargumentti arg : kutsu.getFunktioargumentit()) {
      FunktioargumenttiDTO dto = new FunktioargumenttiDTO();
      dto.setIndeksi(arg.getIndeksi());
      if (arg.getFunktiokutsuChild() != null) {
        FunktioargumentinLapsiDTO lapsi =
            asetaFunktioArgumenttiLapsetRekursiivisesti(arg.getFunktiokutsuChild());
        lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
        lapsi.setTyyppi(arg.getFunktiokutsuChild().getFunktionimi().getTyyppi());
        dto.setLapsi(lapsi);
      }
      if (arg.getLaskentakaavaChild() != null) {
        FunktioargumentinLapsiDTO lapsi =
            map(arg.getLaskentakaavaChild(), FunktioargumentinLapsiDTO.class);
        lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
        lapsi.setTyyppi(arg.getLaskentakaavaChild().getTyyppi());
        dto.setLapsi(lapsi);
      }
      result.add(dto);
    }
    parent.setFunktioargumentit(result);
    parent.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
    parent.setTyyppi(kutsu.getFunktionimi().getTyyppi());
    return parent;
  }

  public Funktiokutsu asetaIndeksitRekursiivisesti(FunktioargumentinLapsiDTO kutsu) {
    for (int i = 0; i < kutsu.getFunktioargumentit().size(); i++) {
      FunktioargumenttiDTO arg = kutsu.getFunktioargumentit().get(i);
      arg.setIndeksi(i + 1);
      if (arg.getLapsi() != null
          && arg.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
        asetaIndeksitRekursiivisesti(arg.getLapsi());
      }
    }
    Funktiokutsu funktiokutsu = map(kutsu, Funktiokutsu.class);
    return funktiokutsu;
  }

  /**
   * @param list must be in order by priority, if TO is Prioritized , as results are assigned
   *     priorities according to their order in the input list.
   * @see Prioritized
   */
  public <FROM, TO> List<TO> mapList(List<FROM> list, final Class<TO> to) {
    List<TO> toList = new ArrayList<TO>();
    int index = 0;
    for (FROM f : list) {
      TO converted = map(f, to);
      if (converted instanceof Prioritized) {
        ((Prioritized) converted).setPrioriteetti(index);
      }
      toList.add(converted);
      ++index;
    }
    return toList;
  }
}
