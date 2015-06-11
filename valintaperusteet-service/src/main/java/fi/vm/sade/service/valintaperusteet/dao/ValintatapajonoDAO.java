package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.Collection;
import java.util.List;

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
