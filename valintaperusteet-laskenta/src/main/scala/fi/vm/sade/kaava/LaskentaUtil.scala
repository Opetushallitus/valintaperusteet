package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti
import java.util.{Set => JSet}

object LaskentaUtil {
  import scala.collection.JavaConversions._

  def jarjestaFunktioargumentit(args: JSet[Funktioargumentti]): List[Funktioargumentti] = {
    args.toList.sortWith(_.getIndeksi < _.getIndeksi)
  }
}
