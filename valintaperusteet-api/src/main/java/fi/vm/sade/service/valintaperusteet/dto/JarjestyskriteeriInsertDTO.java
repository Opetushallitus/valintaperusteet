package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JarjestyskriteeriInsertDTO", description = "Jarjestyskriteeri ja laskentakaava")
public class JarjestyskriteeriInsertDTO {

  @Schema(description = "Järjestyskriteeri", required = true)
  private JarjestyskriteeriCreateDTO jarjestyskriteeri;

  @Schema(description = "Laskentakaava ID", required = true)
  private Long laskentakaavaId;

  public JarjestyskriteeriCreateDTO getJarjestyskriteeri() {
    return jarjestyskriteeri;
  }

  public void setJarjestyskriteeri(JarjestyskriteeriCreateDTO jarjestyskriteeri) {
    this.jarjestyskriteeri = jarjestyskriteeri;
  }

  public Long getLaskentakaavaId() {
    return laskentakaavaId;
  }

  public void setLaskentakaavaId(Long laskentakaavaId) {
    this.laskentakaavaId = laskentakaavaId;
  }
}
