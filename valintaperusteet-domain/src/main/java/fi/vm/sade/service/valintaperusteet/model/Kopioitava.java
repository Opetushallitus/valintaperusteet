package fi.vm.sade.service.valintaperusteet.model;

public interface Kopioitava<T> {
  String getOid();

  void setMaster(T master);

  T getMaster();

  Iterable<T> getKopiot();
}
