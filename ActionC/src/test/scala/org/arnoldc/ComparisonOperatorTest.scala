package org.arnoldc

class ComparisonOperatorTest extends ArnoldGeneratorTest {

  private def compare(left: Int, op: String, right: Int): String = {
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

  // != : IT'S JUST BEEN REVOKED
  it should "evaluate not-equal as true when operands differ" in {
    compare(5, "IT'S JUST BEEN REVOKED", 3) should equal("1\n")
  }

  it should "evaluate not-equal as false when operands match" in {
    compare(5, "IT'S JUST BEEN REVOKED", 5) should equal("0\n")
  }

  // < : YOU'RE THE DISEASE AND I'M THE CURE
  it should "evaluate less-than as true when left is smaller" in {
    compare(3, "YOU'RE THE DISEASE AND I'M THE CURE", 5) should equal("1\n")
  }

  it should "evaluate less-than as false when left is larger" in {
    compare(5, "YOU'RE THE DISEASE AND I'M THE CURE", 3) should equal("0\n")
  }

  it should "evaluate less-than as false when operands are equal" in {
    compare(5, "YOU'RE THE DISEASE AND I'M THE CURE", 5) should equal("0\n")
  }

  // >= : I'M GETTING TOO OLD FOR THIS
  it should "evaluate greater-or-equal as true when left is larger" in {
    compare(5, "I'M GETTING TOO OLD FOR THIS", 3) should equal("1\n")
  }

  it should "evaluate greater-or-equal as true when operands are equal" in {
    compare(5, "I'M GETTING TOO OLD FOR THIS", 5) should equal("1\n")
  }

  it should "evaluate greater-or-equal as false when left is smaller" in {
    compare(3, "I'M GETTING TOO OLD FOR THIS", 5) should equal("0\n")
  }

  // <= : BENEATH YOU
  it should "evaluate less-or-equal as true when left is smaller" in {
    compare(3, "BENEATH YOU", 5) should equal("1\n")
  }

  it should "evaluate less-or-equal as true when operands are equal" in {
    compare(5, "BENEATH YOU", 5) should equal("1\n")
  }

  it should "evaluate less-or-equal as false when left is larger" in {
    compare(5, "BENEATH YOU", 3) should equal("0\n")
  }
}
