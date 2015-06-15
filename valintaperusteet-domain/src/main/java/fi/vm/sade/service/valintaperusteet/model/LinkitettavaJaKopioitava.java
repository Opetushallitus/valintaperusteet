package fi.vm.sade.service.valintaperusteet.model;

import java.util.Collection;

public interface LinkitettavaJaKopioitava<T extends LinkitettavaJaKopioitava, C extends Collection<T>> extends Linkitettava<T>, Kopioitava<T, C> {
    @Override
    void setMaster(T master);

    @Override
    T getMaster();

    @Override
    void setKopiot(C kopiot);

    @Override
    C getKopiot();

    @Override
    T getEdellinen();

    @Override
    T getSeuraava();

    @Override
    void setEdellinen(T edellinen);

    @Override
    void setSeuraava(T seuraava);
}
