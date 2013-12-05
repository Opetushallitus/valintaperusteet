package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;

import java.util.List;
import java.util.Set;

public interface OrganisaatioDAO extends JpaDAO<Organisaatio, Long> {
    Organisaatio readByOid(String oid);

    List<Organisaatio> readByOidList(Set<String> oids);
}
