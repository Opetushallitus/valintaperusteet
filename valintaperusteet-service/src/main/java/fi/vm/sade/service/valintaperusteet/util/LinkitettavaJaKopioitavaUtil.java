package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Kopioitava;
import fi.vm.sade.service.valintaperusteet.model.Linkitettava;
import fi.vm.sade.service.valintaperusteet.model.LinkitettavaJaKopioitava;
import java.util.*;

public abstract class LinkitettavaJaKopioitavaUtil {

  public static <T extends LinkitettavaJaKopioitava> T haeMasterinEdellistaVastaava(
      T edellinenMaster, List<T> lista) {
    T edellinen = null;
    for (T t : lista) {
      if (t == null
          || edellinenMaster == null
          || (t.getMaster() != null && t.getMaster().equals(edellinenMaster))) {
        edellinen = t;
        break;
      }
    }
    return edellinen;
  }

  public static <T extends LinkitettavaJaKopioitava>
      LinkedHashMap<String, T> teeMappiMasterienOidinMukaan(Collection<T> lista) {
    LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
    for (T t : lista) {
      if (t.getMaster() != null) {
        map.put(t.getMaster().getOid(), t);
      }
    }
    return map;
  }

  public static <T extends LinkitettavaJaKopioitava>
      LinkedHashMap<String, T> jarjestaKopiotMasterJarjestyksenMukaan(
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
      if (t == null) {
        continue;
      }

      if (edellinen != null) {
        edellinen.setSeuraava(t);
      }
      t.setEdellinen(edellinen);
      jarjestetty.put(t.getOid(), t);
      edellinen = t;
      // Käydään läpi kaikki seuraavat, jotka eivät ole kopioita
      // ylemmän tason objekteista. Nämä ovat jo valmiiksi oikeassa järjestyksessä eikä niitä
      // tarvitse järjestää.
      while (edellinen.getSeuraava() != null && edellinen.getSeuraava().getMaster() == null) {
        edellinen = (T) edellinen.getSeuraava();
        jarjestetty.put(edellinen.getOid(), edellinen);
      }
    }
    edellinen.setSeuraava(null);
    return jarjestetty;
  }

  public static <T extends LinkitettavaJaKopioitava> LinkedHashMap<String, T> teeMappiOidienMukaan(
      Collection<T> list) {
    LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
    for (T t : list) {
      map.put(t.getOid(), t);
    }
    return map;
  }

  private static <T extends Linkitettava<T>> T seuraava(Iterable<T> linkitettavat, T edellinen) {
    for (T t : linkitettavat) {
      if (Objects.equals(t.getEdellinen(), edellinen)) {
        return t;
      }
    }
    return null;
  }

  public static <T extends Linkitettava<T>> List<T> jarjesta(Iterable<T> linkitettavat) {
    ArrayList<T> jarjestetyt = new ArrayList<>();
    T edellinen = seuraava(linkitettavat, null);
    while (edellinen != null) {
      jarjestetyt.add(edellinen);
      edellinen = seuraava(linkitettavat, edellinen);
    }
    return jarjestetyt;
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

  public static <T extends Linkitettava<T>> List<T> jarjestaUudelleen(
      List<T> jarjestettavat, List<String> uusiJarjestys) {
    LinkedList<T> kaanteinenJarjestys = new LinkedList<>();
    for (String oid : uusiJarjestys) {
      kaanteinenJarjestys.push(
          jarjestettavat.stream()
              .filter(t -> t.getOid().equals(oid))
              .findFirst()
              .orElseThrow(
                  () -> new RuntimeException("Uusi järjestys sisältää ylimääräisiä alkioita")));
    }
    if (kaanteinenJarjestys.size() != jarjestettavat.size()) {
      throw new RuntimeException("Uusi järjestys ei ole täydellinen");
    }

    kaanteinenJarjestys.stream()
        .reduce(
            (seuraava, edellinen) -> {
              seuraava.setEdellinen(edellinen);
              edellinen.setSeuraava(seuraava);
              return edellinen;
            })
        .ifPresent(
            ensimmainen -> {
              kaanteinenJarjestys.peek().setSeuraava(null);
              ensimmainen.setEdellinen(null);
            });

    Collections.reverse(kaanteinenJarjestys);
    return kaanteinenJarjestys;
  }

  private static <T extends Kopioitava> void paivitaKopio(
      T t, T alkuperainenMaster, T paivitettyMaster, Kopioija<T> kopioija) {
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
