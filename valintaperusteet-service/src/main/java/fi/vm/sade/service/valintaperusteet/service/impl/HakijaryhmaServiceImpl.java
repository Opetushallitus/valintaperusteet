package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HakijaryhmaServiceImpl implements HakijaryhmaService {
    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private HakijaryhmaDAO hakijaryhmatyyppikoodiDAO;

    @Autowired
    private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintapajonoService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    @Autowired
    private HakijaryhmatyyppikoodiService hakijaryhmatyyppikoodiService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private OidService oidService;

    private Hakijaryhma haeHakijaryhma(String oid) {
        Hakijaryhma hakijaryhma = hakijaryhmaDAO.readByOid(oid);

        if (hakijaryhma == null) {
            throw new HakijaryhmaEiOleOlemassaException("Hakijaryhma (" + oid + ") ei ole olemassa", oid);
        }

        return hakijaryhma;
    }

    @Override
    public void deleteByOid(String oid, boolean skipInheritedCheck) {
        Hakijaryhma hakijaryhma = haeHakijaryhma(oid);

        if (!skipInheritedCheck && hakijaryhma.getJonot() != null && !hakijaryhma.getJonot().isEmpty()) {
            throw new HakijaryhmaaEiVoiPoistaaException("hakijaryhma on peritty.");
        }

        hakijaryhmaDAO.remove(hakijaryhma);
    }

    @Override
    public List<Hakijaryhma> findByHakukohde(String oid) {
        List<HakijaryhmaValintatapajono> byHakukohde = hakijaryhmaValintatapajonoDAO.findByHakukohde(oid);
        List<HakijaryhmaValintatapajono> jarjestetty = LinkitettavaJaKopioitavaUtil.jarjesta(byHakukohde);
        return jarjestetty.stream().map(HakijaryhmaValintatapajono::getHakijaryhma).collect(Collectors.toList());
    }

    @Override
    public List<Hakijaryhma> findByValintaryhma(String oid) {
        List<Hakijaryhma> byValintaryhma = hakijaryhmaDAO.findByValintaryhma(oid);
        return byValintaryhma;
    }

    @Override
    public Hakijaryhma readByOid(String oid) {
        return haeHakijaryhma(oid);
    }

    @Override
    public void liitaHakijaryhmaValintatapajonolle(String valintatapajonoOid, String hakijaryhmaOid) {
        hakijaryhmaValintatapajonoService.liitaHakijaryhmaValintatapajonolle(valintatapajonoOid, hakijaryhmaOid);
    }

    private void kopioHakijaryhmat(Valintatapajono valintatapajono, Hakijaryhma hakijaryhma, HakijaryhmaValintatapajono master) {
        for (Valintatapajono kopio : valintatapajono.getKopiot()) {
            HakukohdeViite kopioHakukohdeViite = kopio.getValinnanVaihe().getHakukohdeViite();
            Valintaryhma kopioValintaryhma = kopio.getValinnanVaihe().getValintaryhma();
            if (kopioHakukohdeViite == null && kopioValintaryhma == null) {
                throw new ValinnanvaiheellaEiOleHakukohdettaTaiValintaryhmaaException("");
            }
            HakijaryhmaValintatapajono kopioLink = new HakijaryhmaValintatapajono();
            for (HakijaryhmaValintatapajono hrKopio : master.getKopiot()) {
                if (kopioValintaryhma != null && hrKopio.getHakijaryhma().getValintaryhma() != null
                        && hrKopio.getHakijaryhma().getValintaryhma().getOid().equals(kopioValintaryhma.getOid())) {
                    kopioLink.setHakijaryhma(hrKopio.getHakijaryhma());
                } else if (kopioHakukohdeViite != null && hrKopio.getHakukohdeViite() != null
                        && hrKopio.getHakukohdeViite().getOid().equals(kopioHakukohdeViite.getOid())) {
                    kopioLink.setHakijaryhma(hrKopio.getHakijaryhma());
                }
            }
            if (hakijaryhmaValintatapajonoDAO.readByOid(kopioLink.getHakijaryhma().getOid() + "_" + kopio.getOid()) != null) {
                // Hakijaryhma on jo liitetty aikaisemmin lapselle..
                continue;
            }
            if (kopioLink.getHakijaryhma() == null) {
                throw new HakijaryhmanKopiotaEiLoytynytException("");
            }
            kopioLink.setValintatapajono(kopio);
            kopioLink.setOid(kopioLink.getHakijaryhma().getOid() + "_" + kopio.getOid());
            kopioLink.setAktiivinen(true);
            kopioLink.setMaster(master);
            kopioLink.setKiintio(master.getKiintio());
            kopioLink = hakijaryhmaValintatapajonoDAO.insert(kopioLink);
            kopioHakijaryhmat(kopio, kopioLink.getHakijaryhma(), kopioLink);
        }
    }

    @Override
    public Hakijaryhma lisaaHakijaryhmaValintaryhmalle(String valintaryhmaOid, HakijaryhmaCreateDTO dto) {
        Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
        if (valintaryhma == null) {
            throw new ValintaryhmaEiOleOlemassaException("Valintaryhmää (" + valintaryhmaOid + ") ei ole olemassa");
        }
        if (dto.getLaskentakaavaId() == null) {
            throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
        }
        Hakijaryhma hakijaryhma = modelMapper.map(dto, Hakijaryhma.class);
        hakijaryhma.setOid(oidService.haeHakijaryhmaOid());
        hakijaryhma.setValintaryhma(valintaryhma);
        hakijaryhma.setKaytaKaikki(dto.isKaytaKaikki());
        hakijaryhma.setTarkkaKiintio(dto.isTarkkaKiintio());
        hakijaryhma.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
        hakijaryhma.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(hakijaryhma.getLaskentakaavaId()));
        hakijaryhma.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(dto.getHakijaryhmatyyppikoodi()));
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(hakijaryhma);
        valintaryhma.getHakukohdeViitteet().stream().forEach(hk -> {
            hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(hk.getOid(), lisatty.getOid());
        });
        List<Valintaryhma> alaValintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhmaOid);
        for (Valintaryhma alavalintaryhma : alaValintaryhmat) {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(alavalintaryhma, lisatty);
        }
        return lisatty;
    }

    @Override
    public void kopioiHakijaryhmatMasterValintaryhmalta(String parentValintaryhmaOid, String childValintaryhmaoid) {
        Valintaryhma childValintaryhma = valintaryhmaService.readByOid(childValintaryhmaoid);
        hakijaryhmaDAO.findByValintaryhma(parentValintaryhmaOid).stream().forEach(parentHakijaryhma -> {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(childValintaryhma, parentHakijaryhma);
        });
    }


    private void lisaaValintaryhmalleKopioMasterHakijaryhmasta(Valintaryhma valintaryhma, Hakijaryhma masterHakijaryhma) {
        Hakijaryhma kopio = luoKopioHakijaryhmasta(valintaryhma, masterHakijaryhma);
        kopio.setValintaryhma(valintaryhma);
        Hakijaryhma lisatty = hakijaryhmaDAO.insert(kopio);
        List<Valintaryhma> alavalintaryhmat = valintaryhmaService.findValintaryhmasByParentOid(valintaryhma.getOid());
        valintaryhma.getHakukohdeViitteet().stream().forEach(hk -> {
            hakijaryhmaValintatapajonoService.liitaHakijaryhmaHakukohteelle(hk.getOid(), lisatty.getOid());
        });
        alavalintaryhmat.stream().forEach(alavalintaryhma -> {
            lisaaValintaryhmalleKopioMasterHakijaryhmasta(alavalintaryhma, lisatty);
        });
    }

    private Hakijaryhma luoKopioHakijaryhmasta(Valintaryhma kohdeValintaryhma, Hakijaryhma hakijaryhma) {
        Hakijaryhma kopio = new Hakijaryhma();
        kopio.setOid(oidService.haeHakijaryhmaOid());
        kopio.setKiintio(hakijaryhma.getKiintio());
        kopio.setKuvaus(hakijaryhma.getKuvaus());
        kopio.setKaytaKaikki(hakijaryhma.isKaytaKaikki());
        kopio.setLaskentakaava(laskentakaavaService.haeLaskentakaavaTaiSenKopioVanhemmilta(hakijaryhma.getLaskentakaavaId(), null, kohdeValintaryhma).orElse(hakijaryhma.getLaskentakaava()));
        kopio.setHakijaryhmatyyppikoodi(hakijaryhma.getHakijaryhmatyyppikoodi());
        kopio.setNimi(hakijaryhma.getNimi());
        kopio.setTarkkaKiintio(hakijaryhma.isTarkkaKiintio());
        kopio.setKaytetaanRyhmaanKuuluvia(hakijaryhma.isKaytetaanRyhmaanKuuluvia());
        return kopio;
    }

    @Override
    public Hakijaryhma update(String oid, HakijaryhmaCreateDTO dto) {
        if (dto.getLaskentakaavaId() == null) {
            throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
        }
        Hakijaryhma managedObject = haeHakijaryhma(oid);
        managedObject.setKaytaKaikki(dto.isKaytaKaikki());
        managedObject.setKiintio(dto.getKiintio());
        managedObject.setKuvaus(dto.getKuvaus());
        managedObject.setNimi(dto.getNimi());
        managedObject.setTarkkaKiintio(dto.isTarkkaKiintio());
        managedObject.setKaytetaanRyhmaanKuuluvia(dto.isKaytetaanRyhmaanKuuluvia());
        managedObject.setLaskentakaava(laskentakaavaService.haeMallinnettuKaava(dto.getLaskentakaavaId()));
        managedObject.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodiService.getOrCreateHakijaryhmatyyppikoodi(dto.getHakijaryhmatyyppikoodi()));
        hakijaryhmaDAO.update(managedObject);
        return managedObject;
    }

    @Override
    public Optional<Hakijaryhma> siirra(HakijaryhmaSiirraDTO dto) {
        if (dto.getUusinimi() != null) {
            dto.setNimi(dto.getUusinimi());
        }
        Optional<Valintaryhma> ryhma = Optional.ofNullable(valintaryhmaService.readByOid(dto.getValintaryhmaOid()));
        if (!ryhma.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(lisaaHakijaryhmaValintaryhmalle(ryhma.get().getOid(), dto));
    }

    public void deleteHakijaryhmaValintatajono(HakijaryhmaValintatapajono entity) {
        for (HakijaryhmaValintatapajono hakijaryhma : entity.getKopiot()) {
            deleteHakijaryhmaValintatajono(hakijaryhma);
        }
        if (entity.getSeuraava() != null) {
            HakijaryhmaValintatapajono seuraava = entity.getSeuraava();
            seuraava.setEdellinen(entity.getEdellinen());
        }
        hakijaryhmaValintatapajonoDAO.remove(entity);
    }
}
