package fi.vm.sade.service.valintaperusteet.model;

public interface Linkitettava<T extends Linkitettava> {
    String getOid();

    T getEdellinen();

    T getSeuraava();

    void setEdellinen(T edellinen);

    void setSeuraava(T seuraava);
}
