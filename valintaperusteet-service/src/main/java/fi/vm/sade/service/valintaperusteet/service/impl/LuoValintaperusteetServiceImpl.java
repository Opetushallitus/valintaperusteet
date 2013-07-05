package fi.vm.sade.service.valintaperusteet.service.impl;

import com.mysema.query.NonUniqueResultException;
import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 25.2.2013
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Transactional
public class LuoValintaperusteetServiceImpl implements LuoValintaperusteetService, ResourceLoaderAware {

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
    private OpetuskielikoodiDAO opetuskielikoodiDAO;

    @Autowired
    private OpetuskielikoodiService opetuskielikoodiService;

    @Autowired
    private ValintakoekoodiService valintakoekoodiService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    private ResourceLoader resourceLoader;

    private static final String HAKU_OID = "toisenAsteenSyksynYhteishaku";

    private static final String CSV_DELIMITER = ";";

    public static final String KIELI_FI_URI = "kieli_fi";
    public static final String KIELI_SV_URI = "kieli_sv";
    public static final String KIELI_EN_URI = "kieli_en";

    public static final String OLETUS_VALINTAKOEURI = "valintakokeentyyppi_1";

    private enum Kielikoodi {
        SUOMI("Suomi", KIELI_FI_URI), RUOTSI("Ruotsi", KIELI_SV_URI), ENGLANTI("Englanti", KIELI_EN_URI);

        Kielikoodi(String nimi, String kieliUri) {
            this.nimi = nimi;
            this.kieliUri = kieliUri;
        }

        private String nimi;
        private String kieliUri;
    }

    @Override
    public void luo() throws IOException {

        for (Kielikoodi k : Kielikoodi.values()) {
            Opetuskielikoodi opetuskieli = new Opetuskielikoodi();
            opetuskieli.setUri(k.kieliUri);
            opetuskieli.setNimiFi(k.nimi);
            opetuskieli.setNimiSv(k.nimi);
            opetuskieli.setNimiEn(k.nimi);

            try {
                opetuskielikoodiDAO.readByUri(k.kieliUri);
            } catch (NonUniqueResultException e) {
                opetuskielikoodiDAO.insert(opetuskieli);
            }
        }


        PkAineet pkAineet = new PkAineet();
        YoAineet yoAineet = new YoAineet();

        Valintaryhma ammatillinenKoulutusVr = new Valintaryhma();
        ammatillinenKoulutusVr.setNimi("Ammatillinen koulutus");
        ammatillinenKoulutusVr.setHakuOid(HAKU_OID);
        ammatillinenKoulutusVr = valintaryhmaService.insert(ammatillinenKoulutusVr);

        Valintaryhma peruskouluVr = new Valintaryhma();
        peruskouluVr.setNimi("PK");
        peruskouluVr.setHakuOid(HAKU_OID);
        peruskouluVr = valintaryhmaService.insert(peruskouluVr, ammatillinenKoulutusVr.getOid());

        Valintaryhma lukioVr = new Valintaryhma();
        lukioVr.setNimi("LK");
        lukioVr.setHakuOid(HAKU_OID);
        lukioVr = valintaryhmaService.insert(lukioVr, ammatillinenKoulutusVr.getOid());

        for (Laskentakaava kaava : pkAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, peruskouluVr);
        }

