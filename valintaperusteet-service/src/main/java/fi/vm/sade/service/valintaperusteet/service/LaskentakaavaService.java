package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;

import java.util.List;

/**
 * User: kwuoti Date: 21.1.2013 Time: 9.34
 */
public interface LaskentakaavaService extends CRUDService<Laskentakaava, Long, String> {

    List<ValintaperusteDTO> findAvaimetForHakukohdes(List<String> oids);

    HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(String oid);

    Laskentakaava validoi(Laskentakaava laskentakaava);

    Funktiokutsu haeMallinnettuFunktiokutsu(Long id) throws FunktiokutsuMuodostaaSilmukanException;

    boolean onkoKaavaValidi(Laskentakaava laskentakaava);

    List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, String hakukohdeOid, Funktiotyyppi tyyppi);

    /**
     * Päivittää ainostaan laskentakaavan metadatan. Paluuarvossa EI tule funktiokutsuja.
     *
     * @param laskentakaava
     * @return
     */
    Laskentakaava updateMetadata(Laskentakaava laskentakaava);

    Laskentakaava haeLaskettavaKaava(Long id, Laskentamoodi laskentamoodi);

    Laskentakaava haeMallinnettuKaava(Long id);
}
