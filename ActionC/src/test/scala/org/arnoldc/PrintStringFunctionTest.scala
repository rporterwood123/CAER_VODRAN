package org.arnoldc

// Printing a string-returning function result directly with TALK TO THE HAND,
// without first assigning it into a string variable.
class PrintStringFunctionTest extends ArnoldGeneratorTest {

  private def prog(printArg: String): String =
    "IT'S SHOWTIME\n" + printArg + "YOU HAVE BEEN TERMINATED\n"

  it should "print a reversed string directly (PUT IT IN REVERSE)" in {
    getOutput(prog("TALK TO THE HAND PUT IT IN REVERSE \"stressed\"\n")) should equal("desserts\n")
  }

  it should "print an uppercased string directly (SAY IT LOUDER)" in {
    getOutput(prog("TALK TO THE HAND SAY IT LOUDER \"hello\"\n")) should equal("HELLO\n")
  }

  it should "print a number converted to a string directly (SPELL IT OUT)" in {
    val code =
      "HEY CHRISTMAS TREE n\n" +
        "YOU SET US UP 42\n" +
        "TALK TO THE HAND SPELL IT OUT n\n"
    getOutput(prog(code)) should equal("42\n")
  }

  it should "print a replaced string directly (GET A NEW ONE)" in {
    getOutput(prog("TALK TO THE HAND GET A NEW ONE \"a-b-c\" \"-\" \"+\"\n")) should equal("a+b+c\n")
  }
}
