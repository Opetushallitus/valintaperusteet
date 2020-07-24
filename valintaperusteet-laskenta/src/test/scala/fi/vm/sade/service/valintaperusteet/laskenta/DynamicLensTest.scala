package fi.vm.sade.service.valintaperusteet.laskenta

import io.circe.Json
import io.circe.optics.JsonPath
import io.circe.optics.JsonTraversalPath
import org.scalatest.funsuite.AnyFunSuite

/**
  * Idea talteen, josko tätä voisi hyödyntää JSONien käsittelyssä.
  */
class DynamicLensTest extends AnyFunSuite {
  private val inputJson =
    """
          [
            {
              "tutkinto": {
                "arvosanat": [
                  {
                    "matematiikka": {
                      "laajuus": 10,
                      "arvosana": 6
                    }
                  },
                  {
                    "fysiikka": {
                      "laajuus": 10,
                      "arvosana": 10
                    }
                  }
                ],
                "keskiarvo": 8.0
              }
            }
          ]
    """

  test("JSONia voi purkaa dynaamisesti") {
    val json: Json = io.circe.parser.parse(inputJson) match {
      case Right(json) => json
      case Left(e)     => throw e
    }

    assert(
      haeJsonistaDynaamisesti(JsonPath.root.each, "tutkinto.keskiarvo")
        .as[BigDecimal]
        .getAll(json)
        .head == BigDecimal(8.0)
    )
    assert(
      haeJsonistaDynaamisesti(JsonPath.root.each, "tutkinto.arvosanat.[].matematiikka.arvosana")
        .as[BigDecimal]
        .getAll(json)
        .head == BigDecimal(6)
    )
    assert(
      haeJsonistaDynaamisesti(JsonPath.root.each, "tutkinto.arvosanat.[].fysiikka.arvosana")
        .as[BigDecimal]
        .getAll(json)
        .head == BigDecimal(10)
    )
  }

  private def haeJsonistaDynaamisesti(
    juuri: JsonTraversalPath,
    polku: String
  ): JsonTraversalPath = {
    polku
      .split('.')
      .foldLeft[JsonTraversalPath](juuri)((jsonTraversalPath, askel) =>
        askel match {
          case "[]" => jsonTraversalPath.each
          case p    => jsonTraversalPath.selectDynamic(p)
        }
      )
  }
}
