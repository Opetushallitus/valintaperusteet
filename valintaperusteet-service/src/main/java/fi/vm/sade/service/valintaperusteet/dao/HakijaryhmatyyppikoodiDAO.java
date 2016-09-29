package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhmatyyppikoodi;

import java.util.List;

public interface HakijaryhmatyyppikoodiDAO extends JpaDAO<Hakijaryhmatyyppikoodi, Long>, KoodiDAO<Hakijaryhmatyyppikoodi> {

    Hakijaryhmatyyppikoodi readByUri(String koodiUri);

    List<Hakijaryhmatyyppikoodi> findByUris(String[] koodiUris);

    Hakijaryhmatyyppikoodi insertOrUpdate(Hakijaryhmatyyppikoodi koodi);
}
