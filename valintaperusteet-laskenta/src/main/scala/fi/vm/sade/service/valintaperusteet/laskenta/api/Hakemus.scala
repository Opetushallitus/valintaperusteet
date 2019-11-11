package fi.vm.sade.service.valintaperusteet.laskenta.api

import java.lang.{Integer => JInteger}
import java.util.{List => JList, Map => JMap}

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat
import io.circe.Json
import io.circe.syntax.EncoderOps

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class Hakemus(val oid: String,
              val hakutoiveet: JMap[JInteger, Hakutoive],
              jkentat: JMap[String, String],
              jmetatiedot: JMap[String, JList[JMap[String, String]]],
              val koskiOpiskeluoikeudet: Json) {
  val kentat: Kentat = jkentat.asScala.toSeq.toMap
  val metatiedot: Map[String, List[Kentat]] = jmetatiedot.toMap.mapValues(_.toList.map(_.toMap))

  def this(oid: String,
    hakutoiveet: JMap[JInteger, Hakutoive],
    jkentat: JMap[String, String],
    jmetatiedot: JMap[String, JList[JMap[String, String]]]) {
    this(oid, hakutoiveet, jkentat, jmetatiedot, List[Unit]().asJson)
  }

  def onkoHakutoivePrioriteetilla(hakukohde: String, prioriteetti: Int, ryhmaOid: Option[String] = None): Boolean = {
    def hakutoiveKuuluuRyhmaan: Hakutoive => Boolean = {
      _.hakukohdeRyhmat.contains(ryhmaOid.get)
    }

    if(ryhmaOid.isDefined) {
      // Hakutoive on hakukohderyhmansä N:s hakutoive lomakkeella
      hakutoiveet.asScala.values
        .filter(hakutoiveKuuluuRyhmaan).toList
        .lift(prioriteetti - 1)
        .exists(_.hakukohdeOid == hakukohde)
    } else {
      // Hakutoive on N:s hakutoive lomakkeella
      hakutoiveet.asScala.get(prioriteetti).exists(_.hakukohdeOid == hakukohde)
    }
  }

  def onkoHakukelpoinen(hakukohdeOid: String): Boolean = {
    val key = jkentat.keySet().filter(k => k.startsWith("preference") && k.endsWith("-Koulutus-id"))
      .find(k => jkentat.get(k) == hakukohdeOid)
    val result = key match {
      case Some(k) =>
        val status = kentat.getOrElse(s"$k-eligibility", "ELIGIBLE") //jkentat.getOrDefault(s"$k-eligibility", "ELIGIBLE")
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
    val javaMetatiedot: JMap[String, JList[JMap[String, String]]] = h.metatiedot.
      mapValues(list =>
        list.map(_.asJava).asJava).
      asJava
    Some((h.oid, h.hakutoiveet, h.kentat.asJava, javaMetatiedot))
  }
}

class Hakutoive(val hakukohdeOid: String, val hakukohdeRyhmat: JList[String])

object Hakutoive {

  def apply(oid: String, ryhmat: JList[String]): Hakutoive = {
    new Hakutoive(oid, ryhmat)
  }

  def unapply(h: Hakutoive): Option[(String, JList[String])] = Some(h.hakukohdeOid, h.hakukohdeRyhmat)

}
