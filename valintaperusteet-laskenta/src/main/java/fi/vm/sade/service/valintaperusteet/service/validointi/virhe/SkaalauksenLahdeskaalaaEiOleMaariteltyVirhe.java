package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

/**
 * User: wuoti
 * Date: 24.9.2013
 * Time: 9.14
 */
public class SkaalauksenLahdeskaalaaEiOleMaariteltyVirhe extends Validointivirhe {
    public SkaalauksenLahdeskaalaaEiOleMaariteltyVirhe(String virheviesti) {
        super(Virhetyyppi.LAHDESKAALAA_EI_OLE_MAARITELTY, virheviesti);
    }
}
