package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.service.PaasykoeTunnisteetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 *         Palvelu hakukohteeseen liittyvien pääsykoetunnisteiden hakuun.
 * 
 * @Deprecated Älä käytä tätä mihinkään! Tee uusi toteutus pääsykoetiedon
 *             hankintaan kun uudet palvelurajapinnat valmistuu!
 * 
 */
@Deprecated
@Service
@Transactional
public class PaasykoeTunnisteetServiceImpl implements PaasykoeTunnisteetService {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    public List<String> haeTunnisteetHakukohteelle(String hakukohdeoid) {
//        Set<String> tunnisteet = new HashSet<String>();
//        Set<Long> funktioIds = new HashSet<Long>();
//        Set<Long> laskentakaavaIds = new HashSet<Long>();
//        for (HakukohdeViite viite : hakukohdeViiteDAO.findBy("oid", hakukohdeoid)) {
//            for (ValinnanVaihe valinnanvaihe : viite.getValinnanvaiheet()) {
//                for (Valintatapajono jono : valinnanvaihe.getJonot()) {
//                    for (Jarjestyskriteeri kriteeri : jono.getJarjestyskriteerit()) {
//                        Laskentakaava laskentakaava = kriteeri.getLaskentakaava();
//                        if (laskentakaava != null) {
//                            Queue<Laskentakaava> laskentakaavaJono = new ArrayQueue<Laskentakaava>();
//                            laskentakaavaJono.add(laskentakaava);
//                            // ulompi 'laskentakaava'-rekursio
//                            for (Laskentakaava kaava : laskentakaavaJono) {
//                                laskentakaavaIds.add(kaava.getId());
//
//                                if (kaava.getFunktiokutsu() != null) {
//                                    Queue<Funktiokutsu> funktioJono = new ArrayQueue<Funktiokutsu>();
//                                    funktioJono.add(kaava.getFunktiokutsu());
//                                    // sisempi 'funktiokutsu'-rekursio
//                                    // tämä metodi kerää tunnisteet ja täydentää
//                                    // funktiojonoa ja laskentakaavajonoa
//                                    tunnisteFunktiokutsustaHantarekursiolla(laskentakaavaIds, funktioIds, tunnisteet,
//                                            funktioJono, laskentakaavaJono);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            return Lists.newArrayList(tunnisteet);
//        }
//        throw new HakukohdeViiteEiOleOlemassaException(hakukohdeoid);
        throw new RuntimeException();
    }

    // Käsittelee yhden alkion funktiojonossa ja kutsuu itseään rekursiivisesti
    // jonolle uudelleen mikäli jono ei ole tyhjä
//    private static Set<String> tunnisteFunktiokutsustaHantarekursiolla(Set<Long> laskukaavaIds, Set<Long> funktioIds,
//            Set<String> tunnisteet, Queue<Funktiokutsu> funktioJono, Queue<Laskentakaava> laskentakaavaJono) {
//        Funktiokutsu funktiokutsu = funktioJono.poll();
//        if (funktioIds.contains(funktiokutsu.getId())) {
//            if (funktioJono.isEmpty()) {
//                return tunnisteet;
//            }
//            return tunnisteFunktiokutsustaHantarekursiolla(laskukaavaIds, funktioIds, tunnisteet, funktioJono,
//                    laskentakaavaJono);
//        }
//        funktioIds.add(funktiokutsu.getId());
//        if (funktiokutsu.getValintaperuste() != null && funktiokutsu.getValintaperuste().getOnPaasykoe()) {
//            tunnisteet.add(funktiokutsu.getValintaperuste().getTunniste());
//        }
//
//        for (Funktioargumentti argumentti : funktiokutsu.getFunktioargumentit()) {
//            Laskentakaava l = argumentti.getLaskentakaavaChild();
//            Funktiokutsu f = argumentti.getFunktiokutsuChild();
//            if ((l == null && f == null) || (f != null && l != null)) {
//                throw new FunktioargumentinTilaOnVirheellinenException(
//                        "Argumentilla on oltava joko laskentakaava- tai funktiokutsuviite null mutta ei molemmat!");
//            }
//
//            if (f != null) {
//                if (!funktioIds.contains(f.getId())) {
//                    funktioJono.add(f);
//                }
//            } else { // l must be nonnull
//                if (!laskukaavaIds.contains(l.getId())) {
//                    laskentakaavaJono.add(l);
//                }
//            }
//        }
//        if (funktioJono.isEmpty()) {
//            return tunnisteet;
//        }
//        return tunnisteFunktiokutsustaHantarekursiolla(laskukaavaIds, funktioIds, tunnisteet, funktioJono,
//                laskentakaavaJono);
//    }

}
