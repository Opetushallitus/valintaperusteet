package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkAineetHelper;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkJaYoPohjaiset;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkPohjaiset;
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
        Laskentakaava pk_ai = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.aidinkieliJaKirjallisuus, "Äidinkieli ja Kirjallisuus, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_historia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.historia, "Historia, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_yhteiskuntaoppi = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.yhteiskuntaoppi, "Yhteiskuntaoppi, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_matematiikka = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.matematiikka, "Matematiikka, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kemia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.kemia, "Kemia, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_biologia = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.biologia, "Biologia, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kuvataide = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.kuvataide, "Kuvataide, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_musiikki = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.musiikki, "Musiikki, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_maantieto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.maantieto, "Maantieto, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kasityo = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.kasityo, "Käsityö, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_kotitalous = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.kotitalous, "Kotitalous, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_liikunta = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.liikunta, "Liikunta, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_terveystieto = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.terveystieto, "Terveystieto, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a1Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.a1Kieli, "A1-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_a2Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.a2Kieli, "A2-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b1Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.b1Kieli, "B1-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b2Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.b2Kieli, "B2-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));
        Laskentakaava pk_b3Kieli = laskentakaavaService.insert(PkAineetHelper.luoPKAine(PkAineetHelper.b3Kieli, "B3-Kieli, PK päästötodistus, mukaanlukien valinnaiset"));

        //pisteytysmalli
        Laskentakaava painotettavatKeskiarvotLaskentakaava =
                laskentakaavaService.insert(PkAineetHelper.luoPainotettavatKeskiarvotLaskentakaava(pk_kuvataide, pk_musiikki, pk_kasityo, pk_kotitalous, pk_liikunta));

        Laskentakaava pkPohjainenLukuaineidenKeskiarvo = laskentakaavaService.insert(PkAineetHelper.luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(pk_ai, pk_historia, pk_yhteiskuntaoppi, pk_matematiikka, pk_kemia, pk_biologia,
                pk_kuvataide, pk_musiikki, pk_maantieto, pk_kasityo, pk_kotitalous, pk_liikunta, pk_terveystieto, pk_a1Kieli, pk_a2Kieli, pk_b1Kieli, pk_b2Kieli, pk_b3Kieli));

        //pisteytysmalli
        Laskentakaava yleinenKoulumenestys = laskentakaavaService.insert(PkAineetHelper.luoYleinenKoulumenestysLaskentakaava(pkPohjainenLukuaineidenKeskiarvo));

        Laskentakaava pohjakoulutuspisteytysmalli = laskentakaavaService.insert(PkPohjaiset.luoPohjakoulutuspisteytysmalli());

        Laskentakaava ilmanKoulutuspaikkaaPisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.ilmanKoulutuspaikkaaPisteytysmalli());

        Laskentakaava hakutoivejarjestyspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoHakutoivejarjestyspisteteytysmalli());

        Laskentakaava tyokokemuspisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoTyokokemuspisteytysmalli());

        Laskentakaava sukupuolipisteytysmalli = laskentakaavaService.insert(PkJaYoPohjaiset.luoSukupuolipisteytysmalli());
    }


}
