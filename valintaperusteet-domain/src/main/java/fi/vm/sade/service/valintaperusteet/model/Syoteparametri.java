package fi.vm.sade.service.valintaperusteet.model;

import java.util.Objects;

public class Syoteparametri {
  private SyoteparametriId id;

  private long version;

  private String avain;

  private String arvo;

  public Syoteparametri(SyoteparametriId id,
                        long version,
                        String avain,
                        String arvo) {
    Objects.requireNonNull(arvo, "arvo tulee olla asetettu");
    this.id = id;
    this.version = version;
    this.avain = avain;
    this.arvo = arvo;
  }

  public SyoteparametriId getId() {
    return id;
  }

  public String getAvain() {
    return avain;
  }

  public String getArvo() {
    return arvo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Syoteparametri that = (Syoteparametri) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
