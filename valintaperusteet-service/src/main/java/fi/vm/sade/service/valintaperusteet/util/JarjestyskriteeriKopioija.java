package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import org.apache.commons.lang.StringUtils;

public class JarjestyskriteeriKopioija implements Kopioija<Jarjestyskriteeri> {
    @Override
    public Jarjestyskriteeri luoKlooni(Jarjestyskriteeri jarjestyskriteeri) {
        Jarjestyskriteeri klooni = new Jarjestyskriteeri();
        kopioiTiedot(jarjestyskriteeri, klooni);
        return klooni;
    }

    @Override
    public void kopioiTiedot(Jarjestyskriteeri from, Jarjestyskriteeri to) {
        if (from.getAktiivinen() != null) {
            to.setAktiivinen(from.getAktiivinen());
        }

        if (from.getLaskentakaava() != null) {
            to.setLaskentakaava(from.getLaskentakaava());
        }

        if (StringUtils.isNotEmpty(from.getMetatiedot())) {
            to.setMetatiedot(from.getMetatiedot());
        }

    }

    @Override
    public void kopioiTiedotMasteriltaKopiolle(Jarjestyskriteeri alkuperainenMaster,
                                               Jarjestyskriteeri paivitettyMaster, Jarjestyskriteeri kopio) {
        if (kopio.getAktiivinen().equals(alkuperainenMaster.getAktiivinen())) {
            kopio.setAktiivinen(paivitettyMaster.getAktiivinen());
        }

        if (kopio.getLaskentakaava().equals(alkuperainenMaster.getLaskentakaava())) {
            kopio.setLaskentakaava(paivitettyMaster.getLaskentakaava());
        }

        kopio.setMetatiedot(paivitettyMaster.getMetatiedot());
    }
}
