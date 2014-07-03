package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 15.1.2013
 * Time: 17.20
 * To change this template use File | Settings | File Templates.
 */
public interface HakijaryhmaDAO extends JpaDAO<Hakijaryhma, Long> {
    Hakijaryhma readByOid(String oid);

    List<Hakijaryhma> findByValintatapajono(String oid);

    List<Hakijaryhma> findByHakukohde(String oid);

    List<Hakijaryhma> findByValintaryhma(String oid);

    Hakijaryhma haeValintaryhmanViimeinenHakijaryhma(String valintaryhmaOid);

    Hakijaryhma haeHakukohteenViimeinenHakijaryhma(String hakukohdeOid);

    Hakijaryhma haeValintatapajononViimeinenHakijaryhma(String hakukohdeOid);
}
