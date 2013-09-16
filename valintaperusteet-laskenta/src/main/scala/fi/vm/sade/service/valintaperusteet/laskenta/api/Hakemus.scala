package fi.vm.sade.service.valintaperusteet.laskenta.api

import scala.collection.JavaConversions._

/**
 * User: kwuoti
 * Date: 27.2.2013
 * Time: 9.53
 */
class Hakemus(val oid: String,
              val hakutoiveet: java.util.Map[java.lang.Integer, String],
              jkentat: java.util.Map[String, String]) {

  val kentat: scala.collection.immutable.Map[String, String] = jkentat.toMap

  def onkoHakutoivePrioriteetilla(hakukohde: String, prioriteetti: Int) = {
    hakutoiveet.containsKey(prioriteetti) && hakutoiveet.get(prioriteetti) == hakukohde
  }
}
