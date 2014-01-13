package fi.vm.sade.service.valintaperusteet.dto.mapping;


import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

        this.addMappings(new PropertyMap<Funktiokutsu, FunktiokutsuDTO>() {
            @Override
            protected void configure() {
                Converter<Set<Funktioargumentti>, Set<FunktioargumenttiDTO>> funktioargumenttiToDtoConverter = new Converter<Set<Funktioargumentti>, Set<FunktioargumenttiDTO>>() {
                    public Set<FunktioargumenttiDTO> convert(MappingContext<Set<Funktioargumentti>, Set<FunktioargumenttiDTO>> context) {
                        Set<FunktioargumenttiDTO> result = new TreeSet<FunktioargumenttiDTO>();
                        for(Funktioargumentti arg : context.getSource()) {
                            ModelMapper modelMapper = new ModelMapper();
                            result.add(modelMapper.map(arg, FunktioargumenttiDTO.class));

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
                Converter<Set<FunktioargumenttiDTO>, Set<Funktioargumentti>> funktioargumenttiToDtoConverter = new Converter<Set<FunktioargumenttiDTO>, Set<Funktioargumentti>>() {
                    public Set<Funktioargumentti> convert(MappingContext<Set<FunktioargumenttiDTO>, Set<Funktioargumentti>> context) {
                        Set<Funktioargumentti> result = new TreeSet<Funktioargumentti>();

                        for(FunktioargumenttiDTO arg : context.getSource()) {

                            ModelMapper modelMapper = new ModelMapper();
                            result.add(modelMapper.map(arg, Funktioargumentti.class));
                        }
                        return result;
                    }
                };

                using(funktioargumenttiToDtoConverter).map(source.getFunktioargumentit()).setFunktioargumentit(null);
            }
        });
    }

    public <FROM, TO> List<TO> mapList(List<FROM> list, final Class<TO> to) {

        List<TO> toList = new ArrayList<TO>();

        for (FROM f : list) {
            toList.add(map(f, to));
        }

        return toList;
    }
}
