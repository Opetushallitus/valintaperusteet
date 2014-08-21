package fi.vm.sade.service.valintaperusteet.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.VaaranTyyppinenLaskentakaavaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValintakoeKopioija;
import fi.vm.sade.service.valintaperusteet.util.ValintakoeUtil;

/**
 * User: kwuoti Date: 15.4.2013 Time: 16.13
 */
@Transactional
@Service
public class ValintakoeServiceImpl implements ValintakoeService {

	@Autowired
	private ValintakoeDAO valintakoeDAO;

	@Autowired
	private ValinnanVaiheService valinnanVaiheService;

	@Autowired
	private LaskentakaavaDAO laskentakaavaDAO;

	@Autowired
	private OidService oidService;

	@Autowired
	private LaskentakaavaService laskentakaavaService;

	private static ValintakoeKopioija kopioija = new ValintakoeKopioija();

	@Override
	public void deleteByOid(String oid) {
		Valintakoe valintakoe = haeValintakoeOidilla(oid);
		if (valintakoe.getMaster() != null) {
			throw new ValintakoettaEiVoiPoistaaException(
					"Valintakoe on peritty.");
		}

		removeValintakoe(valintakoe);
	}

	private void removeValintakoe(Valintakoe valintakoe) {
		for (Valintakoe koe : valintakoe.getKopiot()) {
			removeValintakoe(koe);
		}

		valintakoeDAO.remove(valintakoe);
	}

	@Override
	public Valintakoe readByOid(String oid) {
		return haeValintakoeOidilla(oid);
	}

	@Override
	public List<Valintakoe> readAll() {
		return valintakoeDAO.findAll();
	}

	private Valintakoe haeValintakoeOidilla(String oid) {
		Valintakoe valintakoe = valintakoeDAO.readByOid(oid);
		if (valintakoe == null) {
			throw new ValintakoettaEiOleOlemassaException("Valintakoetta (oid "
					+ oid + ") ei ole olemassa");
		}

		return valintakoe;
	}

	@Override
	public List<Valintakoe> findValintakoeByValinnanVaihe(String oid) {
		return valintakoeDAO.findByValinnanVaihe(oid);
	}

    @Override
    public List<Valintakoe> findValintakoesByValinnanVaihes(List<ValinnanVaihe> vaiheet) {
        List<Valintakoe> kokeet = new ArrayList<Valintakoe>();
        for (ValinnanVaihe vaihe : vaiheet) {
            kokeet.addAll(valintakoeDAO.findByValinnanVaihe(vaihe.getOid()));
        }
        return kokeet;
    }

    @Override
	public Valintakoe lisaaValintakoeValinnanVaiheelle(String valinnanVaiheOid,
			ValintakoeCreateDTO koe) {
		ValinnanVaihe valinnanVaihe = valinnanVaiheService
				.readByOid(valinnanVaiheOid);
		if (!ValinnanVaiheTyyppi.VALINTAKOE.equals(valinnanVaihe
				.getValinnanVaiheTyyppi())) {
			throw new ValintakoettaEiVoiLisataException(
					"Valintakoetta ei voi lisätä valinnan vaiheelle, jonka "
							+ "tyyppi on "
							+ valinnanVaihe.getValinnanVaiheTyyppi().name());
		}

		Valintakoe valintakoe = new Valintakoe();
		valintakoe.setOid(oidService.haeValintakoeOid());
		valintakoe.setTunniste(koe.getTunniste());
		valintakoe.setNimi(koe.getNimi());
		valintakoe.setKuvaus(koe.getKuvaus());
		valintakoe.setValinnanVaihe(valinnanVaihe);
		valintakoe.setAktiivinen(koe.getAktiivinen());
        valintakoe.setLahetetaankoKoekutsut(koe.getLahetetaankoKoekutsut());
        valintakoe.setKutsutaankoKaikki(koe.getKutsutaankoKaikki());
        valintakoe.setKutsuttavienMaara(koe.getKutsuttavienMaara());
        valintakoe.setKutsunKohde(koe.getKutsunKohde());

		if (koe.getLaskentakaavaId() != null) {
			valintakoe.setLaskentakaava(haeLaskentakaavaValintakokeelle(koe
					.getLaskentakaavaId()));
		}
		Valintakoe lisatty = valintakoeDAO.insert(valintakoe);
		for (ValinnanVaihe kopio : valinnanVaihe.getKopiot()) {
			lisaaValinnanVaiheelleKopioMasterValintakokeesta(kopio, lisatty);
		}

		return lisatty;
	}

