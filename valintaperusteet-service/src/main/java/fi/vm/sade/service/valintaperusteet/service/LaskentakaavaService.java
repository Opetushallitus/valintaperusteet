package fi.vm.sade.service.valintaperusteet.service;

import java.util.List;
import java.util.Optional;

import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;

/**
 * User: kwuoti Date: 21.1.2013 Time: 9.34
 */
public interface LaskentakaavaService {

    List<ValintaperusteDTO> findAvaimetForHakukohde(String hakukohdeOid);

    HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(String oid);

    Laskentakaava validoi(LaskentakaavaDTO laskentakaava);

    Funktiokutsu haeMallinnettuFunktiokutsu(Long id) throws FunktiokutsuMuodostaaSilmukanException;

    boolean onkoKaavaValidi(Laskentakaava laskentakaava);

    List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, String hakukohdeOid,
            fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi);


    Laskentakaava haeLaskettavaKaava(Long id, Laskentamoodi laskentamoodi);

    Laskentakaava haeMallinnettuKaava(Long id);

    Laskentakaava insert(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid);

    Laskentakaava update(Long id, LaskentakaavaCreateDTO laskentakaava);

    Laskentakaava read(Long key);

    Laskentakaava insert(Laskentakaava laskentakaava, String hakukohdeOid, String valintaryhmaOid);

    void tyhjennaCache();

    Optional<Laskentakaava> siirra(LaskentakaavaSiirraDTO dto);

    Optional<Valintaryhma> valintaryhma(long id);

    Optional<Laskentakaava> pelkkaKaava(Long key);

    boolean poista(long id);

    void poistaOrpoFunktiokutsu(Long id);
}
