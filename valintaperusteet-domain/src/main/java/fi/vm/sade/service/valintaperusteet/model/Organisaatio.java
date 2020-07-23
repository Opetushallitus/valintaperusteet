package fi.vm.sade.service.valintaperusteet.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "organisaatio")
@Cacheable(true)
public class Organisaatio extends BaseEntity {
  @ManyToMany(mappedBy = "organisaatiot")
  private Set<Valintaryhma> valintaryhmat = new HashSet<Valintaryhma>();

  @OneToMany(mappedBy = "vastuuorganisaatio")
  private Set<Valintaryhma> vastuuvalintaryhmat = new HashSet<Valintaryhma>();

  @Column(name = "oid", nullable = false, unique = true)
  private String oid;

  @Column(name = "parent_oid_path", nullable = false, unique = true)
  private String parentOidPath;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getParentOidPath() {
    return parentOidPath;
  }

  public void setParentOidPath(String parentOidPath) {
    this.parentOidPath = parentOidPath;
  }

  public Set<Valintaryhma> getValintaryhmat() {
    return valintaryhmat;
  }

  public void setValintaryhmat(Set<Valintaryhma> valintaryhmat) {
    this.valintaryhmat = valintaryhmat;
  }
}
