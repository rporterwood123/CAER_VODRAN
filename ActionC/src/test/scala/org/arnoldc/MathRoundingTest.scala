package org.arnoldc

class MathRoundingTest extends ArnoldGeneratorTest {

  private def withFloat(literal: String, op: String): String =
    "IT'S SHOWTIME\n" +
      "NOW I HAVE A MACHINE GUN f\n" +
      "HO HO HO " + literal + "\n" +
      "HEY CHRISTMAS TREE n\n" +
      "YOU SET US UP " + op + " f\n" +
      "TALK TO THE HAND n\n" +
      "YOU HAVE BEEN TERMINATED\n"

  it should "floor a float down to an int (HIT THE FLOOR)" in {
    getOutput(withFloat("3.7", "HIT THE FLOOR")) should equal("3\n")
  }

  it should "ceil a float up to an int (THROUGH THE ROOF)" in {
    getOutput(withFloat("3.2", "THROUGH THE ROOF")) should equal("4\n")
  }

  it should "round a float to the nearest int (ROUND THEM UP)" in {
    getOutput(withFloat("3.5", "ROUND THEM UP")) should equal("4\n")
  }

  it should "round half-down correctly" in {
    getOutput(withFloat("3.4", "ROUND THEM UP")) should equal("3\n")
  }
}
