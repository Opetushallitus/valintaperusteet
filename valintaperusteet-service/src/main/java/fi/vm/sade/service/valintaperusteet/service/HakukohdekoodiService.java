package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 13.5.2013
 * Time: 13.01
 * To change this template use File | Settings | File Templates.
 */
public interface HakukohdekoodiService {

    void updateValintaryhmaHakukohdekoodit(String valintaryhmaOid, Set<Hakukohdekoodi> hakukohdekoodit);

    void lisaaHakukohdekoodiValintaryhmalle(String valintaryhmaOid, Hakukohdekoodi hakukohdekoodi);

    Hakukohdekoodi lisaaHakukohdekoodiHakukohde(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi);

    Hakukohdekoodi updateHakukohdeHakukohdekoodi(String hakukohdeOid, Hakukohdekoodi hakukohdekoodi);
}
