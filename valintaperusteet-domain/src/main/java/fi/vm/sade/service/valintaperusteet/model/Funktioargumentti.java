package fi.vm.sade.service.valintaperusteet.model;

public class Funktioargumentti implements Comparable<Funktioargumentti> {
  private FunktioargumenttiId id;

  private long version;

  private Funktiokutsu funktiokutsuChild;

  private Laskentakaava laskentakaavaChild;

  private int indeksi;

  public Funktioargumentti(FunktioargumenttiId id,
                           long version,
                           Funktiokutsu funktiokutsuChild,
                           Laskentakaava laskentakaavaChild,
                           int indeksi) {
    this.id = id;
    this.version = version;
    this.funktiokutsuChild = funktiokutsuChild;
    this.laskentakaavaChild = laskentakaavaChild;
    this.indeksi = indeksi;
  }

  public FunktioargumenttiId getId() {
    return id;
  }

  public Funktiokutsu getFunktiokutsuChild() {
    return funktiokutsuChild;
  }

  public Laskentakaava getLaskentakaavaChild() {
    return laskentakaavaChild;
  }

  public int getIndeksi() {
    return indeksi;
  }

  @Override
  public int compareTo(Funktioargumentti o) {
    return indeksi - o.indeksi;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Funktioargumentti that = (Funktioargumentti) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
