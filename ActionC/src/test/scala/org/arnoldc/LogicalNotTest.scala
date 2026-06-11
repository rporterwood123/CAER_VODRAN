package org.arnoldc

class LogicalNotTest extends ArnoldGeneratorTest {

  // NEGATIVE flips the running boolean value of the expression (1 -> 0, 0 -> 1).
  private def notOf(left: Int, op: String, right: Int): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE result\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER result\n" +
        "HERE IS MY INVITATION " + left + "\n" +
        op + " " + right + "\n" +
        "NEGATIVE\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND result\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "negate a true comparison to false" in {
    notOf(5, "LET OFF SOME STEAM BENNET", 3) should equal("0\n")
  }

  it should "negate a false comparison to true" in {
    notOf(3, "LET OFF SOME STEAM BENNET", 5) should equal("1\n")
  }
}
