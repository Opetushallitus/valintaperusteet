package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintakoeService;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.VaaranTyyppinenLaskentakaavaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.13
 */
@Transactional
@Service
public class ValintakoeServiceImpl extends AbstractCRUDServiceImpl<Valintakoe, Long, String> implements ValintakoeService {

    @Autowired
    private ValintakoeDAO valintakoeDAO;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    public ValintakoeServiceImpl(ValintakoeDAO dao) {
        super(dao);
        this.valintakoeDAO = dao;
    }

    @Override
    public Valintakoe update(String oid, Valintakoe valintakoe) {
        Valintakoe entity = valintakoeDAO.readByOid(oid);
        entity.setKuvaus(valintakoe.getKuvaus());
        entity.setNimi(valintakoe.getNimi());
        entity.setTunniste(valintakoe.getTunniste());
        return entity;
    }

    @Override
    public Valintakoe insert(Valintakoe entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteByOid(String oid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Valintakoe readByOid(String oid) {
        return valintakoeDAO.readByOid(oid);
    }

    @Override
    public List<Valintakoe> findValintakoeByValinnanVaihe(String oid) {
        return valintakoeDAO.findByValinnanVaihe(oid);
    }

    @Override
    public Valintakoe lisaaValintakoeValinnanVaiheelle(String valinnanVaiheOid, ValintakoeDTO koe) {
        ValinnanVaihe valinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
        if (!ValinnanVaiheTyyppi.VALINTAKOE.equals(valinnanVaihe.getValinnanVaiheTyyppi())) {
            throw new ValintakoettaEiVoiLisataException("Valintakoetta ei voi lisätä valinnan vaiheelle, jonka " +
                    "tyyppi on " + valinnanVaihe.getValinnanVaiheTyyppi().name());
        }

        Valintakoe valintakoe = new Valintakoe();
        valintakoe.setOid(oidService.haeValintakoeOid());
        valintakoe.setTunniste(koe.getTunniste());
        valintakoe.setNimi(koe.getNimi());
        valintakoe.setKuvaus(koe.getKuvaus());
        valintakoe.setValinnanVaihe(valinnanVaihe);

        if(koe.getLaskentakaavaId() != null) {
            Laskentakaava laskentakaava = laskentakaavaDAO.getLaskentakaava(koe.getLaskentakaavaId());
            if (laskentakaava == null) {
                throw new LaskentakaavaEiOleOlemassaException("Laskentakaavaa (" + koe.getLaskentakaavaId() + ") ei ole " +
                        "olemassa", koe.getLaskentakaavaId());
            } else if (!Funktiotyyppi.TOTUUSARVOFUNKTIO.equals(laskentakaava.getTyyppi())) {
                throw new VaaranTyyppinenLaskentakaavaException("Valintakokeen laskentakaavan tulee olla tyyppiä " +
                        Funktiotyyppi.TOTUUSARVOFUNKTIO.name());
            } else if (laskentakaava.getOnLuonnos()) {
                throw new ValintakokeeseenLiitettavaLaskentakaavaOnLuonnosException("Valintakokeeseen liitettävä " +
                        "laskentakaava on LUONNOS-tilassa");
            }
            valintakoe.setLaskentakaava(laskentakaava);
        }
        Valintakoe lisatty = valintakoeDAO.insert(valintakoe);
        return lisatty;
    }
}
