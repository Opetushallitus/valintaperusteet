package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.model.*;

import java.util.List;

public interface HakijaryhmaService extends CRUDService<Hakijaryhma, Long, String> {

    void deleteByOid(String oid);

    List<Hakijaryhma> findHakijaryhmaByJono(String oid);

    List<Hakijaryhma> findByHakukohde(String oid);

    Hakijaryhma readByOid(String oid);

    void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid);

    Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, Hakijaryhma hakijaryhma);

    Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, Hakijaryhma hakijaryhma);

//    void kopioiHakijaryhmatMasterValintatapajonoltaKopiolle(Valintatapajono lisatty, Valintatapajono master);

    List<Hakijaryhma> jarjestaHakijaryhmat(List<String> oids);
}
