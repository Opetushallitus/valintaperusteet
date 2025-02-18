package fi.vm.sade.service.valintaperusteet.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "hakukohde_viite")
@Cacheable(true)
public class HakukohdeViite extends BaseEntityWithModifyTimestamp {

  @Column(name = "hakuoid", nullable = false)
  private String hakuoid;

  @Column(name = "oid", unique = true, nullable = false)
  private String oid;

  @Column(name = "tarjoajaOid")
  private String tarjoajaOid;

  @Column(name = "nimi")
  private String nimi;

  @Column(name = "tila")
  private String tila;

  @Column(name = "manuaalisesti_siirretty")
  private Boolean manuaalisestiSiirretty = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  private Valintaryhma valintaryhma;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohdeViite")
  private Set<ValinnanVaihe> valinnanvaiheet = new HashSet<ValinnanVaihe>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohde")
  private Set<Laskentakaava> laskentakaava = new HashSet<Laskentakaava>();

  @JoinColumn(name = "hakukohdekoodi_id", nullable = true)
  @ManyToOne(fetch = FetchType.LAZY)
  private Hakukohdekoodi hakukohdekoodi;

  @JoinTable(
      name = "hakukohde_viite_valintakoekoodi",
      joinColumns =
          @JoinColumn(
              name = "hakukohde_viite_id",
              referencedColumnName = BaseEntity.ID_COLUMN_NAME),
      inverseJoinColumns =
          @JoinColumn(
              name = "valintakoekoodi_id",
              referencedColumnName = BaseEntity.ID_COLUMN_NAME))
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Valintakoekoodi> valintakokeet = new ArrayList<Valintakoekoodi>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohde", cascade = CascadeType.ALL)
  @MapKeyColumn(name = "tunniste")
  private Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet =
      new HashMap<String, HakukohteenValintaperuste>();

  @OneToMany(mappedBy = "hakukohdeViite", fetch = FetchType.LAZY)
  private Set<HakijaryhmaValintatapajono> hakijaryhmat = new HashSet<HakijaryhmaValintatapajono>();

  public String getHakuoid() {
    return hakuoid;
  }

  public void setHakuoid(String hakuoid) {
    this.hakuoid = hakuoid;
  }

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

  public Boolean getManuaalisestiSiirretty() {
    return manuaalisestiSiirretty;
  }

  public void setManuaalisestiSiirretty(Boolean manuaalisestiSiirretty) {
    this.manuaalisestiSiirretty = manuaalisestiSiirretty;
  }

  public Valintaryhma getValintaryhma() {
    return valintaryhma;
  }

  public void setValintaryhma(Valintaryhma valintaryhma) {
    this.valintaryhma = valintaryhma;
  }

  public Set<ValinnanVaihe> getValinnanvaiheet() {
    return valinnanvaiheet;
  }

  public void setValinnanvaiheet(Set<ValinnanVaihe> valinnanvaiheet) {
    this.valinnanvaiheet = valinnanvaiheet;
  }

  public Set<Laskentakaava> getLaskentakaava() {
    return laskentakaava;
  }

  public void setLaskentakaava(Set<Laskentakaava> laskentakaava) {
    this.laskentakaava = laskentakaava;
  }

  private String getValintaryhmaOid() {
    return valintaryhma != null ? valintaryhma.getOid() : "";
  }

  public void addValinnanVaihe(ValinnanVaihe valinnanVaihe) {
    valinnanVaihe.setHakukohdeViite(this);
    this.getValinnanvaiheet().add(valinnanVaihe);
  }

  public Hakukohdekoodi getHakukohdekoodi() {
    return hakukohdekoodi;
  }

  public void setHakukohdekoodi(Hakukohdekoodi hakukohdekoodi) {
    this.hakukohdekoodi = hakukohdekoodi;
  }

  public List<Valintakoekoodi> getValintakokeet() {
    return valintakokeet;
  }

  public void setValintakokeet(List<Valintakoekoodi> valintakokeet) {
    this.valintakokeet = valintakokeet;
  }

  public String getTarjoajaOid() {
    return tarjoajaOid;
  }

  public void setTarjoajaOid(String tarjoajaOid) {
    this.tarjoajaOid = tarjoajaOid;
  }

  public String getTila() {
    return tila;
  }

  public void setTila(String tila) {
    this.tila = tila;
  }

  public Map<String, HakukohteenValintaperuste> getHakukohteenValintaperusteet() {
    return hakukohteenValintaperusteet;
  }

  public void setHakukohteenValintaperusteet(
      Map<String, HakukohteenValintaperuste> hakukohteenValintaperusteet) {
    this.hakukohteenValintaperusteet = hakukohteenValintaperusteet;
  }

  public Set<HakijaryhmaValintatapajono> getHakijaryhmat() {
    return hakijaryhmat;
  }

  public void setHakijaryhmat(Set<HakijaryhmaValintatapajono> hakijaryhmat) {
    this.hakijaryhmat = hakijaryhmat;
  }

  public void addHakijaryhma(HakijaryhmaValintatapajono kopio) {
    kopio.setHakukohdeViite(this);
    this.getHakijaryhmat().add(kopio);
  }
}
