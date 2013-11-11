package fi.vm.sade.kaava

import org.scalatest._

/**
 *
 * User: tommiha
 * Date: 1/12/13
 * Time: 6:48 PM
 */
class FunktiokuvausTest extends FunSuite {

  test("annaFunktiokuvaukset") {
    val kuvaukset = Funktiokuvaaja.annaFunktiokuvaukset
    assert(kuvaukset.size > 0)
  }

  test("annaFunktiokuvauksetAsJson") {
    val json = Funktiokuvaaja.annaFunktiokuvauksetAsJson
    assert(!json.isEmpty)
  }
}
