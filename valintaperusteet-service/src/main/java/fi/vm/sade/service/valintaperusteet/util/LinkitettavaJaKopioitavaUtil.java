package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Kopioitava;
import fi.vm.sade.service.valintaperusteet.model.Linkitettava;
import fi.vm.sade.service.valintaperusteet.model.LinkitettavaJaKopioitava;

import java.util.*;

/**
 * User: kwuoti
 * Date: 14.2.2013
 * Time: 10.09
 */
public abstract class LinkitettavaJaKopioitavaUtil {

    public static <T extends LinkitettavaJaKopioitava> T haeMasterinEdellistaVastaava(
            T edellinenMaster, List<T> lista) {
        T edellinen = null;
        for (T t : lista) {
            if (t == null || edellinenMaster == null ||
                    (t.getMaster() != null && t.getMaster().equals(edellinenMaster))) {
                edellinen = t;
                break;
            }
        }

        return edellinen;
    }

    public static <T extends LinkitettavaJaKopioitava> LinkedHashMap<String, T> teeMappiMasterienOidinMukaan(Collection<T> lista) {
        LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
        for (T t : lista) {
            if (t.getMaster() != null) {
                map.put(t.getMaster().getOid(), t);
            }
        }

        return map;
    }

    public static <T extends LinkitettavaJaKopioitava> LinkedHashMap<String, T> jarjestaKopiotMasterJarjestyksenMukaan(
            LinkedHashMap<String, T> jarjestettavatKopiot,
            LinkedHashMap<String, T> uusiMasterJarjestys) {
        Iterator<Map.Entry<String, T>> i = jarjestettavatKopiot.entrySet().iterator();

        T edellinen = null;
        LinkedHashMap<String, T> jarjestetty = new LinkedHashMap<String, T>();

        // Jos ensimmäiset eivät ole kopioita (ts. ne on määritelty suoraan
        // käsiteltävälle masterille) lisätään ne uuden järjestyksen ensimmäiseksi
        while (i.hasNext()) {
            T t = i.next().getValue();
            if (t.getMaster() == null) {
                edellinen = t;
                jarjestetty.put(t.getOid(), t);
            } else {
                break;
            }
        }

        LinkedHashMap<String, T> masterienOidinMukaan =
                teeMappiMasterienOidinMukaan(jarjestettavatKopiot.values());

        for (String masterOid : uusiMasterJarjestys.keySet()) {
            T t = masterienOidinMukaan.get(masterOid);
            if (edellinen != null) {
                edellinen.setSeuraava(t);
            }
            t.setEdellinen(edellinen);

            jarjestetty.put(t.getOid(), t);
            edellinen = t;

            // Käydään läpi kaikki seuraavat, jotka eivät ole kopioita
            // ylemmän tason objekteista. Nämä ovat jo valmiiksi oikeassa järjestyksessä eikä niitä
            // tarvitse järjestää.
            while (edellinen.getSeuraava() != null &&
                    edellinen.getSeuraava().getMaster() == null) {
                edellinen = (T) edellinen.getSeuraava();
                jarjestetty.put(edellinen.getOid(), edellinen);
            }
        }
        edellinen.setSeuraava(null);

        return jarjestetty;
    }

    public static <T extends LinkitettavaJaKopioitava> LinkedHashMap<String, T> teeMappiOidienMukaan(Collection<T> list) {
        LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
        for (T t : list) {
            map.put(t.getOid(), t);
        }

        return map;
    }

    public static <T extends Linkitettava> List<T> jarjesta(Collection<T> linkitettavat) {
        List<T> jarjestetty = new ArrayList<T>();

        T linkitettava = null;

        // Haetaan linkitetyn listan ensimmäinen elementti
        for (T t : linkitettavat) {

            // Oheinen checki on syystä, että querydsl-palauttaa kyselyssä collectionin, joka sisältää null-elementin
            if (t != null) {
                // Ensimmäisen elementin edellinen on null
                if (t.getEdellinen() == null) {
                    linkitettava = t;
                    break;
                }
            }
        }

        // Loopataan ensimmäisestä elementistä alkaen ja tungetaan kaikki uuteen listaan
        while (linkitettava != null) {
            jarjestetty.add(linkitettava);
            linkitettava = (T) linkitettava.getSeuraava();
        }

        return jarjestetty;
    }

    public static <T extends Linkitettava> void asetaSeuraava(T edellinen, T seuraava) {
        seuraava.setEdellinen(edellinen);
        if (edellinen != null) {
            if (edellinen.getSeuraava() != null) {
                edellinen.getSeuraava().setEdellinen(seuraava);
            }

            edellinen.setSeuraava(seuraava);
        }
    }

    private static void tarkistaOidListaSisaltaaKaikkiAnnetut(Collection<String> annetutOidit,
                                                              Collection<String> kaikkiOidit) {
        if (!annetutOidit.containsAll(kaikkiOidit)) {
            throw new RuntimeException("OID-lista ei ole täydellinen.");
        }
    }


    public static <T extends Linkitettava> LinkedHashMap<String, T> jarjestaOidListanMukaan(
            LinkedHashMap<String, T> jarjestettavat,
            List<String> uusiJarjestys) {
        tarkistaOidListaSisaltaaKaikkiAnnetut(uusiJarjestys, jarjestettavat.keySet());

        T edellinen = null;
        LinkedHashMap<String, T> jarjestetty = new LinkedHashMap<String, T>();

        for (String oid : uusiJarjestys) {
            T t = jarjestettavat.get(oid);

            if (edellinen != null) {
                edellinen.setSeuraava(t);
            }

            t.setEdellinen(edellinen);
            jarjestetty.put(t.getOid(), t);
            edellinen = t;
        }
        edellinen.setSeuraava(null);

        return jarjestetty;
    }

    private static <T extends Kopioitava> void paivitaKopio(T t,
                                                            T alkuperainenMaster,
                                                            T paivitettyMaster,
                                                            Kopioija<T> kopioija) {
        // Otetaan talteen alkuperäinen ja päivitetään tiedot kantaobjektiin masterilta
        T alkuperainen = kopioija.luoKlooni(t);
        kopioija.kopioiTiedotMasteriltaKopiolle(alkuperainenMaster, paivitettyMaster, t);


        // Päivitetään kopion kopiot rekursiivisesti
        for (T kopio : (Collection<T>) t.getKopiot()) {
            paivitaKopio(kopio, alkuperainen, t, kopioija);
        }
    }

    public static <T extends Kopioitava> T paivita(T managed, T incoming, Kopioija<T> kopioija) {
        // Otetaan talteen alkuperainen
        T alkuperainen = kopioija.luoKlooni(managed);

        // Kopioidaan tiedot kantaan tallennettuun objektiin
        kopioija.kopioiTiedot(incoming, managed);

        // Päivitetään kopiot
        for (T kopio : (Collection<T>) managed.getKopiot()) {
            paivitaKopio(kopio, alkuperainen, managed, kopioija);
        }

        return managed;
    }
}
