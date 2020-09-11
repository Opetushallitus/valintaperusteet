package fi.vm.sade.service.valintaperusteet.model;

public interface Linkitettava<T extends Linkitettava<T>> {
  String getOid();

  T getEdellinen();

  void setEdellinen(T edellinen);
}
