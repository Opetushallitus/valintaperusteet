package fi.vm.sade.service.valintaperusteet.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.valinta.dokumenttipalvelu.SiirtotiedostoPalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
      @Value("${valintaperusteet.siirtotiedosto.s3-target-role-arn}") final String s3TargetRoleArn,
      @Value("${valintaperusteet.siirtotiedosto.max-hakukohde-count-in-file}")
          final int maxHakukohdeCountInFile) {
    this.siirtotiedostoPalvelu = new SiirtotiedostoPalvelu(awsRegion, s3Bucket, s3TargetRoleArn);
    this.maxHakukohdeCountInFile = maxHakukohdeCountInFile;
  }

  public String createSiirtotiedosto(
      List<ValintaperusteetDTO> data, String operationId, int operationSubId) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
        JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);
        jsonWriter.beginArray();
        for (ValintaperusteetDTO dto : data) {
          gson.toJson(gson.toJsonTree(dto), jsonWriter);
        }
        jsonWriter.endArray();
        jsonWriter.close();

        try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(outputStream.toByteArray())) {
          ObjectMetadata result =
              siirtotiedostoPalvelu.saveSiirtotiedosto(
                  "valintaperusteet", "hakukohde", "", operationId, operationSubId, inputStream, 2);
          return result.key;
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Siirtotiedoston luonti epäonnistui; ", ioe);
    }
  }

  public int getMaxHakukohdeCountInFile() {
    return this.maxHakukohdeCountInFile;
  }
}
