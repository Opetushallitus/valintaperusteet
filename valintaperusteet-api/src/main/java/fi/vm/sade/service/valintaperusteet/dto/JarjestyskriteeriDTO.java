package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JarjestyskriteeriDTO", description = "Järjestyskriteeri")
public class JarjestyskriteeriDTO extends JarjestyskriteeriCreateDTO implements Prioritized {
  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Valintatapajono OID")
  private String valintatapajonoOid;

  @Schema(description = "Onko järjestyskriteeri peritty")
  private Boolean inheritance;

  @Schema(description = "Laskentakaava ID", required = true)
  private Long laskentakaavaId;

  @Schema(description = "Järjestyskriteerin prioriteetti")
  private int prioriteetti;

  @Schema(description = "Funktiokutsu")
  private FunktiokutsuDTO funktiokutsu;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getValintatapajonoOid() {
    return valintatapajonoOid;
  }

  public void setValintatapajonoOid(String valintatapajonoOid) {
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public Boolean getInheritance() {
    return inheritance;
  }

  public void setInheritance(Boolean inheritance) {
    this.inheritance = inheritance;
  }

  public Long getLaskentakaavaId() {
    return laskentakaavaId;
  }

  public void setLaskentakaavaId(Long laskentakaavaId) {
    this.laskentakaavaId = laskentakaavaId;
  }

  public int getPrioriteetti() {
    return prioriteetti;
  }

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }

  public FunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(FunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }
}
