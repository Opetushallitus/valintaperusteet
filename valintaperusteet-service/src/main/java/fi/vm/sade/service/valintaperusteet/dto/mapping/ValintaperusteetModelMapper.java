package fi.vm.sade.service.valintaperusteet.dto.mapping;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.model.*;
import fi.vm.sade.service.valintaperusteet.dto.model.Virhetyyppi;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.model.Abstraktivalidointivirhe;
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.*;

import java.util.*;
import java.util.stream.Collectors;

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
            map().setLaskentakaavaId(source.getLaskentakaavaId());
          }
        });
    this.addMappings(
        new PropertyMap<Jarjestyskriteeri, JarjestyskriteeriDTO>() {
          @Override
          protected void configure() {
            map().setLaskentakaavaId(source.getLaskentakaavaId());
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
  }

  private LokalisoituTekstiDTO ltToDto(LokalisoituTeksti lt) {
      return new LokalisoituTekstiDTO(
              lt.getKieli(),
              lt.getTeksti()
      );
  }

  private TekstiRyhmaDTO trToDto(TekstiRyhma tr) {
      return new TekstiRyhmaDTO(tr.getTekstit().stream().map(this::ltToDto).collect(Collectors.toSet()));
  }

  private ArvokonvertteriparametriDTO akpToDto(Arvokonvertteriparametri akp) {
      return new ArvokonvertteriparametriDTO(
              akp.getPaluuarvo(),
              akp.getArvo(),
              akp.getHylkaysperuste(),
              trToDto(akp.getKuvaukset())
      );
  }

  private ArvovalikonvertteriparametriDTO avkpToDto(Arvovalikonvertteriparametri avpk) {
      return new ArvovalikonvertteriparametriDTO(
              avpk.getPaluuarvo(),
              avpk.getMinValue(),
              avpk.getMaxValue(),
              avpk.getPalautaHaettuArvo(),
              avpk.getHylkaysperuste(),
              trToDto(avpk.getKuvaukset())
      );
  }

  private SyoteparametriDTO spToDto(Syoteparametri sp) {
      return new SyoteparametriDTO(
              sp.getAvain(),
              sp.getArvo()
      );
  }

  private FunktioargumenttiDTO faToDto(Funktioargumentti fa) {
      if (fa.getFunktiokutsuChild() == null) {
          Laskentakaava laskentakaava = fa.getLaskentakaavaChild();
          return new FunktioargumenttiDTO(
                  new FunktioargumentinLapsiDTO(
                          laskentakaava.getOnLuonnos(),
                          laskentakaava.getNimi(),
                          laskentakaava.getKuvaus(),
                          laskentakaava.getFunktiokutsu().getFunktionimi().getTyyppi(),
                          laskentakaava.getId().id
                  ),
                  fa.getIndeksi()
          );
      } else {
          return new FunktioargumenttiDTO(
                  new FunktioargumentinLapsiDTO(fkToDto(fa.getFunktiokutsuChild())),
                  fa.getIndeksi()
          );
      }
  }

  private KoodiDTO satToDto(Syotettavanarvontyyppi sat) {
      return new KoodiDTO(
              sat.getUri(),
              sat.getNimiFi(),
              sat.getNimiSv(),
              sat.getNimiEn(),
              sat.getArvo()
      );
  }

  private ValintaperusteViiteDTO vpvToDto(ValintaperusteViite vpv) {
      return new ValintaperusteViiteDTO(
              vpv.getTunniste(),
              vpv.getKuvaus(),
              vpv.getLahde(),
              vpv.isOnPakollinen(),
              vpv.isEpasuoraViittaus(),
              vpv.getIndeksi(),
              vpv.isVaatiiOsallistumisen(),
              vpv.isSyotettavissaKaikille(),
              trToDto(vpv.getKuvaukset()),
              vpv.getSyotettavanarvontyyppi() == null ? null : satToDto(vpv.getSyotettavanarvontyyppi()),
              vpv.isTilastoidaan()
      );
  }

  private ValidointivirheDTO avvToDto(Abstraktivalidointivirhe virhe) {
      if (!(virhe instanceof Validointivirhe)) {
          throw new RuntimeException("Tuntematon virhetyyppi " + virhe.getClass().getName());
      }
      Validointivirhe validointivirhe = (Validointivirhe) virhe;
      return new ValidointivirheDTO(
              Virhetyyppi.valueOf(validointivirhe.getVirhetyyppi().name()),
              validointivirhe.getVirheviesti()
      );
  }

  public FunktiokutsuDTO fkToDto(Funktiokutsu funktiokutsu) {
    return new FunktiokutsuDTO(
            funktiokutsu.getFunktionimi(),
            funktiokutsu.getTulosTunniste(),
            funktiokutsu.getTulosTekstiFi(),
            funktiokutsu.getTulosTekstiSv(),
            funktiokutsu.getTulosTekstiEn(),
            funktiokutsu.isTallennaTulos(),
            funktiokutsu.isOmaopintopolku(),
            funktiokutsu.getArvokonvertteriparametrit().stream()
                    .map(this::akpToDto)
                    .collect(Collectors.toSet()),
            funktiokutsu.getArvovalikonvertteriparametrit().stream()
                    .sorted()
                    .map(this::avkpToDto)
                    .collect(Collectors.toList()),
            funktiokutsu.getSyoteparametrit().stream()
                    .map(this::spToDto)
                    .collect(Collectors.toSet()),
            funktiokutsu.getFunktioargumentit().stream()
                    .sorted()
                    .map(this::faToDto)
                    .collect(Collectors.toList()),
            funktiokutsu.getValintaperusteviitteet().stream()
                    .sorted()
                    .map(this::vpvToDto)
                    .collect(Collectors.toList()),
            funktiokutsu.getValidointivirheet() == null ?
                    null :
                    funktiokutsu.getValidointivirheet().stream()
                            .map(this::avvToDto)
                            .collect(Collectors.toList())
    );
  }

  public LaskentakaavaDTO lkToDto(Laskentakaava lk) {
      return new LaskentakaavaDTO(
              lk.getId().id,
              lk.getOnLuonnos(),
              lk.getNimi(),
              lk.getKuvaus(),
              fkToDto(lk.getFunktiokutsu())
      );
  }

  public LaskentakaavaCreateDTO lkToCreateDto(Laskentakaava lk) {
      return new LaskentakaavaCreateDTO(
              lk.getOnLuonnos(),
              lk.getNimi(),
              lk.getKuvaus(),
              fkToDto(lk.getFunktiokutsu())
      );
  }

  public LaskentakaavaListDTO lkToListDto(Laskentakaava lk) {
      return new LaskentakaavaListDTO(
              lk.getId().id,
              lk.getOnLuonnos(),
              lk.getNimi(),
              lk.getKuvaus(),
              lk.getFunktiokutsu().getFunktionimi().getTyyppi()
      );
  }

  private ValintaperusteetFunktioargumenttiDTO faToValintaperusteDto(Funktioargumentti funktioargumentti) {
      return new ValintaperusteetFunktioargumenttiDTO(
              funktioargumentti.getId().id,
              this.fkToValintaperusteDto(
                      funktioargumentti.getFunktiokutsuChild() == null ?
                              funktioargumentti.getLaskentakaavaChild().getFunktiokutsu() :
                              funktioargumentti.getFunktiokutsuChild()
              ),
              funktioargumentti.getIndeksi()
      );
  }

  public ValintaperusteetFunktiokutsuDTO fkToValintaperusteDto(Funktiokutsu funktiokutsu) {
      return new ValintaperusteetFunktiokutsuDTO(
              funktiokutsu.getId().id,
              funktiokutsu.getFunktionimi(),
              funktiokutsu.getTulosTunniste(),
              funktiokutsu.getTulosTekstiFi(),
              funktiokutsu.getTulosTekstiSv(),
              funktiokutsu.getTulosTekstiEn(),
              funktiokutsu.isTallennaTulos(),
              funktiokutsu.isOmaopintopolku(),
              funktiokutsu.getArvokonvertteriparametrit().stream()
                      .map(this::akpToDto)
                      .collect(Collectors.toSet()),
              funktiokutsu.getArvovalikonvertteriparametrit().stream()
                      .map(this::avkpToDto)
                      .collect(Collectors.toSet()),
              funktiokutsu.getSyoteparametrit().stream()
                      .map(this::spToDto)
                      .collect(Collectors.toSet()),
              funktiokutsu.getFunktioargumentit().stream()
                      .map(this::faToValintaperusteDto)
                      .collect(Collectors.toSet()),
              funktiokutsu.getValintaperusteviitteet().stream()
                      .map(this::vpvToDto)
                      .collect(Collectors.toSet())
      );
  }

  public ValintaperusteetJarjestyskriteeriDTO jkToValintaperusteDto(Jarjestyskriteeri jarjestyskriteeri,
                                                                    int prioriteetti,
                                                                    Laskentakaava laskentakaava) {
      if (laskentakaava.getId().id != jarjestyskriteeri.getLaskentakaavaId()) {
          throw new IllegalArgumentException("Annettu laskentakaava ei ole annetun järjestyskriteerin käytössä");
      }
      return new ValintaperusteetJarjestyskriteeriDTO(
              laskentakaava.getNimi(),
              prioriteetti,
              fkToValintaperusteDto(laskentakaava.getFunktiokutsu())
      );
  }

  public ValintakoeDTO vkToDto(Valintakoe valintakoe,
                               Laskentakaava laskentakaava) {
      if (laskentakaava.getId().id != valintakoe.getLaskentakaavaId()) {
          throw new IllegalArgumentException("Annettu laskentakaava ei ole annetun valintakokeen käytössä");
      }
      return new ValintakoeDTO(
              valintakoe.getTunniste(),
              valintakoe.getLaskentakaavaId(),
              valintakoe.getNimi(),
              valintakoe.getKuvaus(),
              valintakoe.getAktiivinen(),
              valintakoe.getLahetetaankoKoekutsut(),
              valintakoe.getKutsutaankoKaikki(),
              valintakoe.getKutsuttavienMaara(),
              valintakoe.getKutsunKohde(),
              valintakoe.getKutsunKohdeAvain(),
              valintakoe.getOid(),
              fkToDto(laskentakaava.getFunktiokutsu())
      );
  }

  public ValintakoeDTO ainaPakollinenvkToDto(Valintakoe valintakoe) {
      Set<SyoteparametriDTO> syoteparametrit = new HashSet<>();
      syoteparametrit.add(new SyoteparametriDTO(
              "totuusarvo",
              "true"
      ));
      return new ValintakoeDTO(
              valintakoe.getTunniste(),
              valintakoe.getLaskentakaavaId(),
              valintakoe.getNimi(),
              valintakoe.getKuvaus(),
              valintakoe.getAktiivinen(),
              valintakoe.getLahetetaankoKoekutsut(),
              valintakoe.getKutsutaankoKaikki(),
              valintakoe.getKutsuttavienMaara(),
              valintakoe.getKutsunKohde(),
              valintakoe.getKutsunKohdeAvain(),
              valintakoe.getOid(),
              new FunktiokutsuDTO(
                      Funktionimi.TOTUUSARVO,
                      "",
                      "",
                      "",
                      "",
                      false,
                      false,
                      new HashSet<>(),
                      new ArrayList<>(),
                      syoteparametrit,
                      new ArrayList<>(),
                      new ArrayList<>(),
                      null
              )
      );
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
