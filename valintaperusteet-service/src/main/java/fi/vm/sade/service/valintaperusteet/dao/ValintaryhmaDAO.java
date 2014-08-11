package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 15.1.2013
 * Time: 17.20
 * To change this template use File | Settings | File Templates.
 */
public interface ValintaryhmaDAO extends JpaDAO<Valintaryhma, Long> {

    /**
     * @param oid
     * @return
     */
    List<Valintaryhma> findChildrenByParentOid(String oid);

    Valintaryhma readByOid(String oid);

    /**
     * Hakee valintaryhmähierarkian annetusta lapsesta ylöspäin. Lapsi tulee mukana.
     *
     * @param childOid
     * @return
     */
    List<Valintaryhma> readHierarchy(String childOid);

    List<Valintaryhma> findAllFetchAlavalintaryhmat();

    List<Valintaryhma> haeHakukohdekoodinJaValintakoekoodienMukaan(String hakukohdekoodiUri,
                                                                   Collection<String> valintakoekoodiUrit);

    List<Valintaryhma> readByHakukohdekoodiUri(String koodiUri);
    List<Valintaryhma> readByHakuoid(String hakuoid);
}
