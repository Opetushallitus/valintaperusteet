package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: kwuoti Date: 21.1.2013 Time: 9.34
 */
public interface LaskentakaavaService {

    List<ValintaperusteDTO> findAvaimetForHakukohdes(List<String> oids);

    Laskentakaava validoi(LaskentakaavaDTO laskentakaava);

    Funktiokutsu haeMallinnettuFunktiokutsu(Long id) throws FunktiokutsuMuodostaaSilmukanException;

    boolean onkoKaavaValidi(Laskentakaava laskentakaava);

    List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi);

    /**
     * Päivittää ainostaan laskentakaavan metadatan. Paluuarvossa EI tule funktiokutsuja.
     *
     * @param id
     * @param laskentakaava
     * @return
     */
    Laskentakaava updateMetadata(Long id, LaskentakaavaCreateDTO laskentakaava);

    Laskentakaava haeLaskettavaKaava(Long id, Laskentamoodi laskentamoodi);

    Laskentakaava haeMallinnettuKaava(Long id);

    Laskentakaava insert(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid);

    Laskentakaava update(Long id, LaskentakaavaCreateDTO laskentakaava);

    @Transactional(readOnly = true)
    Laskentakaava read(Long key);

    Laskentakaava insert(Laskentakaava laskentakaava, String hakukohdeOid, String valintaryhmaOid);
}
