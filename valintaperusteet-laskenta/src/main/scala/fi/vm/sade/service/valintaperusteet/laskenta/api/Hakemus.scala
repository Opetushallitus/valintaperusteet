package fi.vm.sade.service.valintaperusteet.laskenta.api

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat

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
              jkentat: JMap[String, String],
              jsuoritustiedot: JMap[String, JMap[String, String]]) {

  val kentat: Kentat = jkentat.toMap
  val suoritustiedot: Map[String, Kentat] = jsuoritustiedot.toMap.mapValues(_.toMap)

  def onkoHakutoivePrioriteetilla(hakukohde: String, prioriteetti: Int) = {
    hakutoiveet.containsKey(prioriteetti) && hakutoiveet.get(prioriteetti) == hakukohde
  }

  def onkoHakukelpoinen(hakukohdeOid: String) = {
    val key = jkentat.keySet().filter(k => k.startsWith("preference") && k.endsWith("-Koulutus-id")).find(k => jkentat.get(k) == hakukohdeOid)
    val result = key match {
      case Some(k) =>
        val status = kentat.getOrElse(s"$k-eligibility", "ELIGIBLE")//jkentat.getOrDefault(s"$k-eligibility", "ELIGIBLE")
        if(status != "INELIGIBLE") true else false
      case _ => false
    }
    result
  }
}

object Hakemus {

  type Kentat = Map[String, String]

  def apply(oid: String, hakutoiveet: JMap[JInteger, String], jkentat: JMap[String, String], jsuoritustiedot: JMap[String, JMap[String, String]]): Hakemus = {
    return new Hakemus(oid, hakutoiveet, jkentat, jsuoritustiedot)
  }

  def unapply(h: Hakemus): Option[(String, JMap[JInteger, String], JMap[String, String], JMap[String, JMap[String, String]])] = {
    return Some((h.oid, h.hakutoiveet, mapAsJavaMap(h.kentat), mapAsJavaMap(h.suoritustiedot.mapValues(mapAsJavaMap(_)))))
  }
}
