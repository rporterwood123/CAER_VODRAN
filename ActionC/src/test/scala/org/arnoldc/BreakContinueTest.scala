package org.arnoldc

class BreakContinueTest extends ArnoldGeneratorTest {

  it should "break out of a for loop with GET OUT" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 1 TO 10\n" +
        "HEY CHRISTMAS TREE atFour\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER atFour\n" +
        "HERE IS MY INVITATION i\n" +
        "YOU ARE NOT YOU YOU ARE ME 4\n" +
        "ENOUGH TALK\n" +
        "BECAUSE I'M GOING TO SAY PLEASE atFour\n" +
        "GET OUT\n" +
        "YOU HAVE NO RESPECT FOR LOGIC\n" +
        "TALK TO THE HAND i\n" +
        "GAME OVER MAN GAME OVER\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\n2\n3\n")
  }

  it should "continue to the next iteration of a for loop with KEEP MOVING" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 1 TO 5\n" +
        "HEY CHRISTMAS TREE isThree\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER isThree\n" +
        "HERE IS MY INVITATION i\n" +
        "YOU ARE NOT YOU YOU ARE ME 3\n" +
        "ENOUGH TALK\n" +
        "BECAUSE I'M GOING TO SAY PLEASE isThree\n" +
        "KEEP MOVING\n" +
        "YOU HAVE NO RESPECT FOR LOGIC\n" +
        "TALK TO THE HAND i\n" +
        "GAME OVER MAN GAME OVER\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\n2\n4\n5\n")
  }

  it should "break out of a while loop with GET OUT" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE i\n" +
        "YOU SET US UP 0\n" +
        "STICK AROUND @NO PROBLEMO\n" +
        "ONE MORE TIME i\n" +
        "HEY CHRISTMAS TREE done\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER done\n" +
        "HERE IS MY INVITATION i\n" +
        "YOU ARE NOT YOU YOU ARE ME 3\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND i\n" +
        "BECAUSE I'M GOING TO SAY PLEASE done\n" +
        "GET OUT\n" +
        "YOU HAVE NO RESPECT FOR LOGIC\n" +
        "CHILL\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\n2\n3\n")
  }
}
