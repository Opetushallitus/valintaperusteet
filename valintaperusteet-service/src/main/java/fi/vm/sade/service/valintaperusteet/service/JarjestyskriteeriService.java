package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.43
 * To change this template use File | Settings | File Templates.
 */
public interface JarjestyskriteeriService extends CRUDService<Jarjestyskriteeri, Long, String>{

    List<Jarjestyskriteeri> findJarjestyskriteeriByJono(String oid);

    List<Jarjestyskriteeri> findByHakukohde(String oid);

    Jarjestyskriteeri insert(Jarjestyskriteeri jarjestyskriteeri, String valintatapajono, Long laskentakaava);

    void deleteByOid(String oid);

    Jarjestyskriteeri readByOid(String oid);

    Jarjestyskriteeri lisaaJarjestyskriteeriValintatapajonolle(String valintatapajonoOid, Jarjestyskriteeri jarjestyskriteeri,
                                                                      String edellinenValintatapajonoOid, Long laskentakaavaOid);

    List<Jarjestyskriteeri> jarjestaKriteerit(List<String> oids);

    void kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(Valintatapajono lisatty, Valintatapajono master);
}
