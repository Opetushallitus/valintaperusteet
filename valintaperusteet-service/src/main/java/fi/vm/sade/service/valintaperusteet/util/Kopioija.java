package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Kopioitava;

public interface Kopioija<T extends Kopioitava> {
    T luoKlooni(T t);
    void kopioiTiedot(T from, T to);
    void kopioiTiedotMasteriltaKopiolle(T alkuperainenMaster, T paivitettyMaster, T kopio);
}
