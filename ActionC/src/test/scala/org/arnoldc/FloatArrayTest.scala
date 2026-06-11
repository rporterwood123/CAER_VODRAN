package org.arnoldc

class FloatArrayTest extends ArnoldGeneratorTest {

  it should "store and read a float array element (LOCK AND LOAD)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LOCK AND LOAD arr WITH 2 UGLY MOTHERFUCKERS\n" +
        "GET IN LINE arr AT 0\n" +
        "HERE IS MY INVITATION 2.5\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND GET IN LINE arr AT 0\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("2.5\n")
  }

  it should "coerce an int written into a float array" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LOCK AND LOAD arr WITH 1 UGLY MOTHERFUCKERS\n" +
        "GET IN LINE arr AT 0\n" +
        "HERE IS MY INVITATION 3\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND GET IN LINE arr AT 0\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("3.0\n")
  }

  it should "sum float array elements in a float expression" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LOCK AND LOAD arr WITH 2 UGLY MOTHERFUCKERS\n" +
        "GET IN LINE arr AT 0\n" +
        "HERE IS MY INVITATION 1.5\n" +
        "ENOUGH TALK\n" +
        "GET IN LINE arr AT 1\n" +
        "HERE IS MY INVITATION 3.0\n" +
        "ENOUGH TALK\n" +
        "NOW I HAVE A MACHINE GUN x\n" +
        "HO HO HO 0.0\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION GET IN LINE arr AT 0\n" +
        "GET UP GET IN LINE arr AT 1\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND x\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("4.5\n")
  }

  it should "report a float array's length (HOW MANY OF THEM)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LOCK AND LOAD arr WITH 5 UGLY MOTHERFUCKERS\n" +
        "TALK TO THE HAND HOW MANY OF THEM arr\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("5\n")
  }
}
