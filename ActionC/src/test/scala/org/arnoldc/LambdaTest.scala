package org.arnoldc

import org.parboiled.errors.ParsingException

class LambdaTest extends ArnoldGeneratorTest {

  it should "reject a lambda referencing a main-method variable" in {
    // Lambdas compile to static methods with their own frame — no closure capture.
    // Even when the lambda is generated after main, the reference must be rejected
    // at compile time instead of silently reading the wrong frame's slot.
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE outer\n" +
        "YOU SET US UP 5\n" +
        "HEY CHRISTMAS TREE r\n" +
        "YOU SET US UP 0\n" +
        "GET YOUR ASS TO MARS r\n" +
        "DO IT NOW addBase 10\n" +
        "TALK TO THE HAND r\n" +
        "YOU HAVE BEEN TERMINATED\n" +
        "CALL ME SNAKE addBase (x) => x GET UP outer\n"
    a[ParsingException] should be thrownBy getOutput(code)
  }

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
