package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

/**
 * User: wuoti
 * Date: 24.9.2013
 * Time: 9.38
 */
public class KohdeskaalaVirheellinenVirhe extends Validointivirhe {
    public KohdeskaalaVirheellinenVirhe(String virheviesti) {
        super(Virhetyyppi.KOHDESKAALA_VIRHEELLINEN, virheviesti);
    }
}
