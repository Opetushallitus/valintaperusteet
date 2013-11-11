package fi.vm.sade.service.valintaperusteet.laskenta

import play.api.libs.json._
import play.api.libs.functional.syntax._
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.{Virhetila, Hyvaksyttavissatila, Hylattytila, Tila}
import org.codehaus.jackson.map.ObjectMapper
import fi.vm.sade.service.valintaperusteet.model.JsonViews
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import java.util.{Map => JMap}
import java.lang.{Integer => JInteger}

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 08/11/13
 * Time: 08:07
 * To change this template use File | Settings | File Templates.
 */
object JsonFormats {
  private var mapper: ObjectMapper = new ObjectMapper()

  implicit def hakemusReads: Reads[Hakemus] = (
    (__ \ "oid").read[String] and
    (__ \ "hakutoiveet").read[JMap[JInteger, String]] and
    (__ \ "jkentat").read[JMap[String, String]]
  )(Hakemus.apply _)

  implicit def hakemusWrites: Writes[Hakemus] = (
    (__ \ "oid").write[String] and
    (__ \ "hakutoiveet").write[JMap[JInteger, String]] and
    (__ \ "jkentat").write[JMap[String, String]]
    )(unlift(Hakemus.unapply _))

  implicit def historiaReads: Reads[Historia] = (
    (__ \ "funktio").read[String] and
    (__ \ "tulos").readNullable[Any] and
    (__ \ "tilat").read(Reads.list[Tila](tilaReads)) and
    (__ \ "historiat").lazyReadNullable(Reads.list[Historia](historiaReads)) and
    (__ \ "avaimet").readNullable[Map[String, Option[Any]]]
  )(Historia)

  implicit def historiaWrites: Writes[Historia] = (
    (__ \ "funktio").write[String] and
    (__ \ "tulos").writeNullable[Any] and
    (__ \ "tilat").write(Writes.traversableWrites[Tila](tilaWrites)) and
    (__ \ "historiat").lazyWriteNullable(Writes.traversableWrites[Historia](historiaWrites)) and
    (__ \ "avaimet").writeNullable[Map[String, Option[Any]]]
    )(unlift(Historia.unapply))

  implicit def tilaReads: Reads[Tila] = new Reads[Tila] {
    def reads(json: JsValue): JsResult[Tila] = json match {
      case o: JsObject=> {
        (o \ "tilatyyppi").as[String] match {
          case "HYLATTY" => JsSuccess(mapper.readValue(Json.stringify(o), classOf[Hylattytila]))
          case "HYVAKSYTTAVISSA" => JsSuccess(mapper.readValue(Json.stringify(o), classOf[Hyvaksyttavissatila]))
          case "VIRHE" => JsSuccess(mapper.readValue(Json.stringify(o), classOf[Virhetila]))
          case _ => JsError("Tuntematon tilatyyppi")
        }

      }
      case _ => JsError("Vaara tilatyyppi")
    }
  }
  implicit def tilaWrites: Writes[Tila] = new Writes[Tila] {
    def writes(o: Tila): JsValue = {
      val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(o);
      Json.parse(json)
    }
  }

  implicit def mapReadsIntString: Reads[JMap[JInteger, String]] =
    new Reads[JMap[JInteger,String]] {
      def reads(json: JsValue): JsResult[JMap[JInteger,String]] = json match {
        case s : JsObject => JsSuccess(mapper.readValue(Json.stringify(s), classOf[JMap[JInteger, String]]))
        case _ => JsError("Tyyppiä Map ei löytynyt")
      }
    }

  implicit def mapWritesIntString: Writes[JMap[JInteger, String]] =
    new Writes[JMap[JInteger,String]] {
      def writes(map: JMap[JInteger,String]): JsValue = {
        //map.foldLeft(Json.obj())((s,a) => s ++ Json.obj(a._1.toString -> a._2))
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(map);
        Json.parse(json)
      }
    }

  implicit def mapReadsStringString: Reads[JMap[String, String]] =
    new Reads[JMap[String,String]] {
      def reads(json: JsValue): JsResult[JMap[String,String]] = json match {
        case s : JsObject => JsSuccess(mapper.readValue(Json.stringify(s), classOf[JMap[String, String]]))
        case _ => JsError("Tyyppiä Map ei löytynyt")
      }
    }

  implicit def mapWritesStringString: Writes[JMap[String, String]] =
    new Writes[JMap[String,String]] {
      def writes(map: JMap[String,String]): JsValue = {
        val json = mapper.writerWithView(classOf[JsonViews.Basic]).writeValueAsString(map);
        Json.parse(json)
      }
    }

  implicit def anyReads: Reads[Any] =
    new Reads[Any] {
      def reads(json: JsValue): JsResult[Any] = json match {
        case JsString(s) => JsSuccess(s)
        case JsBoolean(s) => JsSuccess(s)
        case JsNumber(s) => JsSuccess(s)
        case JsObject(s) => JsSuccess(s)
        case JsArray(s) => JsSuccess(s)
        case _ => JsError("Undefined Any type")
      }
    }

  implicit def anyWrites: Writes[Any] =
    new Writes[Any] {
      def writes(s: Any): JsValue = s match {
        case s: String => JsString(s)
        case s: Boolean => JsBoolean(s)
        case s: Int => JsNumber(s)
        case s: Double => JsNumber(s)
        case s: Long => JsNumber(s)
        case s: BigDecimal => JsNumber(s)
        case _ => JsNull
      }
    }




}
