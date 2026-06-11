package org.arnoldc

class MathFunctionTest extends ArnoldGeneratorTest {

  private def mathResult(expr: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE r\n" +
        "YOU SET US UP " + expr + "\n" +
        "TALK TO THE HAND r\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "compute absolute value" in {
    mathResult("NO MORE HALF MEASURES -42") should equal("42\n")
  }

  it should "compute integer square root" in {
    mathResult("GET TO THE ROOT OF 16") should equal("4\n")
  }

  it should "compute maximum of two values" in {
    mathResult("MAXIMUM EFFORT OF 3 7") should equal("7\n")
  }

  it should "compute minimum of two values" in {
    mathResult("MINIMAL CASUALTIES OF 3 7") should equal("3\n")
  }

  it should "compute integer power" in {
    mathResult("UNLIMITED POWER OF 2 10") should equal("1024\n")
  }

  it should "compute a bounded random value (bound 1 is always 0)" in {
    mathResult("GO AHEAD MAKE MY DAY 1") should equal("0\n")
  }
}
