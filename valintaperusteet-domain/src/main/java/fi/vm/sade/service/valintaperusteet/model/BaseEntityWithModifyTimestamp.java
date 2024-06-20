package fi.vm.sade.service.valintaperusteet.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntityWithModifyTimestamp extends BaseEntity {
  @Column(name = "last_modified")
  protected LocalDateTime lastModified;

  public LocalDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(LocalDateTime lastModified) {
    this.lastModified = lastModified;
  }
}
