package org.arnoldc

class FloatArithmeticTest extends ArnoldGeneratorTest {

  it should "add two floats in an assignment block" in {
    val code =
      "IT'S SHOWTIME\n" +
        "NOW I HAVE A MACHINE GUN x\n" +
        "HO HO HO 1.5\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION x\n" +
        "GET UP 2.0\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("3.5\n")
  }

  it should "subtract, multiply, and divide floats" in {
    val code =
      "IT'S SHOWTIME\n" +
        "NOW I HAVE A MACHINE GUN x\n" +
        "HO HO HO 10.0\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION x\n" +
        "GET DOWN 1.0\n" +
        "YOU'RE FIRED 2.0\n" +
        "HE HAD TO SPLIT 6.0\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    // ((10.0 - 1.0) * 2.0) / 6.0 = 3.0
    getOutput(code) should equal("3.0\n")
  }

  it should "promote an int operand to float in a mixed expression" in {
    val code =
      "IT'S SHOWTIME\n" +
        "NOW I HAVE A MACHINE GUN x\n" +
        "HO HO HO 1.0\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION x\n" +
        "HE HAD TO SPLIT 4\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    // 1.0 / 4 -> int 4 promoted to 4.0 -> 0.25 (not integer division)
    getOutput(code) should equal("0.25\n")
  }

  it should "keep pure int arithmetic as integer division" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP 7\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION x\n" +
        "HE HAD TO SPLIT 2\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    // 7 / 2 stays integer division = 3
    getOutput(code) should equal("3\n")
  }

  it should "coerce an int-valued expression assigned into a float variable" in {
    val code =
      "IT'S SHOWTIME\n" +
        "NOW I HAVE A MACHINE GUN x\n" +
        "HO HO HO 0.0\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION 5\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("5.0\n")
  }
}
