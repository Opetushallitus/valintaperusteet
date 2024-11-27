package fi.vm.sade.service.valintaperusteet.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

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
