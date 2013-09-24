package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

/**
 * User: wuoti
 * Date: 24.9.2013
 * Time: 9.38
 */
public class LahdeskaalaVirheellinenVirhe extends Validointivirhe {
    public LahdeskaalaVirheellinenVirhe(String virheviesti) {
        super(Virhetyyppi.LAHDESKAALA_VIRHEELLINEN, virheviesti);
    }
}
