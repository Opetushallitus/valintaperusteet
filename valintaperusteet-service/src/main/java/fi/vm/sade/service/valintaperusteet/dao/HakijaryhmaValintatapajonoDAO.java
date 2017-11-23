package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;

import java.util.Collection;
import java.util.List;

public interface HakijaryhmaValintatapajonoDAO extends JpaDAO<HakijaryhmaValintatapajono, Long> {
    HakijaryhmaValintatapajono readByOid(String oid);

    List<HakijaryhmaValintatapajono> findByValintatapajono(String oid);

    List<HakijaryhmaValintatapajono> findByValintatapajonos(List<String> oids);

    List<HakijaryhmaValintatapajono> findByHakukohde(String oid);

    List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> oids);

    List<HakijaryhmaValintatapajono> findByHakukohteetWithValintatapajono(Collection<String> hakukohdeOids);

    HakijaryhmaValintatapajono haeHakukohteenViimeinenHakijaryhma(String hakukohdeOid);

    HakijaryhmaValintatapajono haeValintatapajononViimeinenHakijaryhma(String valintatapajonoOid);
}
