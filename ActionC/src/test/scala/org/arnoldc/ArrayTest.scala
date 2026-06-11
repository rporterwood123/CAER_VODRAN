package org.arnoldc

class ArrayTest extends ArnoldGeneratorTest {

  it should "declare an array, set and get elements" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I AIN'T GOT TIME TO BLEED scores WITH 3 UGLY MOTHERFUCKERS\n" +
        "GET IN LINE scores AT 0\n" +
        "HERE IS MY INVITATION 100\n" +
        "ENOUGH TALK\n" +
        "GET IN LINE scores AT 1\n" +
        "HERE IS MY INVITATION 200\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND GET IN LINE scores AT 0\n" +
        "TALK TO THE HAND GET IN LINE scores AT 1\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("100\n200\n")
  }

  it should "report array length with HOW MANY OF THEM" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I AIN'T GOT TIME TO BLEED arr WITH 5 UGLY MOTHERFUCKERS\n" +
        "HEY CHRISTMAS TREE len\n" +
        "YOU SET US UP HOW MANY OF THEM arr\n" +
        "TALK TO THE HAND len\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("5\n")
  }

  it should "default array elements to zero" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I AIN'T GOT TIME TO BLEED arr WITH 3 UGLY MOTHERFUCKERS\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP GET IN LINE arr AT 2\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("0\n")
  }

  it should "fill an array in a loop and read back a computed element" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I AIN'T GOT TIME TO BLEED arr WITH 5 UGLY MOTHERFUCKERS\n" +
        "LET'S ROCK i FROM 0 TO 4\n" +
        "GET IN LINE arr AT i\n" +
        "HERE IS MY INVITATION i\n" +
        "YOU'RE FIRED i\n" +
        "ENOUGH TALK\n" +
        "GAME OVER MAN GAME OVER\n" +
        "TALK TO THE HAND GET IN LINE arr AT 3\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("9\n")
  }
}
