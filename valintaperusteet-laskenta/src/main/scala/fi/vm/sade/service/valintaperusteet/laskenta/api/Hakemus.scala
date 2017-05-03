package fi.vm.sade.service.valintaperusteet.laskenta.api

import java.lang.{Integer => JInteger}
import java.util.{List => JList, Map => JMap}

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class Hakemus(val oid: String,
              val hakutoiveet: JMap[JInteger, Hakutoive],
              jkentat: JMap[String, String],
              jmetatiedot: JMap[String, JList[JMap[String, String]]]) {

  val kentat: Kentat = jkentat.toMap
  val metatiedot: Map[String, List[Kentat]] = jmetatiedot.toMap.mapValues(_.toList.map(_.toMap))

  def onkoHakutoivePrioriteetilla(hakukohde: String, prioriteetti: Int, ryhmaOid: Option[String] = None) = {
    if(ryhmaOid.isDefined){
      // Hakutoive on hakukohderyhmansä N:s hakutoive lomakkeella
      hakutoiveet.filter(_._2.hakukohdeRyhmat.contains(ryhmaOid.get)).get(prioriteetti).exists(_.hakukohdeOid == hakukohde)
    } else {
      // Hakutoive on N:s hakutoive lomakkeella
      hakutoiveet.asScala.get(prioriteetti).exists(_.hakukohdeOid == hakukohde)
    }
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

  def apply(oid: String, hakutoiveet: JMap[JInteger, Hakutoive], jkentat: JMap[String, String], jmetatiedot: JMap[String, JList[JMap[String, String]]]): Hakemus = {
    new Hakemus(oid, hakutoiveet, jkentat, jmetatiedot)
  }

  def unapply(h: Hakemus): Option[(String, JMap[JInteger, Hakutoive], JMap[String, String], JMap[String, JList[JMap[String, String]]])] = {
    Some((h.oid, h.hakutoiveet, mapAsJavaMap(h.kentat), mapAsJavaMap(h.metatiedot.mapValues(list => seqAsJavaList(list.map(mapAsJavaMap))))))
  }
}

class Hakutoive(val hakukohdeOid: String, val hakukohdeRyhmat: JList[String])

object Hakutoive {

  def apply(oid: String, ryhmat: JList[String]): Hakutoive = {
    new Hakutoive(oid, ryhmat)
  }

  def unapply(h: Hakutoive): Option[(String, JList[String])] = Some(h.hakukohdeOid, seqAsJavaList(h.hakukohdeRyhmat))

}
