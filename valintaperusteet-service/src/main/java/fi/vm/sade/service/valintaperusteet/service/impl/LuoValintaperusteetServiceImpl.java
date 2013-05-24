package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
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

    @Override
    public void luo() {
        Laskentakaava pk_ai = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja Kirjallisuus, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_historia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.historia, "Historia, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_yhteiskuntaoppi = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_matematiikka = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.matematiikka, "Matematiikka, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_fysiikka = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.fysiikka, "Fysiikka, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kemia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kemia, "Kemia, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_biologia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.biologia, "Biologia, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kuvataide = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kuvataide, "Kuvataide, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_musiikki = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.musiikki, "Musiikki, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_maantieto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.maantieto, "Maantieto, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kasityo = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kasityo, "Käsityö, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kotitalous = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kotitalous, "Kotitalous, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_liikunta = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.liikunta, "Liikunta, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_terveystieto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.terveystieto, "Terveystieto, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_uskonto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.uskonto, "Uskonto tai elämänkatsomustieto, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a11Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a11Kieli, "1. A1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a12Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a12Kieli, "2. A1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a13Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a13Kieli, "3. A1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a21Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a21Kieli, "1. A2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a22Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a22Kieli, "2. A2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a23Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a23Kieli, "3. A2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b1Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b1Kieli, "B1-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b21Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b21Kieli, "1. B2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b22Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b22Kieli, "2. B2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b23Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b23Kieli, "3. B2-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b31Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b31Kieli, "1. B3-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b32Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b32Kieli, "2. B3-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b33Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b33Kieli, "3. B3-Kieli, PK päättötodistus, mukaanlukien valinnaiset"));


        //pisteytysmalli
        Laskentakaava pk_painotettavatKeskiarvotLaskentakaava =
                laskentakaavaService.insert(PkAineetHelper.luoPainotettavatKeskiarvotLaskentakaava(pk_kuvataide, pk_musiikki, pk_kasityo, pk_kotitalous, pk_liikunta));

        Laskentakaava pkPohjainenLukuaineidenKeskiarvo = laskentakaavaService.insert(PkAineetHelper.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pk_ai, pk_historia, pk_yhteiskuntaoppi,
                pk_matematiikka, pk_fysiikka, pk_kemia, pk_biologia, pk_kuvataide, pk_musiikki, pk_maantieto, pk_kasityo, pk_kotitalous, pk_liikunta, pk_terveystieto,
                pk_uskonto, pk_a11Kieli, pk_a12Kieli, pk_a13Kieli, pk_a21Kieli, pk_a22Kieli, pk_a23Kieli, pk_b1Kieli, pk_b21Kieli, pk_b22Kieli, pk_b23Kieli, pk_b31Kieli,
                pk_b32Kieli, pk_b33Kieli));

        //pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenLukuaineidenKeskiarvo, "Yleinen koulumenestys pisteytysmalli, PK"));
        Laskentakaava pk_pohjakoulutuspisteytysmalli = laskentakaavaService.insert(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
        Laskentakaava pk_ilmanKoulutuspaikkaaPisteytysmalli = laskentakaavaService.insert(PkPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());
        Laskentakaava hakutoivejarjestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoHakutoivejarjestyspisteteytysmalli());
        Laskentakaava tyokokemuspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentakaava sukupuolipisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());


        // Pk koostava iso kaava
        Laskentakaava toisenAsteenPeruskoulupohjainenPeruskaava = laskentakaavaService.insert(PkPohjaiset.luoToisenAsteenPeruskoulupohjainenPeruskaava(pk_painotettavatKeskiarvotLaskentakaava,
                pk_yleinenkoulumenestyspisteytysmalli, pk_pohjakoulutuspisteytysmalli, pk_ilmanKoulutuspaikkaaPisteytysmalli, hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli));

        Laskentakaava lk_ai = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja kirjallisuus, LK päättötodistus"));
        Laskentakaava lk_historia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.historia, "Historia, LK päättötodistus"));
        Laskentakaava lk_yhteiskuntaoppi = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, LK päättötodistus"));
        Laskentakaava lk_matematiikka = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.matematiikka, "Matematiikka, LK päättötodistus"));
        Laskentakaava lk_fysiikka = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.fysiikka, "Fysiikka, LK päättötodistus"));
        Laskentakaava lk_kemia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.kemia, "Kemia, LK päättötodistus"));
        Laskentakaava lk_biologia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.biologia, "Biologia, LK päättötodistus"));
        Laskentakaava lk_kuvataide = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.kuvataide, "Kuvataide, LK päättötodistus"));
        Laskentakaava lk_musiikki = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.musiikki, "Musiikki, LK päättötodistus"));
        Laskentakaava lk_maantieto = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.maantieto, "Maantieto, LK päättötodistus"));
        Laskentakaava lk_filosofia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(YoAineetHelper.filosofia, "Filosofia, LK päättötodistus"));
        Laskentakaava lk_psykologia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(YoAineetHelper.psykologia, "Psykologia, LK päättötodistus"));
        Laskentakaava lk_liikunta = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.liikunta, "Liikunta, LK päättötodistus"));
        Laskentakaava lk_terveystieto = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.terveystieto, "Terveystieto, LK päättötodistus"));
        Laskentakaava lk_uskonto = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.uskonto, "Uskonto tai elämänkatsomustieto, LK päättötodistus"));
        Laskentakaava lk_a11Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a11Kieli, "1. A1-Kieli, LK päättötodistus"));
        Laskentakaava lk_a12Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a12Kieli, "2. A1-Kieli, LK päättötodistus"));
        Laskentakaava lk_a13Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a13Kieli, "3. A1-Kieli, LK päättötodistus"));
        Laskentakaava lk_a21Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a21Kieli, "1. A2-Kieli, LK päättötodistus"));
        Laskentakaava lk_a22Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a22Kieli, "2. A2-Kieli, LK päättötodistus"));
        Laskentakaava lk_a23Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a23Kieli, "3. A2-Kieli, LK päättötodistus"));
        Laskentakaava lk_b1Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b1Kieli, "B1-Kieli, LK päättötodistus"));
        Laskentakaava lk_b21Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b21Kieli, "1. B2-Kieli, LK päättötodistus"));
        Laskentakaava lk_b22Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b22Kieli, "2. B2-Kieli, LK päättötodistus"));
        Laskentakaava lk_b23Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b23Kieli, "3. B2-Kieli, LK päättötodistus"));
        Laskentakaava lk_b31Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b31Kieli, "1. B3-Kieli, LK päättötodistus"));
        Laskentakaava lk_b32Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b32Kieli, "2. B3-Kieli, LK päättötodistus"));
        Laskentakaava lk_b33Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b33Kieli, "3. B3-Kieli, LK päättötodistus"));


        Laskentakaava lk_paattotodistuksenkeskiarvo = laskentakaavaService.insert(YoAineetHelper.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(lk_ai, lk_historia,
                lk_yhteiskuntaoppi, lk_matematiikka, lk_fysiikka, lk_kemia, lk_biologia, lk_kuvataide, lk_musiikki, lk_maantieto, lk_filosofia, lk_psykologia, lk_liikunta,
                lk_terveystieto, lk_uskonto, lk_terveystieto, lk_a11Kieli, lk_a12Kieli, lk_a13Kieli, lk_a21Kieli, lk_a22Kieli, lk_a23Kieli, lk_b1Kieli, lk_b21Kieli, lk_b22Kieli,
                lk_b23Kieli, lk_b31Kieli, lk_b32Kieli, lk_b33Kieli));

        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"));

        // Yo koostava iso kaava
        Laskentakaava toisenAsteenYlioppilaspohjainenPeruskaava = laskentakaavaService.insert(YoPohjaiset.luoToisenAsteenYlioppilaspohjainenPeruskaava(hakutoivejarjestyspisteytysmalli,
                tyokokemuspisteytysmalli, sukupuolipisteytysmalli, lk_yleinenkoulumenestyspisteytysmalli));
    }
}
