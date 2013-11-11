package fi.vm.sade.service.valintaperusteet.laskenta.api

import scala.collection.JavaConversions._
import java.util.{Map => JMap}
import java.lang.{Integer => JInteger}

/**
 * User: kwuoti
 * Date: 27.2.2013
 * Time: 9.53
 */
class Hakemus(val oid: String,
              val hakutoiveet: JMap[JInteger, String],
              jkentat: JMap[String, String]) {

  val kentat: Map[String, String] = jkentat.toMap

  def onkoHakutoivePrioriteetilla(hakukohde: String, prioriteetti: Int) = {
    hakutoiveet.containsKey(prioriteetti) && hakutoiveet.get(prioriteetti) == hakukohde
  }
}

object Hakemus {
  def apply(oid: String, hakutoiveet: JMap[JInteger, String], jkentat: JMap[String, String]): Hakemus = {
    return new Hakemus(oid, hakutoiveet, jkentat)
  }

  def unapply(h: Hakemus): Option[(String, JMap[JInteger, String], JMap[String, String])] = {
    return Some((h.oid, h.hakutoiveet, mapAsJavaMap(h.kentat)))
  }
}
