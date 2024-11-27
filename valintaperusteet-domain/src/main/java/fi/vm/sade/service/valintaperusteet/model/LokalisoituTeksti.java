package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import jakarta.persistence.*;

@Entity
@Table(name = "lokalisoitu_teksti")
@Cacheable(true)
public class LokalisoituTeksti extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @Column(name = "teksti", nullable = false)
  private String teksti;

  @Enumerated(EnumType.STRING)
  @Column(name = "kieli", nullable = false)
  private Kieli kieli;

  @JoinColumn(name = "tekstiryhma_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private TekstiRyhma ryhma;

  public String getTeksti() {
    return teksti;
  }

  public void setTeksti(String teksti) {
    this.teksti = teksti;
  }

  public Kieli getKieli() {
    return kieli;
  }

  public void setKieli(Kieli kieli) {
    this.kieli = kieli;
  }

  public TekstiRyhma getRyhma() {
    return ryhma;
  }

  public void setRyhma(TekstiRyhma ryhma) {
    this.ryhma = ryhma;
  }
}
