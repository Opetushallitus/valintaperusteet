package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.*;
import akka.actor.SupervisorStrategy.Directive;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.LuoValintaperusteetServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LuoValintaperuste;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkJaYoPohjaiset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.List;

@Named("LuoValintaperusteetActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class LuoValintaperusteetActorBean extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValintakoekoodiService valintakoekoodiService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private ValintakoeService valintakoeService;

    @Autowired
    private JpaTransactionManager transactionManager;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    public LuoValintaperusteetActorBean() {

    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof LuoValintaperuste) {
            LuoValintaperuste peruste = (LuoValintaperuste) message;
            String nimi = peruste.getHakukohdekoodi().getNimiFi();
            ValintaryhmaDTO valintaryhma = new ValintaryhmaDTO();
            valintaryhma.setNimi(nimi);
            TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            if (nimi.contains(", pk")) {
                valintaryhma = modelMapper.map(valintaryhmaService.insert(valintaryhma, peruste.getpOid()), ValintaryhmaDTO.class);
            } else {
                valintaryhma = modelMapper.map(valintaryhmaService.insert(valintaryhma, peruste.getlOid()), ValintaryhmaDTO.class);
            }
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            ValinnanVaihe valintakoevaihe = valinnanVaiheService.findByValintaryhma(valintaryhma.getOid()).get(1);
            assert (valintakoevaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.VALINTAKOE));
            valintakoevaihe.setNimi("Kielikokeen pakollisuus ja pääsykoe");
            valintakoevaihe.setKuvaus("Kielikokeen pakollisuus ja pääsykoe");
            valintakoevaihe = valinnanVaiheService.update(valintakoevaihe.getOid(), modelMapper.map(valintakoevaihe, ValinnanVaiheCreateDTO.class));
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            boolean onPoikkeavaRyhma = LuoValintaperusteetServiceImpl.poikkeavatValintaryhmat.contains(peruste.getHakukohdekoodi().getUri());
            String valintakoetunniste;
            String valintakoeNimi;
            String valintakoeKaavanTunniste;
            if (!onPoikkeavaRyhma) {
                valintakoetunniste = nimi + ", pääsy- ja soveltuvuuskoe";
                valintakoeNimi = valintakoetunniste;
                valintakoeKaavanTunniste = valintakoetunniste;
            } else {
                valintakoetunniste = "{{hakukohde.paasykoe_tunniste}}";
                valintakoeKaavanTunniste = "paasykoe_tunniste";
                valintakoeNimi = "Pääsy- ja soveltuvuuskoe";
            }
            ValintakoeDTO valintakoe = new ValintakoeDTO();
            valintakoe.setAktiivinen(false);
            valintakoe.setKuvaus("Pääsy- ja soveltuvuuskoe");
            valintakoe.setTunniste(valintakoetunniste);
            valintakoe.setNimi(valintakoeNimi);
            valintakoe.setLahetetaankoKoekutsut(true);
            valintakoe.setKutsutaankoKaikki(false);
            // Valintakoe on pakollinen niille, joilla ei ole ulkomailla
            // suoritettua koulutusta tai
            // joiden oppivelvollisuuden suorittaminen ei ole keskeytynyt
            valintakoe.setLaskentakaavaId(null);
            valintakoeService.lisaaValintakoeValinnanVaiheelle(valintakoevaihe.getOid(), valintakoe);
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            ValinnanVaiheDTO valinnanVaihe = new ValinnanVaiheDTO();
            valinnanVaihe.setAktiivinen(true);
            valinnanVaihe.setKuvaus("Varsinainen valinnanvaihe");
            valinnanVaihe.setNimi("Varsinainen valinnanvaihe");
            valinnanVaihe.setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);
            valinnanVaihe = modelMapper.map(valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhma.getOid(), valinnanVaihe, valintakoevaihe.getOid()), ValinnanVaiheDTO.class);
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            ValintatapajonoDTO jono = new ValintatapajonoDTO();
            jono.setAktiivinen(true);
            jono.setAutomaattinenLaskentaanSiirto(true);
            jono.setValisijoittelu(false);
            jono.setAloituspaikat(0);
            jono.setKuvaus("Varsinaisen valinnanvaiheen valintatapajono");
            jono.setNimi("Varsinaisen valinnanvaiheen valintatapajono");
            jono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);
            jono.setSiirretaanSijoitteluun(true);
            jono.setPoissaOlevaTaytto(true);
            valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaihe.getOid(), jono, null);
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            Valintaperustelahde lahde;
            Laskentakaava valintakoekaava;
            if (onPoikkeavaRyhma) {
                lahde = Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO;
                valintakoekaava = laskentakaavaService.insert(PkJaYoPohjaiset.luoValintakoekaava(valintakoeKaavanTunniste, lahde, true), null, valintaryhma.getOid());
            } else {
                lahde = Valintaperustelahde.SYOTETTAVA_ARVO;
                valintakoekaava = laskentakaavaService.insert(PkJaYoPohjaiset.luoValintakoekaava(valintakoeKaavanTunniste, lahde), null, valintaryhma.getOid());
            }
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            Laskentakaava peruskaava = null;
            Laskentakaava[] tasasijakriteerit = null;
            if (nimi.contains(", pk")) {
                peruskaava = peruste.getPkPeruskaava();
                tasasijakriteerit = peruste.getPkTasasijakriteerit();
            } else {
                peruskaava = peruste.getLkPeruskaava();
                tasasijakriteerit = peruste.getLkTasasijakriteerit();
            }
            Laskentakaava ensisijainenJarjestyskriteeri = null;
            if (onPoikkeavaRyhma) {
                ensisijainenJarjestyskriteeri = PkJaYoPohjaiset.luoPoikkeavanValintaryhmanLaskentakaava(valintakoekaava, peruste.getKielikoelaskentakaava());
            } else {
                ensisijainenJarjestyskriteeri = PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaValintakoekaava(peruskaava, valintakoekaava);
            }
            transactionManager.commit(tx);
            insertKoe(valintaryhma, valintakoetunniste, ensisijainenJarjestyskriteeri, valintakoekaava, tasasijakriteerit, peruste.getHakukohdekoodi());
            insertEiKoetta(valintaryhma, peruskaava, tasasijakriteerit, peruste.getHakukohdekoodi(), peruste.getLisapisteLaskentakaava());
        } else if (message instanceof Exception) {
            Exception exp = (Exception) message;
            exp.printStackTrace();
            getContext().stop(self());
        } else {
            unhandled(message);
            getContext().stop(getSelf());
        }
    }

    private void insertKoe(ValintaryhmaDTO valintaryhma, String valintakoetunniste,
                           Laskentakaava peruskaavaJaValintakoekaava, Laskentakaava valintakoekaava,
                           Laskentakaava[] tasasijakriteerit, KoodiDTO hakukohdekoodi) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        ValintaryhmaDTO koevalintaryhma = new ValintaryhmaDTO();
        koevalintaryhma.setNimi("Peruskaava ja pääsykoe");
        koevalintaryhma = modelMapper.map(valintaryhmaService.insert(koevalintaryhma, valintaryhma.getOid()), ValintaryhmaDTO.class);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koevalintaryhma.getOid(), hakukohdekoodi);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        peruskaavaJaValintakoekaava = laskentakaavaService.insert(peruskaavaJaValintakoekaava, null, koevalintaryhma.getOid());
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        // Aktivoidaan pääsykoe
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByValintaryhma(koevalintaryhma.getOid());
        ValinnanVaihe valintakoevaihe = valinnanVaiheet.get(1);
        assert (valintakoevaihe.getValinnanVaiheTyyppi().equals(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.VALINTAKOE));
        assert (valintakoevaihe.getNimi().contains("ja pääsykoe"));
        List<Valintakoe> valintakokeet = valintakoeService.findValintakoeByValinnanVaihe(valintakoevaihe.getOid());
        Valintakoe paasykoe = null;
        for (Valintakoe koe : valintakokeet) {
            if (valintakoetunniste.equals(koe.getTunniste())) {
                paasykoe = koe;
                break;
            }
        }
        assert (paasykoe != null);
        paasykoe.setAktiivinen(true);
        ValintakoeDTO dto = new ValintakoeDTO();
        dto.setAktiivinen(true);
        dto.setNimi(paasykoe.getNimi());
        dto.setKuvaus(paasykoe.getKuvaus());
        dto.setTunniste(paasykoe.getTunniste());
        dto.setLaskentakaavaId(paasykoe.getLaskentakaavaId());
        dto.setLahetetaankoKoekutsut(true);
        dto.setKutsutaankoKaikki(false);
        valintakoeService.update(paasykoe.getOid(), dto);
        KoodiDTO valintakoekoodi = new KoodiDTO();
        valintakoekoodi.setUri(LuoValintaperusteetServiceImpl.PAASY_JA_SOVELTUVUUSKOE);
        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(koevalintaryhma.getOid(), valintakoekoodi);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        ValinnanVaihe tavallinenVaihe = valinnanVaiheet.get(2);
        assert (tavallinenVaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(tavallinenVaihe.getOid()).get(0);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        JarjestyskriteeriDTO kriteeri = new JarjestyskriteeriDTO();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(peruskaavaJaValintakoekaava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, peruskaavaJaValintakoekaava.getId());
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            if (i == 1) {
                JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
                jk.setAktiivinen(true);
                jk.setMetatiedot(valintakoekaava.getNimi());
                jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null,
                        valintakoekaava.getId());
            }
            Laskentakaava kaava = tasasijakriteerit[i];
            JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }
        transactionManager.commit(tx);
    }

    private void insertEiKoetta(ValintaryhmaDTO valintaryhma, Laskentakaava peruskaava,
                                Laskentakaava[] tasasijakriteerit, KoodiDTO hakukohdekoodi, Laskentakaava lisapistekaava) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        ValintaryhmaDTO koe = new ValintaryhmaDTO();
        koe.setNimi("Peruskaava");
        koe = modelMapper.map(valintaryhmaService.insert(koe, valintaryhma.getOid()), ValintaryhmaDTO.class);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(2);
        assert (vaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.TAVALLINEN));
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid()).get(0);
        boolean onPoikkeavaRyhma = LuoValintaperusteetServiceImpl.poikkeavatValintaryhmatLisapisteilla.contains(hakukohdekoodi.getUri());
        Laskentakaava lisattava = null;
        if (onPoikkeavaRyhma) {
            lisattava = laskentakaavaService.insert(PkJaYoPohjaiset.luoYhdistettyPeruskaavaJaLisapistekaava(peruskaava, lisapistekaava), null, koe.getOid());
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            ValinnanVaihe koevaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(1);
            assert (koevaihe.getValinnanVaiheTyyppi().equals(ValinnanVaiheTyyppi.VALINTAKOE));
            final String lisapisteNimi = "Lisäpiste";
            ValintakoeDTO lisapiste = new ValintakoeDTO();
            lisapiste.setAktiivinen(true);
            lisapiste.setLahetetaankoKoekutsut(false);
            lisapiste.setKutsutaankoKaikki(false);
            lisapiste.setKuvaus(lisapisteNimi);
            lisapiste.setNimi(lisapisteNimi);
            lisapiste.setTunniste("{{hakukohde.lisapiste_tunniste}}");
            lisapiste.setLaskentakaavaId(null);
            valintakoeService.lisaaValintakoeValinnanVaiheelle(koevaihe.getOid(), lisapiste);
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        } else {
            lisattava = peruskaava;
        }
        JarjestyskriteeriDTO kriteeri = new JarjestyskriteeriDTO();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(lisattava.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, lisattava.getId());
        transactionManager.commit(tx);
        tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        for (int i = 0; i < tasasijakriteerit.length; ++i) {
            Laskentakaava kaava = tasasijakriteerit[i];
            JarjestyskriteeriDTO jk = new JarjestyskriteeriDTO();
            jk.setAktiivinen(true);
            jk.setMetatiedot(kaava.getNimi());
            jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), jk, null, kaava.getId());
        }
        transactionManager.commit(tx);
    }
}