        //pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPainotettavatKeskiarvotLaskentakaava(pkAineet), peruskouluVr);
        Laskentakaava pkPohjainenLukuaineidenKeskiarvo = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pkAineet), peruskouluVr);

        //pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenLukuaineidenKeskiarvo, "Yleinen koulumenestys pisteytysmalli, PK"), peruskouluVr);
        Laskentakaava pk_pohjakoulutuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoPohjakoulutuspisteytysmalli(), peruskouluVr);
        Laskentakaava pk_ilmanKoulutuspaikkaaPisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli(), peruskouluVr);
        Laskentakaava hakutoivejarjestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoHakutoivejarjestyspisteteytysmalli(), ammatillinenKoulutusVr);
        Laskentakaava tyokokemuspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli(), ammatillinenKoulutusVr);
        Laskentakaava sukupuolipisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoSukupuolipisteytysmalli(), ammatillinenKoulutusVr);


        // Pk koostava iso kaava
        Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(pk_painotettavatKeskiarvotLaskentakaava,
                pk_yleinenkoulumenestyspisteytysmalli, pk_pohjakoulutuspisteytysmalli, pk_ilmanKoulutuspaikkaaPisteytysmalli, hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli), peruskouluVr);

        for (Laskentakaava kaava : yoAineet.getLaskentakaavat()) {
            asetaValintaryhmaJaTallennaKantaan(kaava, lukioVr);
        }

        Laskentakaava lk_paattotodistuksenkeskiarvo = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(yoAineet), lukioVr);

        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"), lukioVr);

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli), lukioVr);

        lisaaHakukohdekoodit(peruskouluVr, lukioVr, toisenAsteenPeruskoulupohjainenPeruskaava, toisenAsteenYlioppilaspohjainenPeruskaava);
    }


    private void lisaaHakukohdekoodit(Valintaryhma peruskouluVr, Valintaryhma lukioVr, Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava, Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:hakukohdekoodit/hakukohdekoodit.csv").getInputStream(), Charset.forName("UTF-8")));

            // Luetaan otsikkorivi pois
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(CSV_DELIMITER);
                String arvo = splitted[0];
                String uri = splitted[1];
                String nimi = splitted[2].replace("\"", "");

                Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
                hakukohdekoodi.setArvo(arvo);
                hakukohdekoodi.setUri(uri);
                hakukohdekoodi.setNimiFi(nimi);
                hakukohdekoodi.setNimiSv(nimi);
                hakukohdekoodi.setNimiEn(nimi);

                Valintaryhma valintaryhma = new Valintaryhma();
                valintaryhma.setHakuOid(HAKU_OID);
                valintaryhma.setNimi(nimi);

                if (nimi.contains(", pk")) {
                    valintaryhma = valintaryhmaService.insert(valintaryhma, peruskouluVr.getOid());
                } else {
                    valintaryhma = valintaryhmaService.insert(valintaryhma, lukioVr.getOid());
                }

                ValinnanVaihe valinnanVaihe = new ValinnanVaihe();
                valinnanVaihe.setAktiivinen(true);
                valinnanVaihe.setKuvaus("generoitu");
                valinnanVaihe.setNimi("Generoitu valinnanvaihe");
                valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

                valinnanVaihe = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(valintaryhma.getOid(), valinnanVaihe,
                        null);

                Valintatapajono jono = new Valintatapajono();

                jono.setAktiivinen(true);
                jono.setAloituspaikat(0);
                jono.setKuvaus("generoitu");
                jono.setNimi("Generoitu valintatapajono");
                jono.setTasapistesaanto(Tasapistesaanto.ARVONTA);
                jono.setSiirretaanSijoitteluun(true);

                valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaihe.getOid(), jono, null);

                for (Kielikoodi k : Kielikoodi.values()) {
                    Opetuskielikoodi opetuskieli = new Opetuskielikoodi();
                    opetuskieli.setUri(k.kieliUri);
                    opetuskieli.setNimiFi(k.nimi);
                    opetuskieli.setNimiSv(k.nimi);
                    opetuskieli.setNimiEn(k.nimi);


                    Valintaryhma kielivalintaryhma = new Valintaryhma();
                    kielivalintaryhma.setNimi(k.nimi);
                    kielivalintaryhma.setHakuOid(HAKU_OID);

                    kielivalintaryhma = valintaryhmaService.insert(kielivalintaryhma, valintaryhma.getOid());

                    if (nimi.contains(", pk")) {
                        insertKoe("Pääsykokeelliset", nimi, kielivalintaryhma, toisenAsteenPeruskoulupohjainenPeruskaava, opetuskieli, hakukohdekoodi);
                        insertEiKoetta("Pääsykokeettomat", nimi, kielivalintaryhma, toisenAsteenPeruskoulupohjainenPeruskaava, opetuskieli, hakukohdekoodi);
                    } else {
                        insertKoe("Pääsykokeelliset", nimi, kielivalintaryhma, toisenAsteenYlioppilaspohjainenPeruskaava, opetuskieli, hakukohdekoodi);
                        insertEiKoetta("Pääsykokeettomat", nimi, kielivalintaryhma, toisenAsteenYlioppilaspohjainenPeruskaava, opetuskieli, hakukohdekoodi);
                    }
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }

        }
    }

    private void insertKoe(String koeNimi, String hakukohdeNimi, Valintaryhma valintaryhma, Laskentakaava peruskaava,
                           Opetuskielikoodi opetuskielikoodi, Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma koe = new Valintaryhma();
        koe.setNimi(koeNimi);
        koe.setHakuOid(HAKU_OID);
        koe = valintaryhmaService.insert(koe, valintaryhma.getOid());

        opetuskielikoodiService.lisaaOpetuskielikoodiValintaryhmalle(koe.getOid(), opetuskielikoodi);
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);
        Valintakoekoodi valintakoekoodi = new Valintakoekoodi();
        valintakoekoodi.setUri(OLETUS_VALINTAKOEURI);

        valintakoekoodiService.lisaaValintakoekoodiValintaryhmalle(koe.getOid(), valintakoekoodi);

        ValintaperusteViite valintaperusteViite = GenericHelper.luoValintaperusteViite(hakukohdeNimi, true, true, Valintaperustelahde.HAETTAVA_ARVO);
        Funktiokutsu valintakoe = GenericHelper.luoHaeLukuarvo(valintaperusteViite);

        Funktiokutsu funktiokutsu = GenericHelper.luoSumma(valintakoe, peruskaava);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaava(funktiokutsu, hakukohdeNimi + " - " + koe.getNimi());
        laskentakaava = asetaValintaryhmaJaTallennaKantaan(laskentakaava, koe);

        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(0);
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid()).get(0);

        Jarjestyskriteeri kriteeri = new Jarjestyskriteeri();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(hakukohdeNimi + " - " + koe.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, laskentakaava.getId());
    }

    private void insertEiKoetta(String koeNimi, String hakukohdeNimi, Valintaryhma valintaryhma, Laskentakaava peruskaava
            , Opetuskielikoodi opetuskielikoodi, Hakukohdekoodi hakukohdekoodi) {
        Valintaryhma koe = new Valintaryhma();
        koe.setNimi(koeNimi);
        koe.setHakuOid(HAKU_OID);
        koe = valintaryhmaService.insert(koe, valintaryhma.getOid());

        opetuskielikoodiService.lisaaOpetuskielikoodiValintaryhmalle(koe.getOid(), opetuskielikoodi);
        hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(koe.getOid(), hakukohdekoodi);

        Funktiokutsu funktiokutsu = GenericHelper.luoSumma(peruskaava);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaava(funktiokutsu, hakukohdeNimi + " - " + koe.getNimi());
        laskentakaava = asetaValintaryhmaJaTallennaKantaan(laskentakaava, koe);

        ValinnanVaihe vaihe = valinnanVaiheService.findByValintaryhma(koe.getOid()).get(0);
        Valintatapajono jono = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid()).get(0);

        Jarjestyskriteeri kriteeri = new Jarjestyskriteeri();
        kriteeri.setAktiivinen(true);
        kriteeri.setMetatiedot(hakukohdeNimi + " - " + koe.getNimi());
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(jono.getOid(), kriteeri, null, laskentakaava.getId());
    }


    private Laskentakaava asetaValintaryhmaJaTallennaKantaan(Laskentakaava kaava, Valintaryhma valintaryhma) {
        kaava.setValintaryhma(valintaryhma);
        return laskentakaavaService.insert(kaava);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
