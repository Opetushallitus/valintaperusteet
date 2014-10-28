package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 15.1.2013 Time: 17.20 To
 * change this template use File | Settings | File Templates.
 */
public interface JarjestyskriteeriDAO extends JpaDAO<Jarjestyskriteeri, Long> {
    /**
     * Palauttaa valintatapajonon jarjestyskriteerit prioriteettijarjestyksessa
     * 
     * @param oid
     *            Valintatapajono
     * @return Lista prioriteettijarjestyksessa
     */
    List<Jarjestyskriteeri> findByJono(String oid);

    List<Jarjestyskriteeri> findByHakukohde(String oid);

    Jarjestyskriteeri readByOid(String oid);

    Jarjestyskriteeri haeValintatapajononViimeinenJarjestyskriteeri(String valintatapajonoOid);

    List<Jarjestyskriteeri> findByLaskentakaava(long id);
}
