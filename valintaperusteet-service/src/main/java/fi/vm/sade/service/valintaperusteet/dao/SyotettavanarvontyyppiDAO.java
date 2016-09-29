package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Syotettavanarvontyyppi;

import java.util.List;

public interface SyotettavanarvontyyppiDAO extends JpaDAO<Syotettavanarvontyyppi, Long>, KoodiDAO<Syotettavanarvontyyppi> {

    Syotettavanarvontyyppi readByUri(String koodiUri);

    List<Syotettavanarvontyyppi> findByUris(String[] koodiUris);

    Syotettavanarvontyyppi insertOrUpdate(Syotettavanarvontyyppi koodi);
}