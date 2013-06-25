package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import org.apache.commons.lang.StringUtils;

/**
 * User: kwuoti
 * Date: 14.2.2013
 * Time: 14.55
 */
public class ValintatapajonoKopioija implements Kopioija<Valintatapajono> {
    @Override
    public Valintatapajono luoKlooni(Valintatapajono valintatapajono) {
        Valintatapajono klooni = new Valintatapajono();
        kopioiTiedot(valintatapajono, klooni);
        return klooni;
    }

    @Override
    public void kopioiTiedot(Valintatapajono from, Valintatapajono to) {
        if (from.getAktiivinen() != null) {
            to.setAktiivinen(from.getAktiivinen());
        }

        if (from.getAloituspaikat() != null) {
            to.setAloituspaikat(from.getAloituspaikat());
        }

        if (StringUtils.isNotBlank(from.getKuvaus())) {
            to.setKuvaus(from.getKuvaus());
        }

        if (StringUtils.isNotBlank(from.getNimi())) {
            to.setNimi(from.getNimi());
        }

        if (from.getSiirretaanSijoitteluun() != null) {
            to.setSiirretaanSijoitteluun(from.getSiirretaanSijoitteluun());
        }

        if (from.getEiVarasijatayttoa() != null) {
            to.setEiVarasijatayttoa(from.getEiVarasijatayttoa());
        }

        if (from.getTasapistesaanto() != null) {
            to.setTasapistesaanto(from.getTasapistesaanto());
        }
    }

    @Override
    public void kopioiTiedotMasteriltaKopiolle(Valintatapajono alkuperainenMaster,
                                               Valintatapajono paivitettyMaster, Valintatapajono kopio) {
        if (kopio.getAktiivinen().equals(alkuperainenMaster.getAktiivinen())) {
            kopio.setAktiivinen(paivitettyMaster.getAktiivinen());
        }

        if (kopio.getAloituspaikat().equals(alkuperainenMaster.getAloituspaikat())) {
            kopio.setAloituspaikat(paivitettyMaster.getAloituspaikat());
        }

        if (kopio.getSiirretaanSijoitteluun().equals(alkuperainenMaster.getSiirretaanSijoitteluun())) {
            kopio.setSiirretaanSijoitteluun(paivitettyMaster.getSiirretaanSijoitteluun());
        }

        if (kopio.getEiVarasijatayttoa().equals(alkuperainenMaster.getEiVarasijatayttoa())) {
            kopio.setEiVarasijatayttoa(paivitettyMaster.getEiVarasijatayttoa());
        }

        if (kopio.getTasapistesaanto().equals(alkuperainenMaster.getTasapistesaanto())) {
            kopio.setTasapistesaanto(paivitettyMaster.getTasapistesaanto());
        }

        kopio.setKuvaus(paivitettyMaster.getKuvaus());
        kopio.setNimi(paivitettyMaster.getNimi());
    }
}
