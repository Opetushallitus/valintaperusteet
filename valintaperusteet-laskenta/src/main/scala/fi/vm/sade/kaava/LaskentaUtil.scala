package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti

/**
 * User: kwuoti
 * Date: 31.1.2013
 * Time: 10.13
 */
object LaskentaUtil {
  import scala.collection.JavaConversions._

  def jarjestaFunktioargumentit(args: java.util.Set[Funktioargumentti]): List[Funktioargumentti] = {
    args.toList.sortWith(_.getIndeksi < _.getIndeksi)
  }
}
