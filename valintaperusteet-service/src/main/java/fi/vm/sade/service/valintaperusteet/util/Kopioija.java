package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Kopioitava;

/**
 * User: kwuoti
 * Date: 14.2.2013
 * Time: 14.51
 */
public interface Kopioija<T extends Kopioitava> {
    T luoKlooni(T t);
    void kopioiTiedot(T from, T to);
    void kopioiTiedotMasteriltaKopiolle(T alkuperainenMaster, T paivitettyMaster, T kopio);
}
