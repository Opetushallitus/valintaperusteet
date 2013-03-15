package fi.vm.sade.service.valintaperusteet.model;

/**
 * User: kwuoti
 * Date: 11.2.2013
 * Time: 14.13
 */
public interface Linkitettava<T extends Linkitettava> {

    String getOid();
    T getEdellinen();
    T getSeuraava();
    void setEdellinen(T edellinen);
    void setSeuraava(T seuraava);

}
