package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValintatapajonoKopioija;
import fi.vm.sade.service.valintaperusteet.util.ValintatapajonoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ValintatapajonoServiceImpl implements ValintatapajonoService {
    final static private Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoService.class.getName());

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private OidService oidService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    HakukohdeViiteDAO hakukohdeDao;

    private static ValintatapajonoKopioija kopioija = new ValintatapajonoKopioija();

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Override
    public List<Valintatapajono> findJonoByValinnanvaihe(String oid) {
        return LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(oid));
    }

    @Override
    public Map<String, List<String>> findKopiot(List<String> oidit) {
        Map<String, List<String>> jonot = new ConcurrentHashMap<>();
        oidit.forEach(oid -> {
            Optional<Valintatapajono> jono = Optional.ofNullable(valintatapajonoDAO.readByOid(oid));
            if (jono.isPresent()) {
                LOGGER.info("{} {} {}", jono.get().getValinnanVaihe().getNimi(), jono.get().getValinnanVaihe().getOid(), jono.get().getValinnanVaihe().getValintaryhma());
                Valintatapajono master;
                boolean isMaster = false;
                if (jono.get().getMasterValintatapajono() == null) {
                    master = jono.get();
                    isMaster = true;
                } else {
                    master = valintatapajonoDAO.readByOid(jono.get().getMasterValintatapajono().getOid());
                }
                LOGGER.info("Haetaan master jonon {} kopiot", master.getOid());
                List<String> kopiot = valintatapajonoDAO.haeKopiotValisijoittelulle(master.getOid()).parallelStream()
                        .map(j -> {
                            LOGGER.info("löydettiin kopio {}", j.getOid());
                            return j.getOid();
                        }).collect(Collectors.toList());
                if (!kopiot.contains(master.getOid()) && isMaster) {
                    kopiot.add(master.getOid());
                }
                Optional<Valintaryhma> ryhma = Optional.ofNullable(master.getValinnanVaihe().getValintaryhma());
                if (ryhma.isPresent()) {
                    LOGGER.info("löydettiin valintaryhmä {} jonolle {}", ryhma.get().getNimi(), master.getOid());
                    List<HakukohdeViite> viitteet = hakukohdeDao.findByValintaryhmaOidForValisijoittelu(ryhma.get().getOid());
                    // Muodostetaan map
                    viitteet.parallelStream().forEach(viite -> {
                        viite.getValinnanvaiheet().stream().filter(ValinnanVaihe::getAktiivinen).forEach(vaihe -> {
                            vaihe.getJonot().stream().forEach(j -> {
                                if (kopiot.contains(j.getOid())) {
                                    List<String> lista = jonot.getOrDefault(viite.getOid(), new ArrayList<>());
                                    lista.add(j.getOid());
                                    jonot.put(viite.getOid(), lista);
                                }
                            });
                        });
                    });
                } else {
                    LOGGER.info("jonolle {} ei löytynyt valintaryhmää. Haetaan hakukohdeviite", master.getOid());
                    ValinnanVaihe vaihe = valinnanVaiheService.readByOid(master.getValinnanVaihe().getOid());
                    Optional<HakukohdeViite> viite = Optional.ofNullable(vaihe.getHakukohdeViite());
                    if (viite.isPresent()) {
                        LOGGER.info("löydettiin hakukohdeviite ({}) jonolle {}", viite.get().getNimi(), master.getOid());
                        List<String> lista = jonot.getOrDefault(viite.get().getOid(), new ArrayList<>());
                        lista.addAll(kopiot);
                        jonot.put(viite.get().getOid(), lista);
                    }
                }
            }
        });
        return jonot;
    }

    private void lisaaValinnanVaiheelleKopioMasterValintatapajonosta(ValinnanVaihe valinnanVaihe,
                                                                     Valintatapajono masterValintatapajono, Valintatapajono edellinenMasterValintatapajono) {
        Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(masterValintatapajono);
        kopio.setValinnanVaihe(valinnanVaihe);
        kopio.setOid(oidService.haeValintatapajonoOid());
        List<Valintatapajono> jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(valinnanVaihe.getOid()));
        Valintatapajono edellinenValintatapajono = LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMasterValintatapajono, jonot);
        kopio.setEdellinenValintatapajono(edellinenValintatapajono);
        Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValintatapajono, lisatty);
        for (ValinnanVaihe vaihekopio : valinnanVaihe.getKopioValinnanVaiheet()) {
            lisaaValinnanVaiheelleKopioMasterValintatapajonosta(vaihekopio, lisatty, lisatty.getEdellinenValintatapajono());
        }
    }

    @Override
    public Valintatapajono lisaaValintatapajonoValinnanVaiheelle(String valinnanVaiheOid, ValintatapajonoCreateDTO dto,
                                                                 String edellinenValintatapajonoOid) {
        ValinnanVaihe valinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
        if (!fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN.equals(valinnanVaihe.getValinnanVaiheTyyppi())) {
            throw new ValintatapajonoaEiVoiLisataException("Valintatapajonoa ei voi lisätä valinnan vaiheelle, jonka " + "tyyppi on " + valinnanVaihe.getValinnanVaiheTyyppi().name());
        }
        Valintatapajono edellinenValintatapajono = null;
        if (StringUtils.isNotBlank(edellinenValintatapajonoOid)) {
            edellinenValintatapajono = haeValintatapajono(edellinenValintatapajonoOid);
        } else {
            edellinenValintatapajono = valintatapajonoDAO.haeValinnanVaiheenViimeinenValintatapajono(valinnanVaiheOid);
        }
        Valintatapajono jono = modelMapper.map(dto, Valintatapajono.class);
        jono.setOid(oidService.haeValintatapajonoOid());
        jono.setValinnanVaihe(valinnanVaihe);
        jono.setEdellinenValintatapajono(edellinenValintatapajono);
        if (dto.getTayttojono() != null) {
            Valintatapajono tayttoJono = valintatapajonoDAO.readByOid(dto.getTayttojono());
            jono.setVarasijanTayttojono(tayttoJono);
        }
        Valintatapajono lisatty = valintatapajonoDAO.insert(jono);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValintatapajono, lisatty);
        for (ValinnanVaihe kopio : valinnanVaihe.getKopioValinnanVaiheet()) {
            lisaaValinnanVaiheelleKopioMasterValintatapajonosta(kopio, lisatty, edellinenValintatapajono);
        }
        return lisatty;
    }

    @Override
    public void deleteByOid(String oid) {
        Valintatapajono valintatapajono = haeValintatapajono(oid);
        delete(valintatapajono);
    }

    @Override
    public void delete(Valintatapajono valintatapajono) {
        for (Valintatapajono valintatapajono1 : valintatapajono.getKopiot()) {
            delete(valintatapajono1);
        }
        if (valintatapajono.getSeuraava() != null) {
            Valintatapajono seuraava = valintatapajono.getSeuraava();
            Valintatapajono edellinen = valintatapajono.getEdellinen();
            valintatapajono.setEdellinen(null);
            valintatapajonoDAO.update(valintatapajono);
            seuraava.setEdellinen(edellinen);
        }
        if(valintatapajono.getJarjestyskriteerit() != null) {
            for (Jarjestyskriteeri jarjestyskriteeri : valintatapajono.getJarjestyskriteerit()) {
                jarjestyskriteeriService.delete(jarjestyskriteeri);
            }
        }
        valintatapajono = valintatapajonoDAO.readByOid(valintatapajono.getOid());
        if(valintatapajono != null) {
            valintatapajono.setJarjestyskriteerit(null);
            valintatapajonoDAO.remove(valintatapajono);
        }
    }

    @Override
    public Boolean readAutomaattinenSijoitteluunSiirto(String oid) {
        return haeValintatapajono(oid).getAutomaattinenLaskentaanSiirto();
    }

    @Override
    public Valintatapajono updateAutomaattinenSijoitteluunSiirto(String oid, Boolean value) {
        Valintatapajono valintatapajono = haeValintatapajono(oid);
        valintatapajono.setAutomaattinenLaskentaanSiirto(value);
        valintatapajonoDAO.update(valintatapajono);
        return valintatapajono;
    }

    private Valintatapajono haeValintatapajono(String oid) {
        Valintatapajono valintatapajono = valintatapajonoDAO.readByOid(oid);
        if (valintatapajono == null) {
            throw new ValintatapajonoEiOleOlemassaException("Valintatapajono (" + oid + ") ei ole olemassa", oid);
        }
        return valintatapajono;
    }

    @Override
    public Valintatapajono readByOid(String oid) {
        return haeValintatapajono(oid);
    }

    @Override
    public List<Valintatapajono> findAll() {
        return valintatapajonoDAO.findAll();
    }

    @Override
    public Valintatapajono update(String oid, ValintatapajonoCreateDTO dto) {
        Valintatapajono managedObject = haeValintatapajono(oid);
        Valintatapajono konvertoitu = modelMapper.map(dto, Valintatapajono.class);
        if (dto.getTayttojono() != null) {
            Valintatapajono tayttoJono = valintatapajonoDAO.readByOid(dto.getTayttojono());
            konvertoitu.setVarasijanTayttojono(tayttoJono);
        } else {
            konvertoitu.setVarasijanTayttojono(null);
        }
        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, konvertoitu, kopioija);
    }

    @Override
    public List<Valintatapajono> jarjestaValintatapajonot(List<String> valintatapajonoOidit) {
        if (valintatapajonoOidit.isEmpty()) {
            throw new ValintatapajonoOidListaOnTyhjaException("Valintatapajonojen OID-lista on tyhjä");
        }
        Valintatapajono ensimmainen = haeValintatapajono(valintatapajonoOidit.get(0));
        return jarjestaValintatapajonot(ensimmainen.getValinnanVaihe(), valintatapajonoOidit);
    }

    private Valintatapajono kopioiValintatapajonotRekursiivisesti(ValinnanVaihe valinnanVaihe, Valintatapajono master) {
        if (master == null) {
            return null;
        }
        Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(master);
        kopio.setOid(oidService.haeValintatapajonoOid());
        valinnanVaihe.addJono(kopio);
        Valintatapajono edellinen = kopioiValintatapajonotRekursiivisesti(valinnanVaihe, master.getEdellinenValintatapajono());
        if (edellinen != null) {
            kopio.setEdellinenValintatapajono(edellinen);
            edellinen.setSeuraava(kopio);
        }
        Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
        jarjestyskriteeriService.kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(lisatty, master);
        return lisatty;
    }

    @Override
    public void kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(ValinnanVaihe valinnanVaihe,
                                                                      ValinnanVaihe masterValinnanVaihe) {
        Valintatapajono vv = valintatapajonoDAO.haeValinnanVaiheenViimeinenValintatapajono(masterValinnanVaihe.getOid());
        kopioiValintatapajonotRekursiivisesti(valinnanVaihe, vv);
    }

    private List<Valintatapajono> jarjestaValintatapajonot(ValinnanVaihe vaihe, List<String> valintatapajonoOidit) {
        LinkedHashMap<String, Valintatapajono> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe.getOid())));
        LinkedHashMap<String, Valintatapajono> jarjestetty = LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(
                alkuperainenJarjestys, valintatapajonoOidit);
        for (ValinnanVaihe kopio : vaihe.getKopiot()) {
            jarjestaKopioValinnanVaiheenValintatapajonot(kopio, jarjestetty);
        }
        return new ArrayList<>(jarjestetty.values());
    }

    private void jarjestaKopioValinnanVaiheenValintatapajonot(ValinnanVaihe vaihe, LinkedHashMap<String, Valintatapajono> uusiMasterJarjestys) {
        LinkedHashMap<String, Valintatapajono> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe.getOid())));
        LinkedHashMap<String, Valintatapajono> jarjestetty = LinkitettavaJaKopioitavaUtil.jarjestaKopiotMasterJarjestyksenMukaan(
                alkuperainenJarjestys, uusiMasterJarjestys);
        for (ValinnanVaihe kopio : vaihe.getKopiot()) {
            jarjestaKopioValinnanVaiheenValintatapajonot(kopio, jarjestetty);
        }
    }
}
