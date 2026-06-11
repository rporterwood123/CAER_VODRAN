package org.arnoldc

class LambdaTest extends ArnoldGeneratorTest {

  it should "define and call a single-parameter lambda" in {
    val code =
      "CALL ME SNAKE double (x) => x YOU'RE FIRED 2\n" +
        "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE result\n" +
        "YOU SET US UP 0\n" +
        "GET YOUR ASS TO MARS result\n" +
        "DO IT NOW double 21\n" +
        "TALK TO THE HAND result\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("42\n")
  }

  it should "define and call a two-parameter lambda" in {
    val code =
      "CALL ME SNAKE add (x y) => x GET UP y\n" +
        "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE result\n" +
        "YOU SET US UP 0\n" +
        "GET YOUR ASS TO MARS result\n" +
        "DO IT NOW add 10 20\n" +
        "TALK TO THE HAND result\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("30\n")
  }

  it should "call a lambda through a function reference" in {
    val code =
      "CALL ME SNAKE add (x y) => x GET UP y\n" +
        "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE myFunc\n" +
        "YOU SET US UP THE NAME'S PLISSKEN add\n" +
        "HEY CHRISTMAS TREE sum\n" +
        "YOU SET US UP 0\n" +
        "GET YOUR ASS TO MARS sum\n" +
        "DO IT NOW myFunc 10 20\n" +
        "TALK TO THE HAND sum\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("30\n")
  }
}
