package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti
import java.util.{Set => JSet}
import scala.jdk.CollectionConverters._

object LaskentaUtil {

  def jarjestaFunktioargumentit(args: JSet[Funktioargumentti]): List[Funktioargumentti] = {
    args.asScala.toList.sortWith(_.getIndeksi < _.getIndeksi)
  }
}
