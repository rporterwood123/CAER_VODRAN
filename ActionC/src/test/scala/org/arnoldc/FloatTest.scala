package org.arnoldc

class FloatTest extends ArnoldGeneratorTest {

  private def floatProgram(literal: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "NOW I HAVE A MACHINE GUN x\n" +
        "HO HO HO " + literal + "\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "declare, initialize, and print a float" in {
    floatProgram("3.14") should equal("3.14\n")
  }

  it should "print a whole-number float with a decimal point" in {
    floatProgram("2.0") should equal("2.0\n")
  }

  it should "support negative floats" in {
    floatProgram("-0.5") should equal("-0.5\n")
  }
}
