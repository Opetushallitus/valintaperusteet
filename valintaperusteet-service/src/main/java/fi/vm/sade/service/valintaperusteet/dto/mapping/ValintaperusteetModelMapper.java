package fi.vm.sade.service.valintaperusteet.dto.mapping;


import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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

        this.addMappings(new PropertyMap<Funktiokutsu, FunktiokutsuDTO>() {
            @Override
            protected void configure() {
                Converter<Set<Funktioargumentti>, List<FunktioargumenttiDTO>> funktioargumenttiToDtoConverter = new Converter<Set<Funktioargumentti>, List<FunktioargumenttiDTO>>() {
                    public List<FunktioargumenttiDTO> convert(MappingContext<Set<Funktioargumentti>, List<FunktioargumenttiDTO>> context) {
                        List<FunktioargumenttiDTO> result = new LinkedList<FunktioargumenttiDTO>();
                        for(Funktioargumentti arg : context.getSource()) {
                           ModelMapper modelMapper = new ModelMapper();
                           FunktioargumenttiDTO dto = modelMapper.map(arg, FunktioargumenttiDTO.class);
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
                            if(arg.getFunktiokutsuChild() != null) {
                                asetaIndeksitRekursiivisesti(arg.getFunktiokutsuChild());
                            }
                            ModelMapper modelMapper = new ModelMapper();
                            Funktioargumentti funktioargumentti = modelMapper.map(arg, Funktioargumentti.class);
                            result.add(funktioargumentti);
                        }
                        return result;
                    }
                };

                using(funktioargumenttiToDtoConverter).map(source.getFunktioargumentit()).setFunktioargumentit(null);
            }
        });


    }

    public Funktiokutsu asetaIndeksitRekursiivisesti(FunktiokutsuDTO kutsu) {
        for(int i = 0; i < kutsu.getFunktioargumentit().size(); i++) {
            FunktioargumenttiDTO arg = kutsu.getFunktioargumentit().get(i);
            arg.setIndeksi(i+1);
            if (arg.getFunktiokutsuChild() != null) {
                asetaIndeksitRekursiivisesti(arg.getFunktiokutsuChild());
            }

        }
        ModelMapper modelMapper = new ModelMapper();
        Funktiokutsu funktiokutsu = modelMapper.map(kutsu, Funktiokutsu.class);
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
