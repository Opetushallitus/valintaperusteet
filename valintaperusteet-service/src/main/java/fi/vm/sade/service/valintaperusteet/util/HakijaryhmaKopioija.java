package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import org.apache.commons.lang.StringUtils;

/**
 * User: kwuoti
 * Date: 14.2.2013
 * Time: 14.55
 */
public class HakijaryhmaKopioija implements Kopioija<Hakijaryhma> {
    @Override
    public Hakijaryhma luoKlooni(Hakijaryhma alkuperainen) {
        Hakijaryhma klooni = new Hakijaryhma();
        kopioiTiedot(alkuperainen, klooni);
        return klooni;
    }

    @Override
    public void kopioiTiedot(Hakijaryhma from, Hakijaryhma to) {

        to.setKiintio(from.getKiintio());

        to.setLaskentakaava(from.getLaskentakaava());

        if (StringUtils.isNotBlank(from.getKuvaus())) {
            to.setKuvaus(from.getKuvaus());
        }

        if (StringUtils.isNotBlank(from.getNimi())) {
            to.setNimi(from.getNimi());
        }
    }

    @Override
    public void kopioiTiedotMasteriltaKopiolle(Hakijaryhma alkuperainenMaster,
                                               Hakijaryhma paivitettyMaster, Hakijaryhma kopio) {

        if (kopio.getKiintio() == alkuperainenMaster.getKiintio()) {
            kopio.setKiintio(paivitettyMaster.getKiintio());
        }

        kopio.setKuvaus(paivitettyMaster.getKuvaus());
        kopio.setNimi(paivitettyMaster.getNimi());
        kopio.setLaskentakaava(paivitettyMaster.getLaskentakaava());
    }
}
