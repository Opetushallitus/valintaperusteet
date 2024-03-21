package fi.vm.sade.service.valintaperusteet.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.valinta.dokumenttipalvelu.SiirtotiedostoPalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SiirtotiedostoS3Client {
  private static final Logger logger =
      LoggerFactory.getLogger(SiirtotiedostoS3Client.class.getName());
  private static final Gson gson = new GsonBuilder().create();

  private final SiirtotiedostoPalvelu siirtotiedostoPalvelu;
  private final int maxHakukohdeCountInFile;

  @Autowired
  public SiirtotiedostoS3Client(
      @Value("${valintaperusteet.siirtotiedosto.awsregion}") final String awsRegion,
      @Value("${valintaperusteet.siirtotiedosto.s3bucket}") final String s3Bucket,
      @Value("${valintaperusteet.siirtotiedosto.max-hakukohde-count-in-file}")
          final int maxHakukohdeCountInFile) {
    this.siirtotiedostoPalvelu = new SiirtotiedostoPalvelu(awsRegion, s3Bucket);
    this.maxHakukohdeCountInFile = maxHakukohdeCountInFile;
  }

  public String createSiirtotiedosto(List<ValintaperusteetDTO> data) {
    try {
      JsonArray jsonArray = new JsonArray(data.size());
      data.forEach(item -> jsonArray.add(gson.toJsonTree(item)));
      ObjectMetadata result =
          siirtotiedostoPalvelu.saveSiirtotiedosto(
              "valintaperusteet",
              "hakukohde",
              "",
              new ByteArrayInputStream(jsonArray.toString().getBytes()),
              2);
      return result.key;
    } catch (Exception e) {
      logger.error("Siirtotiedoston luonti epäonnistui; ", e);
      throw new RuntimeException(e);
    }
  }

  public int getMaxHakukohdeCountInFile() {
    return this.maxHakukohdeCountInFile;
  }
}
