package org.arnoldc

class ConversionTest extends ArnoldGeneratorTest {

  it should "convert an int to a string for concatenation (SPELL IT OUT)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE n\n" +
        "YOU SET US UP 42\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"Score: \"\n" +
        "AND KICK ASS SPELL IT OUT n\n" +
        "TALK TO THE HAND s\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("Score: 42\n")
  }

  it should "convert a float to a string (SPELL IT OUT)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "NOW I HAVE A MACHINE GUN f\n" +
        "HO HO HO 3.5\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS SPELL IT OUT f\n" +
        "TALK TO THE HAND s\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("3.5\n")
  }

  it should "parse a string into an int (DO THE MATH)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"100\"\n" +
        "HEY CHRISTMAS TREE n\n" +
        "YOU SET US UP DO THE MATH s\n" +
        "GET TO THE CHOPPER n\n" +
        "HERE IS MY INVITATION n\n" +
        "GET UP 1\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND n\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("101\n")
  }
}
