package fi.vm.sade.service.valintaperusteet.model;

import java.util.Collection;

public interface LinkitettavaJaKopioitava<
        T extends LinkitettavaJaKopioitava<T, C>, C extends Collection<T>>
    extends Linkitettava<T>, Kopioitava<T, C> {}
