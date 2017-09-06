package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.JuureenKopiointiCache;

import java.util.List;
import java.util.Map;

public interface ValintatapajonoService {
    List<Valintatapajono> findJonoByValinnanvaihe(String oid);

    Map<String, List<String>> findKopiot(List<String> oidit);

    Valintatapajono readByOid(String oid);

    List<Valintatapajono> findAll();

    Valintatapajono lisaaValintatapajonoValinnanVaiheelle(String valinnanVaiheOid, ValintatapajonoCreateDTO jono, String edellinenValintatapajonoOid);

    void deleteByOid(String oid);

    List<Valintatapajono> jarjestaValintatapajonot(List<String> valintatapajonoOidit);

    void kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(ValinnanVaihe valinnanVaihe, ValinnanVaihe masterValinnanVaihe, JuureenKopiointiCache kopiointiCache);

    Valintatapajono update(String oid, ValintatapajonoCreateDTO jono);

    void delete(Valintatapajono valintatapajono);

    Boolean readAutomaattinenSijoitteluunSiirto(String oid);

    Valintatapajono updateAutomaattinenSijoitteluunSiirto(String oid, Boolean value);
}
