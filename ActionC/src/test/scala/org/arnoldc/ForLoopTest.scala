package org.arnoldc

class ForLoopTest extends ArnoldGeneratorTest {

  it should "iterate an inclusive range printing the counter" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 1 TO 5\n" +
        "TALK TO THE HAND i\n" +
        "GAME OVER MAN GAME OVER\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\n2\n3\n4\n5\n")
  }

  it should "not execute the body when start is greater than end" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 5 TO 1\n" +
        "TALK TO THE HAND i\n" +
        "GAME OVER MAN GAME OVER\n" +
        "TALK TO THE HAND \"done\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("done\n")
  }

  it should "support variable bounds and accumulate inside the loop" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE sum\n" +
        "YOU SET US UP 0\n" +
        "HEY CHRISTMAS TREE n\n" +
        "YOU SET US UP 4\n" +
        "LET'S ROCK i FROM 1 TO n\n" +
        "GET TO THE CHOPPER sum\n" +
        "HERE IS MY INVITATION sum\n" +
        "GET UP i\n" +
        "ENOUGH TALK\n" +
        "GAME OVER MAN GAME OVER\n" +
        "TALK TO THE HAND sum\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("10\n")
  }

  it should "support nested for loops" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 1 TO 2\n" +
        "LET'S ROCK j FROM 1 TO 2\n" +
        "TALK TO THE HAND i\n" +
        "TALK TO THE HAND j\n" +
        "GAME OVER MAN GAME OVER\n" +
        "GAME OVER MAN GAME OVER\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\n1\n1\n2\n2\n1\n2\n2\n")
  }
}
