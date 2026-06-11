package org.arnoldc

class IncrementDecrementTest extends ArnoldGeneratorTest {

  private def afterOp(start: Int, op: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE counter\n" +
        "YOU SET US UP " + start + "\n" +
        op + " counter\n" +
        "TALK TO THE HAND counter\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "increment a variable with ONE MORE TIME" in {
    afterOp(5, "ONE MORE TIME") should equal("6\n")
  }

  it should "decrement a variable with COUNTDOWN" in {
    afterOp(5, "COUNTDOWN") should equal("4\n")
  }

  it should "increment repeatedly" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE counter\n" +
        "YOU SET US UP 0\n" +
        "ONE MORE TIME counter\n" +
        "ONE MORE TIME counter\n" +
        "ONE MORE TIME counter\n" +
        "TALK TO THE HAND counter\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("3\n")
  }
}
