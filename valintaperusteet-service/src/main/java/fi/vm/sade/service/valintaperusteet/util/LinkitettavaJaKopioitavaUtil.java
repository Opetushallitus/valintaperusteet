package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Kopioitava;
import fi.vm.sade.service.valintaperusteet.model.Linkitettava;
import fi.vm.sade.service.valintaperusteet.model.LinkitettavaJaKopioitava;
import java.util.*;
import java.util.function.Predicate;

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

  private static <T> Iterable<T> takeWhile(Predicate<T> p, Iterable<T> iterable) {
    return () -> {
      Iterator<T> i = iterable.iterator();
      if (!i.hasNext()) {
        return i;
      }
      return new Iterator<T>() {
        private T next = i.next();
        private boolean hasNext = p.test(this.next);

        @Override
        public boolean hasNext() {
          return hasNext;
        }

        @Override
        public T next() {
          if (hasNext) {
            T current = this.next;
            if (i.hasNext()) {
              this.next = i.next();
              this.hasNext = p.test(this.next);
            } else {
              this.hasNext = false;
            }
            return current;
          }
          throw new NoSuchElementException("");
        }
      };
    };
  }

  private static <T> Iterable<T> dropWhile(Predicate<T> p, Iterable<T> iterable) {
    return () -> {
      Iterator<T> i = iterable.iterator();
      while (i.hasNext()) {
        T t = i.next();
        if (!p.test(t)) {
          return new Iterator<T>() {
            boolean first = true;

            @Override
            public boolean hasNext() {
              return first || i.hasNext();
            }

            @Override
            public T next() {
              if (first) {
                first = false;
                return t;
              }
              return i.next();
            }
          };
        }
      }
      return i;
    };
  }

  public static <C extends Collection<T>, T extends LinkitettavaJaKopioitava<T, C>>
      List<T> jarjestaUudelleenMasterJarjestyksenMukaan(
          List<T> jarjestettavat, List<T> uusiMasterJarjestys) {
    LinkedList<String> uusiJarjestys = new LinkedList<>();
    for (T t : takeWhile(t -> t.getMaster() == null, jarjestettavat)) {
      uusiJarjestys.add(t.getOid());
    }
    for (T master : uusiMasterJarjestys) {
      for (T t :
          takeWhile(
              t -> t.getMaster() == null || master.equals(t.getMaster()),
              dropWhile(t -> !master.equals(t.getMaster()), jarjestettavat))) {
        uusiJarjestys.add(t.getOid());
      }
    }
    return jarjestaUudelleen(jarjestettavat, uusiJarjestys);
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
