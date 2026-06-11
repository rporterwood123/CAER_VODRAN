package org.arnoldc

class BitwiseTest extends ArnoldGeneratorTest {

  private def bitop(left: Int, op: String, right: Int): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE result\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER result\n" +
        "HERE IS MY INVITATION " + left + "\n" +
        op + " " + right + "\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND result\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "compute bitwise AND" in {
    bitop(6, "WINNERS GO HOME AND DATE THE PROM QUEEN", 3) should equal("2\n")
  }

  it should "compute bitwise OR" in {
    bitop(6, "DEAD OR ALIVE YOU'RE COMING WITH ME", 3) should equal("7\n")
  }

  it should "compute bitwise XOR" in {
    bitop(6, "FRIEND OR FOE", 3) should equal("5\n")
  }

  it should "compute left shift" in {
    bitop(1, "MOVE IT", 4) should equal("16\n")
  }

  it should "compute right shift" in {
    bitop(16, "FALL BACK", 2) should equal("4\n")
  }
}
