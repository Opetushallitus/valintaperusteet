package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 15.1.2013
 * Time: 17.20
 * To change this template use File | Settings | File Templates.
 */
public interface ValintatapajonoDAO extends JpaDAO<Valintatapajono, Long> {
    List<Valintatapajono> findByValinnanVaihe(String oid);

    Valintatapajono readByOid(String oid);

    List<Valintatapajono> haeValintatapajonotSijoittelulle(String hakukohdeOid);

    List<Valintatapajono> haeValintatapajonotHakukohteelle(String hakukohdeOid);

    Valintatapajono haeValinnanVaiheenViimeinenValintatapajono(String valinnanVaiheOid);

    List<Valintatapajono> haeKopiot(String oid);

    List<Valintatapajono> haeKopiotValisijoittelulle(String oid);

    List<Valintatapajono> ilmanLaskentaaOlevatHakukohteelle(String hakukohdeOid);
}
