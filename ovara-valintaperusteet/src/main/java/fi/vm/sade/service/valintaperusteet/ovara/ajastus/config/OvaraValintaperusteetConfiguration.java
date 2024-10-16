package fi.vm.sade.service.valintaperusteet.ovara.ajastus.config;

import akka.actor.ActorSystem;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.SiirtotiedostoServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.ValinnanVaiheServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.rest.ValintaperusteServiceImpl;
import fi.vm.sade.service.valintaperusteet.util.SiirtotiedostoS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OvaraValintaperusteetConfiguration {

  @Bean
  public SiirtotiedostoS3Client SiirtotiedostoS3Client(
      @Value("${valintaperusteet.siirtotiedosto.awsregion}") final String awsRegion,
      @Value("${valintaperusteet.siirtotiedosto.s3bucket}") final String s3Bucket,
      @Value("${valintaperusteet.siirtotiedosto.s3-target-role-arn}") final String s3TargetRoleArn,
      @Value("${valintaperusteet.siirtotiedosto.max-hakukohde-count-in-file}")
          final int maxHakukohdeCountInFile) {
    return new SiirtotiedostoS3Client(
        awsRegion, s3Bucket, s3TargetRoleArn, maxHakukohdeCountInFile);
  }

  @Bean
  public SiirtotiedostoServiceImpl siirtotiedostoServiceImpl(
      SiirtotiedostoS3Client siirtotiedostoS3Client) {
    return new SiirtotiedostoServiceImpl(siirtotiedostoS3Client);
  }

  @Bean
  public ValintaperusteService valintaperusteService() {
    return new ValintaperusteServiceImpl();
  }

  @Bean
  public ValinnanVaiheService valinnanVaiheService() {
    return new ValinnanVaiheServiceImpl();
  }

  @Bean
  public ValintaperusteetModelMapper valintaperusteetModelMapper() {
    return new ValintaperusteetModelMapper();
  }

  @Bean(destroyMethod = "terminate")
  public ActorSystem actorSystem() {
    return ActorSystem.create();
  }
}
