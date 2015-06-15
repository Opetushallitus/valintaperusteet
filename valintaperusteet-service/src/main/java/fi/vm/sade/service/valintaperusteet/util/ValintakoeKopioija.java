package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import org.apache.commons.lang.StringUtils;

public class ValintakoeKopioija implements Kopioija<Valintakoe> {
    @Override
    public Valintakoe luoKlooni(Valintakoe valintakoe) {
        Valintakoe klooni = new Valintakoe();
        kopioiTiedot(valintakoe, klooni);
        return klooni;
    }

    @Override
    public void kopioiTiedot(Valintakoe from, Valintakoe to) {
        if (from.getLahetetaankoKoekutsut() != null) {
            to.setLahetetaankoKoekutsut(from.getLahetetaankoKoekutsut());
        }
        if (from.getKutsutaankoKaikki() != null) {
            to.setKutsutaankoKaikki(from.getKutsutaankoKaikki());
        }
        if (from.getAktiivinen() != null) {
            to.setAktiivinen(from.getAktiivinen());
        }
        if (StringUtils.isNotBlank(from.getKuvaus())) {
            to.setKuvaus(from.getKuvaus());
        }
        if (StringUtils.isNotBlank(from.getNimi())) {
            to.setNimi(from.getNimi());
        }
        if (StringUtils.isNotBlank(from.getTunniste())) {
            to.setTunniste(from.getTunniste());
        }
        if (StringUtils.isNotBlank(from.getKutsunKohdeAvain())) {
            to.setKutsunKohdeAvain(from.getKutsunKohdeAvain());
        }
        to.setKutsunKohde(from.getKutsunKohde());
        to.setKutsuttavienMaara(from.getKutsuttavienMaara());
        to.setLaskentakaava(from.getLaskentakaava());
    }

    @Override
    public void kopioiTiedotMasteriltaKopiolle(Valintakoe alkuperainenMaster, Valintakoe paivitettyMaster, Valintakoe kopio) {
        if (kopio.getAktiivinen().equals(alkuperainenMaster.getAktiivinen())) {
            kopio.setAktiivinen(paivitettyMaster.getAktiivinen());
        }
        if ((kopio.getLaskentakaava() == null && alkuperainenMaster.getLaskentakaava() == null) ||
                (kopio.getLaskentakaava() != null && alkuperainenMaster.getLaskentakaava() != null &&
                        kopio.getLaskentakaava().equals(alkuperainenMaster.getLaskentakaava()))) {
            kopio.setLaskentakaava(paivitettyMaster.getLaskentakaava());
        }
        kopio.setNimi(paivitettyMaster.getNimi());
        kopio.setKuvaus(paivitettyMaster.getKuvaus());
        kopio.setTunniste(paivitettyMaster.getTunniste());
        kopio.setKutsunKohdeAvain(paivitettyMaster.getKutsunKohdeAvain());
        kopio.setLahetetaankoKoekutsut(paivitettyMaster.getLahetetaankoKoekutsut());
        kopio.setKutsutaankoKaikki(paivitettyMaster.getKutsutaankoKaikki());
        kopio.setKutsuttavienMaara(paivitettyMaster.getKutsuttavienMaara());
        kopio.setKutsunKohde(paivitettyMaster.getKutsunKohde());
    }
}
