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
        Laskentakaava pk_ai = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja Kirjallisuus, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_historia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.historia, "Historia, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_yhteiskuntaoppi = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_matematiikka = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.matematiikka, "Matematiikka, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_fysiikka = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.fysiikka, "Fysiikka, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kemia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kemia, "Kemia, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_biologia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.biologia, "Biologia, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kuvataide = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kuvataide, "Kuvataide, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_musiikki = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.musiikki, "Musiikki, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_maantieto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.maantieto, "Maantieto, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kasityo = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kasityo, "Käsityö, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kotitalous = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.kotitalous, "Kotitalous, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_liikunta = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.liikunta, "Liikunta, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_terveystieto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.terveystieto, "Terveystieto, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_uskonto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.uskonto, "Uskonto tai elämänkatsomustieto, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a1Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a1Kieli, "A1-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a2Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.a2Kieli, "A2-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b1Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b1Kieli, "B1-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b2Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b2Kieli, "B2-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b3Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(GenericHelper.b3Kieli, "B3-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));

        //pisteytysmalli
        Laskentakaava painotettavatKeskiarvotLaskentakaava =
                laskentakaavaService.insert(PkAineetHelper.luoPainotettavatKeskiarvotLaskentakaava(pk_kuvataide, pk_musiikki, pk_kasityo, pk_kotitalous, pk_liikunta));

        Laskentakaava pkPohjainenLukuaineidenKeskiarvo = laskentakaavaService.insert(PkAineetHelper.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pk_ai, pk_historia, pk_yhteiskuntaoppi, pk_matematiikka, pk_fysiikka, pk_kemia, pk_biologia,
                pk_kuvataide, pk_musiikki, pk_maantieto, pk_kasityo, pk_kotitalous, pk_liikunta, pk_terveystieto, pk_uskonto, pk_a1Kieli, pk_a2Kieli, pk_b1Kieli, pk_b2Kieli, pk_b3Kieli));

        //pisteytysmalli
        Laskentakaava pk_yleinenkoulumenestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(pkPohjainenLukuaineidenKeskiarvo, "Yleinen koulumenestys pisteytysmalli, PK"));
        Laskentakaava pohjakoulutuspisteytysmalli = laskentakaavaService.insert(PkPohjaiset.luoPohjakoulutuspisteytysmalli());
        Laskentakaava ilmanKoulutuspaikkaaPisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());
        Laskentakaava hakutoivejarjestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoHakutoivejarjestyspisteteytysmalli());
        Laskentakaava tyokokemuspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());
        Laskentakaava sukupuolipisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());


        Laskentakaava lk_ai = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja kirjallisuus, LK päästötodistus"));
        Laskentakaava lk_historia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.historia, "Historia, LK päästötodistus"));
        Laskentakaava lk_yhteiskuntaoppi = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, LK päästötodistus"));
        Laskentakaava lk_matematiikka = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.matematiikka, "Matematiikka, LK päästötodistus"));
        Laskentakaava lk_fysiikka = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.fysiikka, "Fysiikka, LK päästötodistus"));
        Laskentakaava lk_kemia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.kemia, "Kemia, LK päästötodistus"));
        Laskentakaava lk_biologia = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.biologia, "Biologia, LK päästötodistus"));
        Laskentakaava lk_kuvataide = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.kuvataide, "Kuvataide, LK päästötodistus"));
        Laskentakaava lk_musiikki = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.musiikki, "Musiikki, LK päästötodistus"));
        Laskentakaava lk_maantieto = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.maantieto, "Maantieto, LK päästötodistus"));
        Laskentakaava lk_kasityo = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.kasityo, "Käsityö, LK päästötodistus"));
        Laskentakaava lk_kotitalous = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.kotitalous, "Kotitalous, LK päästötodistus"));
        Laskentakaava lk_liikunta = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.liikunta, "Liikunta, LK päästötodistus"));
        Laskentakaava lk_terveystieto = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.terveystieto, "Terveystieto, LK päästötodistus"));
        Laskentakaava lk_uskonto = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.uskonto, "Uskonto tai elämänkatsomustieto, LK päästötodistus"));
        Laskentakaava lk_a1Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a1Kieli, "A1-Kieli, LK päästötodistus"));
        Laskentakaava lk_a2Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.a2Kieli, "A2-Kieli, LK päästötodistus"));
        Laskentakaava lk_b1Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b1Kieli, "B1-Kieli, LK päästötodistus"));
        Laskentakaava lk_b2Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b2Kieli, "B2-Kieli, LK päästötodistus"));
        Laskentakaava lk_b3Kieli = laskentakaavaService.insert(YoAineetHelper.luoYOAine(GenericHelper.b3Kieli, "B3-Kieli, LK päästötodistus"));

        Laskentakaava lk_paattotodistuksenkeskiarvo = laskentakaavaService.insert(YoAineetHelper.luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(lk_ai, lk_historia, lk_yhteiskuntaoppi, lk_matematiikka, lk_fysiikka,
                lk_kemia, lk_biologia, lk_kuvataide, lk_musiikki, lk_maantieto, lk_kasityo, lk_kotitalous, lk_liikunta, lk_terveystieto, lk_uskonto, lk_terveystieto, lk_a1Kieli, lk_a2Kieli, lk_b1Kieli, lk_b2Kieli, lk_b3Kieli));

        Laskentakaava lk_yleinenkoulumenestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoYleinenKoulumenestysLaskentakaava(lk_paattotodistuksenkeskiarvo, "Yleinen koulumenestys pisteytysmalli, LK"));
    }


}
