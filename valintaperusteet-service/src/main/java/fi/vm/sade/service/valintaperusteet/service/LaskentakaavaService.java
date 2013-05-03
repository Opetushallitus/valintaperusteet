package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * User: kwuoti Date: 21.1.2013 Time: 9.34
 */
public interface LaskentakaavaService extends CRUDService<Laskentakaava, Long, String> {

    JSONObject findAvaimetForHakukohdes(List<String> oids);

    Laskentakaava validoi(Laskentakaava laskentakaava);

    Funktiokutsu haeMallinnettuFunktiokutsu(Long id);

    boolean onkoKaavaValidi(Laskentakaava laskentakaava);

    List<Laskentakaava> findKaavas(boolean all, String valintaryhmaOid, Funktiotyyppi tyyppi);

    /**
     * Päivittää ainostaan laskentakaavan metadatan. Paluuarvossa EI tule funktiokutsuja.
     * @param laskentakaava
     * @return
     */
    Laskentakaava updateMetadata(Laskentakaava laskentakaava);

    Laskentakaava haeLaskettavaKaava(Long id);

    Laskentakaava haeMallinnettuKaava(Long id);
}
