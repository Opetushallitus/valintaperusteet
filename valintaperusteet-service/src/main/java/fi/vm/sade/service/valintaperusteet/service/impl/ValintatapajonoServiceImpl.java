package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.util.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValintatapajonoServiceImpl implements ValintatapajonoService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ValintatapajonoService.class.getName());

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

  private final String rootOrgOid;

  @Autowired
  public ValintatapajonoServiceImpl(
      @Lazy ValintatapajonoDAO valintatapajonoDAO,
      @Lazy ValinnanVaiheService valinnanVaiheService,
      @Lazy OidService oidService,
      @Lazy JarjestyskriteeriService jarjestyskriteeriService,
      @Lazy HakijaryhmaService hakijaryhmaService,
      @Lazy HakukohdeService hakukohdeService,
      @Lazy HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService,
      @Lazy HakukohdeViiteDAO hakukohdeDao,
      @Lazy ValintaperusteetModelMapper modelMapper,
      @Value("${root.organisaatio.oid}") final String rootOrgOid,
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
    this.rootOrgOid = rootOrgOid;
  }

  @Override
  public List<Valintatapajono> findJonoByValinnanvaihe(String oid) {
    return valintatapajonoDAO.findByValinnanVaihe(oid);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, List<String>> findKopiot(List<String> oidit) {
    Map<String, List<String>> jonot = new ConcurrentHashMap<>();
    oidit.forEach(
        oid -> {
          Optional<Valintatapajono> jono = Optional.ofNullable(valintatapajonoDAO.readByOid(oid));
          if (jono.isPresent()) {
            LOGGER.info(
                "{} {} {}",
                jono.get().getValinnanVaihe().getNimi(),
                jono.get().getValinnanVaihe().getOid(),
                jono.get().getValinnanVaihe().getValintaryhma());
            Valintatapajono master;
            boolean isMaster = false;
            if (jono.get().getMasterValintatapajono() == null) {
              master = jono.get();
              isMaster = true;
            } else {
              master = valintatapajonoDAO.readByOid(jono.get().getMasterValintatapajono().getOid());
            }
            LOGGER.info("Haetaan master jonon {} kopiot", master.getOid());
            List<String> kopiot =
                valintatapajonoDAO.haeKopiotValisijoittelulle(master.getOid()).parallelStream()
                    .map(
                        j -> {
                          LOGGER.info("löydettiin kopio {}", j.getOid());
                          return j.getOid();
                        })
                    .collect(Collectors.toList());
            if (!kopiot.contains(master.getOid()) && isMaster) {
              kopiot.add(master.getOid());
            }
            Optional<Valintaryhma> ryhma =
                Optional.ofNullable(master.getValinnanVaihe().getValintaryhma());
            if (ryhma.isPresent()) {
              LOGGER.info(
                  "löydettiin valintaryhmä {} (oid {}) jonolle {}",
                  ryhma.get().getNimi(),
                  ryhma.get().getOid(),
                  master.getOid());
              List<HakukohdeViite> viitteet =
                  hakukohdeDao.findByValintaryhmaOidForValisijoittelu(ryhma.get().getOid());
              // Muodostetaan map
              LOGGER.info(
                  "löydettiin valintaryhmälle {} (oid {}) {} hakukohdeviitettä",
                  ryhma.get().getNimi(),
                  ryhma.get().getOid(),
                  viitteet.size());
              viitteet.stream()
                  .forEach(
                      viite -> {
                        try {
                          viite.getValinnanvaiheet().stream()
                              .filter(ValinnanVaihe::getAktiivinen)
                              .forEach(
                                  vaihe -> {
                                    vaihe.getJonot().stream()
                                        .forEach(
                                            j -> {
                                              if (kopiot.contains(j.getOid())) {
                                                List<String> lista =
                                                    jonot.getOrDefault(
                                                        viite.getOid(), new ArrayList<>());
                                                lista.add(j.getOid());
                                                jonot.put(viite.getOid(), lista);
                                              }
                                            });
                                  });
                        } catch (Exception e) {
                          LOGGER.error(
                              "Odottamaton poikkeus käsiteltäessä hakukohdeviitettä " + viite, e);
                          LOGGER.error(
                              String.format(
                                  "Odottamaton poikkeus käsiteltäessä hakukohdeviitettä %s (%s)",
                                  viite.getOid(), viite.getNimi()),
                              e);
                          throw e;
                        }
                      });
            } else {
              LOGGER.info(
                  "jonolle {} ei löytynyt valintaryhmää. Haetaan hakukohdeviite", master.getOid());
              ValinnanVaihe vaihe =
                  valinnanVaiheService.readByOid(master.getValinnanVaihe().getOid());
              Optional<HakukohdeViite> viite = Optional.ofNullable(vaihe.getHakukohdeViite());
              if (viite.isPresent()) {
                LOGGER.info(
                    "löydettiin hakukohdeviite ({}) jonolle {}",
                    viite.get().getNimi(),
                    master.getOid());
                List<String> lista = jonot.getOrDefault(viite.get().getOid(), new ArrayList<>());
                lista.addAll(kopiot);
                jonot.put(viite.get().getOid(), lista);
              }
            }
          }
        });
    return jonot;
  }

  private void lisaaValinnanVaiheelleKopioMasterValintatapajonosta(
      ValinnanVaihe valinnanVaihe,
      Valintatapajono masterValintatapajono,
      Valintatapajono edellinenMasterValintatapajono) {
    Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(masterValintatapajono, null);
    kopio.setValinnanVaihe(valinnanVaihe);
    kopio.setOid(oidService.haeValintatapajonoOid());
    List<Valintatapajono> jonot = valintatapajonoDAO.findByValinnanVaihe(valinnanVaihe.getOid());
    kopio.setEdellinenValintatapajono(
        LinkitettavaJaKopioitavaUtil.kopioTaiViimeinen(edellinenMasterValintatapajono, jonot));
    Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
    for (ValinnanVaihe vaihekopio : valinnanVaihe.getKopioValinnanVaiheet()) {
      lisaaValinnanVaiheelleKopioMasterValintatapajonosta(
          vaihekopio, lisatty, lisatty.getEdellinenValintatapajono());
    }
  }

  @Override
  public Valintatapajono lisaaValintatapajonoValinnanVaiheelle(
      String valinnanVaiheOid, ValintatapajonoCreateDTO dto, String edellinenValintatapajonoOid) {
    ValinnanVaihe valinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
    if (!fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN.equals(
        valinnanVaihe.getValinnanVaiheTyyppi())) {
      throw new ValintatapajonoaEiVoiLisataException(
          "Valintatapajonoa ei voi lisätä valinnan vaiheelle, jonka "
              + "tyyppi on "
              + valinnanVaihe.getValinnanVaiheTyyppi().name());
    }
    checkTyyppiPakollisuus(dto);
    Valintatapajono edellinenValintatapajono = null;
    if (StringUtils.isNotBlank(edellinenValintatapajonoOid)) {
      edellinenValintatapajono = haeValintatapajono(edellinenValintatapajonoOid);
    } else {
      edellinenValintatapajono =
          valintatapajonoDAO.haeValinnanVaiheenViimeinenValintatapajono(valinnanVaiheOid);
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
    for (ValinnanVaihe kopio : valinnanVaihe.getKopioValinnanVaiheet()) {
      lisaaValinnanVaiheelleKopioMasterValintatapajonosta(kopio, lisatty, edellinenValintatapajono);
    }
    return lisatty;
  }

  private void checkTyyppiPakollisuus(ValintatapajonoCreateDTO jono)
      throws ValintatapajonoaEiVoiLisataException {
    if (Boolean.TRUE.equals(jono.getSiirretaanSijoitteluun())
        && StringUtils.isEmpty(jono.getTyyppi())) {
      throw new ValintatapajonoaEiVoiLisataException(
          "Valintatapajonoa "
              + jono.getNimi()
              + "ei voi lisätä. Sijoitteluun menevällä jonolla on oltava tyyppi.");
    }
  }

  @Override
  public ValintatapajonoDTO delete(String oid) {
    Valintatapajono valintatapajono = haeValintatapajono(oid);
    ValintatapajonoDTO dto = modelMapper.map(valintatapajono, ValintatapajonoDTO.class);
    valintatapajonoDAO.delete(valintatapajono);
    return dto;
  }

  @Override
  public Boolean readAutomaattinenSijoitteluunSiirto(String oid) {
    return haeValintatapajono(oid).getautomaattinenSijoitteluunSiirto();
  }

  @Override
  public Valintatapajono updateAutomaattinenSijoitteluunSiirto(String oid, Boolean value) {
    Valintatapajono valintatapajono = haeValintatapajono(oid);
    valintatapajono.setautomaattinenSijoitteluunSiirto(value);
    valintatapajonoDAO.update(valintatapajono);
    return valintatapajono;
  }

  private Valintatapajono haeValintatapajono(String oid) {
    Valintatapajono valintatapajono = valintatapajonoDAO.readByOid(oid);
    if (valintatapajono == null) {
      throw new ValintatapajonoEiOleOlemassaException(
          "Valintatapajono (" + oid + ") ei ole olemassa", oid);
    }
    return valintatapajono;
  }

  @Override
  public Valintatapajono readByOid(String oid) {
    return haeValintatapajono(oid);
  }

  @Override
  public List<Valintatapajono> readByOids(List<String> oids) {
    return valintatapajonoDAO.readByOids(oids);
  }

  @Override
  public List<Valintatapajono> findAll() {
    return valintatapajonoDAO.findAll();
  }

  @Override
  public Valintatapajono update(String oid, ValintatapajonoCreateDTO dto) {
    Valintatapajono nykyinenValintatapajono = haeValintatapajono(oid);
    Valintatapajono tallennettavaValintatapajono = modelMapper.map(dto, Valintatapajono.class);

    // we must allow registry managers to set siirretaanSijoitteluun to any value at all times
    if (!isOPH()) {
      try {
        if (vtsRestClient.isJonoSijoiteltu(oid)) {
          tallennettavaValintatapajono.setSiirretaanSijoitteluun(true);
          dto.setSiirretaanSijoitteluun(true);
          nykyinenValintatapajono.setSiirretaanSijoitteluun(true);
        }
      } catch (RuntimeException e) {
        LOGGER.error(
            String.format(
                "Virhe tarkistaessa onko valintatapajonolle %s suoritettu sijoitteluajoa", oid),
            e);
      }
    }

    // jonon arvoa "eiLasketaPaivamaaranJalkeen" ei saa muokata ilman rekisterinpitäjän oikeuksia
    // jos se on jo menneisyydessä.
    Date oldEiLasketaJalkeen = nykyinenValintatapajono.getEiLasketaPaivamaaranJalkeen();
    Date newEiLasketaJalkeen = tallennettavaValintatapajono.getEiLasketaPaivamaaranJalkeen();
    if (!isOPH() && oldEiLasketaJalkeen != null) {
      LocalDate oldValueDate =
          oldEiLasketaJalkeen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      if (oldValueDate.isBefore(LocalDate.now())) {
        LOGGER.warn(
            "Yritettiin päivittää jonon {} eiLasketaPaivamaaranJalkeen-arvoa {} ({}) -> {} ilman rekisterinpitäjän oikeuksia."
                + " Pidetään vanha arvo.",
            oid,
            oldEiLasketaJalkeen,
            oldValueDate,
            newEiLasketaJalkeen);
        tallennettavaValintatapajono.setEiLasketaPaivamaaranJalkeen(
            nykyinenValintatapajono.getEiLasketaPaivamaaranJalkeen());
      }
    }

    checkTyyppiPakollisuus(dto);
    if (dto.getTayttojono() != null) {
      Valintatapajono tayttoJono = valintatapajonoDAO.readByOid(dto.getTayttojono());
      tallennettavaValintatapajono.setVarasijanTayttojono(tayttoJono);
    } else {
      tallennettavaValintatapajono.setVarasijanTayttojono(null);
    }
    return LinkitettavaJaKopioitavaUtil.paivita(
        nykyinenValintatapajono, tallennettavaValintatapajono, kopioija);
  }

  @Override
  public List<Valintatapajono> jarjestaValintatapajonot(List<String> valintatapajonoOidit) {
    if (valintatapajonoOidit.isEmpty()) {
      throw new ValintatapajonoOidListaOnTyhjaException("Valintatapajonojen OID-lista on tyhjä");
    }
    Valintatapajono ensimmainen = haeValintatapajono(valintatapajonoOidit.get(0));
    return jarjestaValintatapajonot(ensimmainen.getValinnanVaihe(), valintatapajonoOidit);
  }

  @Override
  public void kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(
      ValinnanVaihe valinnanVaihe,
      ValinnanVaihe masterValinnanVaihe,
      JuureenKopiointiCache kopiointiCache) {
    List<Valintatapajono> valintatapajonot =
        valintatapajonoDAO.findByValinnanVaihe(masterValinnanVaihe.getOid());
    Collections.reverse(valintatapajonot);
    for (Valintatapajono valintatapajono : valintatapajonot) {
      Valintatapajono kopio =
          ValintatapajonoUtil.teeKopioMasterista(valintatapajono, kopiointiCache);
      kopio.setOid(oidService.haeValintatapajonoOid());
      kopio.setValinnanVaihe(valinnanVaihe);
      valinnanVaihe.addJono(kopio);
      Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
      if (kopiointiCache != null) {
        kopiointiCache.kopioidutValintapajonot.put(valintatapajono.getId(), lisatty);
      }
      hakijaryhmaValintatapajonoService.kopioiValintatapajononHakijaryhmaValintatapajonot(
          valintatapajono, kopio, kopiointiCache);
      jarjestyskriteeriService.kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(
          lisatty, valintatapajono, kopiointiCache);
    }
  }

  private List<Valintatapajono> jarjestaValintatapajonot(
      ValinnanVaihe vaihe, List<String> valintatapajonoOidit) {
    List<Valintatapajono> jarjestetty =
        valintatapajonoDAO.jarjestaUudelleen(vaihe, valintatapajonoOidit);
    for (ValinnanVaihe kopio : vaihe.getKopiot()) {
      jarjestaKopioValinnanVaiheenValintatapajonot(kopio, jarjestetty);
    }
    return jarjestetty;
  }

  private void jarjestaKopioValinnanVaiheenValintatapajonot(
      ValinnanVaihe vaihe, List<Valintatapajono> uusiMasterJarjestys) {
    List<Valintatapajono> jarjestetty =
        valintatapajonoDAO.jarjestaUudelleenMasterJarjestyksenMukaan(vaihe, uusiMasterJarjestys);
    for (ValinnanVaihe kopio : vaihe.getKopiot()) {
      jarjestaKopioValinnanVaiheenValintatapajonot(kopio, jarjestetty);
    }
  }

  private boolean isOPH() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    if (authentication == null) return false;
    for (GrantedAuthority authority : authentication.getAuthorities()) {
      if (authority.getAuthority().contains(rootOrgOid)) {
        return true;
      }
    }
    return false;
  }
}
