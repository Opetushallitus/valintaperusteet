package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaValintatapajonoKopioija;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaValintatapajonoUtil;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class HakijaryhmaValintatapajonoServiceImpl implements HakijaryhmaValintatapajonoService {
    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintapajonoService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

    private static HakijaryhmaValintatapajonoKopioija kopioija = new HakijaryhmaValintatapajonoKopioija();

    private HakijaryhmaValintatapajono haeHakijaryhmaValintatapajono(String oid) {
        HakijaryhmaValintatapajono hakijaryhma = hakijaryhmaValintatapajonoDAO.readByOid(oid);
        if (hakijaryhma == null) {
            throw new HakijaryhmaEiOleOlemassaException("Hakijaryhmavalintatapajono (" + oid + ") ei ole olemassa", oid);
        }
        return hakijaryhma;
    }

    @Override
    public List<HakijaryhmaValintatapajono> findHakijaryhmaByJono(String oid) {
        List<HakijaryhmaValintatapajono> byJono = hakijaryhmaValintatapajonoDAO.findByValintatapajono(oid);
        return LinkitettavaJaKopioitavaUtil.jarjesta(byJono);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findHakijaryhmaByJonos(List<String> oids) {
        List<HakijaryhmaValintatapajono> byJonos = hakijaryhmaValintatapajonoDAO.findByValintatapajonos(oids);
        return byJonos;
    }

    @Override
    public HakijaryhmaValintatapajono readByOid(String oid) {
        return haeHakijaryhmaValintatapajono(oid);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHaku(String hakuOid) {
        return hakijaryhmaValintatapajonoDAO.findByHaku(hakuOid);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHakukohteet(Collection<String> hakukohdeOids) {
        return hakijaryhmaValintatapajonoDAO.findByHakukohteet(hakukohdeOids);
    }

    @Override
    public Hakijaryhma lisaaHakijaryhmaValintatapajonolle(String valintatapajonoOid, HakijaryhmaCreateDTO dto) {
        if (dto.getLaskentakaavaId() == null) {
            throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
        }
        Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
        Valintatapajono valintatapajono = valintapajonoService.readByOid(valintatapajonoOid);
        HakijaryhmaValintatapajono edellinenHakijaryhma = hakijaryhmaValintatapajonoDAO.haeValintatapajononViimeinenHakijaryhma(valintatapajonoOid);
        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
        hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
        hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
        hakijaryhma.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
        hakijaryhma.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(dto.getHakijaryhmatyyppikoodi()));
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
        HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
        jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
        jono.setHakijaryhma(hakijaryhma);
        jono.setValintatapajono(valintatapajono);
        jono.setAktiivinen(true);
        jono.setEdellinen(edellinenHakijaryhma);
        jono.setKiintio(hakijaryhma.getKiintio());
        jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
        jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
        jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
        jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
        hakijaryhmaValintatapajonoDAO.insert(jono);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, jono);
        for (Valintatapajono kopio : valintatapajono.getKopioValintatapajonot()) {
            lisaaValintatapajonolleKopioMasterHakijaryhmasta(kopio, jono, edellinenHakijaryhma);
        }
        return lisatty;
    }

    private void lisaaValintatapajonolleKopioMasterHakijaryhmasta(Valintatapajono valintatapajono,
                                                                  HakijaryhmaValintatapajono master, HakijaryhmaValintatapajono edellinenMaster) {
        HakijaryhmaValintatapajono kopio = HakijaryhmaValintatapajonoUtil.teeKopioMasterista(master);
        kopio.setValintatapajono(valintatapajono);
        kopio.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
        List<HakijaryhmaValintatapajono> jonot = LinkitettavaJaKopioitavaUtil.jarjesta(hakijaryhmaValintatapajonoDAO.findByValintatapajono(valintatapajono.getOid()));
        HakijaryhmaValintatapajono edellinen = LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMaster, jonot);
        kopio.setEdellinen(edellinen);
        HakijaryhmaValintatapajono lisatty = hakijaryhmaValintatapajonoDAO.insert(kopio);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinen, lisatty);
        for (Valintatapajono jonokopio : valintatapajono.getKopioValintatapajonot()) {
            lisaaValintatapajonolleKopioMasterHakijaryhmasta(jonokopio, lisatty, lisatty.getEdellinen());
        }
    }

    @Override
    public Hakijaryhma lisaaHakijaryhmaHakukohteelle(String hakukohdeOid, HakijaryhmaCreateDTO dto) {
        if (dto.getLaskentakaavaId() == null) {
            throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
        }
        Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
        HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
        HakijaryhmaValintatapajono edellinenHakijaryhma = hakijaryhmaValintatapajonoDAO.haeHakukohteenViimeinenHakijaryhma(hakukohdeOid);
        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
        hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
        hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
        hakijaryhma.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
        hakijaryhma.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(dto.getHakijaryhmatyyppikoodi()));
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
        HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
        jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
        jono.setHakijaryhma(hakijaryhma);
        jono.setHakukohdeViite(hakukohde);
        jono.setAktiivinen(true);
        jono.setEdellinen(edellinenHakijaryhma);
        jono.setKiintio(hakijaryhma.getKiintio());
        jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
        jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
        jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
        jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
        hakijaryhmaValintatapajonoDAO.insert(jono);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, jono);
        return lisatty;
    }


    @Override
    public void deleteByOid(String oid, boolean skipInheritedCheck) {
        HakijaryhmaValintatapajono hakijaryhmaValintatapajono = hakijaryhmaValintatapajonoDAO.readByOid(oid);
        if (!skipInheritedCheck && hakijaryhmaValintatapajono.getMaster() != null) {
            throw new HakijaryhmaaEiVoiPoistaaException("hakijaryhma on peritty.");
        }
        delete(hakijaryhmaValintatapajono);
    }

    // CRUD
    @Override
    public HakijaryhmaValintatapajono update(String oid, HakijaryhmaValintatapajonoDTO dto) {
        HakijaryhmaValintatapajono managedObject = haeHakijaryhmaValintatapajono(oid);
        HakijaryhmaValintatapajono updatedJono =  new HakijaryhmaValintatapajono();
        updatedJono.setOid(dto.getOid());
        updatedJono.setAktiivinen(dto.getAktiivinen());
        updatedJono.setKiintio(dto.getKiintio());
        updatedJono.setKaytaKaikki(dto.isKaytaKaikki());
        updatedJono.setTarkkaKiintio(dto.isTarkkaKiintio());
        updatedJono.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
        updatedJono.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(dto.getHakijaryhmatyyppikoodi()));
        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, updatedJono, kopioija);
    }

    @Override
    public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {
        Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(hakijaryhmaOid);
        Valintatapajono valintatapajono = valintapajonoService.readByOid(valintatapajonoOid);
        HakijaryhmaValintatapajono edellinenHakijaryhma = hakijaryhmaValintatapajonoDAO.haeValintatapajononViimeinenHakijaryhma(valintatapajonoOid);
        HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
        jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
        jono.setHakijaryhma(hakijaryhma);
        jono.setValintatapajono(valintatapajono);
        jono.setAktiivinen(true);
        jono.setEdellinen(edellinenHakijaryhma);
        jono.setKiintio(hakijaryhma.getKiintio());
        jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
        jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
        jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
        jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
        hakijaryhmaValintatapajonoDAO.insert(jono);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, jono);
        for (Valintatapajono kopio : valintatapajono.getKopioValintatapajonot()) {
            lisaaValintatapajonolleKopioMasterHakijaryhmasta(kopio, jono, edellinenHakijaryhma);
        }
    }

    @Override
    public void liitaHakijaryhmaHakukohteelle(String hakukohdeOid, String hakijaryhmaOid) {
        Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(hakijaryhmaOid);
        HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
        HakijaryhmaValintatapajono edellinenHakijaryhma = hakijaryhmaValintatapajonoDAO.haeHakukohteenViimeinenHakijaryhma(hakukohdeOid);
        HakijaryhmaValintatapajono jono = new HakijaryhmaValintatapajono();
        jono.setOid(oidService.haeValintatapajonoHakijaryhmaOid());
        jono.setHakijaryhma(hakijaryhma);
        jono.setHakukohdeViite(hakukohde);
        jono.setAktiivinen(true);
        jono.setEdellinen(edellinenHakijaryhma);
        jono.setKiintio(hakijaryhma.getKiintio());
        jono.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
        jono.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
        jono.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
        jono.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
        hakijaryhmaValintatapajonoDAO.insert(jono);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenHakijaryhma, jono);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHakukohde(String oid) {
        List<HakijaryhmaValintatapajono> byHakukohde = hakijaryhmaValintatapajonoDAO.findByHakukohde(oid);
        return LinkitettavaJaKopioitavaUtil.jarjesta(byHakukohde);
    }

    private void delete(HakijaryhmaValintatapajono entity) {
        for (HakijaryhmaValintatapajono hakijaryhma : entity.getKopiot()) {
            delete(hakijaryhma);
        }
        if (entity.getSeuraava() != null) {
            HakijaryhmaValintatapajono seuraava = entity.getSeuraava();
            seuraava.setEdellinen(entity.getEdellinen());
        }
        hakijaryhmaValintatapajonoDAO.remove(entity);
    }
}
