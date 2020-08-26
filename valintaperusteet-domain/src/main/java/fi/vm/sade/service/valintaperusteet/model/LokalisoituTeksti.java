package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.service.valintaperusteet.dto.LokalisoituTekstiDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;

public class LokalisoituTeksti {
  private LokalisoituTekstiId id;

  private long version;

  private String teksti;

  private Kieli kieli;

  public LokalisoituTeksti(LokalisoituTekstiId id,
                           long version,
                           String teksti,
                           Kieli kieli) {
    this.id = id;
    this.version = version;
    this.teksti = teksti;
    this.kieli = kieli;
  }

  public LokalisoituTeksti(LokalisoituTekstiId id,
                           LokalisoituTekstiDTO dto) {
    this.id = id;
    this.version = 0;
    this.teksti = dto.getTeksti();
    this.kieli = dto.getKieli();
  }

  public LokalisoituTekstiId getId() {
    return id;
  }

  public String getTeksti() {
    return teksti;
  }

  public Kieli getKieli() {
    return kieli;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LokalisoituTeksti that = (LokalisoituTeksti) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
