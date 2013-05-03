package fi.vm.sade.service.valintaperusteet.model;

import java.util.Collection;

/**
 * User: kwuoti
 * Date: 14.2.2013
 * Time: 9.49
 */
public interface Kopioitava<T extends Kopioitava, C extends Collection<T>> {

    String getOid();
    void setMaster(T master);
    T getMaster();

    void  setKopiot(C kopiot);
    C getKopiot();
}
