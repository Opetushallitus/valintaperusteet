package fi.vm.sade.service.valintaperusteet.service;

import java.util.Collection;
import java.util.List;

import fi.vm.sade.service.valintaperusteet.dto.ValintakoeCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;

/**
 * User: kwuoti Date: 15.4.2013 Time: 16.05
 */
public interface ValintakoeService {

	void deleteByOid(String oid);

	Valintakoe readByOid(String oid);

	List<Valintakoe> readByOids(Collection<String> oids);

	List<Valintakoe> readByTunnisteet(Collection<String> tunnisteet);

	List<Valintakoe> readAll();

	List<Valintakoe> findValintakoeByValinnanVaihe(String oid);

	List<Valintakoe> findValintakoesByValinnanVaihes(List<ValinnanVaihe> vaiheet);

	Valintakoe lisaaValintakoeValinnanVaiheelle(String valinnanVaiheOid,
			ValintakoeCreateDTO koe);

	Valintakoe update(String oid, ValintakoeDTO valintakoe);

	void kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(
			ValinnanVaihe valinnanVaihe, ValinnanVaihe masterValinnanVaihe);
}
