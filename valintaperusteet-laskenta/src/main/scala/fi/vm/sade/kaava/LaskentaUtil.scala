package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti
import java.util.{Set => JSet}

/**
 * User: kwuoti
 * Date: 31.1.2013
 * Time: 10.13
 */
object LaskentaUtil {
  import scala.collection.JavaConversions._

  def jarjestaFunktioargumentit(args: JSet[Funktioargumentti]): List[Funktioargumentti] = {
    args.toList.sortWith(_.getIndeksi < _.getIndeksi)
  }
}
