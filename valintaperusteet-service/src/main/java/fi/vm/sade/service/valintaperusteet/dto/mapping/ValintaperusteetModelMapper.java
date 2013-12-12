package fi.vm.sade.service.valintaperusteet.dto.mapping;


import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;

import java.util.*;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 15.22
 */
public class ValintaperusteetModelMapper extends ModelMapper {

    public ValintaperusteetModelMapper() {
        super();

        this.addMappings(new PropertyMap<Hakijaryhma, HakijaryhmaDTO>() {
            @Override
            protected void configure() {
                map().setLaskentakaavaId(source.getLaskentakaavaId());
            }
        });
        this.addMappings(new PropertyMap<HakijaryhmaDTO, Hakijaryhma>() {
            @Override
            protected void configure() {
                map().setLaskentakaavaId(source.getLaskentakaavaId());
            }
        });
        this.addMappings(new PropertyMap<Valintakoe, ValintakoeDTO>() {
            @Override
            protected void configure() {
                map().setLaskentakaavaId(source.getLaskentakaava().getId());
            }
        });
        this.addMappings(new PropertyMap<Jarjestyskriteeri, JarjestyskriteeriDTO>() {
            @Override
            protected void configure() {
                map().setLaskentakaavaId(source.getLaskentakaava().getId());
            }
        });


        this.addMappings(new PropertyMap<Laskentakaava, FunktioargumentinLapsiDTO>() {
            @Override
            protected void configure() {
                map().setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
            }
        });

        this.addMappings(new PropertyMap<Funktiokutsu, FunktioargumentinLapsiDTO>() {
            @Override
            protected void configure() {
                map().setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);

            }
        });

        this.addMappings(new PropertyMap<Funktiokutsu, FunktiokutsuDTO>() {
            @Override
            protected void configure() {
                Converter<Set<Funktioargumentti>, List<FunktioargumenttiDTO>> funktioargumenttiToDtoConverter = new Converter<Set<Funktioargumentti>, List<FunktioargumenttiDTO>>() {
                    public List<FunktioargumenttiDTO> convert(MappingContext<Set<Funktioargumentti>, List<FunktioargumenttiDTO>> context) {
                        List<FunktioargumenttiDTO> result = new LinkedList<FunktioargumenttiDTO>();
                        for(Funktioargumentti arg : context.getSource()) {
                            FunktioargumenttiDTO dto = new FunktioargumenttiDTO();
                            ModelMapper modelMapper = new ModelMapper();
                            if(arg.getFunktiokutsuChild() != null) {
                                FunktioargumentinLapsiDTO lapsi = asetaFunktioArgumenttiLapsetRekursiivisesti(arg.getFunktiokutsuChild());
                                lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
                                dto.setLapsi(lapsi);
                            }
                            if(arg.getLaskentakaavaChild() != null) {
                                FunktioargumentinLapsiDTO lapsi = modelMapper.map(arg.getLaskentakaavaChild(), FunktioargumentinLapsiDTO.class);
                                lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
                                dto.setLapsi(lapsi);
                            }
                            dto.setIndeksi(arg.getIndeksi());
                            result.add(dto);
                        }

                        return result;
                    }
                };

                using(funktioargumenttiToDtoConverter).map(source.getFunktioargumentit()).setFunktioargumentit(null);
            }
        });

        this.addMappings(new PropertyMap<FunktiokutsuDTO, Funktiokutsu>() {
            @Override
            protected void configure() {
                Converter<List<FunktioargumenttiDTO>, Set<Funktioargumentti>> funktioargumenttiToDtoConverter = new Converter<List<FunktioargumenttiDTO>, Set<Funktioargumentti>>() {
                    public Set<Funktioargumentti> convert(MappingContext<List<FunktioargumenttiDTO>, Set<Funktioargumentti>> context) {
                        Set<Funktioargumentti> result = new TreeSet<Funktioargumentti>();

                        for(int i = 0; i < context.getSource().size(); i++) {
                            FunktioargumenttiDTO arg = context.getSource().get(i);
                            arg.setIndeksi(i+1);
                            ModelMapper modelMapper = new ModelMapper();
                            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
                            Funktioargumentti funktioargumentti = new Funktioargumentti();
                            if(arg.getLapsi() != null && arg.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
                                asetaIndeksitRekursiivisesti(arg.getLapsi());
                                FunktiokutsuDTO dto = modelMapper.map(arg, FunktiokutsuDTO.class);
                                funktioargumentti.setFunktiokutsuChild(convertFromDto(dto));
                            }
                            if(arg.getLapsi() != null && arg.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI)) {
                                LaskentakaavaListDTO dto = modelMapper.map(arg, LaskentakaavaListDTO.class);
                                funktioargumentti.setLaskentakaavaChild(convertFromDto(dto));
                            }

                            funktioargumentti.setIndeksi(arg.getIndeksi());
                            result.add(funktioargumentti);
                        }
                        return result;
                    }
                };

                using(funktioargumenttiToDtoConverter).map(source.getFunktioargumentit()).setFunktioargumentit(null);
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
        for(Funktioargumentti arg : kutsu.getFunktioargumentit()) {
            FunktioargumenttiDTO dto = new FunktioargumenttiDTO();
            dto.setIndeksi(arg.getIndeksi());
            if(arg.getFunktiokutsuChild() != null) {
                FunktioargumentinLapsiDTO lapsi = asetaFunktioArgumenttiLapsetRekursiivisesti(arg.getFunktiokutsuChild());
                lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
                dto.setLapsi(lapsi);
            }
            if(arg.getLaskentakaavaChild() != null) {
                FunktioargumentinLapsiDTO lapsi = map(arg.getLaskentakaavaChild(), FunktioargumentinLapsiDTO.class);
                lapsi.setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
                dto.setLapsi(lapsi);
            }
            result.add(dto);

        }
        parent.setFunktioargumentit(result);
        parent.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
        return parent;
    }

    public Funktiokutsu asetaIndeksitRekursiivisesti(FunktioargumentinLapsiDTO kutsu) {
        for(int i = 0; i < kutsu.getFunktioargumentit().size(); i++) {
            FunktioargumenttiDTO arg = kutsu.getFunktioargumentit().get(i);
            arg.setIndeksi(i+1);
            if (arg.getLapsi() != null && arg.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
                asetaIndeksitRekursiivisesti(arg.getLapsi());
            }

        }
        Funktiokutsu funktiokutsu = map(kutsu, Funktiokutsu.class);
        return funktiokutsu;
    }

    public <FROM, TO> List<TO> mapList(List<FROM> list, final Class<TO> to) {

        List<TO> toList = new ArrayList<TO>();

        for (FROM f : list) {
            toList.add(map(f, to));
        }

        return toList;
    }
}
