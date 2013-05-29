package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 25.2.2013
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Transactional
public class LuoValintaperusteetServiceImpl implements LuoValintaperusteetService {

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    private static final String HAKU_OID = "toisenAsteenSyksynYhteishaku";

    @Override
    public void luo() {
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

        Laskentakaava pk_ai = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja Kirjallisuus, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_historia = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.historia, "Historia, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_yhteiskuntaoppi = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_matematiikka = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.matematiikka, "Matematiikka, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_fysiikka = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.fysiikka, "Fysiikka, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_kemia = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.kemia, "Kemia, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_biologia = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.biologia, "Biologia, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_kuvataide = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.kuvataide, "Kuvataide, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_musiikki = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.musiikki, "Musiikki, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_maantieto = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.maantieto, "Maantieto, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_kasityo = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.kasityo, "Käsityö, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_kotitalous = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.kotitalous, "Kotitalous, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_liikunta = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.liikunta, "Liikunta, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_terveystieto = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.terveystieto, "Terveystieto, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_uskonto = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.uskonto, "Uskonto tai elämänkatsomustieto, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_a11Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.a11Kieli, "1. A1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_a12Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.a12Kieli, "2. A1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_a13Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.a13Kieli, "3. A1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_a21Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.a21Kieli, "1. A2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_a22Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.a22Kieli, "2. A2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_a23Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.a23Kieli, "3. A2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b1Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b1Kieli, "B1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b21Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b21Kieli, "1. B2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b22Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b22Kieli, "2. B2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b23Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b23Kieli, "3. B2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b31Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b31Kieli, "1. B3-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b32Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b32Kieli, "2. B3-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);
        Laskentakaava pk_b33Kieli = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKAine(GenericHelper.b33Kieli, "3. B3-Kieli, PK päättötodistus, mukaanlukien valinnaiset"), peruskouluVr);


        //pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPainotettavatKeskiarvotLaskentakaava(pk_kuvataide, pk_musiikki, pk_kasityo, pk_kotitalous, pk_liikunta), peruskouluVr);

        Laskentakaava pkPohjainenLukuaineidenKeskiarvo = asetaValintaryhmaJaTallennaKantaan(PkAineetHelper.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pk_ai, pk_historia, pk_yhteiskuntaoppi,
                pk_matematiikka, pk_fysiikka, pk_kemia, pk_biologia, pk_kuvataide, pk_musiikki, pk_maantieto, pk_kasityo, pk_kotitalous, pk_liikunta, pk_terveystieto,
                pk_uskonto, pk_a11Kieli, pk_a12Kieli, pk_a13Kieli, pk_a21Kieli, pk_a22Kieli, pk_a23Kieli, pk_b1Kieli, pk_b21Kieli, pk_b22Kieli, pk_b23Kieli, pk_b31Kieli,
                pk_b32Kieli, pk_b33Kieli), peruskouluVr);

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

        Laskentakaava lk_ai = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja kirjallisuus, LK päättötodistus"), lukioVr);
        Laskentakaava lk_historia = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.historia, "Historia, LK päättötodistus"), lukioVr);
        Laskentakaava lk_yhteiskuntaoppi = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, LK päättötodistus"), lukioVr);
        Laskentakaava lk_matematiikka = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.matematiikka, "Matematiikka, LK päättötodistus"), lukioVr);
        Laskentakaava lk_fysiikka = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.fysiikka, "Fysiikka, LK päättötodistus"), lukioVr);
        Laskentakaava lk_kemia = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.kemia, "Kemia, LK päättötodistus"), lukioVr);
        Laskentakaava lk_biologia = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.biologia, "Biologia, LK päättötodistus"), lukioVr);
        Laskentakaava lk_kuvataide = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.kuvataide, "Kuvataide, LK päättötodistus"), lukioVr);
        Laskentakaava lk_musiikki = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.musiikki, "Musiikki, LK päättötodistus"), lukioVr);
        Laskentakaava lk_maantieto = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.maantieto, "Maantieto, LK päättötodistus"), lukioVr);
        Laskentakaava lk_filosofia = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(YoAineetHelper.filosofia, "Filosofia, LK päättötodistus"), lukioVr);
        Laskentakaava lk_psykologia = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(YoAineetHelper.psykologia, "Psykologia, LK päättötodistus"), lukioVr);
        Laskentakaava lk_liikunta = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.liikunta, "Liikunta, LK päättötodistus"), lukioVr);
        Laskentakaava lk_terveystieto = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.terveystieto, "Terveystieto, LK päättötodistus"), lukioVr);
        Laskentakaava lk_uskonto = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.uskonto, "Uskonto tai elämänkatsomustieto, LK päättötodistus"), lukioVr);
        Laskentakaava lk_a11Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.a11Kieli, "1. A1-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_a12Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.a12Kieli, "2. A1-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_a13Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.a13Kieli, "3. A1-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_a21Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.a21Kieli, "1. A2-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_a22Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.a22Kieli, "2. A2-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_a23Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.a23Kieli, "3. A2-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b1Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b1Kieli, "B1-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b21Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b21Kieli, "1. B2-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b22Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b22Kieli, "2. B2-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b23Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b23Kieli, "3. B2-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b31Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b31Kieli, "1. B3-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b32Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b32Kieli, "2. B3-Kieli, LK päättötodistus"), lukioVr);
        Laskentakaava lk_b33Kieli = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOAine(GenericHelper.b33Kieli, "3. B3-Kieli, LK päättötodistus"), lukioVr);


        Laskentakaava lk_paattotodistuksenkeskiarvo = asetaValintaryhmaJaTallennaKantaan(YoAineetHelper.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(lk_ai, lk_historia,
                lk_yhteiskuntaoppi, lk_matematiikka, lk_fysiikka, lk_kemia, lk_biologia, lk_kuvataide, lk_musiikki, lk_maantieto, lk_filosofia, lk_psykologia, lk_liikunta,
                lk_terveystieto, lk_uskonto, lk_terveystieto, lk_a11Kieli, lk_a12Kieli, lk_a13Kieli, lk_a21Kieli, lk_a22Kieli, lk_a23Kieli, lk_b1Kieli, lk_b21Kieli, lk_b22Kieli,
                lk_b23Kieli, lk_b31Kieli, lk_b32Kieli, lk_b33Kieli), lukioVr);

        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = asetaValintaryhmaJaTallennaKantaan(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"), lukioVr);

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = asetaValintaryhmaJaTallennaKantaan(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli), lukioVr);
    }

    private Laskentakaava asetaValintaryhmaJaTallennaKantaan(Laskentakaava kaava, Valintaryhma valintaryhma) {
        kaava.setValintaryhma(valintaryhma);
        return laskentakaavaService.insert(kaava);
    }
}