	private void lisaaValinnanVaiheelleKopioMasterValintakokeesta(
			ValinnanVaihe valinnanVaihe, Valintakoe masterValintakoe) {
		Valintakoe kopio = ValintakoeUtil.teeKopioMasterista(masterValintakoe);
		kopio.setValinnanVaihe(valinnanVaihe);
		kopio.setOid(oidService.haeValintakoeOid());

		Valintakoe lisatty = valintakoeDAO.insert(kopio);

		for (ValinnanVaihe vaihekopio : valinnanVaihe.getKopioValinnanVaiheet()) {
			lisaaValinnanVaiheelleKopioMasterValintakokeesta(vaihekopio,
					lisatty);
		}
	}

	private void validoiFunktiokutsuValintakoettaVarten(
			Funktiokutsu funktiokutsu) {
		if (funktiokutsu != null) {
			if (!funktiokutsu.getFunktionimi().getLaskentamoodit()
					.contains(Laskentamoodi.VALINTAKOELASKENTA)) {
				throw new FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(
						"Funktiokutsua " + funktiokutsu.getFunktionimi().name()
								+ ", id " + funktiokutsu.getId()
								+ " ei voida käyttää valintakoelaskennassa.",
						funktiokutsu.getId(), funktiokutsu.getFunktionimi());
			}

			for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
				if (arg.getFunktiokutsuChild() != null) {
					validoiFunktiokutsuValintakoettaVarten(arg
							.getFunktiokutsuChild());
				} else if (arg.getLaskentakaavaChild() != null) {
					validoiFunktiokutsuValintakoettaVarten(arg
							.getLaskentakaavaChild().getFunktiokutsu());
				}
			}
		}
	}

	private Laskentakaava haeLaskentakaavaValintakokeelle(Long laskentakaavaId) {
		Laskentakaava laskentakaava = laskentakaavaDAO
				.getLaskentakaava(laskentakaavaId);
		if (laskentakaava == null) {
			throw new LaskentakaavaEiOleOlemassaException("Laskentakaavaa ("
					+ laskentakaavaId + ") ei ole " + "olemassa",
					laskentakaavaId);
		}

// VT-938
//        else if (!Funktiotyyppi.TOTUUSARVOFUNKTIO.equals(laskentakaava
//				.getTyyppi())) {
//			throw new VaaranTyyppinenLaskentakaavaException(
//					"Valintakokeen laskentakaavan tulee olla tyyppiä "
//							+ Funktiotyyppi.TOTUUSARVOFUNKTIO.name());
//		}

        // Laskentakaavaa ei voi tällä hetkellä tallentaa luonnoksena
//        else if (laskentakaava.getOnLuonnos()) {
//			throw new ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException(
//					"Valintakokeeseen liitettävä "
//							+ "laskentakaava on LUONNOS-tilassa");
//		}

		validoiFunktiokutsuValintakoettaVarten(laskentakaava.getFunktiokutsu());

		return laskentakaava;
	}

	@Override
	public Valintakoe update(String oid, ValintakoeDTO valintakoe) {
		Valintakoe incoming = new Valintakoe();
		incoming.setAktiivinen(valintakoe.getAktiivinen());
		incoming.setKuvaus(valintakoe.getKuvaus());
		incoming.setNimi(valintakoe.getNimi());
		incoming.setTunniste(valintakoe.getTunniste());
        incoming.setLahetetaankoKoekutsut(valintakoe.getLahetetaankoKoekutsut());
        incoming.setKutsutaankoKaikki(valintakoe.getKutsutaankoKaikki());
        incoming.setKutsuttavienMaara(valintakoe.getKutsuttavienMaara());
        incoming.setKutsunKohde(valintakoe.getKutsunKohde());

		Valintakoe managedObject = haeValintakoeOidilla(oid);
		Long laskentakaavaOid = valintakoe.getLaskentakaavaId();

		if (laskentakaavaOid != null) {
			Laskentakaava laskentakaava = haeLaskentakaavaValintakokeelle(laskentakaavaOid);
			incoming.setLaskentakaava(laskentakaava);
		} else {
			incoming.setLaskentakaava(null);
		}

		return LinkitettavaJaKopioitavaUtil.paivita(managedObject, incoming,
				kopioija);
	}

	@Override
	public void kopioiValintakokeetMasterValinnanVaiheeltaKopiolle(
			ValinnanVaihe valinnanVaihe, ValinnanVaihe masterValinnanVaihe) {
		List<Valintakoe> kokeet = valintakoeDAO
				.findByValinnanVaihe(masterValinnanVaihe.getOid());
		for (Valintakoe master : kokeet) {
			Valintakoe kopio = ValintakoeUtil.teeKopioMasterista(master);
			kopio.setOid(oidService.haeValintakoeOid());
			valinnanVaihe.addValintakoe(kopio);

			valintakoeDAO.insert(kopio);
		}
	}
}
