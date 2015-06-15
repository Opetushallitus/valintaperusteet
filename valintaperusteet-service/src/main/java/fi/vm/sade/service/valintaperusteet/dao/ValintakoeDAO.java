package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.Valintakoe;

import java.util.Collection;
import java.util.List;

public interface ValintakoeDAO extends JpaDAO<Valintakoe, Long> {
    List<Valintakoe> findByValinnanVaihe(String valinnanVaiheOid);

    Valintakoe readByOid(String oid);

    List<Valintakoe> readByOids(Collection<String> oids);

    List<Valintakoe> readByTunnisteet(Collection<String> tunnisteet);

    List<Valintakoe> findByLaskentakaava(long id);
}
