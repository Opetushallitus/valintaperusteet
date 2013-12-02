package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.43
 * To change this template use File | Settings | File Templates.
 */
public interface ValintatapajonoService {

    List<Valintatapajono> findJonoByValinnanvaihe(String oid);

    Valintatapajono readByOid(String oid);

    List<Valintatapajono> findAll();

    Valintatapajono lisaaValintatapajonoValinnanVaiheelle(String valinnanVaiheOid, ValintatapajonoCreateDTO jono, String edellinenValintatapajonoOid);

    void deleteByOid(String oid);

    List<Valintatapajono> jarjestaValintatapajonot(List<String> valintatapajonoOidit);

    void kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(ValinnanVaihe valinnanVaihe, ValinnanVaihe masterValinnanVaihe);

    Valintatapajono update(String oid, ValintatapajonoCreateDTO jono);
}
