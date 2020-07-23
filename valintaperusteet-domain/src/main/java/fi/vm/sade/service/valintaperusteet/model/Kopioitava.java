package fi.vm.sade.service.valintaperusteet.model;

import java.util.Collection;

public interface Kopioitava<T extends Kopioitava, C extends Collection<T>> {
  String getOid();

  void setMaster(T master);

  T getMaster();

  void setKopiot(C kopiot);

  C getKopiot();
}
