package fi.vm.sade.kaava

import java.time.format.DateTimeFormatter
import java.util.{Set => JSet}

import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti

import scala.jdk.CollectionConverters._

object LaskentaUtil {
  val suomalainenPvmMuoto: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

  def jarjestaFunktioargumentit(args: JSet[Funktioargumentti]): List[Funktioargumentti] = {
    args.asScala.toList.sortWith(_.getIndeksi < _.getIndeksi)
  }

  def prettyPrint(x: Any): String = {
    pprint(x).toString()
  }

  private def pprint(obj: Any, depth: Int = 0, paramName: Option[String] = None, acc: StringBuilder = new StringBuilder("")): StringBuilder = {
    val indent = "  " * depth
    val prettyName = paramName.fold("")(x => s"$x: ")
    val ptype = obj match {
      case _: Iterable[Any] => ""
      case obj: Product => obj.productPrefix
      case _ => obj.toString
    }

    acc.append(s"$indent$prettyName$ptype")

    obj match {
      case seq: Iterable[Any] =>
        seq.foreach(pprint(_, depth + 1, acc = acc.append("\n")))
      case obj: Product =>
        (obj.productIterator zip obj.productElementNames)
          .foreach { case (subObj, paramName) => pprint(subObj, depth + 1, Some(paramName), acc = acc.append("\n")) }
      case _ =>
    }
    acc
  }
}
