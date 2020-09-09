package fi.vm.sade.service.valintaperusteet.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "hakijaryhma")
@Cacheable(true)
public class Hakijaryhma extends BaseEntity
    implements LinkitettavaJaKopioitava<Hakijaryhma, Set<Hakijaryhma>> {

  private static final long serialVersionUID = 1L;

  @Column(name = "oid", nullable = false, unique = true)
  private String oid;

  @Column(nullable = false)
  private String nimi;

  @Column private String kuvaus;

  @Column(nullable = false)
  private int kiintio;

  @Column private boolean kaytaKaikki;

  @Column private boolean tarkkaKiintio;

  @Column(name = "kaytetaan_ryhmaan_kuuluvia")
  private boolean kaytetaanRyhmaanKuuluvia = true;

  @OneToMany(mappedBy = "hakijaryhma", fetch = FetchType.LAZY)
  private Set<HakijaryhmaValintatapajono> jonot = new HashSet<>();

  @JoinColumn(name = "valintaryhma_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Valintaryhma valintaryhma;

  @JoinColumn(name = "laskentakaava_id", nullable = false)
  @ManyToOne(optional = false)
  private Laskentakaava laskentakaava;

  @JoinColumn(name = "hakijaryhmatyyppikoodi_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Hakijaryhmatyyppikoodi hakijaryhmatyyppikoodi;

  @JoinColumn(name = "master_hakijaryhma_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Hakijaryhma masterHakijaryhma;

  @JoinColumn(name = "edellinen_hakijaryhma_id")
  @OneToOne(fetch = FetchType.LAZY)
  private Hakijaryhma edellinenHakijaryhma;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "edellinenHakijaryhma")
  private Hakijaryhma seuraavaHakijaryhma;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "masterHakijaryhma")
  private Set<Hakijaryhma> kopioHakijaryhmat = new HashSet<>();

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public int getKiintio() {
    return kiintio;
  }

  public void setKiintio(int kiintio) {
    this.kiintio = kiintio;
  }

  public boolean isKaytaKaikki() {
    return kaytaKaikki;
  }

  public void setKaytaKaikki(boolean kaytaKaikki) {
    this.kaytaKaikki = kaytaKaikki;
  }

  public boolean isTarkkaKiintio() {
    return tarkkaKiintio;
  }

  public void setTarkkaKiintio(boolean tarkkaKiintio) {
    this.tarkkaKiintio = tarkkaKiintio;
  }

  public Set<HakijaryhmaValintatapajono> getJonot() {
    return jonot;
  }

  public void setJonot(Set<HakijaryhmaValintatapajono> jonot) {
    this.jonot = jonot;
  }

  public Valintaryhma getValintaryhma() {
    return valintaryhma;
  }

  public void setValintaryhma(Valintaryhma valintaryhma) {
    this.valintaryhma = valintaryhma;
  }

  public Laskentakaava getLaskentakaava() {
    return laskentakaava;
  }

  public void setLaskentakaava(Laskentakaava laskentakaava) {
    this.laskentakaava = laskentakaava;
  }

  public Hakijaryhmatyyppikoodi getHakijaryhmatyyppikoodi() {
    return hakijaryhmatyyppikoodi;
  }

  public void setHakijaryhmatyyppikoodi(Hakijaryhmatyyppikoodi hakijaryhmatyyppikoodi) {
    this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
  }

  public Hakijaryhma getMasterHakijaryhma() {
    return masterHakijaryhma;
  }

  public void setMasterHakijaryhma(Hakijaryhma masterHakijaryhma) {
    this.masterHakijaryhma = masterHakijaryhma;
  }

  public Hakijaryhma getEdellinenHakijaryhma() {
    return edellinenHakijaryhma;
  }

  public void setEdellinenHakijaryhma(Hakijaryhma edellinenHakijaryhma) {
    this.edellinenHakijaryhma = edellinenHakijaryhma;
  }

  public Hakijaryhma getSeuraavaHakijaryhma() {
    return seuraavaHakijaryhma;
  }

  public void setSeuraavaHakijaryhma(Hakijaryhma seuraavaHakijaryhma) {
    this.seuraavaHakijaryhma = seuraavaHakijaryhma;
  }

  public Set<Hakijaryhma> getKopioHakijaryhmat() {
    return kopioHakijaryhmat;
  }

  public void setKopioHakijaryhmat(Set<Hakijaryhma> kopioHakijaryhmat) {
    this.kopioHakijaryhmat = kopioHakijaryhmat;
  }

  @Transient
  public Long getLaskentakaavaId() {
    return laskentakaava.getId();
  }

  @Transient
  public void setLaskentakaavaId(Long id) {
    laskentakaava = new Laskentakaava();
    laskentakaava.setId(id);
  }

  public List<String> getValintatapajonoIds() {
    List<String> valintatapajonoIds = new ArrayList<String>();
    if (jonot != null) {
      for (HakijaryhmaValintatapajono hakijaryhma : jonot) {
        valintatapajonoIds.add(hakijaryhma.getHakijaryhma().getOid());
      }
    }
    return valintatapajonoIds;
  }

  public void setValintatapajonoIds(List<String> ids) {}

  public boolean isKaytetaanRyhmaanKuuluvia() {
    return kaytetaanRyhmaanKuuluvia;
  }

  public void setKaytetaanRyhmaanKuuluvia(boolean kaytetaanRyhmaanKuuluvia) {
    this.kaytetaanRyhmaanKuuluvia = kaytetaanRyhmaanKuuluvia;
  }

  @Override
  public void setMaster(Hakijaryhma master) {
    setMasterHakijaryhma(master);
  }

  @Override
  public Hakijaryhma getMaster() {
    return getMasterHakijaryhma();
  }

  @Override
  public void setKopiot(Set<Hakijaryhma> kopiot) {
    setKopioHakijaryhmat(kopiot);
  }

  @Override
  public Set<Hakijaryhma> getKopiot() {
    return getKopioHakijaryhmat();
  }

  @Override
  public Hakijaryhma getEdellinen() {
    return getEdellinenHakijaryhma();
  }

  @Override
  public Hakijaryhma getSeuraava() {
    return getSeuraavaHakijaryhma();
  }

  @Override
  public void setEdellinen(Hakijaryhma edellinen) {
    setEdellinenHakijaryhma(edellinen);
  }

  @Override
  public void setSeuraava(Hakijaryhma seuraava) {
    setSeuraavaHakijaryhma(seuraava);
  }
}
