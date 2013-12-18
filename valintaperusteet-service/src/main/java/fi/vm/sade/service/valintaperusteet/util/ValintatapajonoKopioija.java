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

        // VT-657
        if(from.getVarasijanTayttojono() != null) {
            to.setVarasijanTayttojono(from.getVarasijanTayttojono());
        }
        if(from.getPoissaOlevaTaytto() != null) {
            to.setPoissaOlevaTaytto(from.getPoissaOlevaTaytto());
        }
        if(from.getVarasijat() != null) {
            to.setVarasijat(from.getVarasijat());
        }

        to.setVarasijojaKaytetaanAlkaen(from.getVarasijojaKaytetaanAlkaen());


        to.setVarasijojaTaytetaanAsti(from.getVarasijojaTaytetaanAsti());

        if(from.getKaytetaanValintalaskentaa() != null) {
            to.setKaytetaanValintalaskentaa(from.getKaytetaanValintalaskentaa());
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

        // VT-657
        // Pitää kopioida kopion kopio
//        if (kopio.getVarasijanTayttojono().equals(alkuperainenMaster.getVarasijanTayttojono())) {
//            kopio.setVarasijanTayttojono(paivitettyMaster.getVarasijanTayttojono());
//        }
        if (kopio.getPoissaOlevaTaytto().equals(alkuperainenMaster.getPoissaOlevaTaytto())) {
            kopio.setPoissaOlevaTaytto(paivitettyMaster.getPoissaOlevaTaytto());
        }
        if (kopio.getVarasijat().equals(alkuperainenMaster.getVarasijat())) {
            kopio.setVarasijat(paivitettyMaster.getVarasijat());
        }
        if (kopio.getVarasijojaKaytetaanAlkaen() == null || kopio.getVarasijojaKaytetaanAlkaen().equals(alkuperainenMaster.getVarasijojaKaytetaanAlkaen())) {
            kopio.setVarasijojaKaytetaanAlkaen(paivitettyMaster.getVarasijojaKaytetaanAlkaen());
        }
        if (kopio.getVarasijojaTaytetaanAsti() == null || kopio.getVarasijojaTaytetaanAsti().equals(alkuperainenMaster.getVarasijojaTaytetaanAsti())) {
            kopio.setVarasijojaTaytetaanAsti(paivitettyMaster.getVarasijojaTaytetaanAsti());
        }
        if (kopio.getKaytetaanValintalaskentaa().equals(alkuperainenMaster.getKaytetaanValintalaskentaa())) {
            kopio.setKaytetaanValintalaskentaa(paivitettyMaster.getKaytetaanValintalaskentaa());
        }

        kopio.setKuvaus(paivitettyMaster.getKuvaus());
        kopio.setNimi(paivitettyMaster.getNimi());
    }
}
