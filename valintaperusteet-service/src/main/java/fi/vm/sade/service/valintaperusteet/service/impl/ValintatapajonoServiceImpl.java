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
import fi.vm.sade.service.valintaperusteet.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ValintatapajonoServiceImpl implements ValintatapajonoService {
    final static private Logger LOGGER = LoggerFactory.getLogger(ValintatapajonoService.class.getName());

    private final ValintatapajonoDAO valintatapajonoDAO;

    private final ValinnanVaiheService valinnanVaiheService;

    private final OidService oidService;

    private final JarjestyskriteeriService jarjestyskriteeriService;

    private final HakijaryhmaService hakijaryhmaService;

    private final HakukohdeService hakukohdeService;

    private final HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;

    private final HakukohdeViiteDAO hakukohdeDao;

    private static ValintatapajonoKopioija kopioija = new ValintatapajonoKopioija();

    private final ValintaperusteetModelMapper modelMapper;

    private final VtsRestClient vtsRestClient;

    @Autowired
    public ValintatapajonoServiceImpl(@Lazy ValintatapajonoDAO valintatapajonoDAO,
                                      @Lazy ValinnanVaiheService valinnanVaiheService,
                                      @Lazy OidService oidService,
                                      @Lazy JarjestyskriteeriService jarjestyskriteeriService,
                                      @Lazy HakijaryhmaService hakijaryhmaService,
                                      @Lazy HakukohdeService hakukohdeService,
                                      @Lazy HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService,
                                      @Lazy HakukohdeViiteDAO hakukohdeDao,
                                      @Lazy ValintaperusteetModelMapper modelMapper,
                                      VtsRestClient vtsRestClient) {
        this.valintatapajonoDAO = valintatapajonoDAO;
        this.valinnanVaiheService = valinnanVaiheService;
        this.oidService = oidService;
        this.jarjestyskriteeriService = jarjestyskriteeriService;
        this.hakijaryhmaService = hakijaryhmaService;
        this.hakukohdeService = hakukohdeService;
        this.hakijaryhmaValintatapajonoService = hakijaryhmaValintatapajonoService;
        this.hakukohdeDao = hakukohdeDao;
        this.modelMapper = modelMapper;
        this.vtsRestClient = vtsRestClient;
    }

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
        Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(masterValintatapajono, null);
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
        valintatapajono.setKopiot(new HashSet<Valintatapajono>());

        Valintatapajono edellinen = valintatapajono.getEdellinen();
        Valintatapajono seuraava = valintatapajono.getSeuraava();

        if(edellinen != null) {
            edellinen.setSeuraava(null);
            valintatapajonoDAO.update(edellinen);
        }

        valintatapajono.setEdellinen(null);
        valintatapajono.setSeuraava(null);
        valintatapajonoDAO.update(valintatapajono);

        if(seuraava != null) {
            seuraava.setEdellinen(edellinen);
            valintatapajonoDAO.update(seuraava);
            if(edellinen != null) {
                edellinen.setSeuraava(seuraava);
                valintatapajonoDAO.update(edellinen);
            }
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

        try {
            boolean isJonoSijoiteltu = vtsRestClient.isJonoSijoiteltu(oid);
            if(isJonoSijoiteltu) {
                konvertoitu.setSiirretaanSijoitteluun(true);
                dto.setSiirretaanSijoitteluun(true);
                managedObject.setSiirretaanSijoitteluun(true);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Virhe tarkistaessa onko valintatapajonolle %s suoritettu sijoitteluajoa", oid), e);
        }
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

    private Valintatapajono kopioiValintatapajonotRekursiivisesti(ValinnanVaihe valinnanVaihe, Valintatapajono master, JuureenKopiointiCache kopiointiCache) {
        if (master == null) {
            return null;
        }
        Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(master, kopiointiCache);
        kopio.setOid(oidService.haeValintatapajonoOid());
        kopio.setValinnanVaihe(valinnanVaihe);
        valinnanVaihe.addJono(kopio);
        Valintatapajono edellinen = kopioiValintatapajonotRekursiivisesti(valinnanVaihe, master.getEdellinenValintatapajono(), kopiointiCache);
        if (edellinen != null) {
            kopio.setEdellinenValintatapajono(edellinen);
            edellinen.setSeuraava(kopio);
        }
        Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
        if (kopiointiCache != null) {
            kopiointiCache.kopioidutValintapajonot.put(master.getId(), lisatty);
            hakijaryhmaValintatapajonoService.kopioiValintatapajononHakijaryhmaValintatapajonot(master, kopio, kopiointiCache);
        }
        jarjestyskriteeriService.kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(lisatty, master, kopiointiCache);
        return lisatty;
    }

    @Override
    public void kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(ValinnanVaihe valinnanVaihe,
                                                                      ValinnanVaihe masterValinnanVaihe,
                                                                      JuureenKopiointiCache kopiointiCache) {
        Valintatapajono vv = valintatapajonoDAO.haeValinnanVaiheenViimeinenValintatapajono(masterValinnanVaihe.getOid());
        kopioiValintatapajonotRekursiivisesti(valinnanVaihe, vv, kopiointiCache);
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
