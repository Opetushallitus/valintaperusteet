package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import org.apache.commons.lang.StringUtils;

/**
 * User: kwuoti
 * Date: 14.2.2013
 * Time: 14.55
 */
public class HakijaryhmaValintatapajonoKopioija implements Kopioija<HakijaryhmaValintatapajono> {
    @Override
    public HakijaryhmaValintatapajono luoKlooni(HakijaryhmaValintatapajono alkuperainen) {
        HakijaryhmaValintatapajono klooni = new HakijaryhmaValintatapajono();
        kopioiTiedot(alkuperainen, klooni);
        return klooni;
    }

    @Override
    public void kopioiTiedot(HakijaryhmaValintatapajono from, HakijaryhmaValintatapajono to) {

        to.setAktiivinen(from.getAktiivinen());
    }

    @Override
    public void kopioiTiedotMasteriltaKopiolle(HakijaryhmaValintatapajono alkuperainenMaster,
                                               HakijaryhmaValintatapajono paivitettyMaster, HakijaryhmaValintatapajono kopio) {

        if (kopio.getAktiivinen() == alkuperainenMaster.getAktiivinen()) {
            kopio.setAktiivinen(paivitettyMaster.getAktiivinen());
        }
    }
}
