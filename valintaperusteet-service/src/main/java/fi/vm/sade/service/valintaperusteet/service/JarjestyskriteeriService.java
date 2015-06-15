package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.List;

public interface JarjestyskriteeriService {
    List<Jarjestyskriteeri> findJarjestyskriteeriByJono(String oid);

    List<Jarjestyskriteeri> findByHakukohde(String oid);

    void deleteByOid(String oid);

    Jarjestyskriteeri readByOid(String oid);

    Jarjestyskriteeri lisaaJarjestyskriteeriValintatapajonolle(String valintatapajonoOid, JarjestyskriteeriCreateDTO jarjestyskriteeri, String edellinenValintatapajonoOid, Long laskentakaavaOid);

    List<Jarjestyskriteeri> jarjestaKriteerit(List<String> oids);

    void kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(Valintatapajono lisatty, Valintatapajono master);

    Jarjestyskriteeri update(String oid, JarjestyskriteeriCreateDTO incoming, Long laskentakaavaId);

    void delete(Jarjestyskriteeri jarjestyskriteeri);
}
