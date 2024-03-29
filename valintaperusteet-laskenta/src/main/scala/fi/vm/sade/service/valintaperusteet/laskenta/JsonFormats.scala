package fi.vm.sade.service.valintaperusteet.laskenta

import com.fasterxml.jackson.databind.ObjectMapper

import java.lang.{Integer => JInteger}
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Map => JMap}
import java.util.{List => JList}
import fi.vm.sade.kaava.Funktiokuvaaja.Konvertterinimi.Konvertterinimi
import fi.vm.sade.kaava.Funktiokuvaaja.Konvertterinimi
import fi.vm.sade.kaava.Funktiokuvaaja._
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hylattytila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Virhetila
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakutoive
import fi.vm.sade.service.valintaperusteet.model.JsonViews
import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonFormats {
  import JsonHelpers.arrayMapWrites
  import JsonHelpers.enumFormat

  // Enumit
  implicit def funktiotyyppiFormat = enumFormat(Funktiotyyppi)
  implicit def syoteparametrityyppiFormat = enumFormat(Syoteparametrityyppi)
  implicit def konvertterinimiFormat = enumFormat(Konvertterinimi)

  def kardinaliteettiReads[Kardinaliteetti <: Enumeration](
    enum: Kardinaliteetti
  ): Reads[Kardinaliteetti#Value] =
    new Reads[Kardinaliteetti#Value] {
      def reads(json: JsValue): JsResult[Kardinaliteetti#Value] =
        json match {
          case JsString(s) => {
            try {
              s match {
                case "n" => JsSuccess(enum.withName("N"))
                case "1" => JsSuccess(enum.withName("YKSI"))
                case _   => JsSuccess(enum.withName("LISTA_PAREJA"))
              }

            } catch {
              case _: NoSuchElementException =>
                JsError(
                  s"Enumeration expected of type: '${enum.getClass}', but it does not contain '$s'"
                )
            }
          }
          case _ => JsError("String value expected")
        }
    }

  def kardinaliteettiWrites[Kardinaliteetti <: Enumeration]: Writes[Kardinaliteetti#Value] =
    new Writes[Kardinaliteetti#Value] {
      def writes(v: Kardinaliteetti#Value): JsValue = {
        v.toString match {
          case "N"    => JsString("n")
          case "YKSI" => JsString("1")
          case _      => JsString("lista_pareja")
        }
      }
    }

  def kardinaliteettiFormatHelper[Kardinaliteetti <: Enumeration](
    enum: Kardinaliteetti
  ): Format[Kardinaliteetti#Value] = {
    Format(kardinaliteettiReads(enum), kardinaliteettiWrites)
  }

  implicit def kardinaliteettiFormat = kardinaliteettiFormatHelper(Kardinaliteetti)

  // Perus case classit
  //implicit def arvovalikonvertterikuvausFormat = Json.format[Arvovalikonvertterikuvaus]
  implicit def arvokonvertterikuvausFormat = Json.format[Arvokonvertterikuvaus]
  implicit def konvertterikuvausFormat = Json.format[Konvertterikuvaus]

  implicit def valintaperusteparametrikuvausFormat = Json.format[Valintaperusteparametrikuvaus]
  implicit def syoteparametrikuvausFormat = Json.format[Syoteparametrikuvaus]
  implicit def funktioargumenttikuvausFormat = Json.format[Funktioargumenttikuvaus]
  implicit def funktiokuvausFormat = Json.format[Funktiokuvaus]

  // Map[Konvertterinimi, KonvertteriTyyppi]
  implicit def mapWritesKonvertteriNimiKonvertteriKuvaus
    : Writes[Map[Konvertterinimi, KonvertteriTyyppi]] = {
    new Writes[Map[Konvertterinimi, KonvertteriTyyppi]] {
      def writes(map: Map[Konvertterinimi, KonvertteriTyyppi]): JsValue = {
        val tyypit = map.map(t => {
          t._2 match {
            case Arvokonvertterikuvaus(arvotyyppi, arvojoukko) => {
              Json.obj(
                "tyyppi" -> t._2.nimi.toString,
                "arvotyyppi" -> arvotyyppi.toString,
                "arvojoukko" -> Json.toJson(arvojoukko)
              )
            }
            case _ => Json.obj("tyyppi" -> t._2.nimi.toString)
          }
        })
        tyypit.foldLeft(JsArray())(_ ++ Json.arr(_))
      }
    }
  }

  implicit def mapReadsKonvertteriNimiKonvertteriKuvaus
    : Reads[Map[Konvertterinimi, KonvertteriTyyppi]] =
    new Reads[Map[Konvertterinimi, KonvertteriTyyppi]] {
      def reads(json: JsValue): JsResult[Map[Konvertterinimi, KonvertteriTyyppi]] = {
        val map = json
          .as[List[JsObject]]
          .foldLeft(
            Map[Konvertterinimi, KonvertteriTyyppi](
              Konvertterinimi.withName("ARVOVALIKONVERTTERI") -> Arvovalikonvertterikuvaus
            )
          )((s, a) => {
            val tyyppi: JsLookupResult = json \ "tyyppi"
            tyyppi.as[String] match {
              case "ARVOKONVERTTERI" => {
                val arvotyyppi = (json \ "arvotyyppi").as[String]
                s ++ Map[Konvertterinimi, KonvertteriTyyppi](
                  Konvertterinimi.withName("ARVOKONVERTTERI") -> Arvokonvertterikuvaus(
                    Syoteparametrityyppi.withName(arvotyyppi)
                  )
                )
              }
              case _ => {
                s ++ Map[Konvertterinimi, KonvertteriTyyppi](
                  Konvertterinimi.withName("ARVOVALIKONVERTTERI") -> Arvovalikonvertterikuvaus
                )
              }
            }
          })
        JsSuccess(map)
      }
    }

  import JsonHelpers._

  //Hakemus
  implicit def hakutoiveReads: Reads[Hakutoive] =
    (
      (__ \ "hakukohdeoid").read[String] and
        (__ \ "hakukohdeRyhmatOids").read[JList[String]]
    )(Hakutoive.apply _)

  implicit def hakutoiveWrites: Writes[Hakutoive] =
    (
      (__ \ "hakukohdeoid").write[String] and
        (__ \ "hakukohdeRyhmatOids").write[JList[String]]
    )(unlift(Hakutoive.unapply))

  implicit def hakemusReads: Reads[Hakemus] =
    (
      (__ \ "oid").read[String] and
        (__ \ "hakutoiveet").read[JMap[JInteger, Hakutoive]] and
        (__ \ "jkentat").read[JMap[String, String]] and
        (__ \ "jsuoritukset").read[JMap[String, JList[JMap[String, String]]]]
    )(Hakemus.apply _)

  implicit def hakemusWrites: Writes[Hakemus] =
    (
      (__ \ "oid").write[String] and
        (__ \ "hakutoiveet").write[JMap[Integer, Hakutoive]] and
        (__ \ "jkentat").write[JMap[String, String]] and
        (__ \ "jsuoritukset").write[JMap[String, JList[JMap[String, String]]]]
    )(unlift(Hakemus.unapply))

  implicit val mapReads: Reads[Map[String, Option[Any]]] = (jv: JsValue) =>
    JsSuccess(jv match {
      case jsObject: JsObject => mapJsObjectToLooseMap(jsObject)
      case x =>
        throw new UnsupportedOperationException(
          s"Don't know how to deserialise '$x' of type ${x.getClass} to Map"
        )
    })

  private def mapJsObjectToLooseMap(jsObject: JsObject): Map[String, Option[Any]] = {
    jsObject.value
      .mapValues {
        case JsNull      => None
        case s: JsString => Some(s.value)
        case i: JsNumber => Some(i.value)
        case x           => throw new UnsupportedOperationException(s"Don't know how to deserialise '$x'")
      }
      .toMap
      .asInstanceOf[Map[String, Option[Any]]]
  }

  //Historia
  implicit def historiaReads: Reads[Historia] =
    (
      (__ \ "funktio").read[String] and
        (__ \ "tulos").readNullable[Any] and
        (__ \ "tilat").read(Reads.list[Tila](tilaReads)) and
        (__ \ "historiat").lazyReadNullable(Reads.list[Historia](historiaReads)) and
        (__ \ "avaimet").readNullable[Map[String, Option[Any]]]
    )((funktio, tulos, tilat, historiat, avaimet) =>
      Historia(funktio, None, tulos, tilat, historiat, avaimet)
    )

  implicit def historiaWrites: Writes[Historia] =
    (
      (__ \ "funktio").write[String] and
        (__ \ "tulos").writeNullable[Any] and
        (__ \ "tilat").write(Writes.traversableWrites[Tila](tilaWrites)) and
        (__ \ "historiat").lazyWriteNullable(Writes.traversableWrites[Historia](historiaWrites)) and
        (__ \ "avaimet").writeNullable[Map[String, Option[Any]]]
    )(h => (h.funktio, h.tulos, h.tilat, h.historiat, h.avaimet))

  //Tila
  implicit def tilaReads: Reads[Tila] =
    new Reads[Tila] {
      def reads(json: JsValue): JsResult[Tila] =
        json match {
          case o: JsObject => {
            (o \ "tilatyyppi").as[String] match {
              case "HYLATTY" => JsSuccess(mapper.readValue(Json.stringify(o), classOf[Hylattytila]))
              case "HYVAKSYTTAVISSA" =>
                JsSuccess(mapper.readValue(Json.stringify(o), classOf[Hyvaksyttavissatila]))
              case "VIRHE" => JsSuccess(mapper.readValue(Json.stringify(o), classOf[Virhetila]))
              case _       => JsError("Tuntematon tilatyyppi")
            }

          }
          case _ => JsError("Vaara tilatyyppi")
        }
    }
  implicit def tilaWrites: Writes[Tila] =
    new Writes[Tila] {
      def writes(o: Tila): JsValue = {
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(o)
        Json.parse(json)
      }
    }
}

object JsonHelpers {
  var mapper: ObjectMapper = new ObjectMapper()

  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] =
    new Reads[E#Value] {
      def reads(json: JsValue): JsResult[E#Value] =
        json match {
          case JsString(s) => {
            try {
              JsSuccess(enum.withName(s))
            } catch {
              case _: NoSuchElementException =>
                JsError(
                  s"Enumeration expected of type: '${enum.getClass}', but it does not contain '$s'"
                )
            }
          }
          case _ => JsError("String value expected")
        }
    }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] =
    new Writes[E#Value] {
      def writes(v: E#Value): JsValue = JsString(v.toString)
    }

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumReads(enum), enumWrites)
  }

  implicit def arrayMapReads: Reads[Array[(String, String)]] =
    new Reads[Array[(String, String)]] {
      def reads(json: JsValue): JsResult[Array[(String, String)]] =
        json match {
          case s: JsArray =>
            JsSuccess(mapper.readValue(Json.stringify(s), classOf[Array[(String, String)]]))
          case _ => JsError("Tyyppiä Array ei löytynyt")
        }
    }

  implicit def arrayMapWrites: Writes[Array[(String, String)]] =
    new Writes[Array[(String, String)]] {
      def writes(map: Array[(String, String)]): JsValue = {
        map.foldLeft(Json.arr())((s, a) => s.append(Json.obj("avain" -> a._1, "arvo" -> a._2)))
      }
    }

  implicit def mapReadsIntString: Reads[JMap[JInteger, String]] =
    new Reads[JMap[JInteger, String]] {
      def reads(json: JsValue): JsResult[JMap[JInteger, String]] =
        json match {
          case s: JsObject =>
            JsSuccess(mapper.readValue(Json.stringify(s), classOf[JMap[JInteger, String]]))
          case _ => JsError("Tyyppiä Map ei löytynyt")
        }
    }

  implicit def mapWritesIntString: Writes[JMap[JInteger, String]] =
    new Writes[JMap[JInteger, String]] {
      def writes(map: JMap[JInteger, String]): JsValue = {
        //map.foldLeft(Json.obj())((s,a) => s ++ Json.obj(a._1.toString -> a._2))
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(map)
        Json.parse(json)
      }
    }

  implicit def mapReadsStringString: Reads[JMap[String, String]] =
    new Reads[JMap[String, String]] {
      def reads(json: JsValue): JsResult[JMap[String, String]] =
        json match {
          case s: JsObject =>
            JsSuccess(mapper.readValue(Json.stringify(s), classOf[JMap[String, String]]))
          case _ => JsError("Tyyppiä Map ei löytynyt")
        }
    }

  implicit def mapWritesStringString: Writes[JMap[String, String]] =
    new Writes[JMap[String, String]] {
      def writes(map: JMap[String, String]): JsValue = {
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(map)
        Json.parse(json)
      }
    }

  implicit def mapReadsIntegerHakukohde: Reads[JMap[JInteger, Hakutoive]] =
    new Reads[JMap[JInteger, Hakutoive]] {
      def reads(json: JsValue): JsResult[JMap[JInteger, Hakutoive]] =
        json match {
          case s: JsObject =>
            JsSuccess(mapper.readValue(Json.stringify(s), classOf[JMap[JInteger, Hakutoive]]))
          case _ => JsError("Tyyppiä Map ei löytynyt")
        }
    }

  implicit def mapWritesIntegerHakukohde: Writes[JMap[JInteger, Hakutoive]] =
    new Writes[JMap[JInteger, Hakutoive]] {
      def writes(map: JMap[JInteger, Hakutoive]): JsValue = {
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(map)
        Json.parse(json)
      }
    }

  implicit def mapReadsListString: Reads[JList[String]] =
    new Reads[JList[String]] {
      def reads(json: JsValue): JsResult[JList[String]] =
        json match {
          case s: JsObject => JsSuccess(mapper.readValue(Json.stringify(s), classOf[JList[String]]))
          case _           => JsError("Tyyppiä List ei löytynyt")
        }
    }

  implicit def mapWritesListString: Writes[JList[String]] =
    new Writes[JList[String]] {
      def writes(list: JList[String]): JsValue = {
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(list)
        Json.parse(json)
      }
    }

  implicit def mapReadsStringStringMapList: Reads[JMap[String, JList[JMap[String, String]]]] =
    new Reads[JMap[String, JList[JMap[String, String]]]] {
      def reads(json: JsValue): JsResult[JMap[String, JList[JMap[String, String]]]] =
        json match {
          case s: JsObject =>
            JsSuccess(
              mapper.readValue(
                Json.stringify(s),
                classOf[JMap[String, JList[JMap[String, String]]]]
              )
            )
          case _ => JsError("Tyyppiä Map ei löytynyt")
        }
    }

  implicit def mapWritesStringStringMapList: Writes[JMap[String, JList[JMap[String, String]]]] =
    new Writes[JMap[String, JList[JMap[String, String]]]] {
      def writes(map: JMap[String, JList[JMap[String, String]]]): JsValue = {
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(map)
        Json.parse(json)
      }
    }

  implicit def anyReads: Reads[Any] =
    new Reads[Any] {
      def reads(json: JsValue): JsResult[Any] =
        json match {
          case JsString(s)  => JsSuccess(s)
          case JsBoolean(s) => JsSuccess(s)
          case JsNumber(s)  => JsSuccess(s)
          case JsObject(s)  => JsSuccess(s)
          case JsArray(s)   => JsSuccess(s)
          case _            => JsError("Undefined Any type")
        }
    }

  implicit def anyWrites: Writes[Any] =
    new Writes[Any] {
      def writes(a: Any): JsValue =
        a match {
          case s: String            => JsString(s)
          case s: Boolean           => JsBoolean(s)
          case s: Int               => JsNumber(s)
          case s: Double            => JsNumber(s)
          case s: Long              => JsNumber(s)
          case s: BigDecimal        => JsNumber(s)
          case s: JBigDecimal       => JsNumber(s)
          case Some(s: String)      => JsString(s)
          case Some(s: Boolean)     => JsBoolean(s)
          case Some(s: Int)         => JsNumber(s)
          case Some(s: Double)      => JsNumber(s)
          case Some(s: Long)        => JsNumber(s)
          case Some(s: BigDecimal)  => JsNumber(s)
          case Some(s: JBigDecimal) => JsNumber(s)
          case Some(s: Any)         => writes(s)
          case null                 => JsNull
          case None                 => JsNull
          case s: Map[_, _] =>
            JsObject(s.toSeq.map(t => t._1.toString -> writes(t._2)))
          case s: Seq[_] => {
            JsArray(s.map(writes))
          }
          case s: Any => {
            JsString(s"No json formatter found for $s")
          }
        }
    }

}
