package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;

public interface OrganisaatioDAO extends JpaDAO<Organisaatio, Long> {
    Organisaatio readByOid(String oid);

}
