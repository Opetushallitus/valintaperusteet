package fi.vm.sade.service.valintaperusteet.model;

public class Poistettu {
  private Long id;
  private Long parentId;
  private String tunniste;
  private boolean isDeletedItself = true;

  public Poistettu() {}

  public Poistettu(Object[] raw) {
    this.id = raw[0] != null ? ((Number) raw[0]).longValue() : null;
    this.parentId = raw[1] != null ? ((Number) raw[1]).longValue() : null;
    this.tunniste = raw[2] != null ? raw[2].toString() : null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getTunniste() {
    return tunniste;
  }

  public void setTunniste(String tunniste) {
    this.tunniste = tunniste;
  }

  public boolean isDeletedItself() {
    return isDeletedItself;
  }

  public Poistettu setDeletedItself(boolean deletedItself) {
    isDeletedItself = deletedItself;
    return this;
  }
}
