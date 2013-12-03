package fi.vm.sade.service.valintaperusteet.dto.mapping;


import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.ArrayList;
import java.util.List;

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
    }

    public <FROM, TO> List<TO> mapList(List<FROM> list, final Class<TO> to) {

        List<TO> toList = new ArrayList<TO>();

        for (FROM f : list) {
            toList.add(map(f, to));
        }

        return toList;
    }
}
