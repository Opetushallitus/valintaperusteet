package fi.vm.sade.service.valintaperusteet.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.JarjestyskriteeriService;
import fi.vm.sade.service.valintaperusteet.service.LaskentakaavaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintalaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaOidTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.VaaranTyyppinenLaskentakaavaException;
import fi.vm.sade.service.valintaperusteet.util.JarjestyskriteeriKopioija;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 14.44 To
 * change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class JarjestyskriteeriServiceImpl implements JarjestyskriteeriService {
    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private JarjestyskriteeriDAO jarjestyskriteeriDAO;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    private static JarjestyskriteeriKopioija kopioija = new JarjestyskriteeriKopioija();

    private Jarjestyskriteeri haeJarjestyskriteeri(String oid) {
        Jarjestyskriteeri jarjestyskriteeri = jarjestyskriteeriDAO.readByOid(oid);
        if (jarjestyskriteeri == null) {
            throw new JarjestyskriteeriEiOleOlemassaException("Järjestyskriteeri (" + oid + ") ei ole olemassa", oid);
        }

        return jarjestyskriteeri;
    }

    private void validoiFunktiokutsuJarjestyskriteeriaVarten(Funktiokutsu funktiokutsu) {
        if (funktiokutsu != null) {
            if (!funktiokutsu.getFunktionimi().getLaskentamoodit().contains(Laskentamoodi.VALINTALASKENTA)) {
                throw new FunktiokutsuaEiVoidaKayttaaValintalaskennassaException("Funktiokutsua "
                        + funktiokutsu.getFunktionimi().name() + ", id " + funktiokutsu.getId()
                        + " ei voida käyttää valintalaskennassa.", funktiokutsu.getId(), funktiokutsu.getFunktionimi());
            }

            for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
                if (arg.getFunktiokutsuChild() != null) {
                    validoiFunktiokutsuJarjestyskriteeriaVarten(arg.getFunktiokutsuChild());
                } else if (arg.getLaskentakaavaChild() != null) {
                    validoiFunktiokutsuJarjestyskriteeriaVarten(arg.getLaskentakaavaChild().getFunktiokutsu());
                }
            }
        }
    }

    private void validoiLaskentakaavaJarjestyskriteeriaVarten(Laskentakaava laskentakaava) {
        if (laskentakaava == null) {
            throw new LaskentakaavaEiOleOlemassaException("Laskentakaavaa ei ole olemassa", null);
        }
// VT-938
//        else if (!Funktiotyyppi.LUKUARVOFUNKTIO.equals(laskentakaava.getTyyppi())) {
//            throw new VaaranTyyppinenLaskentakaavaException("Järjestyskriteerin laskentakaavan tulee olla tyyppiä "
//                    + Funktiotyyppi.LUKUARVOFUNKTIO.name());
//        }

        // Laskentakaavaa ei voi tällä hetkellä tallentaa luonnoksena
//        else if (laskentakaava.getOnLuonnos()) {
//            throw new JarjestyskriteeriinLiitettavaLaskentakaavaOnLuonnosException("Luonnos-tilassa olevaa"
//                    + " laskentakaavaa ei voi liittää " + "valintatapajonoon", laskentakaava.getId());
//        }

        validoiFunktiokutsuJarjestyskriteeriaVarten(laskentakaava.getFunktiokutsu());
    }

    @Override
    public Jarjestyskriteeri update(String oid, JarjestyskriteeriCreateDTO dto, Long laskentakaavaId) {

        Jarjestyskriteeri entity = modelMapper.map(dto, Jarjestyskriteeri.class);
        Jarjestyskriteeri managedObject = haeJarjestyskriteeri(oid);

        if (laskentakaavaId != null) {
            Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(laskentakaavaId);
            validoiLaskentakaavaJarjestyskriteeriaVarten(laskentakaava);
            entity.setLaskentakaava(laskentakaava);
        } else {
            throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
        }

        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, entity, kopioija);
    }

    @Override
    public List<Jarjestyskriteeri> findJarjestyskriteeriByJono(String oid) {

        return LinkitettavaJaKopioitavaUtil.jarjesta(jarjestyskriteeriDAO.findByJono(oid));
    }

    @Override
    public List<Jarjestyskriteeri> findByHakukohde(String oid) {
        return jarjestyskriteeriDAO.findByHakukohde(oid);
    }

    @Override
    public Jarjestyskriteeri lisaaJarjestyskriteeriValintatapajonolle(String valintatapajonoOid,
            JarjestyskriteeriCreateDTO dto, String edellinenValintatapajonoOid, Long laskentakaavaOid) {

        Jarjestyskriteeri jarjestyskriteeri = modelMapper.map(dto, Jarjestyskriteeri.class);
        if (laskentakaavaOid != null) {
            Laskentakaava laskentakaava = laskentakaavaDAO.getLaskentakaava(laskentakaavaOid);
            validoiLaskentakaavaJarjestyskriteeriaVarten(laskentakaava);
            jarjestyskriteeri.setLaskentakaava(laskentakaava);
        } else {
            throw new LaskentakaavaOidTyhjaException("LaskentakaavaOid oli tyhjä.");
        }

        Valintatapajono valintatapajono = valintatapajonoService.readByOid(valintatapajonoOid);

        Jarjestyskriteeri edellinenJarjestyskriteeri = null;
        if (StringUtils.isNotBlank(edellinenValintatapajonoOid)) {
            edellinenJarjestyskriteeri = haeJarjestyskriteeri(edellinenValintatapajonoOid);
        } else {
            edellinenJarjestyskriteeri = jarjestyskriteeriDAO
                    .haeValintatapajononViimeinenJarjestyskriteeri(valintatapajonoOid);
        }

        jarjestyskriteeri.setOid(oidService.haeJarjestyskriteeriOid());
        jarjestyskriteeri.setValintatapajono(valintatapajono);
        jarjestyskriteeri.setEdellinen(edellinenJarjestyskriteeri);

        Jarjestyskriteeri lisatty = jarjestyskriteeriDAO.insert(jarjestyskriteeri);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenJarjestyskriteeri, lisatty);

        for (Valintatapajono kopio : valintatapajono.getKopiot()) {
            lisaaValintatapajonolleKopioMasterJarjestyskriteerista(kopio, lisatty, edellinenJarjestyskriteeri);
        }

        return lisatty;
    }

    private void lisaaValintatapajonolleKopioMasterJarjestyskriteerista(Valintatapajono valintatapajono,
            Jarjestyskriteeri masterJarjestyskriteeri, Jarjestyskriteeri edellinenMasterJarjestyskriteeri) {
        Jarjestyskriteeri kopio = teeKopioMasterista(masterJarjestyskriteeri);

        kopio.setValintatapajono(valintatapajono);
        kopio.setOid(oidService.haeJarjestyskriteeriOid());

        List<Jarjestyskriteeri> jonot = LinkitettavaJaKopioitavaUtil.jarjesta(jarjestyskriteeriDAO
                .findByJono(valintatapajono.getOid()));

        Jarjestyskriteeri edellinenJarjestyskriteeri = LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(
                edellinenMasterJarjestyskriteeri, jonot);

        kopio.setEdellinen(edellinenJarjestyskriteeri);
        Jarjestyskriteeri lisatty = jarjestyskriteeriDAO.insert(kopio);

        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenJarjestyskriteeri, lisatty);

        for (Valintatapajono jonokopio : valintatapajono.getKopiot()) {
            lisaaValintatapajonolleKopioMasterJarjestyskriteerista(jonokopio, lisatty, lisatty.getEdellinen());
        }
    }

    private Jarjestyskriteeri teeKopioMasterista(Jarjestyskriteeri master) {
        Jarjestyskriteeri kopio = new Jarjestyskriteeri();
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setLaskentakaava(master.getLaskentakaava());
        kopio.setMetatiedot(master.getMetatiedot());
        kopio.setMaster(master);
        return kopio;
    }

    @Override
    public List<Jarjestyskriteeri> jarjestaKriteerit(List<String> oids) {
        if (oids.isEmpty()) {
            throw new JarjestyskriteeriOidListaOnTyhjaException("Jarjestyskriteeri OID-lista on tyhjä");
        }

        Jarjestyskriteeri ensimmainen = haeJarjestyskriteeri(oids.get(0));
        return jarjestaKriteerit(ensimmainen.getValintatapajono(), oids);
    }

    private List<Jarjestyskriteeri> jarjestaKriteerit(Valintatapajono jono, List<String> oids) {
        LinkedHashMap<String, Jarjestyskriteeri> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil
                .teeMappiOidienMukaan(LinkitettavaJaKopioitavaUtil.jarjesta(jarjestyskriteeriDAO.findByJono(jono
                        .getOid())));

        LinkedHashMap<String, Jarjestyskriteeri> jarjestetty = LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(
                alkuperainenJarjestys, oids);

        for (Valintatapajono kopio : jono.getKopiot()) {
            jarjestaKopioValintatapajononKriteerit(kopio, jarjestetty);
        }

        return new ArrayList<Jarjestyskriteeri>(jarjestetty.values());
    }

    private void jarjestaKopioValintatapajononKriteerit(Valintatapajono jono,
            LinkedHashMap<String, Jarjestyskriteeri> uusiMasterJarjestys) {
        LinkedHashMap<String, Jarjestyskriteeri> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil
                .teeMappiOidienMukaan(LinkitettavaJaKopioitavaUtil.jarjesta(jarjestyskriteeriDAO.findByJono(jono
                        .getOid())));

        LinkedHashMap<String, Jarjestyskriteeri> jarjestetty = LinkitettavaJaKopioitavaUtil
                .jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys, uusiMasterJarjestys);

        for (Valintatapajono kopio : jono.getKopiot()) {
            jarjestaKopioValintatapajononKriteerit(kopio, jarjestetty);
        }
    }

    @Override
    public void deleteByOid(String oid) {
        Jarjestyskriteeri jarjestyskriteeri = haeJarjestyskriteeri(oid);

        if (jarjestyskriteeri.getMaster() != null) {
            throw new JarjestyskriteeriaEiVoiPoistaaException("Jarjestyskriteeri on peritty.");
        }

        delete(jarjestyskriteeri);
    }

    @Override
    public void delete(Jarjestyskriteeri jarjestyskriteeri) {
        for (Jarjestyskriteeri jk : jarjestyskriteeri.getKopiot()) {
            delete(jk);
        }

        if (jarjestyskriteeri.getSeuraava() != null) {
            Jarjestyskriteeri seuraava = jarjestyskriteeri.getSeuraava();
            seuraava.setEdellinen(jarjestyskriteeri.getEdellinen());
        }

        jarjestyskriteeriDAO.remove(jarjestyskriteeri);
    }

    @Override
    public Jarjestyskriteeri readByOid(String oid) {
        return haeJarjestyskriteeri(oid);
    }

    @Override
    public void kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(Valintatapajono valintatapajono,
            Valintatapajono masterValintatapajono) {
        Jarjestyskriteeri jk = jarjestyskriteeriDAO.haeValintatapajononViimeinenJarjestyskriteeri(masterValintatapajono
                .getOid());
        kopioiJarjestyskriteeritRekursiivisesti(valintatapajono, jk);
    }

    private Jarjestyskriteeri kopioiJarjestyskriteeritRekursiivisesti(Valintatapajono valintatapajono,
            Jarjestyskriteeri master) {
        if (master == null) {
            return null;
        }

        Jarjestyskriteeri kopio = teeKopioMasterista(master);
        kopio.setOid(oidService.haeJarjestyskriteeriOid());
        valintatapajono.addJarjestyskriteeri(kopio);
        Jarjestyskriteeri edellinen = kopioiJarjestyskriteeritRekursiivisesti(valintatapajono, master.getEdellinen());
        if (edellinen != null) {
            kopio.setEdellinen(edellinen);
            edellinen.setSeuraava(kopio);
        }

        return jarjestyskriteeriDAO.insert(kopio);
    }
}
